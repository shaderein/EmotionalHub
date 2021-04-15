# Unicorns of Love, Iteration 1

---

## Progress Made

### Class Design

The primary components of our application are the Users who register to use it, and the Posts that they make, and the Comments that are made on each of these posts. We have implemented the first of these two, User and Post, as classes which store their relevant information. 

Tag implementation:
	We added (1) the tag field and (2) the intention detected from each post and stored them as a field in the post database. The related operations for manipulating the database tag content were implemented and roughly tested. We initially planned to label each post by the intention detected and its strength. However, we noticed that we can’t find an appropriate data set that explicitly labels plain text with intentions. Therefore, in the next iteration we will label the posts by their emotion and implement another self-design algorithm that maps the emotion into some possible relevant intention. (For example, “joy” to the intention “cheer me up.”) Doing enough research about the feasibility of such a feature is necessary to help us proceed in iteration 2.

### Data Persistence

Data Access Objects were created for our User and Post classes to connect them to a MongoDB database using a Java MongoDB API. Data structure and database operations for the User and Post classes, including inserts, lists, updates, deletes, and finds are implemented and tested. 

### Application Endpoints

The basic user “signup account” and “login” pages are implemented, as well as our main page to view other users’ posts, and to add posts. The landing page where users can select a color as an indication of their current emotion is also implemented.

SparkJava was used to set up local endpoints for basic application navigation, including through our signup, login, and main post view pages. These pages were connected to our database operations through our DAO objects. 

---

## Challenges
Although we have basic navigation and database functionality set up, hashing out the details of the algorithms we plan to develop has been difficult, leading to frequent changes in our class design. 
It was hard to come up with the emotional tags for each post, because it would depend on the algorithm that we use for detecting emotions and connecting those to users’ intentions, and the algorithm will be our focus on iteration two.

---

## Next Iteration Plans
For the next interation plan, we will focus on allowing users to to view, edit, and delete their own posts.We will also start implementing the algorithm for recommending posts and the model that extracts the emotion in posts. In the meantime, we will refine the frontend pages to make them look nice.
