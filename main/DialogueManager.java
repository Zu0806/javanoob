package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DialogueManager 負責管理所有對話
 * - 儲存對話資料
 * - 根據選擇切換到下一段對話
 */
public class DialogueManager {
    private Map<String, Dialogue> dialogueMap;
    private Dialogue currentDialogue;

    public DialogueManager() {
        dialogueMap = new HashMap<>();
    }

    public void addDialogue(String id, Dialogue dialogue) {
        dialogueMap.put(id, dialogue);
    }

    public void setCurrentDialogue(String id) {
        currentDialogue = dialogueMap.get(id);
    }

    public Dialogue getCurrentDialogue() {
        return currentDialogue;
    }

    public List<Choice> getCurrentChoices() {
        return currentDialogue != null ? currentDialogue.getChoices() : null;
    }

    public void choose(int index, AffectionSystem affectionSystem) {
        if (currentDialogue == null || currentDialogue.getChoices() == null) return;
        if (index < 0 || index >= currentDialogue.getChoices().size()) return;

        Choice choice = currentDialogue.getChoices().get(index);

        // 調整好感度
        if (choice.getTargetNPC() != null) {
            affectionSystem.addAffection(choice.getTargetNPC(), choice.getAffectionChange());
        }

        // 切換到下一段對話
        setCurrentDialogue(choice.getNextDialogueId());
    }
}

