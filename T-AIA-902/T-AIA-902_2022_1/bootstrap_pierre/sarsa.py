import gym
import numpy as np

environment = gym.make("FrozenLake-v1")
environment.reset()

alpha = 0.5
gamma = 0.9

qtable = np.zeros((environment.observation_space.n, environment.action_space.n))
score = []

episodes = 1000       
alpha = 0.5            
gamma = 0.9 

for i in range(episodes):
    state = environment.reset()
    action = np.argmax(qtable[state])

    prev_state = None
    prev_action      = None

    t = 0

    for t in range(2500):
        state, reward, done, info = environment.step(action)

        action = np.argmax(qtable[state])

        if not prev_state is None:
            q_old = qtable[prev_state][prev_action]
            q_new = q_old
            if done:
                q_new += alpha * (reward - q_old)
            else:
                q_new += alpha * (reward + gamma * qtable[state][action] - q_old)

            new_table = qtable[prev_state]
            new_table[prev_action] = q_new
            qtable[prev_state] = new_table

        prev_state = state
        prev_action = action

        if done:
            if len(score) < 100:
                score.append(reward)
            else:
                score[i % 100] = reward

            print("Episode {} finished after {} timesteps with r={}. Running score: {}".format(i, t, reward, np.mean(score)))
            break