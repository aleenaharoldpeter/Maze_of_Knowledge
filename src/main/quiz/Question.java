package src.main.quiz;

public class Question {
    // The full text of the question.
    private String questionText;
    // Array of answer options (empty for code challenges).
    private String[] options;
    // The index of the correct answer in the options array (-1 for code challenges).
    private int correctIndex;
    // Hint to help the user answer the question.
    private String hint; // New field for the hint
    // New fields:
    // Keywords associated with the question (useful for searching or categorization).
    private String keywords;
    // Explanation for the answer or additional information.
    private String explanation;
    // The following field was for code challenges but is commented out:
    // private boolean isCodeChallenge; // false for MCQ; true for coding challenge

    /**
     * Constructs a multiple-choice Question with a hint, keywords, and explanation.
     *
     * @param questionText  the text of the question.
     * @param options       the available answer options.
     * @param correctIndex  the index of the correct answer.
     * @param hint          a hint for answering the question.
     * @param keywords      keywords related to the question.
     * @param explanation   an explanation for the answer.
     */
    public Question(String questionText, String[] options, int correctIndex, String hint, String keywords, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.hint = hint;
        // New fields:
        this.keywords = keywords;
        this.explanation = explanation;
        // this.isCodeChallenge = false;
    }
    
    /**
     * Constructs a Code Challenge question.
     * For code challenges, there are no options, a correct index of -1, and empty hint, keywords, and explanation.
     *
     * @param questionText the text of the coding challenge.
     */
    public Question(String questionText) {
        this.questionText = questionText;
        this.options = new String[0]; // No options for a code challenge.
        this.correctIndex = -1;
        this.hint = "";
        this.keywords = "";
        this.explanation = "";
        // this.isCodeChallenge = true;
    }
    
    // Getter for the full question text.
    public String getQuestionText() {
        return questionText;
    }
    
    // Getter for the array of answer options.
    public String[] getOptions() {
        return options;
    }
    
    // Getter for the index of the correct answer.
    public int getCorrectIndex() {
        return correctIndex;
    }
    
    // Getter for the hint associated with the question.
    public String getHint() {
        return hint;
    }
    
    // Getter for the keywords related to the question.
    public String getKeywords() {
        return keywords;
    }
    
    // Getter for the explanation of the question.
    public String getExplanation() {
        return explanation;
    }
    
    // Uncomment the following if you wish to include a code challenge flag.
    // public boolean isCodeChallenge() {
    //     return isCodeChallenge;
    // }
}
