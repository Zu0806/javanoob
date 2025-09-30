
package main;

import javax.swing.SwingUtilities;

/**
 * Main：程式入口，專責呼叫 Game。
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }
}
