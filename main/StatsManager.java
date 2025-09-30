package main;

/**
 * StatsManager：提供常見的屬性操作與規則：
 * - 花費體力 / 可否花費
 * - 依時段/事件恢復或衰減
 * - 依小考/期中結果提升 IQ 或影響心情
 * - 將選項對應的屬性變化統一入口（之後可集中做平衡）
 */
public class StatsManager {
    private final Stats stats;

    // 可調參數（平衡用）
    private int staminaCostPerAction = 5;
    private int restRecoverMorning = 10;
    private int restRecoverNoon = 6;
    private int restRecoverEvening = 8;

    private int moodDecayPerBusyBlock = 2; // 忙碌段落後心情自然下降
    private int iqGainPerCorrectQuiz = 1;  // 每題答對的 IQ 增益
    private int moodGainPerCorrectQuiz = 1;
    private int moodLossPerWrongQuiz = 1;

    public StatsManager(Stats stats) {
        this.stats = stats;
    }

    public Stats getStats() {
        return stats;
    }

    // ======== 通用操作 ========

    public boolean canSpendStamina(int cost) {
        return stats.getStamina() >= cost;
    }

    public boolean spendStamina(int cost) {
        if (!canSpendStamina(cost)) return false;
        stats.addStamina(-cost);
        return true;
    }

    /** 進行一次會消耗體力的行動（例如移動地點/事件互動） */
    public boolean doActionSpendDefaultStamina() {
        return spendStamina(staminaCostPerAction);
    }

    /** 忙碌段落後的心情自然衰減（可在每個 Story Node 結束時呼叫一次） */
    public void applyBusyMoodDecay() {
        stats.addMood(-moodDecayPerBusyBlock);
    }

    /** 根據時段的休息恢復（你可在切換 MORNING/NOON/EVENING 時呼叫） */
    public void recoverByTimeOfDay(String timeOfDay) {
        switch (timeOfDay.toUpperCase()) {
            case "MORNING":
                stats.addStamina(restRecoverMorning);
                stats.addMood(1); // 早上精神較佳
                break;
            case "NOON":
                stats.addStamina(restRecoverNoon);
                break;
            case "EVENING":
                stats.addStamina(restRecoverEvening);
                // 晚上也可稍回 mood（或不回，看平衡）
                break;
            default:
                // 未知時段不處理
                break;
        }
    }

    // ======== 題庫/考試回饋 ========

    /** 小考答對一題 */
    public void onQuizCorrect() {
        stats.addIntelligence(iqGainPerCorrectQuiz);
        stats.addMood(moodGainPerCorrectQuiz);
    }

    /** 小考答錯一題 */
    public void onQuizWrong() {
        stats.addMood(-moodLossPerWrongQuiz);
    }

    /** 一次考試結束後，可依成績做額外獎懲（score: 0~100） */
    public void onQuizFinished(int score) {
        if (score >= 90) {
            stats.addIntelligence(2);
            stats.addMood(2);
        } else if (score >= 70) {
            stats.addIntelligence(1);
        } else if (score < 50) {
            stats.addMood(-2);
        }
    }

    // ======== 選項效果統一入口（可由劇情 Choice 呼叫） ========

    public void applyChoiceEffects(int staminaDelta, int moodDelta, int iqDelta) {
        stats.addStamina(staminaDelta);
        stats.addMood(moodDelta);
        stats.addIntelligence(iqDelta);
    }

    // ======== 參數調整（平衡） ========

    public void setStaminaCostPerAction(int v) { this.staminaCostPerAction = v; }
    public void setRestRecoverMorning(int v) { this.restRecoverMorning = v; }
    public void setRestRecoverNoon(int v) { this.restRecoverNoon = v; }
    public void setRestRecoverEvening(int v) { this.restRecoverEvening = v; }
    public void setMoodDecayPerBusyBlock(int v) { this.moodDecayPerBusyBlock = v; }
    public void setIqGainPerCorrectQuiz(int v) { this.iqGainPerCorrectQuiz = v; }
    public void setMoodGainPerCorrectQuiz(int v) { this.moodGainPerCorrectQuiz = v; }
    public void setMoodLossPerWrongQuiz(int v) { this.moodLossPerWrongQuiz = v; }
}
