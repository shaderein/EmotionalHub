package model;

import org.python.google.common.primitives.Doubles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prompt {
    private static final String topPrompt = "Make a post about anything.";
    private final List<String> prompts = new ArrayList<>();
    private final List<Double> promptProbs;  // Cumulative list of the probabilities of the prompts.

    public Prompt() {
        prompts.add("Share something fun or exciting!");
        prompts.add("Tell a joke or post a meme.");
        prompts.add("Share your inspiring thoughts here!");
        prompts.add("What’s your favorite line?");
        prompts.add("You could encourage someone by posting here.");
        prompts.add("Tell a story.");
        prompts.add("What's the best way for you to get relaxed?");
        prompts.add("What's your hobby?");
        promptProbs = Doubles.asList(0.2, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1);
    }

    public String getTopPrompt() {
        return topPrompt;
    }

    public List<String> getPrompts() {
        return prompts;
    }

    // Return a random index from 0 to prompts.size()-1, inclusive.
    public int getRandomPromptIdx() {
        Random rand = new Random();
        double prob = rand.nextDouble();  // random double in [0, 1)
        for (int i = 0; i < promptProbs.size(); i++) {
            if (prob <= promptProbs.get(i)) {
                return i;
            }
        }
        return -1;  // This will not happen.
    }

    // Return a random integer from 0 to 4, inclusive.
    public int getRandomPos() {
        Random rand = new Random();
        return rand.nextInt(5);
    }
}
