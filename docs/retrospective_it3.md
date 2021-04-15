Retrospective for iteration 3#

## Progress Made

Posts are now more responsive, resulting in popups when clicked and displaying all relevant actions here instead of within each grid square. Media uploads into the MongoDB database were also added, although the display for these media files has not yet been completed. 

We’ve also collected some initial posts and made a script to add them from our official account. We made a separate database for storing test posts. 

#### Models

The VAD emotion detection model is fully deployed. A simple post recommendation algorithm is implemented, which will connect the emotions detected with user intentions. 

The CrowdFlower model is now well-trained and can predict 5 different emotions (happy, sad, hate, anger, neutral) from plain text, using a combined deep learning architecture (LSTM+CNN). 

## Challenges

One of the challenges we had was to find a good way to incorporate the model into our app. We tackled this problem by deploying the model on the cloud and querying it from our application rather than packaging the model with the application.

Maintaining consistent style in our front-end is still a prevailing issue. We aim to refactor our css for more consistency across pages once all of our core features are built and robust. 

## Next Iteration Plans

better frontend for post page according to the wireframe \
look into React for frontend \
finish image uploads and display \
add buttons to like or dislike other people's posts; show the number of likes and dislikes on the post popup window\
allow users to save their favorite posts \
allow users to change emotional intention on the post page \
display user name and time info together with the posts \
allow user to remain anonymous when making post \
Collect posts that attracts users to retain on the platform and triggers them to create posts \
Incorporate CrowdFlower model with the current VAD model and deploy it\
Refine post recommendation algorithm from tags
