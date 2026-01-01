package com.morris.mms.mms;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/items")

@CrossOrigin(origins = "http://localhost:5173")
public class ItemApiController {

    private final ItemRepository itemRepo;
    private final ItemQueryService queryService;

    public ItemApiController(ItemRepository itemRepo, ItemQueryService queryService) {
        this.itemRepo = itemRepo;
        this.queryService = queryService;
    }

    // 讀全部 / 查詢（用 query params）
    @GetMapping
    public List<Item> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String room,
            @RequestParam(required = false) String location
    ) {
        // 沒帶任何條件 => 全部
        if (isBlank(name) && isBlank(sku) && isBlank(category) && isBlank(room) && isBlank(location)) {
            return itemRepo.findAll();
        }

        Item probe = new Item();
        probe.setName(name);
        probe.setSku(sku);
        probe.setCategory(category);
        probe.setRoom(room);
        probe.setLocation(location);

        return queryService.search(probe);
    }

    // 新增（沿用你原本“同名+同儲位+同日期 合併”的邏輯） :contentReference[oaicite:5]{index=5}
    @PostMapping
    public Item create(@RequestBody Map<String, Object> body,
                       @RequestParam(defaultValue = "false") boolean forceNew) {

        Item item = new Item();
        item.setName(str(body.get("name")));
        item.setSku(str(body.get("sku")));
        item.setCategory(str(body.get("category")));
        item.setRoom(str(body.get("room")));
        item.setLocation(str(body.get("location")));
        item.setUnit(str(body.get("unit")));
        item.setQuantity(intOrZero(body.get("quantity")));

        String expireDateStr = str(body.get("expireDate"));
        if (!isBlank(expireDateStr)) {
            try { item.setExpireDate(LocalDate.parse(expireDateStr)); } catch (Exception ignored) {}
        }

        if (isBlank(item.getLocation())) item.setLocation("");

        List<Item> candidates = itemRepo.findByNameAndLocation(item.getName(), item.getLocation());
        Item sameBatchItem = null;

        for (Item dbItem : candidates) {
            boolean dateMatch = (dbItem.getExpireDate() == null && item.getExpireDate() == null) ||
                    (dbItem.getExpireDate() != null && dbItem.getExpireDate().equals(item.getExpireDate()));
            if (dateMatch) { sameBatchItem = dbItem; break; }
        }

        if (!forceNew && sameBatchItem != null) {
            int q = (sameBatchItem.getQuantity() == null ? 0 : sameBatchItem.getQuantity());
            sameBatchItem.setQuantity(q + item.getQuantity());
            return itemRepo.save(sameBatchItem);
        }

        return itemRepo.save(item);
    }

    // +1 / -1
    @PostMapping("/{id}/adjust")
    public Item adjust(@PathVariable Long id, @RequestParam int delta) {
        Item it = itemRepo.findById(id).orElseThrow();
        int q = (it.getQuantity() == null) ? 0 : it.getQuantity();
        q = Math.max(0, q + delta);
        it.setQuantity(q);
        return itemRepo.save(it);
    }

    // 直接設 quantity
    @PutMapping("/{id}/quantity")
    public Item setQuantity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Item it = itemRepo.findById(id).orElseThrow();
        int q = intOrZero(body.get("quantity"));
        it.setQuantity(Math.max(0, q));
        return itemRepo.save(it);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemRepo.deleteById(id);
    }

    // 給前端下拉選單用
    @GetMapping("/meta")
    public Map<String, Object> meta() {
        return Map.of(
                "categories", itemRepo.distinctCategories(),
                "locations", itemRepo.distinctLocations()
        );
    }

    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static String str(Object o){ return o == null ? null : String.valueOf(o); }
    private static int intOrZero(Object o){
        if (o == null) return 0;
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; }
    }
}
