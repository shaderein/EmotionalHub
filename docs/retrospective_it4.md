# Retrospective for iteration 4

## Progress Made

We completed the comments feature. Specifically, we implemented the Comment class and the DAO class for CRUD operations on Comment. We also made the View and Controller for making comments.

We also implemented the feature where users would be prompted to make posts that address specific emotional intentions, and we made those posting blocks appear randomly on our UI.

The CrowdFlower emotion classification model is now deployed and ready to be used in addition to the VAD model.


## Challenges

Incorporating the Comment class into our existing data structures was a challenge. We were finding the best class design for the comment class and decided to let the comment be retrieved in a 1D manner so that the user can reply to other user's comments.

While we tackled image upload, it was also difficult to manage to retrieve and display user-uploaded images.

The original crowdflower model was too big to deploy since the embedding layers were not incorporated into the model and the text preprocessing stage was merely retrieving representations of words from a prepared dictionary. The model was compressed to be able to deploy.


## Next Iteration Plans

Integrate two emotion models and improve our recommendation algorithm \
- The Crowdflower model will be used to filter out posts with negative emotions since the dataset for this model contains a lot of negative samples. The content will be blocked if the user choose intention such as "help me calm down" or "make me laugh".\

Add options to save favorite posts; upvote and downvote posts \
Make the frontend clean and nice \
Enhance the security of login \
Make adaptations for phone display \
Refractor the codebase \
(going further) Design user profile and forum page

