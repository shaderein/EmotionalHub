import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.UserDaoMongo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoCRUDTest {
    private static UserDaoMongo userDaoMongo;
    private static User user1;

    @BeforeClass
    public static void beforeClassTests() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = "mongodb+srv://mongo-db-user:lemonade@cluster0.nu1bq.mongodb.net/"
                + "<dbname>?retryWrites=true&w=majority";

        MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionString));
        userDaoMongo = new UserDaoMongo(mongoClient, true);

        user1 = new User("testing@jhu.edu", "testing", "tester");
    }

    @Before
    public void beforeEachTest() {
        userDaoMongo.deleteAllDocuments();
    }

    @Test
    public void testAddUser() {
        assert(userDaoMongo.add(user1) != null);
    }

    @Test
    public void testGetUser() {
        userDaoMongo.add(user1);
        User user1FromDb = userDaoMongo.getUser(user1.getEmail());
        assert(user1FromDb.getEmail().equals(user1.getEmail()));
    }

    @Test
    public void testListUser() {
        userDaoMongo.add(user1);
        System.out.println(userDaoMongo.listAll());
    }
}
