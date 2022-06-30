<!-- PROJECT LOGO -->
<br />
<p align="center">
    <img src="images/logo.png" alt="Logo" width="80" height="80">

<h3 align="center">Scripts for Axelor</h3>

  <p align="center">
    Resolve the frozen lake game!
    <br />
    <a href="https://github.com/EpitechMscPro2020/T-AIA-902-group1">
      <strong>Explore the docs »</strong>
    </a>
    <br />
    <br />
    <a href="https://github.com/EpitechMscPro2020/T-AIA-902-group1/issues">Report Bug</a>
    ·
    <a href="https://github.com/EpitechMscPro2020/T-AIA-902-group1/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

This is the bootstrap of the main AI project. The aims are to learn what are and how to use Q-Learning, SARSA and Deep Q-LEarning resolving the frozen lake game.

### Built With

* [Poetry](https://python-poetry.org/)
* [Gym](https://www.gymlibrary.ml/)
* [TensorFlow](https://www.tensorflow.org/)

<!-- GETTING STARTED -->

## Getting Started

To get a local copy up and running follow these simple example steps.

### Prerequisites

It needs Poetry to run.

### Installation

1. Clone the repo

  ```sh
  $ git clone https://github.com/EpitechMscPro2020/T-AIA-902-group1.git
  $ git checkout gregory-koenig-bootstrap
  ```

2. Activate the virtual environment

  ```sh
  $ poetry shell
  ```

3. Install the dependencies

  ```sh
  $ poetry install
  ```

<!-- USAGE EXAMPLES -->

## Usage

To use Q-learning with training:

  ```sh
  $ poetry run python T-AIA-902-group1/q-learning_with_train.py
  ```

To use Q-learning without training once the train is done:

  ```sh
  $ poetry run python T-AIA-902-group1/q-learning_without_train.py
  ```

To use SARSA:

  ```sh
  $ poetry run python T-AIA-902-group1/sarsa.py
  ```

To use Deep Q-learning:

  ```sh
  $ poetry run python T-AIA-902-group1/deep_q-learning.py
  ```

<!-- ROADMAP -->

## Roadmap

See the [open issues](https://github.com/Autogriff/axelor-ag/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->

## Contributing

Any contributions you make are **greatly appreciated**.

1. Clone the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- CONTACT -->

## Contact

Gregory Koenig - [@gregory-koenig](https://github.com/gregory-koenig) - gregory.koenig@epitech.eu

Project Links:

* [https://github.com/EpitechMscPro2020/T-AIA-902-group1](https://github.com/EpitechMscPro2020/T-AIA-902-group1)

<!-- ACKNOWLEDGEMENTS -->

## Acknowledgements

* [Q-learning](https://en.wikipedia.org/wiki/Q-learning)
* [SARSA](https://en.wikipedia.org/wiki/State%E2%80%93action%E2%80%93reward%E2%80%93state%E2%80%93action)
