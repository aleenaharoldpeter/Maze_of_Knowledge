package src.main.quiz;

public class WrongAnswerRecord {
    // The text of the question.
    private String question;
    // The answer selected by the user.
    private String selectedAnswer;
    // The correct answer for the question.
    private String correctAnswer;
    // A hint provided for the question.
    private String hint;
    // Keywords associated with the question (useful for categorization or search).
    private String keywords;
    // An explanation for the correct answer.
    private String explanation;
    
    /**
     * Constructs a WrongAnswerRecord with all the details about the wrong answer.
     *
     * @param question       the text of the question.
     * @param selectedAnswer the answer selected by the user.
     * @param correctAnswer  the correct answer.
     * @param hint           a hint related to the question.
     * @param keywords       keywords associated with the question.
     * @param explanation  an explanation for the correct answer.
     */
    public WrongAnswerRecord(String question, String selectedAnswer, String correctAnswer, String hint, String keywords, String explanation) {
        this.question = question;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.hint = hint;
        this.keywords = keywords;
        this.explanation = explanation;
    }
    
    /**
     * Returns the question text.
     *
     * @return the question.
     */
    public String getQuestion() {
        return question;
    }
    
    /**
     * Returns the answer that was selected.
     *
     * @return the selected answer.
     */
    public String getSelectedAnswer() {
        return selectedAnswer;
    }
    
    /**
     * Returns the correct answer.
     *
     * @return the correct answer.
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Returns the hint associated with the question.
     *
     * @return the hint.
     */
    public String getHint() {
        return hint;
    }
    
    /**
     * Returns the keywords associated with the question.
     *
     * @return the keywords.
     */
    public String getKeywords() {
        return keywords;
    }
    
    /**
     * Returns the explanation for the correct answer.
     *
     * @return the explanation.
     */
    public String getExplanation() {
        return explanation;
    }
}
