package com.morris.mms.mms.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "名稱必填")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "SKU 必填")
    private String sku;

    @NotBlank(message = "單位必填")
    private String unit;

    @Min(value = 0, message = "數量不可小於 0")
    private Integer quantity = 0;


    private String category;

    private String location;



    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    /**
     * 是否為低庫存：目前先簡單判定 quantity < 5
     * 你之後可以改成根據每個商品自己的安全庫存值來判斷
     */
    public boolean isLowStock() {
        if (quantity == null) return false;
        return quantity < 5;   // <= 這個數字你可以自己調整
    }
     @Transient
    public Long getDaysToExpire() {
        if (expireDate == null) return null;
        return ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
    }
    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
