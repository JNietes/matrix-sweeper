import java.util.*;

public class MatrixSweeper {
   public static void main(String[] args) {
      Scanner input = new Scanner(System.in);

      printHelp();

      // My solution to prevent 8 if statements when checking adj elements on a matrix
      int[][] coolMatrix = {{1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}};

      MatrixSweeperBoard board = new MatrixSweeperBoard();
      boolean firstDig = true;
      int padLen;

      // Main loop
      while (true) {
         padLen = (board.getTopBoard()[0].length) - (7 + String.valueOf(board.getFlagsLeft()).length());
         board.printBoard(board.getTopBoard(), padLen, ":)");
         System.out.print(">>> ");
         String userInput = input.nextLine();
         System.out.println();
         String[] inputs = userInput.split(" ");
         try {
            if (inputs[0].equals("help")) {
               printHelp();
               continue;
            } else if (inputs[0].equals("exit")) {
               break;
            }

            // Hard coded difficulties
            // Creating a board object would make the cases much shorter. board = new MatrixSweeperBoard(rows, columns, mines)
            if (inputs[0].equals("difficulty")) {
               System.out.print(">>> difficulty: ");
               inputs = input.nextLine().split(" ");
               switch (inputs[0]) {
                  case "beginner" -> {
                     firstDig = true;
                     board = new MatrixSweeperBoard(9, 9, 10);
                  }
                  case "intermediate" -> {
                     firstDig = true;
                     board = new MatrixSweeperBoard(16, 16, 40);
                  }
                  case "expert" -> {
                     firstDig = true;
                     board = new MatrixSweeperBoard(16, 30, 99);
                  }
                  case "custom" -> {
                     System.out.print(">>> enter \"column\" \"rows\" \"mines\": ");
                     inputs = input.nextLine().split(" ");
                     firstDig = true;
                     board = new MatrixSweeperBoard(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]), Integer.parseInt(inputs[2]));
                  }
                  default -> System.out.println(">>> enter \"help\" for difficulties");
               }

               continue;
            }

            // Flagging Mines
            if (inputs[0].equals("f") && inputs.length == 3) {
               String[] coords = {inputs[1], inputs[2]};
               while (!validCoordinate(board.getTopBoard(), coords)) {
                  System.out.println("Please use the syntax \"f column row\"");
                  System.out.print(">>> f: ");
                  System.out.println();
               }
               board.flagMineAt(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

               // Victory
               if (board.getMinesFlagged() == board.getTotalMines()) {
                  board.printBoard(board.getTopBoard(), padLen, ":D");
                  break;
               }
               continue;
            }

            // Digging
            if (board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == ' ') {
               if (validCoordinate(board.getTopBoard(), inputs) && firstDig) {
                  board.firstDig(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
               } else
                  board.digHere(board.getTopBoard(), board.getHeatmap(), coolMatrix, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
               firstDig = false;
            } else
               System.out.println("Cannot dig on flag");

            // Game Over
            if (board.getHeatmap()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X' && board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] != 'f' && !firstDig) {
               board.showMines(board.getTopBoard(), board.getHeatmap());
               board.printBoard(board.getTopBoard(), padLen, "X(");
               System.out.print("Keep playing? \"yes\" or \"no\": ");
               if (input.nextLine().equals("yes")) {
                  firstDig = true;
                  board = new MatrixSweeperBoard();
               } else
                  break;
            }
         }
         catch (Exception e){
            System.out.println(">>> Invalid Syntax");
         }
      }
      System.out.println("Thanks for playing!");
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
      if (!isValidSpace(mat, Integer.parseInt(array[1]), Integer.parseInt(array[0]))) { // column row
         System.out.println("Not in board");
         return false;
      }
      return true;
   }

   public static boolean digitsOnly(String str) {
      for (int i=0; i<str.length(); i++)
         if (!Character.isDigit(str.charAt(i))){
            return true;
         }
      return false;
   }

   public static boolean isValidSpace(char[][] mat, int row, int column) {
      if (row<0 || row>mat.length-1) {
         return false;
      }
      else return column >= 0 && column <= mat[row].length - 1;
   }

   public static void printHelp() {
      System.out.println(  "___---Matrix Mine Sweeper Commands---___\n\n" +
                           "To dig, enter a coordinate \"column row\"\n\n" +// Column, row is intuitive for the user (x, y)
                           "To flag a mine, enter \"f column row\"\n\n"   +       // Row, column is intuitive for matrices
                           "To change the difficulty, enter \"difficulty\",\n" +
                           "then \"beginner\", \"intermediate\", \"expert\", or \"custom\"\n\n" +
                           "To reprint these commands, enter \"help\"\n\n" +
                           "To quit, enter \"exit\"\n");
   }
}