package main;

/**
 * 玩家角色
 * - 擁有額外的資源（例如題庫分數）
 */
public class Player extends Character {
    private int knowledgePoints;

    public Player(String name) {
        super(name);
        this.knowledgePoints = 0;
    }

    public int getKnowledgePoints() {
        return knowledgePoints;
    }

    public void addKnowledgePoints(int points) {
        knowledgePoints += points;
    }
}
