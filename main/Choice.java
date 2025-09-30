package main;

/**
 * Choice 代表對話選項
 * - 文字
 * - 選擇後的效果（好感度、狀態值變化）
 * - 對應的下一段對話 ID
 */
public class Choice {
    private String text;
    private String nextDialogueId;
    private String targetNPC;
    private int affectionChange;

    public Choice(String text, String nextDialogueId, String targetNPC, int affectionChange) {
        this.text = text;
        this.nextDialogueId = nextDialogueId;
        this.targetNPC = targetNPC;
        this.affectionChange = affectionChange;
    }

    public String getText() {
        return text;
    }

    public String getNextDialogueId() {
        return nextDialogueId;
    }

    public String getTargetNPC() {
        return targetNPC;
    }

    public int getAffectionChange() {
        return affectionChange;
    }
}

