package com.morris.mms.mms;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemQueryService {

    private final ItemRepository repo;

    public ItemQueryService(ItemRepository repo) {
        this.repo = repo;
    }

    public List<Item> search(Item probe) {
        // 最小可用：先用 in-memory filter（量小很OK，作業用最穩）
        // 若資料量大再改成 Specification
        String name = trim(probe.getName());
        String sku = trim(probe.getSku());
        String category = trim(probe.getCategory());
        String room = trim(probe.getRoom());
        String location = trim(probe.getLocation());

        return repo.findAll().stream()
                .filter(i -> name == null || (i.getName() != null && i.getName().contains(name)))
                .filter(i -> sku == null || (i.getSku() != null && i.getSku().equals(sku)))
                .filter(i -> category == null || (i.getCategory() != null && i.getCategory().equals(category)))
                .filter(i -> room == null || (i.getRoom() != null && i.getRoom().equals(room)))
                .filter(i -> location == null || (i.getLocation() != null && i.getLocation().equals(location)))
                .toList();
    }

    private String trim(String s){
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
