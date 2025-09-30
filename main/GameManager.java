package main;

import java.util.Scanner;

public class GameManager {
    private boolean isRunning;
    private Scanner scanner;

    public GameManager() {
        this.isRunning = true;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("遊戲初始化完成！");
        mainMenu();
    }

    private void mainMenu() {
        while (isRunning) {
            System.out.println("\n=== 主選單 ===");
            System.out.println("1. 開始新遊戲");
            System.out.println("2. 載入遊戲 (尚未實作)");
            System.out.println("3. 離開遊戲");
            System.out.print("請選擇：");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    System.out.println("載入遊戲功能尚未開放。");
                    break;
                case 3:
                    exitGame();
                    break;
                default:
                    System.out.println("無效選項，請重新輸入！");
            }
        }
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // 回傳錯誤選項
        }
    }

    private void startNewGame() {
        System.out.println("\n--- 新遊戲開始 ---");
        System.out.println("你是一位大學生，剛進入校園生活...");
        // TODO: 未來在這裡串接角色建立、背景圖、音樂、對話系統
    }

    private void exitGame() {
        System.out.println("感謝遊玩，再見！");
        isRunning = false;
        scanner.close();
    }
}

