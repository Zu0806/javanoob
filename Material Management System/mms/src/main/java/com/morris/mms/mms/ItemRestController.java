package com.morris.mms.mms;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemRestController {

    private final ItemRepository itemRepo;
    private final ItemQueryService queryService;

    public ItemRestController(ItemRepository itemRepo, ItemQueryService queryService) {
        this.itemRepo = itemRepo;
        this.queryService = queryService;
    }

    // 1) 全部列表
    @GetMapping
    public List<Item> list() {
        return itemRepo.findAll();
    }

    // 2) 查詢（最簡單版本：用 name / category / location / sku 你現有 queryService）
    @GetMapping("/search")
    public List<Item> search(Item probe) {
        return queryService.search(probe);
    }

    // 3) 新增
    @PostMapping
    public Item create(@RequestBody Item item) {
        if (item.getQuantity() == null) item.setQuantity(0);
        if (item.getLocation() == null) item.setLocation("");
        if (item.getCategory() == null) item.setCategory("");
        if (item.getSku() == null) item.setSku("");
        if (item.getName() == null) item.setName("");
        return itemRepo.save(item);
    }

    // 4) 刪除
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        itemRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 5) 改數量（直接 PUT）
    @PutMapping("/{id}/quantity")
    public ResponseEntity<Item> setQuantity(@PathVariable Long id, @RequestParam int quantity) {
        if (quantity < 0) quantity = 0;
        int q = quantity;

        return itemRepo.findById(id)
                .map(it -> {
                    it.setQuantity(q);
                    return ResponseEntity.ok(itemRepo.save(it));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
