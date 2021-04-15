package tools;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import model.Post;
import model.User;
import persistence.PostDaoMongo;
import persistence.UserDaoMongo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddInitialPosts {
    private static UserDaoMongo userDaoMongo;
    private static PostDaoMongo postDaoMongo;

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = "mongodb+srv://mongo-db-user:lemonade@cluster0.nu1bq.mongodb.net/"
                + "<dbname>?retryWrites=true&w=majority";

        MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionString));
        userDaoMongo = new UserDaoMongo(mongoClient, false);
        postDaoMongo = new PostDaoMongo(mongoClient, false);

        User userOfficial = new User("official@jhu.edu", "1234", "EM Official");
        userDaoMongo.add(userOfficial);
        userOfficial = userDaoMongo.getUser("official@jhu.edu");

        List<Post> officialPosts = makeOfficialPosts(userOfficial);
        for (Post officialPost : officialPosts) {
            officialPost.updateIntentions();
            postDaoMongo.add(officialPost);
        }
    }

    private static List<Post> makeOfficialPosts(User userOfficial) {
        // Add all posts from the pool.
        List<Post> officialPosts = new ArrayList<>();
        officialPosts.add(new Post(userOfficial.getId(),
                "when you’re proud of your buns so you post them on the internet"));
        officialPosts.add(new Post(userOfficial.getId(),
                "Summers gone but the good vibes haven’t."));
        officialPosts.add(new Post(userOfficial.getId(), "My prayers go out to Chadwick’s family and loved ones. " +
                "The world will miss his tremendous talent. God rest his soul. #wakandaforever"));
        officialPosts.add(new Post(userOfficial.getId(), "Thank you everyone for the birthday wishes! " +
                "It’s a wild time we live in right now, but I’m grateful to my family and friends who keep my spirit up. " +
                "Love y’all \uD83E\uDD7A❤️"));
        officialPosts.add(new Post(userOfficial.getId(), "And with that, the UC San Diego chapter of my life is finished " +
                "\uD83C\uDF0A\uD83C\uDF93✌\uD83C\uDFFC One month ago, " +
                "I graduated from UCSD and completed my BA in International Business with a minor in Japanese Studies. " +
                "Thank you to my family and friends who’ve supported me throughout college and all the struggles that came with it. " +
                "It’s been a long four years, and while it may have not ended in the way that I expected, " +
                "I’m grateful for everything that has led up to my final quarter. " +
                "Thank you to everyone who has followed me on this journey and congrats to my friends who also graduated this year \uD83E\uDD70\uD83C\uDF89"));
        officialPosts.add(new Post(userOfficial.getId(), "all this news about travel to japan makes me (︶︹︺) #covidSucks"));
        officialPosts.add(new Post(userOfficial.getId(), "First day of Graduate school orientation done at @usccinema. " +
                "So lucky to be a Trojan at the best entertainment school in the world & fight on for the next three years! \uD83D\uDC9B❤️✌\uD83C\uDFFC"));
        officialPosts.add(new Post(userOfficial.getId(), "power of god and anime on our sides!!!!!!!! #animeexpo"));
        officialPosts.add(new Post(userOfficial.getId(), "happy mother’s day to the one & only ruth sherlock. " +
                "thanks for showing me a working mom, a businesswoman, an amazing friend, giving christian, " +
                "great sister and what a strong female role model looks like all in one! \uD83D\uDCF8"));
        officialPosts.add(new Post(userOfficial.getId(), "Thank you to these lovely ladies for giving me the most amazing opportunity to be " +
                "a Sixth College Orientation Leader. From the spring trainings to the actual orientations and Sixth Super Week, " +
                "it was such a good start to my last year at UCSD."));
        officialPosts.add(new Post(userOfficial.getId(), "It was great to see my favorites from Japan again \uD83D\uDE2D I miss you both already!!"));
        officialPosts.add(new Post(userOfficial.getId(), "A trip away from the city\uD83C\uDF43"));
        officialPosts.add(new Post(userOfficial.getId(), "chicago snow has got me missing michigan fall \uD83C\uDF41\uD83C\uDF42"));
        officialPosts.add(new Post(userOfficial.getId(), "hey mtv welcome to my crib !\uD83D\uDCAF\uD83D\uDCB8⌛️" +
                "\uD83D\uDCF8\uD83D\uDD79\uD83D\uDE80\uD83C\uDFAE\uD83C\uDFC6\uD83C\uDFB1\uD83C\uDF78\uD83C\uDF1F\uD83D\uDC8B\uD83D\uDC7E"));
        officialPosts.add(new Post(userOfficial.getId(), "\"Many of my movies have strong female leads- brave, " +
                "self-sufficient girls that don't think twice about fighting for what they believe with all their heart. " +
                "They'll need a friend, or a supporter, but never a savior. Any woman is just as capable of being a hero as any man." +
                "\" ~ Hayao Miyazaki ✌\uD83C\uDFFC\uD83C\uDF38 #explorejapan"));
        officialPosts.add(new Post(userOfficial.getId(), "Welcome to Wednesday! As Bon Jovi once said: WOOOAAAHHHH! We’re halfway there!"));
        officialPosts.add(new Post(userOfficial.getId(), "MATH: Mental Abuse To Humans."));
        officialPosts.add(new Post(userOfficial.getId(), "You are strong enough to face it all, even if it doesn’t feel like it right now."));
        officialPosts.add(new Post(userOfficial.getId(), "A great deal of your decisions are informed by your emotional responses " +
                "because that is what emotions are designed to do: to appraise and summarize an experience and inform your actions. " +
                "Hence, emotional intelligence is important in helping you become more aware of these emotional responses " +
                "so that they don’t control you but rather you control them."));

        return officialPosts;
    }
}
