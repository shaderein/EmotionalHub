package persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import model.*;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;

/** Data access object for manipulating
 *  instances of Posts in a MongoDB
 *  database.
 */
public class PostDaoMongo implements DaoMongo<Post> {

    // Active client connection
    private final MongoClient mongoClient;
    // Document collection representation of data
    private final MongoCollection<Document> collection;
    // Document collection with Posts reacted to by each User
    private final MongoCollection<Document> reactions;

    /** Constructs a new Post DAO using the given
     *  client connection.
     *
     *  @param mongoClient Client connection used for
     *                     data access operations.
     */
    public PostDaoMongo(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.collection = mongoClient.getDatabase("db1").getCollection("Post");
        this.reactions = mongoClient.getDatabase("db1").getCollection("Reactions");
    }

    /**
     * Constructs a new User DAO.
     * @param mongoClient Client connection
     * @param test Whether this is a test instance.
     */
    public PostDaoMongo(MongoClient mongoClient, boolean test) {
        this.mongoClient = mongoClient;
        String dbName = test ? "test" : "db1";
        this.collection = mongoClient.getDatabase(dbName).getCollection("Post");
        this.reactions = mongoClient.getDatabase(dbName).getCollection("Reactions");
    }

    public MongoClient getClient() { return mongoClient; }

    /** Adds the given Post to the database.
     *
     *  @param post The Post to be added.
     *  @return The Post's ID, which is newly
     *          generated before insertion if
     *          not previously set.
     */
    public ObjectId add(Post post) {
        ObjectId pid;

        Map<String, Object> intentions_map = new HashMap<>(post.getIntentions());
        Document intentions = new Document(intentions_map);

        Document doc = new Document("_id", pid =
                (post.getId() != null ? post.getId() : new ObjectId()))
                .append("userId", post.getUserId())
                .append("content", post.getContent())
                .append("promptIdx", post.getPromptIdx())
                .append("isAnonymous", post.isAnonymous())
                .append("intentions", intentions)
                .append("topics", post.getTopics())
                .append("mediaId", post.getMediaId())
                .append("mediaType", post.getMediaType())
                .append("analyzed", post.isAnalyzed());
        collection.insertOne(doc);
        post.setId(pid);
        return pid;
    }

    /** Finds and returns the Post in the database
     *  matching the given ID.
     *
     *  @param pid The ID of the Post to find.
     *  @return The Post matching the given ID.
     *  @throws IndexOutOfBoundsException when no matching post is found.
     */
    public Post get(ObjectId pid) throws IndexOutOfBoundsException {
        Bson filter = eq("_id", pid);
        return this.query(filter).get(0);
    }

    /** Finds and returns the Post in the database
     *  using the given filter.
     *
     *  @param filter The Bson filter.
     *  @return The Post matching the given ID.
     */
    public List<Post> query(Bson filter) {
        List<Document> elems = collection.find(filter).into(new ArrayList<>());
        List<Post> posts = new ArrayList<>();
        elems.forEach(elem -> {
            posts.add(this.createPostFromDoc(elem));
        });
        return posts;
    }

    /** Returns a list of all Posts currently in
     *  the database.
     *
     *  @return List of all Posts found in the
     *          database.
     */
    public List<Post> listAll() {
        List<Document> elems = collection.find().into(new ArrayList<>());
        List<Post> posts = new ArrayList<>();
        elems.forEach(elem -> {
            posts.add(this.createPostFromDoc(elem));
        });
        return posts;
    }

    /** Updates the given Post in the database,
     *  using the Post ID to index and updating
     *  all other fields.
     *
     *  @param post The Post to be updated.
     *  @return Whether the post was successfully
     *          updated.
     */
    public boolean update(Post post) {
        Bson filter = eq("_id", post.getId());
        collection.findOneAndUpdate(filter, combine(
                set("content", post.getContent()),
                set("isAnonymous", post.isAnonymous()),
                set("intentions", post.getIntentions()),
                set("topics", post.getTopics()),
                set("analyzed", post.isAnalyzed()),
                set("mediaId", post.getMediaId()),
                set("mediaType", post.getMediaType())
        ));
        return true;
    }

    /** Deletes the given Post in the database,
     *  using the Post ID to index.
     *
     *  @param post The Post to be deleted.
     *  @return Whether the post was successfully
     *          deleted.
     */
    public boolean delete(Post post) {
        Bson filter = eq("_id", post.getId());
        collection.findOneAndDelete(filter);
        return true;
    }

    public boolean deleteAllDocuments() {
        this.collection.deleteMany(new BasicDBObject());
        return true;
    }

    /**
     * Return a list of posts that corresponds to the given intent. A post
     * is selected if its score for the given intent is greater than or equal to 0.8
     *
     * @param intent One of "excite, chill, inspire, laugh, opinions, skip"
     * */
    public List<Post> list(String intent) {
        if ("skip".equals(intent)) {
            return listAll();
        }
        List<Document> elems = collection.find().into(new ArrayList<>());
        List<Post> posts = new ArrayList<>();
        elems.forEach(elem -> {
            Map<String, Double> intentScores = (Map<String, Double>) elem.get("intentions");
            // Skip posts that don't have a score (yet).
            // TODO: think about the threshold
            if (intentScores != null && intentScores.containsKey(intent) &&
                    intentScores.get("negative") ==0 && // filter out negative posts
                    intentScores.get(intent) >= 0.8) {
                posts.add(this.createPostFromDoc(elem));
            }
        });
        return posts;
    }

    /**
     * Deletes the post specified by the id.
     */
    public boolean delete(ObjectId id) {
        Bson filter = eq("_id", id);
        collection.findOneAndDelete(filter);
        return true;
    }

    /**
     * Saves the media file in the database.
     * @param post The post that the media belongs to.
     * @param stream The input stream of the media file.
     * @param type The media type.
     * @throws IOException
     */
    public void addMedia(Post post, InputStream stream, String type) throws IOException {
        GridFSBucket uploader = GridFSBuckets.create(
                mongoClient.getDatabase("db1"),
                "Files");
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(358400)
                .metadata(new Document("type", "presentation"));
        ObjectId id = uploader.uploadFromStream(post.getId().toString(), stream, options);
        post.setMediaId(id);
        post.setMediaType(type);
    }

    /**
     * Retrieves the media file.
     * @param mediaId The ID of the media to be retrieved.
     * @return Byte array of the media data.
     * @throws IOException when there is no data corresponding to the given ID.
     */
    public byte[] retrieveMedia(ObjectId mediaId) throws IOException {
        GridFSBucket downloader = GridFSBuckets.create(
                mongoClient.getDatabase("db1"),
                "Files");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        downloader.downloadToStream(mediaId, out);
        if (out.toString().isEmpty()) {
            System.err.println("Image output stream is empty.");
            throw new IOException();
        }
        return out.toByteArray();
    }

    public int getLikes(Post post) {
        return reactions.find(and(
                eq("postId", post.getId()),
                eq("reaction", 1)
        )).into(new ArrayList<>()).size();
    }

    private Post createPostFromDoc(Document elem) {
        Post post = new Post((ObjectId) elem.get("userId"),
                (String) elem.get("content"),
                elem.get("promptIdx") != null ? (Integer) elem.get("promptIdx") : -1,
                elem.get("isAnonymous") != null && (boolean) elem.get("isAnonymous"),
                (Map<String, Double>) elem.get("intentions"), // TODO: test it!
                (List<String>) elem.get("topics"));
        post.setId((ObjectId) elem.get("_id"));
        post.setMediaId((ObjectId) elem.get("mediaId"));
        post.setMediaType((String) elem.get("mediaType"));
        if (post.getMediaId() != null) {
            try {
                retrieveMedia(post.getMediaId());
            } catch (IOException e) {
                System.out.println("WARNING: file not loaded");
                System.out.println(e.toString());
            }
        }
        return post;
    }

}
