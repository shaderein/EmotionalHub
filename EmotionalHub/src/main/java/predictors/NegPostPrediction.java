package predictors;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NegPostPrediction implements Predictor<Double> {
    private static final String cfPredictUrl = "http://6fa2426c-bdd7-4861-80e2-c5fab88c89b9.eastus2.azurecontainer.io/score";
    private static final List<String> negEmotions = Arrays.asList("sad", "angry", "hate");

    /**
     * Send request to API to get the prediction of the post's emotion
     * Possible tags returned : {neutral, happy, sad, hate, anger}.
     * @return Map with single entry where key = "negative", value = 1 if the post is considered
     * containing either sad, hate or anger emotions, 0 otherwise.
     * Returns null if there is an exception.
     */
    public Map<String, Double> predict(String text) {
        try {
            URL vadUrl = new URL(cfPredictUrl);
            HttpURLConnection connection = (HttpURLConnection) vadUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Endpoint receives a single line of text as input
            Gson gson = new Gson();
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("text", text);
            String requestParamsJson = gson.toJson(inputMap);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestParamsJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            InputStream response = connection.getInputStream();
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\n").next();
                System.out.println(responseBody);
                // example response:
                // {"tags": ["happy"]}

                // Naive way to extract the tag since there is only one field
                String tag = responseBody.substring(responseBody.indexOf("[") + 1);
                tag = tag.substring(0, tag.indexOf("]"));
                tag = tag.substring(2, tag.length() -2);

                Double negScore = 0.0;
                if (negEmotions.contains(tag)){
                    negScore = 1.0;
                }

                Map<String, Double> negEmotion = new HashMap<>();
                negEmotion.put("negative", negScore);
                return negEmotion;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
