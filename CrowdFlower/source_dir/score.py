import tensorflow as tf
import keras
import numpy as np
# print(tf.__version__)
# print(keras.__version__)
# print(np.__version__)

from keras.models import load_model
import itertools, pickle
import os
import json
from keras.preprocessing.sequence import pad_sequences

from azureml.core.model import Model

classes = ["neutral", "happy", "sad", "hate","anger"]
MAX_SEQUENCE_LENGTH = 30

def init():
    global model
    global tokenizer

    # Get the path where the deployed model can be found.
    model_path = os.path.join(os.getenv('AZUREML_MODEL_DIR'), './models')
    # model_path = "./models"
    # print(os.environ)
    # model_path = os.path.join('.', 'models')

    # model_path = Model.get_model_path(model_name='cf-model')
    # model_path = os.path.join(model_path, 'models')

    model = load_model(model_path + "/model.h5")

    with open(model_path+ '/tokenizer.pickle', 'rb') as handle:
        tokenizer = pickle.load(handle)

def run(data):
    try:
        # Pick out the text property of the JSON request.
        # This expects a request in the form of {"text": "some text to score for sentiment"}
        data = json.loads(data)
        # text = data['text']
        # return json.dumps({"text_loaded": text})
        prediction = predict(data['text'])
        #Return prediction
        return json.dumps({"tags": prediction})
    except Exception as e:
        error = str(e)
        return json.dumps({"error": error})

def predict(text):
    texts = [text]
    sequences = tokenizer.texts_to_sequences(texts)
    data_int_t = pad_sequences(sequences, padding='pre', maxlen=(MAX_SEQUENCE_LENGTH-5))
    data_input = pad_sequences(data_int_t, padding='post', maxlen=(MAX_SEQUENCE_LENGTH))
    y_prob = model.predict(data_input)
    print(y_prob)
    pred = []
    for n in range(len(y_prob)):
        tag = y_prob.argmax(axis=-1)[n]
        pred.append(classes[tag])
    return pred

# def main():
#     init()
#     text =  ["I'm so happy today!", "I'm very sad..."]
#     test_json = json.dumps({"text": text})
#     print(run(test_json))
  
# if __name__== "__main__":
#   main()
