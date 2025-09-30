
package main;

import javax.swing.*;
import java.awt.*;

/**
 * UIManager 負責遊戲的圖形介面顯示
 * - 背景顯示
 * - 人物顯示
 * - 對話框顯示
 * - 選項顯示
 * - 狀態欄（好感度、體力、心情、智商）
 */
public class UIManager extends JPanel {
    private Image backgroundImage;
    private Image characterImage;
    private String dialogueText;
    private String[] options;
    private int selectedOption;

    // 狀態欄
    private int affection;
    private int stamina;
    private int mood;
    private int intelligence;

    public UIManager() {
        this.dialogueText = "";
        this.options = new String[0];
        this.selectedOption = -1;

        this.affection = 50;
        this.stamina = 100;
        this.mood = 50;
        this.intelligence = 50;

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
    }

    public void setBackgroundImage(Image bg) {
        this.backgroundImage = bg;
        repaint();
    }

    public void setCharacterImage(Image img) {
        this.characterImage = img;
        repaint();
    }

    public void setDialogueText(String text) {
        this.dialogueText = text;
        repaint();
    }

    public void setOptions(String[] opts) {
        this.options = opts;
        this.selectedOption = -1;
        repaint();
    }

    public void updateStats(int affection, int stamina, int mood, int intelligence) {
        this.affection = affection;
        this.stamina = stamina;
        this.mood = mood;
        this.intelligence = intelligence;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製背景
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // 繪製人物
        if (characterImage != null) {
            g.drawImage(characterImage, getWidth() - 300, getHeight() - 500, 280, 480, null);
        }

        // 繪製對話框
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(20, getHeight() - 150, getWidth() - 40, 130);
        g.setColor(Color.WHITE);
        g.drawString(dialogueText, 40, getHeight() - 120);

        // 繪製選項
        if (options != null && options.length > 0) {
            for (int i = 0; i < options.length; i++) {
                g.drawString((i + 1) + ". " + options[i], 60, getHeight() - 90 + (i * 20));
            }
        }

        // 狀態欄顯示
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(20, 20, 200, 100);
        g.setColor(Color.BLACK);
        g.drawString("好感度: " + affection, 30, 40);
        g.drawString("體力: " + stamina, 30, 60);
        g.drawString("心情: " + mood, 30, 80);
        g.drawString("智商: " + intelligence, 30, 100);
    }
}
