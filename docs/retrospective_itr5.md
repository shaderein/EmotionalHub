# Final Retrospective
## Changes from the original SRS
The goal of this project is to develop a social platform that is conscious of the emotion of the users. The initial proposal was to associate colors with emotions and use the colors as an indicator of what kind of posts to show users. However, we figured that the color-emotion association may not be very explicit and is hard for us to implement. We came up with several ideas but none of them was great. We finally decided to explicitly ask for users’ “emotional intention” instead of using colors as a proxy.

After we’ve made the emotion detection models, we decided to add the prompt feature (where users can make posts in response to a prompt) to aid the emotional intention scoring.

We have not had time to create the forum page where users can ask questions and seek response. We also haven’t implement the landing, about-us, and contact pages that are in the wireframe, but that is not an important part of the app and can always be added easily.

## Challenges Overall

The conceptual challenge is to find a way to understand how the users are feeling and also figure out how the posts can influence the users’ emotions.

Technical-wise, the deployment of ML models is one of our biggest challenges. While some of us have experience with training the models, it’s the first time for us to deploy them and access them from a web application. We made use of Google AI and Microsoft Azure, and followed online tutorials to manage the process.

We also had a hard time to figure out how to upload images into MongoDB and display them on the webpage. 

Also, as the app becomes more complex, making changes to existing code without creating problems for existing functionality is a challenge. We tried to refactor the code to adhere to design principles to avoid those problems. Specifically, we create more interfaces (ObjectAccess, Predictor, etc.) to make our code readable and UML more interpretable.

If we were to do it again, we would have more knowledge about the various tools that we’ve used (e.g. Bootstrap, MongoDB, Heroku, etc) and would spend less time reading documentation and more time developing interesting features. We would also make sure to follow design principles and apply design patterns so that we can add additional features later more easily. For instance, we should have used more interfaces instead of straight Java classes.

# Retrospective for iteration 5

## Progress Made

- Code review and refactoring of code base
- Completed the integration of post prompts to the intention scores. 
- Changed the post page to be a responsive layout; made frontend adjustments to make it look better
- Added like and dislike buttons; display posts that a user liked on their favorite post page
- The CrowdFlower model is integrated with the app for recommendation scoring. It filters out negative posts.
