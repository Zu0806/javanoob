package com.morris.mms.mms;

public class ItemSearchRequest {
    private String q;        // 名稱關鍵字
    private String sku;
    private String category;
    private String room;

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}
