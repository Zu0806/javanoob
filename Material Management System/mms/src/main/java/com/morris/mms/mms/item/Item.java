package com.morris.mms.mms.item;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 物品編號（你要不要用都可以，先保留）
    @NotBlank(message = "SKU 不能空白")
    private String sku;

    @NotBlank(message = "名稱不能空白")
    private String name;

    @Min(value = 0, message = "數量不能是負數")
    private int quantity;

    // 單位，例如：個、瓶、包
    private String unit;

    // 放在哪裡（例如：廚房櫃子、冰箱冷凍）
    private String location;

    // 到期日
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expire_date")
    private LocalDate expireDate;

    // 類別：用簡單的字串就好（例如：食物、日用品）
    private String category;

    // ======= Getter / Setter，注意 category 的型別是 String =======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getCategory() {          // ★ 這個很重要
        return category;
    }

    public void setCategory(String category) {   // ★ 這個也要 String
        this.category = category;
    }
}
