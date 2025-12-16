package com.morris.mms.mms;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.CRC32;

import org.springframework.stereotype.Service;

@Service
public class AiSuggestService {

    private final ItemRepository itemRepo;

    public AiSuggestService(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    public AiSuggestResponse suggest(String rawName) {
        AiSuggestResponse r = new AiSuggestResponse();

        String name = (rawName == null) ? "" : rawName.trim();
        if (name.isEmpty()) {
            r.location = "UNASSIGNED";
            r.sku = "";
            r.category = "";
            r.confidence = 0;
            r.reason = "請先輸入名稱";
            return r;
        }

        // ===== A) 先從 DB 自學：最常用儲位 =====
        String learnedLocation = null;
        var locRows = itemRepo.topLocationsByName(name);
        if (locRows != null && !locRows.isEmpty()) {
            Object[] row = locRows.get(0);
            learnedLocation = (row[0] == null) ? null : row[0].toString();
        }

        // ===== A2) 先從 DB 自學：最常用類別 =====
        String learnedCategory = null;
        var catRows = itemRepo.topCategoriesByName(name);
        if (catRows != null && !catRows.isEmpty()) {
            Object[] row = catRows.get(0);
            learnedCategory = (row[0] == null) ? null : row[0].toString();
        }

        // ===== B) SKU：同名沿用（lookup） =====
        String learnedSku = null;
        Item lastWithSku = itemRepo.findTop1ByNameIgnoreCaseAndSkuIsNotNullOrderByIdDesc(name);
        if (lastWithSku != null && lastWithSku.getSku() != null && !lastWithSku.getSku().isBlank()) {
            learnedSku = lastWithSku.getSku().trim();
        }

        // ===== C) 規則 fallback（不用花錢也能 demo）=====
        String ruleLocation = guessLocationByRule(name);
        String ruleCategory = guessCategoryByRule(name);

        // location：優先 DB 自學，其次規則
        if (learnedLocation != null && !learnedLocation.isBlank()) {
            r.location = learnedLocation;
            r.confidence = 0.85;
            r.reason = "依歷史資料：同名物品常放在「" + learnedLocation + "」";
        } else if (ruleLocation != null) {
            r.location = ruleLocation;
            r.confidence = 0.55;
            r.reason = "依規則推測儲位：" + ruleLocation;
        } else {
            r.location = "UNASSIGNED";
            r.confidence = 0.2;
            r.reason = "沒有歷史資料，規則也無法判斷";
        }

        // category：優先 DB 自學，其次規則
        if (learnedCategory != null && !learnedCategory.isBlank()) {
            r.category = learnedCategory;
        } else if (ruleCategory != null) {
            r.category = ruleCategory;
        } else {
            r.category = "";
        }

        // sku：優先沿用，其次固定 hash 生成（同名永遠同 SKU）
        if (learnedSku != null) {
            r.sku = learnedSku;
        } else {
            r.sku = stableSkuFromName(name);
        }

        return r;
    }

    private String stableSkuFromName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT).trim();
        CRC32 crc = new CRC32();
        crc.update(normalized.getBytes(StandardCharsets.UTF_8));
        long v = crc.getValue();
        String base36 = Long.toString(v, 36).toUpperCase(Locale.ROOT);
        if (base36.length() > 8) base36 = base36.substring(0, 8);
        return "SKU-" + base36;
    }


    private String guessLocationByRule(String name) {
        String n = name.toLowerCase(Locale.ROOT);

        if (containsAny(n, "牛奶", "乳", "優格", "起司", "蛋", "肉", "魚", "冷凍", "冰")) return "fridge";
        if (containsAny(n, "米", "麵", "泡麵", "罐頭", "餅乾", "零食", "糖", "鹽", "醬油")) return "pantry";
        if (containsAny(n, "洗衣", "清潔", "漂白", "衛生紙", "垃圾袋")) return "bathroom";
        if (containsAny(n, "螺絲", "螺帽", "工具", "電池", "膠帶", "五金")) return "storage";
        if (containsAny(n, "杯", "碗", "盤", "鍋", "保鮮盒")) return "kitchen";

        return null;
    }

    private String guessCategoryByRule(String name) {
        String n = name.toLowerCase(Locale.ROOT);

        if (containsAny(n, "牛奶", "乳", "優格", "起司", "蛋", "肉", "魚")) return "食材";
        if (containsAny(n, "米", "麵", "泡麵", "罐頭", "餅乾", "零食")) return "乾糧";
        if (containsAny(n, "洗衣", "清潔", "漂白", "衛生紙", "垃圾袋")) return "清潔用品";
        if (containsAny(n, "螺絲", "螺帽", "工具", "電池", "膠帶", "五金")) return "五金工具";
        if (containsAny(n, "杯", "碗", "盤", "鍋", "保鮮盒")) return "廚具";

        return null;
    }

    private boolean containsAny(String s, String... keys) {
        for (String k : keys) if (s.contains(k.toLowerCase(Locale.ROOT))) return true;
        return false;
    }
}
