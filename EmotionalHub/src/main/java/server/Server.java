package server;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import model.Post;
import model.Prompt;
import model.User;
import model.Comment;
import org.bson.types.ObjectId;
import persistence.CommentDaoMongo;
import persistence.DataAccessObject;
import persistence.PostDaoMongo;
import persistence.UserDaoMongo;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import javax.servlet.MultipartConfigElement;

import static com.mongodb.client.model.Filters.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

/** Runs a local server which provides
 *  RESTful endpoints to application pages and
 *  supported database operations.
 */
public class Server {

    // Active client connection
    private static MongoClient mongoClient;
    // User DAO created from client connection
    private static DataAccessObject<User> userDaoMongo;
    // Post DAO created from client connection
    private static DataAccessObject<Post> postDaoMongo;
    // Comment DAO created from the client connection
    private static DataAccessObject<Comment> commentDaoMongo;

    // Local port number
    public static final int PORT_NUM = 7000;

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return PORT_NUM;
    }

    private static List<Integer> generateEmptyBlockPositions() {
        List<Integer> lstPos = new ArrayList<>();
        Random rand = new Random();
        int numRows = 5;
        // Generate one empty position for each row, for 'numRow' rows.
        // Each row has 5 positions.
        for (int i = 0; i < numRows; i++) {
            lstPos.add(rand.nextInt(5) + i * 5);
        }
        return lstPos;
    }

    private static boolean checkUserLoggedIn(Request req, Response res) {
        if (req.cookie("userEmail") == null) {
            res.redirect("/login");
            return false;
        }
        return true;
    }

    private static User getCurrentUser(Request req) {
        return userDaoMongo.query(eq("email", req.cookie("userEmail"))).get(0);
    }

    /** Runs server locally by binding to specified
     *  port.
     *
     *  @param args Unused.
     */
    public static void main(String[] args)  {
        // Connection initialization and DAO setup
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = "mongodb+srv://mongo-db-user:lemonade@cluster0.nu1bq.mongodb.net/"
                + "<dbname>?retryWrites=true&w=majority";
        //connectionString = System.getenv("MONGODB_URI");

        mongoClient = new MongoClient(new MongoClientURI(connectionString));
        userDaoMongo = new UserDaoMongo(mongoClient);
        postDaoMongo = new PostDaoMongo(mongoClient);
        commentDaoMongo = new CommentDaoMongo(mongoClient);

        port(getHerokuAssignedPort());

        staticFiles.location("/public");

        // Landing page for color selection
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String email = req.cookie("userEmail");
            if (email != null) {
                model.put("user", userDaoMongo.query(eq("email", email)).get(0));
                res.status(200);
                return new ModelAndView(model, "public/templates/intent-selection.vm");
            } else {
                res.redirect("/login");
                return null;
            }
        }, new VelocityTemplateEngine());

        // Login page for registered users
        // Other pages requiring login redirect here
        // TODO: Clear cookies on logout
        get("/login", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "public/templates/login.vm");
        }, new VelocityTemplateEngine());

        // Processes and accepts/rejects login request
        post("/login", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String email = req.queryParams("email");
            String pwd = req.queryParams("pwd");
            // Check the user exists and password is correct
            try {
                List<User> users = userDaoMongo.query(eq("email", email));
                if (users.isEmpty()) {
                    throw new NullPointerException();
                }
                User user = users.get(0);
                if (!user.getPassword().equals(pwd)) {
                    throw new NullPointerException();
                }
                // Remember the user using cookie.
                // Note that the cookie cannot contain white space.
                res.cookie("userEmail", email);
                // user name may contain whitespace and thus is not a valid cookie!
                //res.cookie("userName", user.getUserName());
                res.redirect("/");
                return null;
            } catch(NullPointerException e) {
                model.put("failure", "true");
                ModelAndView mdl = new ModelAndView(model, "public/templates/login.vm");
                return new VelocityTemplateEngine().render(mdl);
            }
        });

        // Page for new users to register
        get("/adduser", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "public/templates/user-register.vm");
        }, new VelocityTemplateEngine());

        // This function helps to processes registration request
        // TODO: Validate email
        // TODO: Create password rules, confirm password, hash password
        post("/adduser", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String email = req.queryParams("email");
            String pwd = req.queryParams("pwd");
            String userName = req.queryParams("userName");
            User user = new User(email, pwd, userName);

            ObjectId success = userDaoMongo.add(user);
            if (success != null) {
                model.put("success", "true");
            } else {
                model.put("failure", "true");
            }

            ModelAndView mdl = new ModelAndView(model, "public/templates/user-register.vm");
            return new VelocityTemplateEngine().render(mdl);
        });

        // This function returns feed of all posts from all users
        // In the future, we need to filter posts based on emotion tags, let users expand posts to full view,
        //let user edit/de;ete posts, and give user list of their owb post. 
        get("/feed", (req, res) -> {
            if (!checkUserLoggedIn(req, res)) { return null; }
            Map<String, Object> model = new HashMap<>();
            //model.put("userEmail", req.cookie("userEmail"));

            String intent = req.queryParams("intent");
            if (intent == null) {
                String cookie_intent = req.cookie("intent");
                intent = (cookie_intent != null) ? cookie_intent : "skip";
            } else {
                res.cookie("intent", intent);
            }
            model.put("user", getCurrentUser(req));
            model.put("posts", ((PostDaoMongo) postDaoMongo).list(intent)); // TODO: Generalize
            model.put("userDao", ((UserDaoMongo) userDaoMongo)); // TODO: Generalize
            model.put("postDao", ((PostDaoMongo) postDaoMongo)); // TODO: Generalize
            model.put("commentDao", (CommentDaoMongo) commentDaoMongo); // TODO: Generalize
            model.put("promptObj", new Prompt());
            res.status(200);
            return new ModelAndView(model, "public/templates/feed.vm");
        }, new VelocityTemplateEngine());

        // Update the post intentions
        // TODO: Add different types of content
        post("/analyze-post", (req, res) -> {
            // sleep for 1 second to wait for the post to be stored in database
            Thread.sleep(1000);

            List<Post> toAnalyze = postDaoMongo.query(eq("analyzed", 0));
            toAnalyze.forEach(post -> {
                post.updateIntentions();
                postDaoMongo.update(post);
            });
            res.status(200);
            return null;
        });

        post("/feed", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/tmp"));
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    new MultipartConfigElement("/tmp"));
            User user = getCurrentUser(req);
            if (req.queryParams("discuss") != null) {
                Comment comment = new Comment(new ObjectId(req.queryParams("discuss")),
                        user.getId(),
                        req.queryParams("anonymous") != null,
                        req.queryParams("comment"),
                        null); // TODO: Implement comment replies
                commentDaoMongo.add(comment);
            } else {
                Post post = new Post(user.getId(), req.queryParams("content"),
                        Integer.parseInt(req.queryParams("prompt-idx")),
                        req.queryParams("anonymous") != null);
                post.setId(new ObjectId());
                InputStream stream = req.raw().getPart("media").getInputStream();
                String type = req.raw().getPart("media").getSubmittedFileName();
                type = type.substring(type.lastIndexOf(".") + 1);
                if (stream.available() != 0) {
                    ((PostDaoMongo) postDaoMongo).addMedia(post, stream, type); // TODO: Generalize
                }
                postDaoMongo.add(post);
            }
            res.status(201);
            res.redirect("/feed");
            return null;
        });

        get("/activity", (req, res) -> {
            if (!checkUserLoggedIn(req, res)) { return null; }
            Map<String, Object> model = new HashMap<>();
            User user = getCurrentUser(req);
            List<Post> posts = postDaoMongo.query(eq("userId", user.getId()));
            model.put("userId", user.getId());
            model.put("user", getCurrentUser(req));
            model.put("posts", posts);
            model.put("userDao", ((UserDaoMongo) userDaoMongo)); // TODO: Generalize
            model.put("postDao", ((PostDaoMongo) postDaoMongo)); // TODO: Generalize
            model.put("commentDao", (CommentDaoMongo) commentDaoMongo); // TODO: Generalize
            res.status(200);
            return new ModelAndView(model, "public/templates/activity.vm");
        }, new VelocityTemplateEngine());

        post("/activity", (req, res) -> {
            if (!checkUserLoggedIn(req, res)) { return null; }
            if (req.queryParams("delete") != null) {
                ObjectId postID = new ObjectId(req.queryParams("delete"));
                postDaoMongo.delete(postDaoMongo.get(postID));
                List<Comment> comments = commentDaoMongo.query(eq("postId", postID));
                for (Comment comment : comments) {
                    commentDaoMongo.delete(comment);
                }
            } else if (req.queryParams("update") != null) {
                ObjectId postID = new ObjectId(req.queryParams("update"));
                Post post = postDaoMongo.get(postID);
                post.setContent(req.queryParams("content"));
                postDaoMongo.update(post);
            }
            res.redirect("/activity");
            return null;
        });

        get("/favorites", (req, res) -> {
            if (!checkUserLoggedIn(req, res)) { return null; }
            Map<String, Object> model = new HashMap<>();
            User user = getCurrentUser(req);
            List<Post> posts = new ArrayList<>();
            List<ObjectId> likedPosts = new ArrayList<>(user.getLikedPosts());
            for (ObjectId postId : likedPosts) {
                try {
                    posts.add(postDaoMongo.get(postId));
                } catch (IndexOutOfBoundsException e) {
                    //user.removeLikedPost(postId);
                    //userDaoMongo.update(user);
                    ((UserDaoMongo) userDaoMongo).removeLikedPost(user, postId);
                    System.out.println("Removing favorite post that doesn't exist anymore.");
                }
            }
            model.put("user", user);
            model.put("posts", posts);
            model.put("userDao", ((UserDaoMongo) userDaoMongo)); // TODO: Generalize
            model.put("postDao", ((PostDaoMongo) postDaoMongo)); // TODO: Generalize
            model.put("commentDao", (CommentDaoMongo) commentDaoMongo); // TODO: Generalize
            res.status(200);
            return new ModelAndView(model, "public/templates/favorites.vm");
        }, new VelocityTemplateEngine());

        post("/add-favorite", (req, res) -> {
            Gson gson = new Gson();
            Map<String, String> reqMap = gson.fromJson(req.body(), HashMap.class);

            User user = getCurrentUser(req);
            ObjectId postId = new ObjectId(reqMap.get("postId"));
            //if (user.getLikedPosts().contains(postId)) {
            if (Boolean.parseBoolean(reqMap.get("like"))) {
                ((UserDaoMongo) userDaoMongo).addLikedPost(user, postId); // TODO: Generalize
            } else {
                ((UserDaoMongo) userDaoMongo).removeLikedPost(user, postId); // TODO: Generalize
            }
            //userDaoMongo.update(user);

            res.status(200);
            return res;
        });

        get("/serve-media", (req, res) -> {
            OutputStream os = res.raw().getOutputStream();
            String type = req.queryParams("type");  // img, audio, or video
            try {
                ObjectId mediaId = new ObjectId(req.queryParams("id"));
                byte[] buf = ((PostDaoMongo) postDaoMongo).retrieveMedia(mediaId);
                if (type.equals("audio")) {
                    res.type("audio/mpeg");
                } else if (type.equals("video")) {
                    res.type("video/mp4");
                } else {
                    res.type("image/jpeg");
                }
                os.write(buf);
                // TODO: fix the broken pipe exception for video display
            } catch (IOException e) {
                System.err.println("Error when serving media");
                //res.type("text/plain");
                //os.write("Failed to serve media".getBytes());
            }
            return res;
        });
    }
}
