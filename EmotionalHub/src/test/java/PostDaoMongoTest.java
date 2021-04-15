import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import model.*;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.PostDaoMongo;
import persistence.UserDaoMongo;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDaoMongoTest {
    private static PostDaoMongo postDaoMongo;
    private static User user1;
    private static Post post1;
    private static Post post2;

    @BeforeClass
    public static void beforeClassTests() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = "mongodb+srv://mongo-db-user:lemonade@cluster0.nu1bq.mongodb.net/"
                + "<dbname>?retryWrites=true&w=majority";

        MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionString));

        UserDaoMongo userDaoMongo = new UserDaoMongo(mongoClient, true);

        user1 = new User("testing@jhu.edu", "testing", "tester");
        userDaoMongo.add(user1);
        // Get user so that it has the object id.
        user1 = userDaoMongo.getUser("testing@jhu.edu");

        postDaoMongo = new PostDaoMongo(mongoClient, true);
    }

    @Before
    public void beforeEachTest() {
        postDaoMongo.deleteAllDocuments();

        List<String> topics1 = Arrays.asList("topic1", "topic2");
        Map<String, Double> intentions1 = new HashMap<String, Double>() {{
            put("intention1", 0.5);
            put("intention2", 0.8);
        }};
        post1 = new Post(user1.getId(), "This is post1", intentions1, topics1);
        post2 = new Post(user1.getId(), "This is post2", intentions1, topics1);
    }

    // TODO: refine test cases

    @Test
    public void testAdd() {
        postDaoMongo.add(post1);
        postDaoMongo.add(post2);
    }

    @Test
    public void testList() {
        postDaoMongo.add(post1);
        postDaoMongo.add(post2);

        List<Post> posts = postDaoMongo.listAll();
        for (Post post: posts) {
            System.out.println(post);
            assert(0.5 == post.getIntentions().get("intention1"));
            assert(0.8 == post.getIntentions().get("intention2"));
        }
    }

    @Test
    public void testUpdate() {
        ObjectId pid1 = postDaoMongo.add(post1);
        ObjectId pid2 = postDaoMongo.add(post2);

        post1 = postDaoMongo.get(pid1);

        post1.addTopics(Arrays.asList("topic3", "topic4"));
        postDaoMongo.update(post1);
    }
}
