# SudokuSolver

`SudokuSolver` is a Java Swing application that solves a 9x9 Sudoku puzzle using the backtracking algorithm and displays the completed grid on screen.

## Features

- Enter a custom Sudoku puzzle in a 9x9 grid
- Solve the puzzle automatically using backtracking
- Validate the puzzle before solving
- Display solved cells in the grid
- Clear the board and start again
- Load a sample Sudoku puzzle for testing

## Technologies Used

- Java
- Java Swing
- AWT Graphics for custom UI design

## Project File

- `SudokuSolver.java` - main source code file

## How to Run

1. Make sure Java is installed on your system.
2. Open a terminal in the project folder.
3. Compile the program:

```bash
javac SudokuSolver.java
```

4. Run the program:

```bash
java SudokuSolver
```

## How to Use

1. Enter the Sudoku puzzle values in the grid.
2. Leave empty cells blank.
3. Click `SOLVE` to solve the puzzle.
4. Click `CLEAR` to reset the board.
5. Click `SAMPLE` to load a sample puzzle.

## Algorithm Used

- The program uses the backtracking algorithm.
- It checks whether a number is safe in a row, column, and 3x3 box.
- If a number does not work, it backtracks and tries the next possible number.

## Validation

- The program checks for duplicate numbers before solving.
- If the puzzle is invalid, it shows an error message.
- If no solution exists, it informs the user.

## Learning Outcome

This project helps practice:

- Java Swing GUI development
- Backtracking algorithm
- 2D array handling
- Input validation
- Problem solving with recursion

## Author

Created as a Java mini project for practice and learning.
