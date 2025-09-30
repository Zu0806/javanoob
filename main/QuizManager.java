package main;

import java.util.Objects;

/**
 * QuizManager：小考/考試流程控制
 * - start(bank)：開始指定題庫
 * - getCurrentQuestion()：取得目前題目
 * - answer(optionIdx)：回傳是否答對並前進
 * - isFinished() / getScore() / getScorePercent()：結算
 * - finish()：套用題庫 finishEffects，並可依分數再做 StatsManager 額外調整
 *
 * 可直接在劇情節點呼叫：
 *   quizManager.start(bank);
 *   while(!quizManager.isFinished()) { 顯示 quizManager.getCurrentQuestion() ... }
 *   quizManager.finish();
 */
public class QuizManager {
    private final StatsManager stats;
    private final AffectionSystem affection;

    private QuestionBank currentBank;
    private int index;
    private int correctCount;
    private boolean finished;

    public QuizManager(StatsManager stats, AffectionSystem affection) {
        this.stats = Objects.requireNonNull(stats);
        this.affection = Objects.requireNonNull(affection);
    }

    /** 開始新的題庫 */
    public void start(QuestionBank bank) {
        if (bank == null || bank.isEmpty()) {
            throw new IllegalArgumentException("QuestionBank is null or empty");
        }
        this.currentBank = bank;
        this.index = 0;
        this.correctCount = 0;
        this.finished = false;
    }

    public boolean isRunning() { return currentBank != null && !finished; }
    public boolean isFinished() { return finished; }

    public Question getCurrentQuestion() {
        if (!isRunning()) return null;
        if (index < 0 || index >= currentBank.size()) return null;
        return currentBank.getQuestions().get(index);
    }

    /**
     * 提交答案並前進到下一題
     * @param chosenIndex 使用者選擇的選項索引 (0-based)
     * @return 是否答對
     */
    public boolean answer(int chosenIndex) {
        Question q = getCurrentQuestion();
        if (q == null) return false;

        boolean correct = q.isCorrect(chosenIndex);
        // 題目層效果
        q.applyResult(correct, stats, affection);

        // StatsManager 內建考題回饋（可再加強）
        if (correct) {
            stats.onQuizCorrect();
            correctCount++;
        } else {
            stats.onQuizWrong();
        }

        // 下一題
        index++;
        if (index >= currentBank.size()) {
            finished = true;
        }
        return correct;
    }

    /** 分數（整數題數） */
    public int getScore() { return correctCount; }

    /** 百分比（四捨五入） */
    public int getScorePercent() {
        if (currentBank == null || currentBank.size() == 0) return 0;
        double p = (correctCount * 100.0) / currentBank.size();
        return (int) Math.round(p);
    }

    /**
     * 交卷：套用題庫 finishEffects，並依分數做 StatsManager 額外調整
     * 可在劇情引擎回到故事節點前呼叫。
     */
    public void finish() {
        if (currentBank == null) return;

        // 題庫統一 finish effects（例如：文錦+1）
        currentBank.applyFinishEffects(stats, affection);

        // 依成績做額外調整（你也可把這段移到外層）
        stats.onQuizFinished(getScorePercent());

        // 清理狀態（若想保留結果可不清）
        currentBank = null;
        finished = true;
    }
}
