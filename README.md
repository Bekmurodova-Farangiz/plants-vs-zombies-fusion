# Plants vs Zombies Fusion

A JavaFX tower-defense game inspired by the classic Plants vs Zombies, developed using Object-Oriented Programming principles.

## Overview

This project was created as a university Object-Oriented Programming project.  
It demonstrates software engineering concepts through a fully interactive real-time strategy game with custom mechanics, multiple enemy types, resource systems, and scalable architecture.

## Technologies Used

- Java
- JavaFX
- Object-Oriented Programming (OOP)
- Git / GitHub

## Main Features

- Real-time gameplay using JavaFX Timeline
- Multiple plant classes with unique abilities
- Multiple zombie enemy types
- Day Mode and Night Mode
- Easy / Medium / Hard difficulty settings
- Fusion mechanic for upgraded plants
- Parasite Zombie special behavior
- Cage trap mechanic
- Resource system (Sun + Water)
- Win / Lose screens
- Settings menu

## OOP Concepts Demonstrated

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Composition
- Modularity
- Reuse
- Subtyping

## Project Architecture

- GameBoard – Main gameplay engine
- GameBoardView – Rendering and UI layer
- Plant – Parent class for all plants
- Zombie – Parent class for all zombies
- ZombieFactory – Centralized enemy creation system

## Screenshots

### Main Menu
<img src="https://raw.githubusercontent.com/Bekmurodova-Farangiz/plants-vs-zombies-fusion/main/screenshots/menu-page.jpg" width="800">

### Gameplay
<img src="https://raw.githubusercontent.com/Bekmurodova-Farangiz/plants-vs-zombies-fusion/main/screenshots/day-mode.jpg" width="800">

### Night Mode
<img src="https://raw.githubusercontent.com/Bekmurodova-Farangiz/plants-vs-zombies-fusion/main/screenshots/night-mode.jpg" width="800">

### Difficulty Settings
<img src="https://raw.githubusercontent.com/Bekmurodova-Farangiz/plants-vs-zombies-fusion/main/screenshots/difficulty-page.jpg" width="800">

### Victory Screen
<img src="https://raw.githubusercontent.com/Bekmurodova-Farangiz/plants-vs-zombies-fusion/main/screenshots/victory.jpg" width="800">

## How to Run

1. Install JDK 21 and a JavaFX SDK.
2. Either set `JAVAFX_HOME` to your JavaFX SDK folder or place `javafx-sdk-*` next to this project.
3. Build with:

```sh
./build.sh
```

4. Run with:

```sh
./run.sh
```

The scripts automatically look for JavaFX in:

- `JAVAFX_HOME`
- `../javafx-sdk-*`
- `~/Desktop/javafx-sdk-*`

## Future Improvements

- Save / Load system
- Additional plant and zombie classes
- Sound effects and animations
- Endless survival mode
- Leaderboard system
- Multiplayer support

## Authors

Developed by Farangiz Bekmurodova and Durbek Turaev.
