package main;

/**
 * Stats：集中管理三條屬性與上下限/夾擠邏輯。
 */
public class Stats {
    private int stamina;      // 體力
    private int mood;         // 心情
    private int intelligence; // 智商

    // 上下限（可依需求調整或做成可配置）
    private int minValue = 0;
    private int maxValue = 100;

    public Stats() {
        this(100, 50, 50);
    }

    public Stats(int stamina, int mood, int intelligence) {
        this.stamina = clamp(stamina);
        this.mood = clamp(mood);
        this.intelligence = clamp(intelligence);
    }

    private int clamp(int v) {
        return Math.max(minValue, Math.min(maxValue, v));
    }

    // 基本 getter
    public int getStamina() { return stamina; }
    public int getMood() { return mood; }
    public int getIntelligence() { return intelligence; }

    // 基本 setter（自動夾擠）
    public void setStamina(int v) { stamina = clamp(v); }
    public void setMood(int v) { mood = clamp(v); }
    public void setIntelligence(int v) { intelligence = clamp(v); }

    // 增減（帶夾擠）
    public void addStamina(int delta) { stamina = clamp(stamina + delta); }
    public void addMood(int delta) { mood = clamp(mood + delta); }
    public void addIntelligence(int delta) { intelligence = clamp(intelligence + delta); }

    // 上下限配置（若之後想做裝備/事件擴張上限）
    public int getMinValue() { return minValue; }
    public int getMaxValue() { return maxValue; }
    public void setRange(int minValue, int maxValue) {
        if (minValue > maxValue) throw new IllegalArgumentException("minValue > maxValue");
        this.minValue = minValue;
        this.maxValue = maxValue;
        // 重新夾擠
        this.stamina = clamp(this.stamina);
        this.mood = clamp(this.mood);
        this.intelligence = clamp(this.intelligence);
    }

    // 便捷狀態檢查
    public boolean isExhausted() { return stamina <= minValue; }
    public boolean isDepressed() { return mood <= minValue; }
    public boolean isGenius() { return intelligence >= maxValue; }
}
