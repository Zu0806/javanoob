
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * StoryLoader 負責載入劇情資料
 * - 這裡簡化用 JSON-like 格式或 CSV，也可以換成真正的 JSON parser (例如 Gson)
 * - 載入 Dialogue 和 Choice
 */
public class StoryLoader {
    private Map<String, Dialogue> dialogueMap;

    public StoryLoader() {
        dialogueMap = new HashMap<>();
    }

    /**
     * 從一個簡單的文字檔載入對話
     * 格式示例：
     * DIALOGUE,intro,老師,歡迎來到新學校！
     * CHOICE,intro,0,點頭回應,next1,謝塵,5
     * CHOICE,intro,1,低頭不語,next2,謝塵,-2
     */
    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split(",");
        if (parts[0].equals("DIALOGUE")) {
            String id = parts[1];
            String speaker = parts[2];
            String text = parts[3];
            Dialogue dialogue = new Dialogue(speaker, text, new ArrayList<>());
            dialogueMap.put(id, dialogue);
        } else if (parts[0].equals("CHOICE")) {
            String parentId = parts[1];
            String choiceText = parts[3];
            String nextId = parts[4];
            String targetNPC = parts[5];
            int affectionChange = Integer.parseInt(parts[6]);

            Choice choice = new Choice(choiceText, nextId, targetNPC, affectionChange);
            Dialogue parent = dialogueMap.get(parentId);
            if (parent != null) {
                parent.getChoices().add(choice);
            }
        }
    }

    public Dialogue getDialogue(String id) {
        return dialogueMap.get(id);
    }

    public Map<String, Dialogue> getDialogueMap() {
        return dialogueMap;
    }
}

