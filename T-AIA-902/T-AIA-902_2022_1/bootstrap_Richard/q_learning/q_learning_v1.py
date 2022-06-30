import gym
import numpy as np
import random

env = gym.make('FrozenLake-v1')
env.render()

# Values for Q Table:

action_size = env.action_space.n
print('Action Space: ', action_size)

state_size = env.observation_space.n
print('State Size: ', state_size)

# Build Q Table:

q_table = np.zeros((state_size, action_size))
q_table

# Hyper params:

total_ep = 1500
total_test_ep = 100
max_steps = 100

lr = 0.81
gamma = 0.96

# Exploration Params:

epsilon = 0.9
max_epsilon = 1.0
min_epsilon = 0.01
decay_rate = 0.01

# Implementing the Q Learning Algorithm:

for episode in range(total_ep):

  # Reset Environment:
  state = env.reset()
  step = 0
  done = False

  for step in range(max_steps):

    # Choose an action a in the current world state(s) (step 3)
    # First we randomize a number
    exp_exp_tradeoff = random.uniform(0, 1)

    # If this number > greater than epsilon --> exploitation (taking the biggest q value for the current state):
    if exp_exp_tradeoff > epsilon:
      action = np.argmax(q_table[state, :])

    # Else, doing random choice:
    else:
      action = env.action_space.sample()

    # Take the action (a) and observe the outcome state (s') and the reward (r)
    new_state, reward, done, info = env.step(action)

    # Update Q(s,a):= Q(s,a) + lr [R(s,a) + gamma * max Q(s',a') - Q(s,a)]
    q_table[state, action] = q_table[state, action] + lr * (reward + gamma *
                                    np.max(q_table[new_state, :]) - q_table[state, action])
    
    # Our new state:
    state = new_state

    # If done True, finish the episode:
    if done == True:
      break

  # Increment number of episodes:
  episode += 1

  # Reduce epsilon (because we need less and less exploration):
  epsilon = min_epsilon + (max_epsilon - min_epsilon) *np.exp(-decay_rate*episode)
# Using Q Table:

env.reset()
rewards = []

for episode in range(total_test_ep):
  state = env.reset()
  step = 0
  done = False
  total_rewards = 0
  print('=========================')
  print('EPISODE: ', episode)

  for step in range(max_steps):

    env.render()

    # Take the action based on the Q Table:
    action = np.argmax(q_table[state, :])

    new_state, reward, done, info = env.step(action)

    total_rewards += reward

    # If episode finishes:
    if done:
      rewards.append(total_rewards)
      print('Score: ', total_rewards)
      break

    state = new_state

env.close()
print('Score Over Time: {}'.format(sum(rewards)/total_test_ep))
