from azureml.core.webservice import AciWebservice, Webservice, LocalWebservice
from azureml.core.model import Model

from azureml.core import Workspace
ws = Workspace.from_config(path=".azureml/config.json")

# Inference Configuration
from azureml.core.model import InferenceConfig
from azureml.core.environment import Environment
from azureml.core.conda_dependencies import CondaDependencies

# Create the environment
myenv = Environment(name="myenv")
conda_dep = CondaDependencies()

# Define the packages needed by the model and scripts
conda_dep.add_conda_package("python=3.6.2")
conda_dep.add_conda_package("numpy=1.18.5")
conda_dep.add_conda_package("scikit-learn")
conda_dep.add_conda_package("pip")
# You must list azureml-defaults as a pip dependency
conda_dep.add_pip_package("azureml-defaults")
conda_dep.add_pip_package("tensorflow==2.3.0")
conda_dep.add_pip_package("keras==2.4.3")

# Adds dependencies to PythonSection of myenv
myenv.python.conda_dependencies=conda_dep

inference_config = InferenceConfig(entry_script="./source_dir/score.py",
                                   environment=myenv)

# Deployment config
deployment_config = AciWebservice.deploy_configuration(cpu_cores = 1, memory_gb = 1)
# deployment_config = LocalWebservice.deploy_configuration(port=7000)

# deploy model
from azureml.core.model import Model

model = Model(ws, name='cf-model')
service = Model.deploy(ws, 'cf-aci2', [model], inference_config, deployment_config)

service.wait_for_deployment(True)
print(service.state)
print("scoring URI: " + service.scoring_uri)