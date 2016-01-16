# coms572
A project repository for AI applied to the Pokemon Trading Card Game

Originally created during Fall 2015 for Com S 572/472: Intro to Artificial Intelligence at Iowa State University

## Project Description
This repository hosts code which represents and manages the state for the Pokemon card game and a couple computer agents for playing the game.  Game representation is done with custom data structures to represent cards held by both players, with substantial logic for implementing game play mechanics and card effects.  Additionally, three increasingly complex agents are included:
* RandomPlAI: repeatedly selects random moves from a list of valid actions
* RandomSearchAI: essentially does a partial minimax search with a heuristic for scoring non-terminal game states of sufficient depth.  Since the Pokemon TCG includes some stochastic effects (e.g. coin flips), minimax search may yield suboptimal results
* MonteCarloTreeSearchBot: uses Monte Carlo Tree Search to estimate which move will yield an optimal result
 
Decks for the players are defined via a plaintext database file, such as that found at project/src/data.  Each entry describes quantity and attributes for a card which are necessary to play the game.  This representation allows new cards to be added for future play without necessarily modifying the Java code written for this project.  The game engine was designed in an effort to support a variety of card effects described using a shorthand notation demonstrated in the sample database.

## Demo Instructions
The project was originally developed with the Eclipse IDE and runs without any command line arguments.  All setup and actions are performed via text-based arguments after starting the program.  For each player, it is possible to choose whether a computer or human agent will choose moves.

## Repository Contents
This project contains two folders:
1. The final project report
2. An eclipse project containing all code
