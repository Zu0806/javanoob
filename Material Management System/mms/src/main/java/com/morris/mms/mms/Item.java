package com.morris.mms.mms;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // ✅ 1. 記得匯入 LocalDate
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "名稱不能為空")
    private String name;

    private String category;
    private String sku;
    private String room;
    private String location;
    private String unit;
    private Integer quantity;

    // ✅ 2. 補上這個變數 (如果原本沒有的話)
    private LocalDate expireDate;

    // ... (其他的 Getters/Setters 省略) ...

    // ✅ 3. 一定要補上這兩個方法，Controller 才抓得到！
    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }
    // ... (原本的 getters / setters) ...

    // ✅ 4. 新增這個：計算距離過期還有幾天
    public Long getDaysToExpire() {
        if (this.expireDate == null) return null;
        return java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), this.expireDate);
    }

    // ✅ 5. 新增這個：判斷是否庫存不足 (這裡設定數量 <= 1 就當作不足，您可以自己改數字)
    public boolean isLowStock() {
        return this.quantity == null || this.quantity <= 1;
    }
    
   
    // ... 其他原本的程式碼保持不變 ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}