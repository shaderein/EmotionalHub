This folder contains work of team "unicorns of love" -- Chenyu Zhang, Jinhan Zhang, Shravan Venkatesan, Sophia Xu, Xi He, and Lynn Yin for the fall 2020 OOSE class. 

The `homework` folder contains the course homeworks. The `EmotionalHub` folder contains our main project, and the `docs` folder contains relevant documentation.

## EmotionalHub

This is the source code for a web application that features an "emotionally conscious" social platform, where users would receive feeds that fit their emotional needs, and they will also be able to make posts themselves.

After logging in, the user would be prompted to a page where they can select one of "cheer me up," "chill," "inspire me," "care for my emotion" (where they in turn need to select an emotion), "skip," etc. They will then be directed to the "The Hub" page, where they see interesting contents (text, image, audio, video) placed in blocks, posted by other users, which are taylored towards the intentions they selected, using our machine learning algorithm. They would be able to make posts about anything in the empty blocks. Users can make comments under each post. They can also choose to remain anonymous while posting or making comments.

When a user makes a post under a specific prompt (which are preset by us, e.g. "Share something fun or exciting!"), the score for the corresponding intentions would be adjusted. This is a good supplement to the ML models. 

The grid layout is now adaptive to the length of the post and fits on different screen sizes (including phones).

The <username> tab on the top navbar includes user-specific options. Currently supported are "my posts," where users can edit or delete what they have posted, and "favorite posts," where they can see the posts that they liked.

(Not implemented yet) The user would also be able to open a thread on an emotionally-aware forum so that they can ask questions or express myself. They can follow up on a thread to answer other people's questions, help with their concerns, share their happiness, etc.

The app is currently deployed with Heroku at [https://emotional-hub.herokuapp.com/](https://emotional-hub.herokuapp.com/).\
Commands to run to deploy:\
`./gradlew build jar`\
`./gradlew build deployHeroku`

To run this application locally, run the `Server.java` file under `src/min/java/server`. Then the app would be running on `localhost:7000`. 

You can use the account with email `testing@jhu.edu` and password `testing` for testing. 

**Updated Dec 13.**

### Emotion tagging models and post recommendation system (prototype done)

We combine two machine learning models (VAD model and CrowdFlower model) to detect emotions from posts. Both of they are trained now. The post recommendation algorithm will connect the emotions detected with user intentions. The current version of this recommendation algorithm can be found in [this issue](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/issues/25), which will be further optimized.

In the application, this algorithm is presented as the connection between the home page (where the user selects their intention) to the feed page, where the user sees posts from other users. Different chosen intentions bring up different posts which correspond to that intention as predicted by the algorithm.

#### VAD model

**Training:** We first trained models with EmoBank data to give valence-arousal-dominance (VAD) scores to posts. The pretrained T5 model is used. Check [this colab notebook](https://colab.research.google.com/drive/1Hv3Rl7qRjVO31feJ4z2cNE7hNHLfIn6J?usp=sharing) to see how the model was trained. Details and citations can be found in [this issue](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/issues/18).

**Deployment:** When running our app locally, if you make a post, you will be able to see VAD scores of your post printed in the Java console.

In the current version, we have deployed the ML models on a separate endpoint and, in our app, we fetch prediction results from that endpoint. The endpoint is built with Flask, and the models themselves are served on Google AI platform. Details can be found in [this issue](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/issues/23).

The `vad-predict-app` is the model application. It is included in this repo as a submodule. To see the code in the submodule after pulling this repo, use `git submodule init`. There is a simple frontend made for testing the model API endpoint.

Try the model endpoint here: https://vad-predict.herokuapp.com/

> Example 1:  “It is a great day.” -> 
{"results":{"arousal":"3.44","dominance":"3.2","valence":"4.0"}}

**VAD Score Explanation** On a scale of 1 to 5, 
- for Arousal, 1 means calm, and 5 means excited;  
- for Dominance, 1 means being controlled, and 5 means in control; 
- for Valence, 1 means negative, 5 means positive.

#### CrowdFlower Model

**(See README in `CrowdFlower` dir for details`)**

- Dataset: [CrowdFlower](https://data.world/crowdflower/sentiment-analysis-in-text) (modified) (labelled with 5 possible emotion tags: happy, sad, hate, anger, and neutral)
- Model: multi-channel model (CNN+LSTM) adapted from this [repo](https://github.com/tlkh/text-emotion-classification)

##### Deployment
The model was deployed on Microsoft Azure platform. For the detail of the deployment, see README [here](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/tree/master/CrowdFlower).

We originally deployed the model on Google AI but switch to use Azure since a package used in text preprocessing is too large, which requires us to deploy the preprocessing and predicting code. (Check this [issue](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/issues/33) for work behind.

##### Usage
We also incorporate the model trained on CrowdFlower dataset, which contains 40,000 tweets labeled with 5 different emotion tags (happy, sad, hate, anger, and neutral). We found a well-designed deep learning model that achieved high accuracy in this emotion classification task. The model is really good at detecting the negative emotion from the post. So we use this model to filter out the negative posts when users choose to start with a positive intention like "cheer me up" first. The posts are still available when the user choose not to specity intentions though.

### Database

User and Post information is stored in mongoDB. The DB access information needs to be removed from the code if this repo becomes public. 
