# Assignment 2 --- Quoridor

## 👤 Student Information

**Hoang Nguyen** hnguy@bu.edu\
**Vishakha Kumaresan** vish1909@bu.edu

------------------------------------------------------------------------

------------------------------------------------------------------------

## ⚙️ Compile & Run Instructions

From the **`src`** directory:

``` bash
# Compile all Java source files
javac app/*.java game/core/*.java puzzles/sliding/*.java puzzles/dots/*.java

# Run the program
java app.Main
```
--------------------------------------------------------------------------------

## 🧩 Project Overview

This project extends the existing **terminal-based game suite** to support a **third turn-based strategy game — Quoridor**, built on the same reusable object-oriented framework that powered *Sliding Puzzle* and *Dots & Boxes*.

All three games share a unified **modular architecture**, ensuring a consistent user experience, efficient code reuse, and easy extensibility for future games.

### ✨ Framework Highlights
- **Scalable design:** any new turn-based grid game can be added with minimal setup.  
- **Unified interfaces:** all games implement `Game`, `Rules`, `Renderer`, and `TurnAgent`.  
- **Consistent UX:** identical menu flow — `Play / Rules / High Scores / Back` — for each game.  
- **Terminal-friendly visuals:** box-drawn ASCII/Unicode boards with ANSI colors.  
- **Player & stats management:** tracks moves, scores, and session summaries.  

-----------------------------------------------------------------------------

## 📁 Directory Structure

```bash
src/
├── app/
│   ├── Main.java
│   └── ConsoleIO.java
├── game/core/
│   ├── Game.java
│   ├── GameFactory.java
│   ├── GameRegistry.java
│   ├── Player.java
│   ├── TurnAgent.java
│   ├── Position.java
│   ├── Tile.java
│   └── Piece.java
├── puzzles/
│   ├── sliding/
│   ├── dots/
│   └── quoridor/

```

## 📁 File Information

### 🎮 Core Framework

| File                        | Description                                                                                                           |
| --------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| **Main**                    | Entry point of the suite. Registers all three games and provides the main menu (`Play / Rules / High Scores / Back`). |
| **ConsoleIO**               | Handles all user I/O from the terminal.                                                                               |
| **Game**                    | Interface for all playable games.                                                                                     |
| **GameFactory**             | Factory interface used to create games dynamically.                                                                   |
| **GameRegistry**            | Keeps track of registered games.                                                                                      |
| **Position / Tile / Piece** | Fundamental grid elements shared across modules.                                                                      |
| **TurnAgent / Player**      | Abstractions for human or AI-controlled turns.                                                                        |


### 🧩 Sliding Puzzle Module

| File                              | Description                                                |
| --------------------------------- | ---------------------------------------------------------- |
| **SlidingGame**                   | Main controller managing moves, shuffling, and win checks. |
| **SlidingFactory**                | Builds the game with its rules and renderer.               |
| **SlidingState**                  | Stores the board tiles and blank space position.           |
| **SlidingRules**                  | Defines legal moves and solvability logic.                 |
| **SlidingRenderer**               | Displays the puzzle grid in ASCII format.                  |
| **GoalStrategy / StandardGoal**   | Defines the “solved” configuration.                        |
| **Shuffler / RandomMoveShuffler** | Shuffles by legal blank moves to ensure solvability.       |
| **SessionBest**                   | Tracks fewest moves per session for high scores.           |


### Dots & Boxes Module

| File                                         | Description                                            |
| -------------------------------------------- | ------------------------------------------------------ |
| **DotsGame**                                 | Controller for turns, edge claiming, and scoring.      |
| **DotsAndBoxesFactory**                      | Factory for building the Dots & Boxes game.            |
| **DotsState**                                | Stores box ownership and H/V edge claims.              |
| **DotsRules**                                | Determines valid edges and box completion.             |
| **DotsRenderer / DotsRendererAnsi**          | Renders the grid with colored edges and box owners.    |
| **EdgePos / Orientation / Side / ClaimEdge** | Represent and validate edges.                          |
| **BoxPiece**                                 | Represents a filled box with player ID.                |
| **PlayerInfo / SessionStats**                | Store per-player stats and session summaries.          |
| **EdgeUtils**                                | Lists all unclaimed edges and potential scoring moves. |


### Quoridor Module

| File                      | Description                                                                                                            |
| ------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **QuoridorGame**          | Game controller with menu, input loop, and score display.                                                              |
| **QuoridorFactory**       | Registers and builds the Quoridor instance.                                                                            |
| **QuoridorState**         | Represents the game board (pawns, walls, turns, and stats).                                                            |
| **QuoridorRules**         | Handles all move legality (steps, jumps, diagonal side-steps) and wall placement validation using BFS for path safety. |
| **QuoridorRenderer**      | Displays the board as a fully boxed grid (`+---+ / │ │`) with ANSI color highlights for walls and pawns.               |
| **QuoridorAction**        | Encapsulates an action (`MOVE`, `WALL_H`, `WALL_V`).                                                                   |
| **QuoridorHuman**         | Human input parser for move and wall commands.                                                                         |
| **QuoridorStats**         | Tracks moves, walls, jumps, and win statistics.                                                                        |
| **QuoridorRulesText**     | In-game rules description used in the “Rules” menu.                                                                    |
| **PawnPiece / WallPiece** | Piece implementations for pawn and wall visualization.                                                                 |



## 💡 Example I/O

### Game Selection

    Select a game:
    1) Sliding Puzzle
    2) Dots & Boxes
    3) Quit
    Choice: 2

------------------------------------------------------------------------

### Dots & Boxes Menu

    === Quoridor ===
1) Play
2) Rules
3) High Scores
4) Back
Choice: 1
Enter board size (rows cols, default 9 9): 9 9
Player 1 name [A]: Shy
Player 2 name [B]: Ro


------------------------------------------------------------------------

### Gameplay Example

    Commands:
  move r c      — move pawn to target cell
  wall H r c    — place horizontal wall
  wall V r c    — place vertical wall
  help | quit

P1@0,4   P2@8,4   Walls P1:10 P2:10

      0   1   2   3   4   5   6   7   8
    +---+---+---+---+---+---+---+---+---+
  0 |   |   |   |   | A |   |   |   |   |
    +---+---+---+---+---+---+---+---+---+
  8 |   |   |   |   | B |   |   |   |   |
    +---+---+---+---+---+---+---+---+---+

P1> wall H 2 3
P2> move 7 4
...
🏆 Shy wins!

------------------------------------------------------------------------

### Exmple stats

    === Quoridor Stats ===
Games played: 4
Average moves: 39.2
Average walls: 12.0
Average jumps: 3.1
Wins:
  Shy — 2
  Ro — 2


------------------------------------------------------------------------

### Winning Move

    b (B) move [H r c | V r c | avail | edges | rules | q]: V 0 0

    .---.
    |   |
    .---.

    Score: a=0, b=1
    Winner: b

------------------------------------------------------------------------

## Example Commands

| Command      | Description                                                         |
| ------------ | ------------------------------------------------------------------- |
| `move 4 4`   | Move pawn to (4,4) if reachable. Supports jumps and diagonal steps. |
| `wall H 3 2` | Place a horizontal 2-segment wall under (3,2).                      |
| `wall V 5 5` | Place a vertical 2-segment wall to the right of (5,5).              |
| `help`       | Displays valid command usage.                                       |
| `quit`       | Exits the game.                                                     |

------------------------------------------------------------------------

### Design Principles

MVC-like layering: separation of model, view, and controller responsibilities.

Immutability: all states are cloned before mutation for safety and undo potential.

Generics: consistent Rules<S, A> and Renderer<S> interfaces for all games.

Reusability: shared use of ConsoleIO and GameRegistry.

Player abstraction: uniform handling of names, turns, and score tracking.

----------------------------------------------------------------

### Enhancements & Features

Full ANSI color and box-drawn grids.

Jump + diagonal movement identical to official Quoridor rules.

BFS validation for legal wall placements.

Unified stats and high-score menus for all games.

Difficulty selector for Sliding Puzzle.

avail and edges commands for Dots & Boxes.

---------------------------------------------------------------

### Future Improvements

AI agents via TurnAgent.

Undo / Replay for Quoridor.

Persistent score saving (e.g., CSV).

4-player Quoridor variant.

Visual replay mode for completed matches.

----------------------------------------------