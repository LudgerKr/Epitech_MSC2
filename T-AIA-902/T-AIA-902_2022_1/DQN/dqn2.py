import gym
import numpy as np
from keras.models import Sequential
from keras.layers import Dense, Embedding, Reshape
from keras.optimizers import Adam
from rl.agents.dqn import DQNAgent
from rl.policy import EpsGreedyQPolicy
from rl.memory import SequentialMemory
import wandb
from wandb.keras import WandbCallback

w = wandb.init(project="Taxi Driver", entity="nostradamovies")

epsilon = 0.1
target_model_update = 1e-2
learning_rate = 1e-3
nb_max_episode_steps = 50

""" wandb.run.name = "DQN_eps" + str(epsilon) + "_lr" + str(learning_rate) + "_tmu" + str(target_model_update)  """
wandb.run.name = "DQN_eps" + str(epsilon) + "_lr" + str(learning_rate) + "_maxepisode" + str(nb_max_episode_steps) 
wandb.run.save()

env = gym.make("Taxi-v3").env

env.render()

print("Number of actions: %d" % env.action_space.n)
print("Number of states: %d" % env.observation_space.n)

action_size = env.action_space.n
state_size = env.observation_space.n

np.random.seed(123)
env.seed(123)

env.reset()
env.step(env.action_space.sample())[0]

""" model_only_embedding = Sequential()
model_only_embedding.add(Embedding(500, 6, input_length=1))
model_only_embedding.add(Reshape((6,)))
print(model_only_embedding.summary()) """

""" memory = SequentialMemory(limit=50000, window_length=1)
policy = EpsGreedyQPolicy()
dqn_only_embedding = DQNAgent(model=model_only_embedding, nb_actions=action_size, memory=memory, nb_steps_warmup=500, target_model_update=1e-2, policy=policy)
dqn_only_embedding.compile(Adam(learning_rate=1e-3), metrics=['mae'])
dqn_only_embedding.fit(env, nb_steps=1000000, visualize=False, verbose=1, nb_max_episode_steps=99, log_interval=100000, callbacks=[WandbCallback()])

dqn_only_embedding.test(env, nb_episodes=5, visualize=True, nb_max_episode_steps=99)

w.finish()

w = wandb.init(project="Taxi Driver", entity="nostradamovies") """

model = Sequential()
model.add(Embedding(500, 10, input_length=1))
model.add(Reshape((10,)))
model.add(Dense(50, activation='relu'))
model.add(Dense(50, activation='relu'))
model.add(Dense(50, activation='relu'))
model.add(Dense(action_size, activation='linear'))
print(model.summary())

memory = SequentialMemory(limit=50000, window_length=1)
policy = EpsGreedyQPolicy(eps=epsilon)
dqn = DQNAgent(model=model, nb_actions=action_size, memory=memory, nb_steps_warmup=500, target_model_update=target_model_update, policy=policy)
dqn.compile(Adam(learning_rate=learning_rate), metrics=['mae'])
dqn.fit(env, nb_steps=1000000, visualize=False, verbose=1, nb_max_episode_steps=nb_max_episode_steps, log_interval=100000, callbacks=[WandbCallback()])

dqn.test(env, nb_episodes=5, visualize=True, nb_max_episode_steps=nb_max_episode_steps)

#dqn.save_weights('dqn_{}_weights.h5f'.format("Taxi-v3"), overwrite=True)