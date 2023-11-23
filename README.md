# Flappy Bird Game
Welcome to the Flappy Bird! This is a simple game developed in Java using the LibGDX framework.

### Introduction
Flappy Bird is a classic 2D game where the player controls a bird, guiding it through a series of pipes. The goal is to navigate through the pipes without colliding. The game features day and night cycles, scoring, and a game-over screen.

### Game Features
#### Day and Night Cycles: 
The game features dynamic day and night cycles, changing every 30 seconds.

#### Scoring: 
Players earn points by successfully passing through pipes. The score is displayed on the screen.

#### Game Over Screen: 
When the bird collides with a pipe or reaches the top or bottom of the screen, the game enters a "Game Over" state. Players can restart the game by tapping the screen.

#### Responsive Controls: 
The bird's flight is controlled by tapping the screen, providing a responsive and engaging gameplay experience.

### Implementation Details
#### LibGDX Framework: 
The project is built using the LibGDX framework, a popular Java game development framework.
#### Sprites and Textures: 
The game utilizes various textures for the background, pipes, bird, and game over screen.
#### Collision Detection: 
Collision detection is implemented using rectangles for pipes and circles for the bird.
#### Scoring Logic: 
The game keeps track of the score, updating it when the bird successfully passes through pipes.
#### Game State Management: 
The game has three states: start, play, and game over. The state is managed to control gameplay flow.
