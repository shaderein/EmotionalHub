package predictors;

import com.google.gson.Gson;
import sun.font.TrueTypeFont;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CfPredictSample {
    // REST endpopint provided by Microsoft Azure
    private static final String cfPredictUrl = "http://6fa2426c-bdd7-4861-80e2-c5fab88c89b9.eastus2.azurecontainer.io/score";

    public static void getPreiction(String text){
        try{
            URL vadUrl = new URL(cfPredictUrl);
            HttpURLConnection connection = (HttpURLConnection) vadUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Endpoint receives list of text as input
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
                // example response:
                // {"tags": ["happy"]}

                // Naive way to extract the tag since there is only one field
                String tag = responseBody.substring(responseBody.indexOf("[") + 1);
                tag = tag.substring(0, tag.indexOf("]"));
                tag = tag.substring(2, tag.length() - 2);

                System.out.println("Prediction:\t" + tag);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // Sample sentence
        String[] samples = {"Think about something good. Tomorrow will be better!",
                "To be or not to be. That's a question.",
                "You should persue your dream.",
                "Ugh! What's this disgusting object?!",
                "How can you think in this way. You disappointed me so much!",
                "Gosh, you are SO annoying! I CAN'T stand you! Just GET AWAY from me!",
        };

        System.out.println("======== Sample sentences ========\n");
        for(String s : samples){
            System.out.println("Input sentence:\t" + s);
            getPreiction(s);
        }

        Scanner reader = new Scanner(System.in);
        System.out.println("\n======== Test your own sentences ========\n");
        while (true) {
            // Endpoint receives list of text as input
            // Reading from System.in
            System.out.println("Enter a sentence to predict: ");
            String text = reader.nextLine(); // Scans the next token of the input as an int.
            getPreiction(text);
        }
    }

}
