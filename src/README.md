# Assignment 2 --- Sliding Puzzle & Dots and Boxes

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

## 📁 File Information

### 🎮 Core Framework

  -----------------------------------------------------------------------
  File                  Description
  --------------------- -------------------------------------------------
  **Main**              Entry point of the program. Registers both games
                        and provides the main terminal menu (Play / Rules
                        / High Scores / Back).

  **ConsoleIO**         Handles console input/output.

  **Game**              Interface for all playable games.

  **GameFactory**       Factory interface to create and configure games.

  **GameRegistry**      Keeps track of available game factories.
  -----------------------------------------------------------------------

### 🧩 Sliding Puzzle Module

  ------------------------------------------------------------------------
  File                   Description
  ---------------------- -------------------------------------------------
  **SlidingGame**        Game controller for the sliding puzzle ---
                         handles moves, shuffling, and completion checks.

  **SlidingFactory**     Builds and wires the sliding puzzle with its
                         rules, renderer, and state.

  **SlidingState**       Stores current tile layout and blank position.

  **SlidingRules**       Defines valid-move logic and solvability checks.

  **SlidingRenderer**    Renders the puzzle grid in plain text.

  **GoalStrategy /       Define what the solved configuration looks like.
  StandardGoal**         

  **SolvabilityPolicy /  Ensure the puzzle is solvable before play.
  SolverUtils**          

  **Shuffler /           Shuffle the board using random valid moves.
  RandomMoveShuffler**   

  **SessionBest**        Tracks best scores (fewest moves) per session.
  ------------------------------------------------------------------------

### 🔳 Dots & Boxes Module

  ---------------------------------------------------------------------------
  File                      Description
  ------------------------- -------------------------------------------------
  **DotsGame**              Main controller for Dots & Boxes. Handles turns,
                            edge claiming, box completion, scoring, and
                            per-session stats.

  **DotsAndBoxesFactory**   Factory that builds a ready-to-play Dots & Boxes
                            instance.

  **DotsState**             Holds grid configuration, player data, and
                            ownership arrays for H/V edges.

  **DotsRules**             Validates moves and determines when a box is
                            completed.

  **DotsRenderer /          Draws the board in ASCII or with ANSI colors to
  DotsRendererAnsi**        differentiate players.

  **EdgePos / Orientation / Represent edges and directions in the grid.
  Side / ClaimEdge**        

  **BoxPiece**              Represents a claimed box (with owning player).

  **PlayerInfo /            Maintain per-player info and track win/loss/tie
  SessionStats**            stats.

  **EdgeUtils**             Lists available edges and highlights those that
                            would complete a box.
  ---------------------------------------------------------------------------
------------------------------------------------------------------------

## 💡 Example I/O

### Game Selection

    Select a game:
    1) Sliding Puzzle
    2) Dots & Boxes
    3) Quit
    Choice: 2

------------------------------------------------------------------------

### Dots & Boxes Menu

    === Dots & Boxes ===
    1) Play
    2) Rules
    3) High Scores
    4) Back
    Choice: 1
    Boxes rows (>=1) [3]: 2
    Boxes cols (>=1) [3]: 2
    Player 1 name [Player1]: alice
    Player 2 name [Player2]: bob

------------------------------------------------------------------------

### Gameplay Example

    INPUT (strict):
      H r c   or   V r c
      Commands: avail | edges | rules | q

    Box coords: rows 0..1, cols 0..1
    .   .   .
             
    .   .   .
             
    .   .   .

------------------------------------------------------------------------

### Sample Moves

    bob (B) move [H r c | V r c | avail | edges | rules | q]: V 1 0

    .---.   .
    |        
    .---.   .
    |        
    .   .   .

    alice (A) move [H r c | V r c | avail | edges | rules | q]: V 0 1

    .---.   .
    |   |    
    .---.   .
    |        
    .   .   .

------------------------------------------------------------------------

### Checking Available Edges

    b (B) move [H r c | V r c | avail | edges | rules | q]: avail

    Available edges (showing 5 of 12):
      H/V move  | Note
      --------- | ----
      H 0 0     |
      H 0 1     | closes a box
      V 1 0     |
      V 2 0     | closes a box
      V 0 2     |

------------------------------------------------------------------------

### Winning Move

    b (B) move [H r c | V r c | avail | edges | rules | q]: V 0 0

    .---.
    |   |
    .---.

    Score: a=0, b=1
    Winner: b

------------------------------------------------------------------------

## 🧠 Design Documentation

-   **Scalability:**\
    The shared `Game`, `Rules`, `Renderer`, and `Factory` interfaces let
    new games plug in easily.\
-   **Extendibility:**\
    Both games reuse the same console framework and player/session
    management.\
-   **Readability & Best Practices:**\
    Clear file headers, method comments, and strict separation of game
    logic, state, and view.\
-   **Usability:**\
    Player menus, color-coded boards, rules menu, and high-score
    tracking improve UX.\
-   **Concise Main:**\
    `Main` only registers and launches games --- no internal game logic.

------------------------------------------------------------------------

## 🏆 Bonus Features

-   ANSI-colored boxes and edges for player distinction.\
-   `avail` command lists unclaimed edges and marks those that would
    close a box.\
-   Session statistics persist during runtime.\
-   Difficulty selector for Sliding Puzzle.\
-   Rules and high-score menus for both games.
