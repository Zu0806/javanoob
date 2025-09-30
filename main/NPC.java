

package main;

/**
 * 非玩家角色（NPC）
 * - 包含基本性格描述
 * - 與玩家的互動會影響好感度
 */
public class NPC extends Character {
    private String personality;
    private String imagePath;

    public NPC(String name, String personality, String imagePath) {
        super(name);
        this.personality = personality;
        this.imagePath = imagePath;
    }

    public String getPersonality() {
        return personality;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void interact(String action) {
        // 根據不同互動調整屬性
        switch (action) {
            case "talk":
                adjustAffection(5);
                break;
            case "gift":
                adjustAffection(10);
                adjustMood(5);
                break;
            case "ignore":
                adjustAffection(-5);
                break;
        }
    }
}
