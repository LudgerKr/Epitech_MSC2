import numpy as np
import gym
import argparse

def play(slow=False):
    env = gym.make("Taxi-v3")

    q_table = np.load("q_table_qlearning.npy")
    done = False
    result = 0
    state = env.reset()

    env.render(mode='ansi')
    steps = 0

    while not done:
        action = np.argmax(q_table[state])
        next_state, reward, done, _ = env.step(action)

        result += reward
        state = next_state

        print(env.render(mode='ansi'))
            
        steps += 1

        if steps >= 99:
            break

        if slow:
            input("Press anything to continue...")
            print("\r", end="\r")


    print("****************************************************")
    print("[{} MOVES] - Total reward: {}".format(steps, result))

    env.close()

    return steps, result

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    step_average = 0
    result_average = 0

    parser.add_argument(
        "-s",
        "--slow",
        dest="slow",
        action="store_true",
        default=False,
        help="Activate Slow Mode",
    )

    parser.add_argument("-l",
                        "--loop",
                        type=int,
                        help="Number of episodes",
                        default=1)

    args = parser.parse_args()

    for l in range(args.loop):
        steps, result = play(args.slow, args.loop)
        step_average += steps
        result_average += result
        if steps >= 100:
            total_failed += 1

    print("****************************************************")
    print("Summary result:")
    print(f"{args.loop} loop")
    print(f"Average Steps Per Loop: {np.round(step_average / args.loop, 2)}")
    print(f"Average Reward Per Loop: {np.round(result_average / args.loop, 2)}")