import requests
import json

scoring_uri = 'http://6fa2426c-bdd7-4861-80e2-c5fab88c89b9.eastus2.azurecontainer.io/score'

text =  "I'm so happy today!"
headers = {'Content-Type':'application/json'}
test_data = json.dumps({'text': text})

data = json.loads(test_data)

response = requests.post(scoring_uri, data=test_data, headers=headers)
print(response.status_code)
print(response.elapsed)
print(response.json())