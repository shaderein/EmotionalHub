package model;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** Represents a Post and its contents made by a User. */
public class Post implements Data {

    // Post ID
    private ObjectId _id;
    // User ID of posting user
    private ObjectId userId;
    // Textual content
    private String content;
    private boolean isAnonymous = false;
    private String mediaType;
    private ObjectId mediaId;

    private int promptIdx;

    // Post intentions returned by backend analysis
    // Invisible to the user
    // TODO: maybe using integers as identifiers instead
    private Map<String, Double> intentions;
    private int analyzed;  // Whether the intention scores are calculated
    // Post topics assigned by users. Default empty
    // Visible to the users
    private List<String> topics;

    /** Reconstructs a Post from Database entry.
     *
     * @param userId ObjectId associated to the posting User.
     * @param content Text content of post.
     * @param topics A list of topics associated with the post.
     * @param intentions A map from intentions to strength assigned by doing sentiment/
     *                   intention analysis.
     */
    public Post(ObjectId userId, String content, int promptIdx, boolean isAnonymous, Map<String, Double> intentions, List<String> topics) {
        this.userId = userId;
        this.content = content;
        this.promptIdx = promptIdx;
        this.isAnonymous = isAnonymous;
        this.mediaType = "";
        this.mediaId = null;
        this.intentions = intentions;
        this.analyzed = 0;
        this.topics = topics;
    }

    public Post(ObjectId userId, String content, Map<String, Double> intentions, List<String> topics) {
        this(userId, content, -1, false, intentions, topics);
    }

    /** Constructs a new Post from a user with the given User ID and the given content.
     * Compute the associated intentions and initialize a set of topics (TBD).
     *
     * @param userId ObjectId associated to the posting User.
     * @param content Text content of post.
     */
    public Post(ObjectId userId, String content, int promptIdx, boolean isAnonymous) {
        this.userId = userId;
        this.content = content;
        this.promptIdx = promptIdx;
        this.isAnonymous = isAnonymous;
        this.mediaId = null;
        this.intentions = new HashMap<>();
        this.analyzed = 0;
        this.topics = new ArrayList<>();
    }

    public Post(ObjectId userId, String content) {
        // Default prompt is -1, which is the "Post anything" prompt.
        // Default value of isAnonymous is false.
        this(userId, content, -1, false);
    }

    public void updateIntentions() {
        IntentionCalculation tags = new IntentionCalculation(this.content, this.promptIdx);
        this.intentions = tags.getIntentions();
        this.analyzed = 1;
    }

    /** Returns the ID of the Post.
     *
     *  @return ObjectId of Post.
     */
    public ObjectId getId() { return _id; }

    /** Sets the ID of the Post.
     *
     *  @param _id New ObjectId of Post.
     */
    public void setId(ObjectId _id) { this._id = _id; }

    /** Returns ID of posting User.
     *
     *  @return ObjectId of posting User.
     */
    public ObjectId getUserId() { return userId; }

    /** Associates a new User to the given Post
     *
     *  @param userId ObjectId of new associated
     *                 User.
     */
    public void setUserId(ObjectId userId) { this.userId = userId; }

    /** Returns the content of the Post.
     *
     *  @return Text content of Post.
     */
    public String getContent() { return content; }

    /** Replaces content of the Post.
     *
     *  @param content New content of Post.
     */
    public void setContent(String content) { this.content = content; this.analyzed = 0; }

    /** Return intentions and their strength assigned to the post
     *
     *  @return a map from intentions to strength assigned to them.
     */
    public Map<String, Double> getIntentions() {
        return intentions;
    }

    /** Update the intention map with new strength assigned to them
     *
     *  @param intentions a map from intentions to updated strength
     */
    public void setIntentions(Map<String, Double> intentions) {
        this.intentions = intentions;
    }

    /** Return topics assigned to the post
     *
     *  @return a list of topic contents
     */
    public List<String> getTopics() {
        return topics;
    }

    /** Update the topic list
     *
     *  @param topics an updated list of topics
     */
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    /** Find the media file related to the post
     *
     *  @return the ID of the media file in storage
     */
    public ObjectId getMediaId() { return this.mediaId; }

    /** Set the media file related to the post
     *
     *  @param mediaId ID of media file related to post
     */
    public void setMediaId(ObjectId mediaId) { this.mediaId = mediaId; }

    /** Set the type of the media file related to the post
     *
     * @param mediaType file extension of attached media
     */
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    /** Returns the file extension of the media attached to the post
     *
     * @return file extension (without '.')
     */
    public String getMediaType() { return this.mediaType; }

    /** Returns whether the post has been tagged with
     *  emotions and intentions
     * @return 1 if analyzed, 0 otherwise
     */
    public int isAnalyzed() { return this.analyzed; }

    /** Update the topics list
     *
     *  @param newTopics topics to be added to the list
     */
    public void addTopics(List<String> newTopics) {
        this.topics.addAll(newTopics);
    }

    /** Update the topics list
     *
     *  @param removedTopics topics to be removed from the list
     */
    public void deleteTopics(List<String> removedTopics) throws NullPointerException{
        this.topics.removeAll(removedTopics);
    }

    /**
     * Returns a String representation of the Post
     * in list format.
     *
     * @return String representation of Post.
     */
    public String toString() {
        return "Post{" +
                "_id=" + _id +
                ", user_id=" + userId +
                ", content='" + content + '\'' +
                ", intentions=" + intentions +
                ", tags=" + topics +
                '}';
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public int getPromptIdx() {
        return promptIdx;
    }

    public void setPromptIdx(int promptIdx) {
        this.promptIdx = promptIdx;
    }
}
