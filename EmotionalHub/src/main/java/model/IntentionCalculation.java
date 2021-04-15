package model;

import predictors.NegPostPrediction;
import predictors.Predictor;
import predictors.VadPrediction;

import java.util.*;

public class IntentionCalculation {
    private final Double valence;
    private final Double arousal;
    private final Double dominance;
    private final Integer promptIdx;
    private Map<String, Double> intentions;
    private final List<String> promptToIntentions;

    private static final String EXCITE = "excite";
    private static final String CHILL = "chill";
    private static final String INSPIRE = "inspire";
    private static final String LAUGH = "laugh";
    private static final String OPINIONS = "opinions";

    public IntentionCalculation(String text, Integer promptIdx) {
        // VAD score mapping
        Predictor<Double> predictor = new VadPrediction();
        Map<String, Double> predMap = predictor.predict(text);
        if (predMap == null) {
            this.valence = 0.0;
            this.arousal = 0.0;
            this.dominance = 0.0;
        } else {
            // Get the predicted valence and arousal values (in range [1, 5]) and transform to range [-2, 2]
            this.valence = predMap.get("valence") - 3;
            this.arousal = predMap.get("arousal") - 3;
            this.dominance = predMap.get("dominance") - 3;
        }
        this.promptIdx = promptIdx;
        this.promptToIntentions = Arrays.asList(
                EXCITE, LAUGH, INSPIRE, INSPIRE, INSPIRE, INSPIRE, CHILL, CHILL);

        this.calculateIntentions();

        // Negative post tagging
        Predictor<Double> neg_predictor = new NegPostPrediction();
        Map<String, Double> neg_score = neg_predictor.predict(text);
        this.intentions.put("negative", neg_score.get("negative"));
    }

    /**
     * Calculates scores for each intention.
     * Creates a map from the intention names to their scores.
     */
    // TODO: make a better algorithm to calculate the intentions.
    private void calculateIntentions() {
        Double exciteS = this.valence + this.arousal;
        Double chillS = this.valence - this.arousal;
        Double inspireS = this.valence + this.dominance;
        Double laughS = this.valence;

        // normalize the scores?
        // Double sum = exciteS + chillS + inspireS + laughS;

        this.intentions = new HashMap<String, Double>(){{
            put(EXCITE, exciteS);
            put(CHILL, chillS);
            put(INSPIRE, inspireS);
            put(LAUGH, laughS);
            put(OPINIONS, 0.0);
        }};

        // Add 1 to the score of the prompted intent
        String promptedIntent = this.promptIdx == -1 ? OPINIONS : this.promptToIntentions.get(promptIdx);
        this.intentions.put(promptedIntent, this.intentions.get(promptedIntent) + 1);
    }

    public Map<String, Double> getIntentions() {
        System.out.println();
        return this.intentions;
    }
}
