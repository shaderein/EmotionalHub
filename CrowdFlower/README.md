## Training

See training code and model's skeleton from [this notebook](https://colab.research.google.com/drive/1AuQO5jj-hw8ZAxhCvLkD7ZPxkAdMajLo?usp=sharing). The code is not pushed because it requires an embedding dataset that is too large(2GB) to run the code.

## Deployment

`source_dir` contains the model, text preprocessing code, and prediction code which were registered and deployed on Microsoft Azure platform. 

The platform itself provides a REST endpoint, which receives a single text string. The text string will then be preprocessed by `tokenizer.pickle` in `source_dir/models` and fed into the well-trained machine learning model `model.h5`. `score.py` performs the whole process and outputs the prediction from the emotion list [happy, sad, neutral, hate, anger].

## Testing

To test the deployed model on your own, run either `CFPredictSample.java` or `test_endpoint.py`. You can specify any text you want to use and the response was received from the deployed model.

## Usage

The model was found to be good at detecting negative emotions (sad, anger, hate with precision > 0.85). Therefore, we may want to use it to filter out negative posts when we retrieve posts from the database and present them to a user who comes here just to relax himself/herself and doesn't want to see too many negative stuff.