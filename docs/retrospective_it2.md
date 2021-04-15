## Progress Made
So far, we have collected and created some posts to be used on our initial feed. Marginal updates were made to the main feed view, which allow users to view, edit, and delete their own posts. We also determined the emotion and influence tags for users to choose when they enter the application, although these do not yet affect other views. 

### Emotion Tagging: Datasets and models
In this iteration we did a paper overview and determined 2 datasets we will be focusing on: (1) CrowdFlower, which is tagged with 13 discrete emotions, and (2) EmoBank, which is continuous and evaluated at 3 dimensions (valence, arousal, and dominance). We started working on these 2 datasets in parallel and plan to compare the accuracy and fix to one dataset in the next iteration. T5, a pre-trained model successful in sequence prediction, was fine-tuned on the EmoBank dataset. Customized models with pre-trained embedding layer were implemented for the CrowdFlower dataset. We will also continue working on the front end as well. 

## Challenges
In this iteration, some of the challengs we will face include tagging the right emotions to the corresponding post, requiring constant group discussions. 

## Next Iteration Plans
For the next iteration, we aim to finish tagging each post with multiple tags and add posts to the database. We will also continue working on the post emotion detection model and develop algorithm to recommend posts based on emtion tags. We hope to also improve the display and quality of posts by enabling image uploads in posts, displaying user names on posts, allowing anonymous posting, and possibly implementing comments. 
