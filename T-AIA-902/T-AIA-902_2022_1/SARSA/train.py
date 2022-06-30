from datetime import datetime
import numpy as np
import argparse
import time
import gym

env = gym.make("Taxi-v3")

def train(episodes = 50000
      , gamma = 0.95
      , epsilon = 1
      , max_epsilon = 1
      , min_epsilon = 0.001
      , epsilon_decay = 0.01
      , alpha = 0.85
    ):

    start_date = datetime.now()
    start_time = time.time()
    total_reward = []
    steps_per_episode = []
    
    print("/***************************************************/")
    print("epsilon",  epsilon)
    print("max_epsilon",  max_epsilon)
    print("gamma",  gamma)
    print("min_epsilon",  min_epsilon)
    print("epsilon_decay",  epsilon_decay)
    print("alpha",  alpha)
    print("/***************************************************/")

    Q = np.zeros((env.observation_space.n, env.action_space.n))

    reward = 0

    print("{} - Starting Training\n".format(start_date))
    for episode in range(episodes):
      start_episode = time.time()
      done = False
      total_reward.append(0)
      steps_per_episode.append(0)
      state1 = env.reset()

      epsilon = min_epsilon + (max_epsilon - min_epsilon) * np.exp(-epsilon_decay * episode)

      if np.random.uniform(0, 1) < epsilon:
          action1 = env.action_space.sample()
      else:
          action1 = np.argmax(Q[state1, :])

      while not done:
          state2, reward, done, _ = env.step(action1)
          total_reward[episode] += reward
          steps_per_episode[episode] += 1
          if np.random.uniform(0, 1) < epsilon:
              action2 = env.action_space.sample()
          else:
              action2 = np.argmax(Q[state2, :])

          predict = Q[state1, action1]
          target = reward + gamma * Q[state2, action2]

          # Update Q(s,a) = Q(s,a) + alpha * ((r + y * Q(s',a') - Q(s,a))
          Q[state1,action1] = Q[state1, action1] + alpha * (target - predict)
            
          state1 = state2
          action1 = action2

          reward += 1

    end_date = datetime.now()
    execution_time = (time.time() - start_time)

    print("{} - Training Ended".format(end_date))
    print("Mean Reward: {}".format(np.mean(total_reward)))
    print("Mean Step: {}".format(np.mean(steps_per_episode)))
    print("Time to train: \n    - {}s\n    - {}min\n    - {}h".format(
        np.round(execution_time, 2), np.round(execution_time / 60, 2),
        np.round(execution_time / 3600, 2)))

    np.save("q_table_sarsa", Q)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Train Taxi Driver Using the Q-Learning Algorithm")
    parser.add_argument(
        "--episodes",
        type=int,
        default=25000,
        help="Number of episodes",
    )
    parser.add_argument("-a",
                        "--alpha",
                        type=float,
                        default=0.85,
                        help="Alpha Factor")
    parser.add_argument("-g",
                        "--gamma",
                        type=float,
                        default=0.99,
                        help="Discount Rating")
    parser.add_argument("-e",
                        "--epsilon",
                        type=float,
                        default=1,
                        help="Exploration Rate")
    parser.add_argument("--min_epsilon",
                        type=float,
                        default=0.001,
                        help="Minimal value for Exploration Rate")
    parser.add_argument("-d",
                        "--decay_rate",
                        type=float,
                        default=0.01,
                        help="Exponential decay rate for Exploration Rate")

    args = parser.parse_args()

    epsilon = args.epsilon
    max_epsilon = args.epsilon
    episodes = args.episodes
    gamma = args.gamma
    min_epsilon = args.min_epsilon
    epsilon_decay = args.decay_rate
    alpha = args.alpha

    train(episodes, gamma, epsilon, max_epsilon, min_epsilon, epsilon_decay,
          alpha)
