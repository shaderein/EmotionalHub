package tools;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Updates.set;

/** Example class for MongoDB operations using Java API.
 */
public class MongoDbExamples {
    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = "mongodb+srv://mongo-db-user:lemonade@cluster0.nu1bq.mongodb.net/"
            + "<dbname>?retryWrites=true&w=majority";

        MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionString));
        MongoDatabase database = mongoClient.getDatabase("test");
        System.out.println(database.getName());

        MongoCollection<Document> collection = mongoClient.getDatabase("xmas").getCollection("cookies");
        createDocuments(collection);
        updateDocuments(collection);
    }

    /** Updates documents in example MongoDB database with random values.
     *
     *  @param collection Document collection representation of MongoDB data.
     */
    private static void updateDocuments(MongoCollection<Document> collection) {
        Random random = new Random();
        //collection.updateMany(new Document(), set("calories", random.nextInt(1000)));

        List<Document> cookiesList = collection.find().into(new ArrayList<>());
        cookiesList.forEach(c -> {
            Object id = c.get("_id");
            Document cookie = collection.findOneAndUpdate(new Document("_id", id), set("calories", random.nextInt(1000)));
            // Returned is the one before update by default; could specify findOneAndUpdateOptions
            System.out.println(cookie.toJson());
        });
    }

    /** Creates and inserts values into the example MongoDB database.
     *
     *  @param collection Document collection representation of MongoDB data.
     */
    private static void createDocuments(MongoCollection<Document> collection) {
        Document doc = new Document("name", "chocolate chips");
        collection.insertOne(doc);

        List<Document> cookiesList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cookiesList.add(new Document("cookie_id", i).append("color", "pink"));
        }
        collection.insertMany(cookiesList);

        // collection.deleteMany(new Document("name", "chocolate chips"));
        collection.deleteMany(in("color", Arrays.asList("blue", "pink")));
    }

    /** Prints all databases from MongoDB client in JSON format.
     *
     * @param mongoClient MongoDB client to access.
     */
    private static void printDatabases(MongoClient mongoClient) {
        List<Document> dbDocuments = mongoClient.listDatabases().into(new ArrayList<>());
        dbDocuments.forEach(document -> System.out.println(document.toJson()));
    }
}
