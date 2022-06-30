import numpy as np
import gym
import tensorflow as tf
import random

import numpy as np
import gym
import tensorflow as tf
import tensorflow.compat.v1 as tf
import random

env = gym.make("FrozenLake-v1")
count_actions = env.action_space.n
count_states = env.observation_space.n
tf.disable_v2_behavior() 

# Actions are left, up, right, down
print(count_actions)
# States are the 16 fields
print(count_states)
env.render()

# q table where rows=states, columns=actions
# We do not need that anymore
qtable = np.zeros((count_states, count_actions))
print(qtable)

# The inputs of the NN is the state space + next action
tf_input_size = count_states
# The output of the NN is the action space
tf_output_size = count_actions
# The hidden layer size
tf_hidden_layer_size = (tf_input_size+tf_output_size)//2
print(tf_input_size)
print(tf_output_size)
print(tf_hidden_layer_size)


# Reset the computational graph
tf.compat.v1.reset_default_graph()

tf_inputs = tf.placeholder(tf.float32, [None, tf_input_size])
tf_next_q = tf.placeholder(tf.float32, [None, tf_output_size])

# Hidden Layers
tf_weights_1 = tf.get_variable("tf_weights_1", [tf_input_size, tf_hidden_layer_size], initializer=tf.zeros_initializer)
tf_biases_1 = tf.get_variable("tf_biases_1", [tf_hidden_layer_size], initializer=tf.zeros_initializer)
tf_outputs_1 = tf.nn.relu(tf.matmul(tf_inputs, tf_weights_1) + tf_biases_1)

# Output
tf_weights_out = tf.get_variable("tf_weights_out", [tf_hidden_layer_size, tf_output_size], initializer=tf.zeros_initializer)
tf_biases_out = tf.get_variable("tf_biases_out", [tf_output_size], initializer=tf.zeros_initializer)
# Calculate the output layer
tf_outputs = tf.matmul(tf_outputs_1, tf_weights_out) + tf_biases_out

tf_action = tf.argmax(tf_outputs, 1)

# Calculate the loss by applying the softmax first
tf_loss = tf.reduce_sum(tf.square(tf_outputs - tf_next_q))

# Use adam optimizer (instead of GD) with a suboptimal learning rate of 0.1
tf_optimize = tf.train.GradientDescentOptimizer(learning_rate=0.1).minimize(tf_loss)

sess = tf.InteractiveSession()
initializer = tf.global_variables_initializer()

sess.run(initializer)

total_episodes = 15000        # Total episodes
learning_rate = 0.8           # Learning rate
max_steps = 99                # Max steps per episode
gamma = 0.95                  # Discounting rate

# Exploration parameters
epsilon = 1.0                 # Exploration rate
max_epsilon = 1.0             # Exploration probability at start
min_epsilon = 0.01            # Minimum exploration probability
decay_rate = 0.005             # Exponential decay rate for exploration prob
# List of rewards
rewards = []

# 2 For life or until learning is stopped
for episode in range(total_episodes):
    # Reset the environment
    state = env.reset()
    step = 0
    done = False
    total_rewards = 0

    for step in range(max_steps):
        # Choose an action a in the current world state (s)
        ## First we randomize a number
        exp_exp_tradeoff = random.uniform(0, 1)

        ## If this number > greater than epsilon --> exploitation (taking the biggest Q value for this state)
        if exp_exp_tradeoff > epsilon:
            #action = np.argmax(qtable[state,:])

            # Get the next action from our neural network
            action = sess.run([tf_action], feed_dict={tf_inputs: np.identity(16)[state:state+1]})[0][0]
        # Else doing a random choice --> exploration
        else:
            action = env.action_space.sample()

        # Take the action (a) and observe the outcome state(s') and reward (r)
        new_state, reward, done, info = env.step(action)

        # Update Q(s,a):= Q(s,a) + lr [R(s,a) + gamma * max Q(s',a') - Q(s,a)]
        # qtable[new_state,:] : all the actions we can take from new state
        #qtable[state, action] = qtable[state, action] + learning_rate * (reward + gamma * np.max(qtable[new_state, :]) - qtable[state, action])

        # Get the q-values for the current state
        old_q_values = sess.run([tf_outputs], feed_dict={tf_inputs: np.identity(16)[state:state+1]})[0][0]
        old_value = old_q_values[action]

        # Get the q-values for the next state
        next_q_values = sess.run([tf_outputs], feed_dict={tf_inputs: np.identity(16)[new_state:new_state+1]})[0][0]
        next_max = np.max(next_q_values)

        # Calculate the target value
        y_hat = reward + gamma * next_max

        # Set the new q-values (overwrite the old one)
        #q_table[current_state, action] = new_value
        new_q_values = old_q_values
        new_q_values[action] = y_hat

        # Train the NN
         # Run optimizer and calculate mean loss
        _, loss = sess.run([tf_optimize, tf_loss],
                                 feed_dict={tf_inputs: np.identity(16)[state:state+1], tf_next_q:new_q_values.reshape(1,4)})

        # Our new state is state
        state = new_state

        # If done (if we're dead) : finish episode
        if done == True:
            print("Episode {} finished after {} timesteps".format(episode, step+1))
             # Reduce epsilon (because we need less and less exploration)
            epsilon = min_epsilon + (max_epsilon - min_epsilon)*np.exp(-decay_rate*episode)
            break


    rewards.append(total_rewards)

print ("Score over time: " +  str(sum(rewards)/total_episodes))
