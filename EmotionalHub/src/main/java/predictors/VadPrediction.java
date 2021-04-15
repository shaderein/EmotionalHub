package predictors;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class VadPrediction implements Predictor<Double> {
    private static final String vadPredictUrl = "https://vad-predict.herokuapp.com/vad";

    /**
     * Send request to API to get the valence, arousal, and dominance scores of the given text.
     * @return Map whose keys consist of "valence," "arousal," and "dominance," and values are the scores.
     * Returns null if there is an exception.
     */
    public Map<String, Double> predict(String text) {
        try {
            URL vadUrl = new URL(vadPredictUrl);
            HttpURLConnection connection = (HttpURLConnection) vadUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            Gson gsonInput = new Gson();
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("input", text);
            String requestParamsJson = gsonInput.toJson(inputMap);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestParamsJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            InputStream response = connection.getInputStream();
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                System.out.println(responseBody);
                // example response:
                // {"results":{"arousal": 3.1,"dominance": 3.0,"valence": 3.6}}

                Gson gson = new Gson();
                Map<String, Map<String, Double>> resultsMap = new HashMap<>();
                resultsMap = (Map<String, Map<String, Double>>) gson.fromJson(responseBody, resultsMap.getClass());
                return resultsMap.get("results");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
