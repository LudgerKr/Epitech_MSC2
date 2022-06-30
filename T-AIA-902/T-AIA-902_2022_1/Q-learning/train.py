import numpy as np
import gym
import random
import argparse

env = gym.make("Taxi-v3")
env.render(mode='ansi')

action_size = env.action_space.n
print("Action size ", action_size)

state_size = env.observation_space.n
print("State size ", state_size)

q_table = np.zeros((state_size, action_size))

def train(epsilon= 1
        , max_epsilon = 1
        , episodes = 50000
        , gamma = 0.99
        , min_epsilon= 0.001
        , epsilon_decay = 0.01
        , alpha = 0.01
        , max_steps = 99
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

    q_table = np.zeros((state_size, action_size))

    print("{} - Starting Training\n".format(start_date))
    for episode in range(episodes):
        start_episode = time.time()
        total_reward.append(0)
        steps_per_episode.append(0)

        reward = 0

        state = env.reset()
        step = 0
        done = False

        for step in range(max_steps):
            exp_exp_tradeoff = random.uniform(0,1)

            ## If this number > greater than epsilon --> exploitation (taking the biggest Q value for this state)
            if exp_exp_tradeoff > epsilon:
                action = np.argmax(q_table[state,:])

            # Else doing a random choice --> exploration
            else:
                action = env.action_space.sample()

            # Take the action (a) and observe the outcome state(s') and reward (r)
            new_state, reward, done, info = env.step(action)
            total_reward[episode] += reward
            steps_per_episode[episode] += 1

            # Update Q(s,a) = Q(s,a) + alpha [R(s,a) + gamma * max Q(s',a') - Q(s,a)]
            q_table[state, action] = q_table[state, action] + alpha * (reward + gamma *
                                        np.max(q_table[new_state, :]) - q_table[state, action])

            # Our new state is state
            state = new_state

            # If done : finish episode
            if done == True:
                break

    # Reduce epsilon (because we need less and less exploration)
    epsilon = min_epsilon + (max_epsilon - min_epsilon)*np.exp(-epsilon_decay*episode)

    end_date = datetime.now()
    execution_time = (time.time() - start_time)

    print("/***************************************************/")
    print("{} - Training Ended".format(end_date))
    print("Mean Reward: {}".format(np.mean(total_reward)))
    print("Mean Step: {}".format(np.mean(steps_per_episode)))
    print("Time to train: \n    - {}s\n    - {}min\n    - {}h".format(
        np.round(execution_time, 2), np.round(execution_time / 60, 2),
        np.round(execution_time / 3600, 2)))
    
    np.save("q_table_qlearning", q_table)

    print ("Train done, q_table_qlearning saved")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Train Taxi Driver Using the Q-Learning Algorithm")
    parser.add_argument(
        "--episodes",
        type=int,
        default=50000,
        help="Number of episodes",
    )
    parser.add_argument("-a",
                        "--alpha",
                        type=float,
                        default=0.01,
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
                        default=0.01,
                        help="Minimal value for Exploration Rate")
    parser.add_argument("--max_epsilon",
                        type=float,
                        default=1,
                        help="Maximum value for Exploration Rate")
    parser.add_argument("-d",
                        "--epsilon_decay",
                        type=float,
                        default=0.01,
                        help="Exponential decay rate for Exploration Rate")
    parser.add_argument("-s",
                        "--steps",
                        type=float,
                        default=99,
                        help="Maximum value for step")

    args = parser.parse_args()

    epsilon = args.epsilon
    max_epsilon = args.epsilon
    episodes = args.episodes
    gamma = args.gamma
    min_epsilon = args.min_epsilon
    epsilon_decay = args.epsilon_decay
    alpha = args.alpha
    max_steps = args.steps

    train(episodes, gamma, epsilon, max_epsilon, min_epsilon, epsilon_decay,
          alpha, max_steps)