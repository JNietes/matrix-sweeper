import java.util.*;

public class MatrixSweeper {
   public static void main(String[] args) {
      Scanner input = new Scanner(System.in);

      System.out.println("____----Matrix Mine Sweeper----____");
      System.out.println("\nEnter a coordinate \"column row\" to \ndig the ground.\n"); // Column, row is intuitive for the user (x, y)
      System.out.println("To flag a mine, enter \"f\" and the \ncoordinate\n");         // Row, column is intuitive for matrices
      System.out.println("To quit, enter \"exit\"\n");

      // My solution to prevent 8 if statements when checking adj elements on a matrix
      int[][] coolMatrix = {{1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}};

      int digs = 0;
      int totalMines = 40; // Can modify
      int flagsLeft = totalMines;
      int minesFlagged = 0;
      char[][] topBoard = createTopBoard(16, 16); // Can modify
      char[][] mineBoard = createMineBoard(topBoard.length, topBoard[0].length, totalMines);
      char[][] heatmap = createHeatmap(mineBoard, coolMatrix);
      boolean gameOn = true;
      int padLen;

      // Main loop
      while (gameOn) {
         //printBoard(heatmap);
         padLen = (topBoard.length) - (7 + String.valueOf(flagsLeft).length());
         printBoard(topBoard, flagsLeft, padLen, ":)");
         System.out.print(">>> ");
         String userInput = input.nextLine();
         System.out.println();
         String[] inputs = userInput.split(" ");

         // Flagging Mines
         if (inputs[0].equals("f")) {
            System.out.print(">>> f: ");
            inputs = input.nextLine().split(" ");
            while (!validCoordinate(topBoard, inputs)) {
               System.out.println("Please use the syntax \"column row\"");
               System.out.print(">>> f: ");
               inputs = input.nextLine().split(" ");
               System.out.println();
            }
            if (topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == ' ') {
               if (heatmap[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X')
                  minesFlagged++;
               flagsLeft--;
               topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] = 'f';
            }
            else if (topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'f') {
               if (heatmap[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X') {
                  minesFlagged--;
               }
               flagsLeft++;
               topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] = ' ';
            }

            // Victory
            if (minesFlagged == totalMines) {
               printBoard(topBoard, flagsLeft, padLen, ":D");
               break;
            }
            continue;
         }

         // Digging
         if (topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == ' ') {
            if (validCoordinate(topBoard, inputs) && digs == 0)
               firstDig(topBoard, mineBoard, heatmap, coolMatrix, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
            else
               digHere(topBoard, heatmap, coolMatrix, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
            digs++;
         }
         else
            System.out.println("Cannot dig on flag");

         // Game Over
         if (heatmap[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X' && topBoard[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] != 'f' && digs != 1) {
            showMines(topBoard, heatmap);
            printBoard(topBoard, flagsLeft, padLen, "X(");
            gameOn = false;
         }
         if (userInput.equals("exit"))
            gameOn = false;
      }
      System.out.println("       Thanks for playing!");
   }

   public static void showMines(char[][] top, char[][] heat) {
      for (int row=0; row<top.length; row++) {
         for (int column=0; column<top[0].length; column++) {
            if (heat[row][column] == 'X')
               top[row][column] = 'X';
         }
      }
   }

   // Uses recursion to uncover blobs of empty space
   public static void uncoverAdjTiles(char[][] top, char[][] heat, int[][] adjTiles, int row, int column) {
      for (int i=0; i<8; i++) {
         if ((isValidSpace(heat, row+adjTiles[i][0], column+adjTiles[i][1])) && top[row+adjTiles[i][0]][column+adjTiles[i][1]] == ' ') {
            uncoverTile(top, heat, row+adjTiles[i][0], column+adjTiles[i][1]);
            if (heat[row+adjTiles[i][0]][column+adjTiles[i][1]] == '0') {
               uncoverAdjTiles(top, heat, adjTiles, row+adjTiles[i][0], column+adjTiles[i][1]);
            }
         }
      }
   }

   public static void digHere(char[][] top, char[][] heat, int[][] adjTiles, int row, int column) {
      uncoverTile(top, heat, row, column);
      if (heat[row][column] == '0')
         uncoverAdjTiles(top, heat, adjTiles, row, column);
   }

   public static void uncoverTile(char[][] top, char[][] heat, int row, int column) {
      top[row][column] = heat[row][column];
   }

   // Ensures the first dig is not a mine
   public static void firstDig(char[][] top, char[][] mines, char[][] heat, int[][] adjTiles, int row, int column) {
      while (heat[row][column] == 'X') {
         shuffleMatrix(mines);
         System.out.println("Creating another heatmap");
         heat = createHeatmap(mines, adjTiles);
      }
      digHere(top, heat, adjTiles, row, column);

   }

   public static char[][] createTopBoard(int rowSize, int columnSize) {
      char[][] top = new char[rowSize][columnSize];
      for (int row=0; row<rowSize; row++) {
         for (int column=0; column<columnSize; column++) {
            top[row][column] = ' ';
         }
      }
      return top;
   }

   public static char[][] createMineBoard(int row, int column, int mines) {
      char[][] mineBoard = new char[row][column];
      placeMines(mineBoard, mines);
      shuffleMatrix(mineBoard);
      return mineBoard;
   }

   public static boolean digitsOnly(String str) {
      for (int i=0; i<str.length(); i++)
         if (!Character.isDigit(str.charAt(i))){
            return true;
         }
      return false;
   }

   public static boolean validCoordinate(char[][] mat, String[] array) {
      if (array.length !=2) {
         System.out.println("Not two elements");
         return false;
      }
      if (digitsOnly(array[0]) || digitsOnly(array[1])) {
         System.out.println("Digits only");
         return false;
      }
      if (!isValidSpace(mat, Integer.parseInt(array[0]), Integer.parseInt(array[1]))) {
         System.out.println("Not in board");
         return false;
      }
      return true;
   }

   public static boolean isValidSpace(char[][] mat, int row, int column) {
      if (row<0 || row>mat.length-1) {
         return false;
      }
      else return column >= 0 && column <= mat[row].length - 1;
   }

   public static char[][] createHeatmap(char[][] mat, int[][] adjTiles) {
      char[][] heatMap = new char[mat.length][mat[0].length];
      for (int row=0; row<mat.length; row++) {
         for (int column=0; column<mat[row].length; column++) {
            char adjMines = '0';
            if ( mat[row][column]!='X') {
               for (int i=0; i<8; i++) {
                  if ((isValidSpace(mat, row+adjTiles[i][0], column+adjTiles[i][1])) && mat[row+adjTiles[i][0]][column+adjTiles[i][1]]=='X')
                     adjMines++;
               }
            }
            else
               adjMines = 'X';
            heatMap[row][column] = adjMines;
         }
      }
      return heatMap;
   }

   // Fisher-Yates, but with a matrix
   public static void shuffleMatrix(char[][] mat) {
      for (int row=mat.length-1; row>0; row--) {
         for (int column=mat[row].length-1; column>0; column--) {
            int rowIndex = (int)(Math.random() * row);
            int columnIndex = (int)(Math.random() * column);

            // Simple swap
            char a = mat[rowIndex][columnIndex];
            mat[rowIndex][columnIndex] = mat[row][column];
            mat[row][column] = a;
         }
      }
   }

   public static void placeMines(char[][] board, int mines) {
      int mineSpacing = (board.length * board[0].length) / mines;
      int total=0;
      for (int row=0; row<board.length; row++) {
         for (int column=0; column<board[row].length; column=column+mineSpacing) {
            if (total<mines) {
               board[row][column] = 'X';
               total++;
            }
         }
      }
   }

   // This would have been repeated three times in main loop
   public static void printBoard(char[][] board, int flags, int pad, String face) {
      System.out.printf("Flags: " + flags + " ");
      for (int i=0; i<pad; i++)
         System.out.print(" ");
      System.out.println(face);
      printBoard(board);
   }

   public static void printBoard(char[][] board) {
      for (int row=board.length-1; row>=0; row--) {
         System.out.printf("%2s", row);
         for (int column=0; column<board[row].length; column++) {
            System.out.print("|" + board[row][column]);
         }
         System.out.println("|");
      }
      int xSpaces = board[0].length;
      for (int xLabelHeight=String.valueOf(board[0].length).length(); xLabelHeight>0; xLabelHeight--) {
         System.out.print("   ");
         for (int xLabel=0; xLabel<xSpaces; xLabel++) {
            System.out.print((xLabel / (int)Math.pow(10, xLabelHeight-1)) % 10  + " ");
         }
         System.out.println();
      }
      System.out.println();
   }
}