package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * SaveLoadSystem
 * - 將遊戲狀態序列化為 JSON 存檔/讀檔
 * - 存內容：玩家名稱、Stats(體力/心情/智商)、知識點、好感度Map、
 *          當前場景/對話ID、時段、背景Key、BGM Key、版本、時間戳
 *
 * 用法：
 *   SaveLoadSystem.SaveData data = SaveLoadSystem.capture(player, stats, affection,
 *         currentSceneId, currentDialogueId, timeOfDay, backgroundKey, bgmKey);
 *   SaveLoadSystem.saveToFile(data, "save1.json");
 *
 *   SaveLoadSystem.SaveData loaded = SaveLoadSystem.loadFromFile("save1.json");
 *   SaveLoadSystem.restore(loaded, player, stats, affection, ctx -> {
 *       // 這裡把場景/對話/背景/BGM 還原到你的系統
 *       // e.g. dialogueManager.setCurrentDialogue(ctx.currentDialogueId());
 *       //      resourceManager / uiManager 切背景與音樂...
 *   });
 */
public class SaveLoadSystem {

    // ====== 公用 API ======

    /** 擷取目前遊戲快照 */
    public static SaveData capture(
            Player player,
            Stats stats,
            AffectionSystem affection,
            String currentSceneId,
            String currentDialogueId,
            String timeOfDay,
            String backgroundKey,
            String bgmKey
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(stats, "stats");
        Objects.requireNonNull(affection, "affection");

        SaveData d = new SaveData();
        d.version = 1;
        d.savedAtEpochSec = Instant.now().getEpochSecond();

        d.playerName = player.getName();
        d.knowledgePoints = player.getKnowledgePoints();

        d.stamina = stats.getStamina();
        d.mood = stats.getMood();
        d.intelligence = stats.getIntelligence();

        d.affectionMap = snapshotAffection(affection);

        d.currentSceneId = currentSceneId;
        d.currentDialogueId = currentDialogueId;
        d.timeOfDay = timeOfDay;           // "MORNING" / "NOON" / "NIGHT" / ...
        d.backgroundKey = backgroundKey;   // e.g. "campus_morning"
        d.bgmKey = bgmKey;                 // e.g. "main_theme"

        return d;
    }

    /** 還原到現有物件；並把與場景/對話/資源相關的欄位交給 caller via callback 應用 */
    public static void restore(
            SaveData data,
            Player player,
            Stats stats,
            AffectionSystem affection,
            Consumer<SaveContext> applyContext
    ) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(stats, "stats");
        Objects.requireNonNull(affection, "affection");

        // 玩家
        // （注意：這裡假設 Player 名稱不可變更，若要一起改可在 Player 增加 setter）
        player.addKnowledgePoints(Math.max(0, data.knowledgePoints - player.getKnowledgePoints()));

        // 屬性
        stats.setStamina(data.stamina);
        stats.setMood(data.mood);
        stats.setIntelligence(data.intelligence);

        // 好感
        restoreAffection(affection, data.affectionMap);

        // 場景/對話/資源交給外層實際套用（避免 Save 系統依賴 UI/引擎實作）
        if (applyContext != null) {
            applyContext.accept(new SaveContext(
                    data.currentSceneId,
                    data.currentDialogueId,
                    data.timeOfDay,
                    data.backgroundKey,
                    data.bgmKey
            ));
        }
    }

    /** 存檔到檔案 */
    public static void saveToFile(SaveData data, String path) throws IOException {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(path, "path");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(gson.toJson(data));
        }
    }

    /** 從檔案讀檔 */
    public static SaveData loadFromFile(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        Gson gson = new GsonBuilder().create();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return gson.fromJson(br, SaveData.class);
        }
    }

    // ====== 內部工具 ======

    private static Map<String, Integer> snapshotAffection(AffectionSystem affection) {
        // AffectionSystem 沒有直接 expose map，這裡示範一個安全作法：
        // - 先列出已知的角色ID/名稱清單由外層傳入較好
        // 為了範例，提供一個反射/備案介面：如果你有 getAffection(npcName) 但沒有列舉功能，
        // 你可以自行維護一份角色名清單存在別處並在 capture(...) 傳入，這裡簡化為空 Map。
        // ----
        // 這裡我們假設你會在遊戲流程中，對應三位男主固定 key：
        String[] known = new String[]{"xiechen", "wenjin", "muyang"};
        Map<String, Integer> map = new HashMap<>();
        for (String k : known) {
            map.put(k, affection.getAffection(k));
        }
        return map;
    }

    private static void restoreAffection(AffectionSystem affection, Map<String, Integer> saved) {
        if (saved == null) return;
        for (Map.Entry<String, Integer> e : saved.entrySet()) {
            affection.setAffection(e.getKey(), e.getValue());
        }
    }

    // ====== DTO / Context ======

    /** 存檔資料結構（Gson 需要無參數建構子） */
    public static class SaveData {
        public int version;
        public long savedAtEpochSec;

        public String playerName;
        public int knowledgePoints;

        public int stamina;
        public int mood;
        public int intelligence;

        public Map<String, Integer> affectionMap;

        public String currentSceneId;
        public String currentDialogueId;
        public String timeOfDay;
        public String backgroundKey;
        public String bgmKey;

        public SaveData() {}
    }

    /** 還原時讓外層一次拿到需要套用到 UI/引擎的資訊 */
    public record SaveContext(
            String currentSceneId,
            String currentDialogueId,
            String timeOfDay,
            String backgroundKey,
            String bgmKey
    ) {}
}
