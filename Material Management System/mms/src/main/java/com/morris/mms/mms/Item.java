package com.morris.mms.mms;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "items")
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String sku;
    private String category;
    private String room;
    private String unit;

    @Min(0)
    private Integer quantity = 0;

    private String location;     // 儲位
    private LocalDate expireDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ====== 你頁面會用到的計算欄位 ======
    @Transient
    public boolean isLowStock() {
        int q = (quantity == null) ? 0 : quantity;
        return q <= 2; // 低庫存門檻：先固定 2（你也可改成從設定檔讀）
    }

    @Transient
    public Long getDaysToExpire() {
        if (expireDate == null) return null;
        return ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
    }

    // ===== getters/setters =====
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
