package main;

/**
 * 基礎角色類別
 * - 名字
 * - 好感度
 * - 狀態屬性（體力、心情、智商）
 */
public abstract class Character {
    protected String name;
    protected int affection;
    protected int stamina;
    protected int mood;
    protected int intelligence;

    public Character(String name) {
        this.name = name;
        this.affection = 50;
        this.stamina = 100;
        this.mood = 50;
        this.intelligence = 50;
    }

    public String getName() {
        return name;
    }

    public int getAffection() {
        return affection;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMood() {
        return mood;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void adjustAffection(int value) {
        affection = Math.min(100, Math.max(0, affection + value));
    }

    public void adjustStamina(int value) {
        stamina = Math.min(100, Math.max(0, stamina + value));
    }

    public void adjustMood(int value) {
        mood = Math.min(100, Math.max(0, mood + value));
    }

    public void adjustIntelligence(int value) {
        intelligence = Math.min(100, Math.max(0, intelligence + value));
    }
}