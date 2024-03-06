public class MatrixSweeperBoard {
    int minesFlagged;
    int flagsLeft;
    int totalMines;
    int[][] coolMatrix = {{1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}};
    char[][] topBoard;
    char[][] mineBoard;
    char[][] heatmap;

    MatrixSweeperBoard(){
        int rows = 16;
        int columns = 30;
        int totalMines = 99;
        mineBoard = createMineBoard(rows, columns, totalMines);
        heatmap = createHeatmap(mineBoard, coolMatrix);
        topBoard = createTopBoard(rows, columns);

    }

    MatrixSweeperBoard(int rowSize, int columnSize, int mines){
        topBoard = createTopBoard(rowSize, columnSize);
        mineBoard = createMineBoard(rowSize, columnSize, mines);
        heatmap = createHeatmap(mineBoard, coolMatrix);
    }

    public char[][] getTopBoard() {
        return topBoard;
    }
    public char[][] getMineBoard() {
        return mineBoard;
    }
    public char[][] getHeatmap() {
        return heatmap;
    }
    public int getMinesFlagged() { return minesFlagged; }
    public int getTotalMines() { return totalMines; }
    public void createNewHeatmap() {
        shuffleMatrix(mineBoard);
        heatmap = createHeatmap(mineBoard, coolMatrix);
    }

    public void flagMineAt(int column, int row) {
        if (topBoard[row][column] == ' ') {
            if (heatmap[row][column] == 'X')
                minesFlagged++;
            flagsLeft--;
            topBoard[row][column] = 'f';
        }
        else if (topBoard[row][column] == 'f') {
            if (heatmap[row][column] == 'X') {
                minesFlagged--;
            }
            flagsLeft++;
            topBoard[row][column] = ' ';
        }
    }

    public void showMines(char[][] top, char[][] heat) {
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

    public void digHere(char[][] top, char[][] heat, int[][] adjTiles, int row, int column) {
        uncoverTile(top, heat, row, column);
        if (heat[row][column] == '0')
            uncoverAdjTiles(top, heat, adjTiles, row, column);
    }

    public static void uncoverTile(char[][] top, char[][] heat, int row, int column) {
        top[row][column] = heat[row][column];
    }

    // Ensures the first dig is not a mine
    public void firstDig(char[][] top, char[][] mines, char[][] heat, int[][] adjTiles, int row, int column) {
        while (heat[row][column] == 'X') {
            shuffleMatrix(mines);
            //System.out.println("Creating another heatmap");
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

    public static boolean isValidSpace(char[][] mat, int row, int column) {
        if (row<0 || row>mat.length-1) {
            return false;
        }
        else return column >= 0 && column <= mat[row].length - 1;
    }

    public void printBoard(char[][] board, int flags, int pad, String face) {
        System.out.printf("Flags: " + flags + " ");
        for (int i=0; i<pad; i++)
            System.out.print(" ");
        System.out.println(face);
        printBoard(board);
    }

    private void printBoard(char[][] board) {
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
