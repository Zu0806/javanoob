package com.morris.mms.mms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class ItemCreateRequest {
    @NotBlank
    private String name;

    private String sku;
    private String category;
    private String room;

    @Min(0)
    private Integer quantity = 0;

    private LocalDate expireDate;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }
}
