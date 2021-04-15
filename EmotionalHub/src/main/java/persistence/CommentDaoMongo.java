package persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import model.Comment;
import model.Post;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class CommentDaoMongo implements DaoMongo<Comment> {
    // Active client connection
    private final MongoClient mongoClient;
    // Document collection representation of data
    private final MongoCollection<Document> collection;

    public CommentDaoMongo(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.collection = mongoClient.getDatabase("db1").getCollection("Comment");
    }

    public CommentDaoMongo(MongoClient mongoClient, boolean test) {
        this.mongoClient = mongoClient;
        String dbName = test ? "test" : "db1";
        this.collection = mongoClient.getDatabase(dbName).getCollection("Comment");
    }

    public MongoClient getClient() { return mongoClient; }

    public ObjectId add(Comment comment) {
        ObjectId id;
        Document doc = new Document("_id", id =
                (comment.getId() != null ? comment.getId() : new ObjectId()))
                .append("postId", comment.getPostId())
                .append("userId", comment.getUserId())
                .append("anonymous", comment.isAnonymous())
                .append("content", comment.getContent())
                .append("parentId", comment.getParentId());
        collection.insertOne(doc);
        comment.setId(id);
        return id;
    }

    public Comment get(ObjectId cid) {
        Bson filter = eq("_id", cid);
        return this.query(filter).get(0);
    }

    public List<Comment> query(Bson filter) {
        List<Document> elems = collection.find(filter).into(new ArrayList<>());
        List<Comment> comments = new ArrayList<>();
        elems.forEach(elem -> {
            Comment comment = new Comment(
                    (ObjectId) elem.get("postId"),
                    (ObjectId) elem.get("userId"),
                    (boolean) elem.get("anonymous"),
                    (String) elem.get("content"),
                    (ObjectId) elem.get("parentId"));
            comment.setId((ObjectId) elem.get("_id"));
            comments.add(comment);
        });
        return comments;
    }

    public List<Comment> listAll() {
        List<Document> elems = collection.find().into(new ArrayList<>());
        List<Comment> comments = new ArrayList<>();
        elems.forEach(elem -> {
            Comment comment = new Comment(
                    (ObjectId) elem.get("postId"),
                    (ObjectId) elem.get("userId"),
                    (boolean) elem.get("anonymous"),
                    (String) elem.get("content"),
                    (ObjectId) elem.get("parentId"));
            comment.setId((ObjectId) elem.get("_id"));
            comments.add(comment);
        });
        return comments;
    }

    public boolean update(Comment comment) {
        Bson filter = eq("_id", comment.getId());
        collection.findOneAndUpdate(filter, combine(
                set("content", comment.getContent()),
                set("anonymous", comment.isAnonymous())
        ));
        return true;
    }

    public boolean delete(Comment comment) {
        Bson filter = eq("_id", comment.getId());
        collection.findOneAndDelete(filter);
        return true;
    }

    public boolean deleteAllDocuments() {
        this.collection.deleteMany(new BasicDBObject());
        return true;
    }

    public List<Comment> getCommentsByPost(ObjectId postId) {
        return query(eq("postId", postId));
    }

}
