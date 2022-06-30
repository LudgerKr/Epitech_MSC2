import os
import pickle
import time

import gym
import numpy as np

env = gym.make('FrozenLake-v1')

with open('./data/q-learning.pkl', 'rb') as f:
    Q = pickle.load(f)


def choose_action(state):
    action = np.argmax(Q[state, :])
    return action


# start
for episode in range(5):

    state = env.reset()
    print("*** Episode: ", episode)
    t = 0
    while t < 100:
        env.render()

        action = choose_action(state)

        state2, reward, done, info = env.step(action)

        state = state2

        if done:
            break

        time.sleep(0.5)
        os.system('clear')
