test_sents = ["vad-detection: i don’t wish to be reminded of how old i actually am.",
              "vad-detection: i like you"]

import json
data = json.dumps({"signature_name": "serving_default", "instances": [test_sents[1]]})
print('Data: {} ... {}'.format(data[:50], data[len(data)-52:]))

import requests
headers = {"content-type": "application/json"}
json_response = requests.post('http://localhost:8501/v1/models/valence_model:predict', data=data, headers=headers)
predictions = json.loads(json_response.text)['predictions']

print(predictions)