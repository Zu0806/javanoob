package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Game：整合入口（Swing）
 * - 建立視窗與 UIManager 畫面
 * - 初始化各系統
 * - 載入劇情並從 intro 節點開始
 * - 以 JOptionPane 讓玩家輸入選項（1..N）
 *
 * 資源約定（可改）：
 *   背景 key: "campus_morning" / "campus_noon" / "campus_night"
 *   人物 key: "heroine"（女主），其他你可依 ResourceManager 加入
 *   BGM key : "main_theme"
 *
 * 劇本檔（示例 CSV-like）：
 *   DIALOGUE,intro,老師,歡迎來到新學校！
 *   CHOICE,intro,0,點頭回應,next1,謝塵,5
 *   CHOICE,intro,1,低頭不語,next2,謝塵,-2
 *   DIALOGUE,next1,謝塵,別緊張，我上課會睡但不吵你。
 *   CHOICE,next1,0,小聲道謝,end,null,0
 *   DIALOGUE,next2,旁白,你選擇先觀察環境。
 *   CHOICE,next2,0,走向教室,end,null,0
 *   DIALOGUE,end,旁白,第一天就到這裡吧！
 *
 * 放檔名：project 根目錄或執行目錄下 "story.txt"
 */
public class Game extends JFrame {

    // 視覺
    private final UIManager uiPanel = new UIManager();

    // 資源與音樂
    private final ResourceManager resources = new ResourceManager();
    private final BGMPlayer bgm = new BGMPlayer();

    // 角色與狀態
    private final Player player = new Player("女主角");
    private final AffectionSystem affection = new AffectionSystem();
    private final Stats stats = new Stats();
    private final StatsManager statsManager = new StatsManager(stats);

    // 題庫（先初始化，等你需要時接上）
    private final QuizManager quizManager = new QuizManager(statsManager, affection);

    // 對話/劇情
    private final DialogueManager dialogueManager = new DialogueManager();
    private final StoryLoader storyLoader = new StoryLoader();

    public Game() {
        super("校園分歧劇情 DEMO");

        // ----- 視窗/畫面 -----
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(uiPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

        // ----- 背景 & 立繪預設 -----
        // 背景
        setBackgroundByKey("campus_morning");
        // 角色（可換成你需要的人物）
        setCharacterByKey("heroine");

        // ----- BGM -----
        // ⚠ 注意：javax.sound.sampled 的 Clip 通常只能播 WAV/AIFF，MP3 需要 SPI。
        // 若你沒裝 MP3 SPI，請把 ResourceManager 的音檔改成 .wav。
        String bgmPath = resources.getBGM("main_theme");
        bgm.play(bgmPath, true);

        // ----- 載入劇情 -----
        File storyFile = new File("story.txt");
        if (!storyFile.exists()) {
            System.out.println("[提示] 找不到 story.txt，請先建立一份測試劇本。");
        } else {
            storyLoader.loadFromFile(storyFile.getPath());
            // 把載入到的對話資料掛上 DialogueManager
            for (var entry : storyLoader.getDialogueMap().entrySet()) {
                dialogueManager.addDialogue(entry.getKey(), entry.getValue());
            }
        }

        // ----- 進入劇情起點 -----
        if (storyLoader.getDialogue("intro") != null) {
            dialogueManager.setCurrentDialogue("intro");
        } else if (!storyLoader.getDialogueMap().isEmpty()) {
            // 隨便找一個第一個節點
            var firstKey = storyLoader.getDialogueMap().keySet().iterator().next();
            dialogueManager.setCurrentDialogue(firstKey);
        }

        // 初始狀態列
        refreshStatusBar();

        // 渲染一次
        renderCurrentDialogueAndPrompt();
    }

    /** 依目前對話節點在畫面更新對話與選項，並以 JOptionPane 讓玩家選擇 */
    private void renderCurrentDialogueAndPrompt() {
        Dialogue d = dialogueManager.getCurrentDialogue();
        if (d == null) {
            uiPanel.setDialogueText("（沒有更多對話了）");
            uiPanel.setOptions(new String[0]);
            uiPanel.repaint();
            return;
        }

        // 對話人物的立繪（若有對應 key 可以自行 mapping）
        // 這裡簡單示範：若 speaker 是「謝塵/文錦/沐暘」可自行加到 ResourceManager
        maybeShowPortraitForSpeaker(d.getSpeaker());

        // 顯示文字
        uiPanel.setDialogueText(d.getSpeaker() + "： " + d.getText());

        // 顯示選項
        List<Choice> choices = d.getChoices();
        String[] optionTexts = choices == null ? new String[0]
                : choices.stream().map(Choice::getText).toArray(String[]::new);
        uiPanel.setOptions(optionTexts);
        uiPanel.repaint();

        // 若有選項，叫出輸入框
        if (optionTexts.length > 0) {
            int pick = promptPick(optionTexts.length); // 1..N，-1 表示取消
            if (pick != -1) {
                // 選擇後：套用好感變化
                Choice chosen = choices.get(pick);
                if (chosen.getTargetNPC() != null) {
                    affection.addAffection(chosen.getTargetNPC(), chosen.getAffectionChange());
                }

                // 示範：每次選擇視為一次「行動」，扣一點體力、心情自然衰減
                statsManager.doActionSpendDefaultStamina();
                statsManager.applyBusyMoodDecay();

                // 切下一段對話
                dialogueManager.setCurrentDialogue(chosen.getNextDialogueId());

                // 更新狀態列並重畫
                refreshStatusBar();
                renderCurrentDialogueAndPrompt();
            }
        } else {
            // 沒有選項時，視為章節終點
            JOptionPane.showMessageDialog(this, "本段落結束！");
        }
    }

    /** 取得使用者輸入的選項（1..N） */
    private int promptPick(int n) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "請輸入選項編號（1-" + n + "），或取消關閉",
                    "選擇",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (input == null) return -1; // 取消
            try {
                int v = Integer.parseInt(input.trim());
                if (v >= 1 && v <= n) return v - 1;
            } catch (NumberFormatException ignored) {}
            JOptionPane.showMessageDialog(this, "輸入不合法，請重試。");
        }
    }

    /** 用 ResourceManager key 設定背景圖 */
    private void setBackgroundByKey(String bgKey) {
        String path = resources.getBackgroundImage(bgKey);
        uiPanel.setBackgroundImage(loadImage(path));
    }

    /** 用 ResourceManager key 設定人物立繪 */
    private void setCharacterByKey(String charKey) {
        String path = resources.getCharacterImage(charKey);
        uiPanel.setCharacterImage(loadImage(path));
    }

    /** 根據說話者名詞做簡單對應（可改成 characters.json 驅動） */
    private void maybeShowPortraitForSpeaker(String speaker) {
        if (speaker == null) return;
        // 你可以在 ResourceManager 先 addCharacterImage("xiechen", "...") 等
        String key;
        if (speaker.contains("謝塵")) key = "xiechen";
        else if (speaker.contains("文錦")) key = "wenjin";
        else if (speaker.contains("沐暘")) key = "muyang";
        else key = "heroine";

        String path = resources.getCharacterImage(key);
        uiPanel.setCharacterImage(loadImage(path));
    }

    /** 載圖工具：若檔案不存在回傳透明占位 */
    private Image loadImage(String filePath) {
        try {
            if (filePath == null) return placeholder();
            File f = new File(filePath);
            if (!f.exists()) return placeholder();
            return new ImageIcon(filePath).getImage();
        } catch (Exception e) {
            return placeholder();
        }
    }

    private Image placeholder() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0,0,0,0));
        g.fillRect(0,0,20,20);
        g.dispose();
        return img;
    }

    /** 重新整理狀態條（好感度用三位主角平均做示意） */
    private void refreshStatusBar() {
        int xc = affection.getAffection("xiechen");
        int wj = affection.getAffection("wenjin");
        int my = affection.getAffection("muyang");
        int avgAff = (xc + wj + my) / 3;
        uiPanel.updateStats(avgAff, stats.getStamina(), stats.getMood(), stats.getIntelligence());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }
}
