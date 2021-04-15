package model;

import org.bson.types.ObjectId;

public class Comment implements Data {
    private ObjectId id;  // Comment ID
    private ObjectId postId;
    private ObjectId userId;
    private boolean anonymous;
    private String content;
    
    // The parent comment's id. If it is the root comment, then null.
    private ObjectId parentId;

    public Comment(ObjectId postId, ObjectId userId, boolean anonymous, String content, ObjectId parentId) {
        this.postId = postId;
        this.userId = userId;
        this.anonymous = anonymous;
        this.content = content;
        this.parentId = parentId;
    }

    /** Get the Id of comment.
     *
     *  @param no param
     */
    public ObjectId getId() {
        return id;
    }

    /** Set the Id of comment.
     *
     *  @param the Id number
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /** Get the post Id
     *
     *  @param no param
     */
    public ObjectId getPostId() {
        return postId;
    }

    /** Set the Id of post.
     *
     *  @param no param
     */
    public void setPostId(ObjectId postId) {
        this.postId = postId;
    }

    /** Get the Id of user who made the comment.
     *
     *  @param no param
     */
    public ObjectId getUserId() {
        return userId;
    }

    /** Set the Id of if user who made the comment.
     *
     *  @param id of user
     */
    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    /** Check if the comment is anonymous.
     *
     *  @param no param
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /** Get the comment to be anonymous. 
     *
     *  @param true or false
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    /** Get the content of the comment. 
     *
     *  @param true or false
     */
    public String getContent() {
        return content;
    }
    
    /** Set the content of the comment. 
     *
     *  @param string 
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /** Set the content of the comment. 
     *
     *  @param no param
     */
    public ObjectId getParentId() {
        return parentId;
    }
    
    /** Set the parent id of the comment. 
     *
     *  @param no param
     */
    public void setParentId(ObjectId parentId) {
        this.parentId = parentId;
    }
}
