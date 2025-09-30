
package main;

import java.util.HashMap;
import java.util.Map;

/**
 * AffectionSystem 負責管理玩家與 NPC 的好感度
 * - 提供獲取與修改好感度的方法
 * - 設定不同好感度等級的狀態（普通、親密、戀愛等）
 */
public class AffectionSystem {
    private Map<String, Integer> affectionMap;

    public AffectionSystem() {
        affectionMap = new HashMap<>();
    }

    public void setAffection(String npcName, int value) {
        affectionMap.put(npcName, Math.min(100, Math.max(0, value)));
    }

    public int getAffection(String npcName) {
        return affectionMap.getOrDefault(npcName, 50);
    }

    public void addAffection(String npcName, int value) {
        int current = affectionMap.getOrDefault(npcName, 50);
        int newValue = Math.min(100, Math.max(0, current + value));
        affectionMap.put(npcName, newValue);
    }

    public String getAffectionLevel(String npcName) {
        int value = getAffection(npcName);
        if (value >= 80) {
            return "戀愛階段";
        } else if (value >= 60) {
            return "親密朋友";
        } else if (value >= 40) {
            return "普通朋友";
        } else {
            return "冷淡關係";
        }
    }

    public void decreaseAll(int value) {
        for (String npc : affectionMap.keySet()) {
            addAffection(npc, -value);
        }
    }
}

