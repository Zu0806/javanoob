
package main;

import java.util.List;

/**
 * Dialogue 代表一段對話
 * - 說話者
 * - 對話文字
 * - 可選擇的選項
 */
public class Dialogue {
    private String speaker;
    private String text;
    private List<Choice> choices;

    public Dialogue(String speaker, String text, List<Choice> choices) {
        this.speaker = speaker;
        this.text = text;
        this.choices = choices;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
