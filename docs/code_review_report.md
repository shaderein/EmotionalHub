# Unicorns of Love | Code Review
## December 3rd, 2020

#### Contributions
- Chenyu (Heidi): Make sure we follow the design principles; remove code smells
- Jinhan: Apply design patterns; check naming and code style
- Shravan: All interfaces and dependency removal, repackaging
- Xi (Bella): Check our tests and code complexity
- Fanbo (Sophia): Add comments to insufficient parts
- Sihao (Lynn): Check the documentation

### Design Decisions

Principles in use:

- **Single Responsibility**: All classes in our model contain variables which are strongly related to each other, and methods specific to the purpose of the class (mostly getters and setters, or methods specified by the implemented interface). Smaller classes were also created to remove data clumps as described below. 
- **Open-Closed**: Multiple interfaces were created to make it easier to add new components, such as the Data interface for new objects for database storage, the DataAccessObject interface to specify alternative DAO objects for different data stores, and the Predictor interface to open the possibility of new prediction algorithms being used in the future. Other classes now rely on these interfaces, so they do not need to be modified to add these new features. 
- **LSP**: When we designed the data structure for VAD scores, we were thinking about implementing them as another set of intentions, since both intentions and VAD scores are essentially a map from strings to float numbers. However, based on the LSP, we realize that since there are many additional operations on intention other than that of VAD scores (e.g. adding intentions score based on the user’s prompt), we decided to separate them out. In general, no interface implementations or subclasses violate the expected behavior of a parent. 
- **Interface Segregation**: No interface is implemented where its methods could not in principle be useful for the implementing class, although the methods themselves may not currently be called for every such class. 
- **Dependency Inversion**: Our Server class, which exercises database operations for every element of our object model, originally relied on methods specific to our DaoMongo class implementations. It now depends mostly on the DataAccessOperation interface and its method signatures. The PostTags class now only depends on the Predictor interface as well rather than VadPrediction directly. 

Patterns in use:

- *Adapter*: We use Data Access Objects (DAOs), a version of the Adapter design pattern. We have three DAO classes (CommentDaoMongo, PostDaoMongo, and UserDaoMongo), which implements data access operations using a MongoDB database, such as add() and query(). 
- *Singleton*: We implicitly use the Singleton design pattern, by instantiating one instance of a MongoClient (our data store), and passing it as a constructor argument to each of our Data Access Objects as our Server begins to run. 

Code smells removed:

- *Data clumps / large classes*: We added a PostTag class to store the emotion and intention tags of the posts rather than storing the tags directly in the Post class, ensuring variables relevant to our prediction algorithm are grouped and making the Post class more readable. We use a Prompt class that stores all the prompts for users to make new Posts and how they are retrieved instead of putting the code in the frontend .vm file directly. 
- *Long methods / duplicate code*: With the exception of Server main, which handles all of our web endpoints, methods have been refactored to move duplicate code into separate methods and keep all methods within a reasonable amount of lines. 

### Complexity

Our code is now easy to understand and use.
We had some duplicate code in methods in PostDaoMongo class and Post class during development, and we refactored them to make our code simpler.

### Tests

We have written tests for our Dao classes.

### Naming and Style

All the variable, method, file, and class namings clearly indicate how they are used.
We changed “color-select” which was used in our initial design to “intent-select” which fits our current design better.
We also changed all variable names to lowerCamelCase to ensure a consistent style.
In general, our code follows a good programming style.

### Comments

Our code is well-documented.

### Documentation

We have detailed documentation on the Readme.
