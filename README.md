# Matrix Sweeper
A version of minesweeper that uses characters instead of a gui. 
Missing features: timer, question mark

## How to Play
Enter coordinated with the syntax: x y
To place a flag, enter the character f, then enter a coordinate.
To exit the program, enter the string: exit

## How to Run
Install [java](https://www.java.com/en/download/)

To compile the program, open a terminal where the `MatrixSweeper.java` file is located. Then, type the following command:

```
javac MatrixSweeper.java
```

To execute the program, type the following command:

```
java MatrixSweeper
```

## Bugs
(fixed) If the first dig is a mine, the heatMap reference variable is not updated to the new matrix for a heatmap created in the firstDig method.
