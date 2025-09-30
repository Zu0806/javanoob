package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Question：單一題目
 * - prompt：題幹
 * - options：選項
 * - answerIndex：正解索引（0-based）
 * - onCorrect/onWrong：作答後的效果（例如 IQ+1、Mood-1、某角色好感+1）
 *
 * 搭配 QuizEffect (functional interface) 讓效果可直接用 lambda：
 *   (stats, affection) -> stats.onQuizCorrect() / affection.addAffection("wenjin", 1)
 */
public class Question {
    private final String id;
    private final String prompt;
    private final List<String> options;
    private final int answerIndex;

    // 效果
    private final List<QuizEffect> onCorrect = new ArrayList<>();
    private final List<QuizEffect> onWrong   = new ArrayList<>();

    public Question(String id, String prompt, List<String> options, int answerIndex) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("options cannot be empty");
        }
        if (answerIndex < 0 || answerIndex >= options.size()) {
            throw new IllegalArgumentException("answerIndex out of range");
        }
        this.id = id;
        this.prompt = prompt;
        this.options = new ArrayList<>(options);
        this.answerIndex = answerIndex;
    }

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return Collections.unmodifiableList(options); }
    public int getAnswerIndex() { return answerIndex; }

    public List<QuizEffect> getOnCorrect() { return onCorrect; }
    public List<QuizEffect> getOnWrong() { return onWrong; }

    public Question addOnCorrect(QuizEffect effect) { if (effect!=null) onCorrect.add(effect); return this; }
    public Question addOnWrong(QuizEffect effect)   { if (effect!=null) onWrong.add(effect);   return this; }

    /** 作答檢查 */
    public boolean isCorrect(int chosenIndex) {
        return chosenIndex == answerIndex;
    }

    /** 套用對應效果 */
    public void applyResult(boolean correct, StatsManager stats, AffectionSystem affection) {
        List<QuizEffect> list = correct ? onCorrect : onWrong;
        for (QuizEffect e : list) e.apply(stats, affection);
    }

    /** 小考效果介面（functional） */
    @FunctionalInterface
    public interface QuizEffect {
        void apply(StatsManager stats, AffectionSystem affection);
    }
}
