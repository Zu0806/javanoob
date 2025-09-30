
package main;

import java.util.List;

/**
 * Scene 代表一個場景
 * - 場景名稱
 * - 對應的背景圖片 key
 * - 場景中的對話起點
 * - 出現的人物
 * - 場景時間（早、中、晚）
 */
public class Scene {
    private String id;
    private String name;
    private String backgroundKey;
    private String startDialogueId;
    private List<String> characters;
    private String timeOfDay; // morning, noon, night

    public Scene(String id, String name, String backgroundKey, String startDialogueId,
                 List<String> characters, String timeOfDay) {
        this.id = id;
        this.name = name;
        this.backgroundKey = backgroundKey;
        this.startDialogueId = startDialogueId;
        this.characters = characters;
        this.timeOfDay = timeOfDay;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBackgroundKey() {
        return backgroundKey;
    }

    public String getStartDialogueId() {
        return startDialogueId;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }
}

