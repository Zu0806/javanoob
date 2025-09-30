package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QuestionBank：題庫
 * - id/title：識別與標題
 * - timeLimitSec：限時（留欄位，可由 UI/引擎實作倒數）
 * - questions：題目清單
 * - finishEffects：交卷後的統一效果（可依得分另外在 QuizManager 內再判斷）
 */
public class QuestionBank {
    private final String id;
    private final String title;
    private final int timeLimitSec;
    private final List<Question> questions = new ArrayList<>();
    private final List<Question.QuizEffect> finishEffects = new ArrayList<>();

    public QuestionBank(String id, String title, int timeLimitSec) {
        this.id = id;
        this.title = title;
        this.timeLimitSec = timeLimitSec;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getTimeLimitSec() { return timeLimitSec; }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }
    public QuestionBank addQuestion(Question q) {
        if (q != null) questions.add(q);
        return this;
    }

    public List<Question.QuizEffect> getFinishEffects() {
        return Collections.unmodifiableList(finishEffects);
    }
    public QuestionBank addFinishEffect(Question.QuizEffect effect) {
        if (effect != null) finishEffects.add(effect);
        return this;
    }

    /** 交卷後套用 finish effects（不帶分數邏輯，分數可在 QuizManager 再判） */
    public void applyFinishEffects(StatsManager stats, AffectionSystem affection) {
        for (Question.QuizEffect e : finishEffects) e.apply(stats, affection);
    }

    public int size() { return questions.size(); }
    public boolean isEmpty() { return questions.isEmpty(); }
}
