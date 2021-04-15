package persistence;

import com.google.gson.*;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import model.Comment;
import model.Post;
import model.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/** Data access object for manipulating
 *  instances of Users in a MongoDB
 *  database.
 */
public class UserDaoMongo implements DaoMongo<User> {

    // Active client connection
    private final MongoClient mongoClient;
    // Document collection representation of data
    private final MongoCollection<Document> collection;
    // Document collection with Posts reacted to by each User
    private final MongoCollection<Document> reactions;

    /** Constructs a new User DAO using the given
     *  client connection.
     *
     *  @param mongoClient Client connection used for
     *                     data access operations.
     */
    public UserDaoMongo(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.collection = mongoClient.getDatabase("db1").getCollection("User");
        this.reactions = mongoClient.getDatabase("db1").getCollection("Reactions");
    }

    /**
     * Constructs a new User DAO.
     * @param mongoClient Client connection
     * @param test Whether this is a test instance.
     */
    public UserDaoMongo(MongoClient mongoClient, boolean test) {
        this.mongoClient = mongoClient;
        String dbName = test ? "test" : "db1";
        this.collection = mongoClient.getDatabase(dbName).getCollection("User");
        this.reactions = mongoClient.getDatabase(dbName).getCollection("Reactions");
    }

    public MongoClient getClient() { return mongoClient; }

    /**
     * @param user The user to be added.
     * @return The id of the added user.
     * TODO: add more security
     */
    public ObjectId add(User user) {
        ObjectId uid;
        Document doc = new Document("_id",
                uid = user.getId() != null ? user.getId() : new ObjectId())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("userName", user.getUserName())
                .append("likedPosts", user.getLikedPosts());
        this.collection.insertOne(doc);
        user.setId(uid);
        return uid;
    }

    public User get(ObjectId uid) {
        Bson filter = eq("_id", uid);
        return this.query(filter).get(0);
    }

    public List<User> query(Bson filter) {
        List<Document> elems = collection.find(filter).into(new ArrayList<>());
        List<User> users = new ArrayList<>();
        elems.forEach(elem -> {
            User user = new User(
                    (String) elem.get("email"),
                    (String) elem.get("password"),
                    (String) elem.get("userName"),
                    elem.get("likedPosts") != null ? (ArrayList<ObjectId>) elem.get("likedPosts") : new ArrayList<>()
            );
            user.setId((ObjectId) elem.get("_id"));
            users.add(user);
        });
        return users;
    }

    public List<User> listAll() {
        return this.query(new BasicDBObject());
    }

    public boolean update(User user) {
        Bson filter = eq("_id", user.getId());
        collection.findOneAndUpdate(filter, combine(
                set("email", user.getEmail()),
                set("password", user.getPassword()),
                set("userName", user.getUserName()),
                set("likedPosts", user.getLikedPosts())
        ));
        return true;
    }

    // TODO: (Heidi) implement this
    public boolean delete(User user) {
        return true;
    }

    public boolean deleteAllDocuments() {
        this.collection.deleteMany(new BasicDBObject());
        return true;
    }

    /**
     * @param email The email of the user to be retrieved.
     * @return The user retrieved.
     * @throws NullPointerException if there is no user with the given email.
     */
    public User getUser(String email) throws NullPointerException {
        try {
            return query(eq("email", email)).get(0);
        }
        catch (IndexOutOfBoundsException e) {
            throw new NullPointerException();
        }
    }

    public boolean addLikedPost(User user, ObjectId postId) {
        user.addLikedPost(postId);
        Document react = new Document()
                .append("userId", user.getId())
                .append("postId", postId)
                .append("reaction", 1);
        reactions.insertOne(react);
        this.update(user);
        return true;
    }

    public boolean removeLikedPost(User user, ObjectId postId) {
        user.removeLikedPost(postId);
        reactions.findOneAndDelete(and(
                eq("userId", user.getId()),
                eq("postId", postId),
                eq("reaction", 1)
        ));
        this.update(user);
        return true;
    }

    /**
     * Get the reaction of a user on a post.
     * @param user
     * @param postId
     * @return The reaction score, 1 for like.
     */
    public int reaction(User user, ObjectId postId) {
        List<Document> reacts = reactions.find(and(
                eq("userId", user.getId()),
                eq("postId", postId),
                eq("reaction", 1)))
                .into(new ArrayList<>());
        if (reacts.size() == 0) { return 0; }
        return (Integer) reacts.get(0).get("reaction");
    }
}
