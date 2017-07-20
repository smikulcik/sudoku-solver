import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class SudokuRunner {
    public static void main(String[] args) {
    	int[][] board = {
			{0,0,5,6,0,7,0,0,0},
			{0,7,9,0,0,0,0,0,0},
			{0,0,0,5,0,4,0,0,6},
			{5,0,0,0,0,0,0,2,0},
			{0,0,0,0,0,3,0,0,0},
			{2,9,0,0,0,0,1,0,0},
			{0,4,3,1,0,0,0,0,0},
			{0,0,0,0,0,0,8,0,1},
			{0,8,0,0,4,0,0,7,9}
    	};
    	Sudoku s= new Sudoku(board);
    	s.solve();
    	s.printBoard();
    	
    	for(int i=0;i<50; i++){
	        Sudoku puzzle = new Sudoku();
	        puzzle.printBoard();
	        puzzle.solve();
	        puzzle.printBoard();
	        System.out.println(i);
    	}
    }
}

class Sudoku {
    public int[][] board;
    public int[][][] allowed;

    public Sudoku(Sudoku s){
        this.board = new int[9][9];
        this.allowed = new int[9][9][9]; //1 means disallowed, 0 means allowed
        this.set(s);
    }
    public Sudoku(int[][] board){
        this.board = board;
        this.allowed = new int[9][9][9]; //1 means disallowed, 0 means allowed
        }
    
    public Sudoku(){
        this.board = new int[9][9];
        this.allowed = new int[9][9][9]; //1 means disallowed, 0 means allowed
        //grab puzzle from davidbau.com/generated/sudoku.txt
        try {
        URL yahoo = new URL("http://davidbau.com/generated/sudoku.txt");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        populateBoardFromString(in);
        in.close();
        } catch(Exception e) {
            
        }
    }
    
    private void populateBoardFromString(BufferedReader br){
        Scanner s = new Scanner(br);
        int row = 0;
        int col = 0;
        while(s.hasNextLine()){
            String nextLine = s.nextLine();
            if(nextLine.length() >= 37 && nextLine.charAt(0) == '|' && !nextLine.contains("+")){
                for(int i = 2; i < 37 ; i+= 4){
                  col = (i-2)/4;
                  if(nextLine.charAt(i) == ' ')
                      board[row][col] = 0;
                  else
                      board[row][col] = nextLine.charAt(i) - '0';
                }
                row++;
            }
        }
    }
    
    public void set(Sudoku s){
        for(int r = 0; r<9; r++)
            for(int c = 0; c<9;c++){
                board[r][c] = s.board[r][c];
		        for(int v = 0; v<9; v++)
		            allowed[r][c][v] = s.allowed[r][c][v];
            }
    }
    
    public boolean solve(){
        if(isStuck())
            return false;
        while(simpleSolve() > 0){}
        if(!isSolved())
            backtrackSolve();
        return !isStuck();
    }
    
    public boolean isSolved(){
        for(int r = 0; r<9; r++)
            for(int c = 0; c<9;c++)
                if(board[r][c] == 0)
                    return false;
        return true;
    }
    
    public boolean isStuck(){
        for(int r = 0; r<9; r++)
            for(int c = 0; c<9;c++){
                int numAllowed = 0;
                for(int v=0; v<9;v++)
                    if(allowed[r][c][v] == 0)
                        numAllowed++;
                if(numAllowed == 0)
                    return true;
            }
        return false;
    }
    
    public void backtrackSolve(){
        for(int r = 0; r<9; r++)
            for(int c = 0; c<9;c++)
                for(int v=0; v<9;v++)
                    if(board[r][c] == 0 && allowed[r][c][v] == 0){
                    	System.out.println("BT: put "+(v+1) +" row:"+(r+1)+" col:"+(c+1));
                        Sudoku test = new Sudoku(this);
                        test.board[r][c] = v+1;
                        if(test.solve())
                            this.set(test);
                    }
    }
    
    /**
     * Go through one iteration of check each row, col, sub-square
     * for single element, and put in easy ones
     */
    public int simpleSolve(){
        int numMoves = 0; //number of moves done in this iteration
        
        //Check each row for an element to put in
        int zeroRow = -1;
        int zeroCol = -1;
        
        //by row
        for(int row = 0; row < board.length; row++){
            ArrayList<Integer> allElem = new ArrayList<Integer>();
            for(int i=0; i < board[row].length; i++)
                allElem.add(0);
            int numEmpty = 0;
            for(int col = 0; col < board[row].length; col++)
                if(board[row][col] == 0){
                    zeroCol = col;
                    numEmpty++;
                }else{
                    allElem.set(board[row][col]-1, 1);
                    for(int colI = 0; colI< board[row].length; colI++)
                        if(board[row][colI] == 0)
                            allowed[row][colI][board[row][col]-1] = 1;
                }
            if(numEmpty == 1){
                //put other element in spot
                int missingNo;
                missingNo = allElem.indexOf(0);
                board[row][zeroCol] = missingNo+1;
                numMoves++;
            }
        }
        
        
        //by col
        for(int col = 0; col < board[0].length; col++){
            ArrayList<Integer> allElem = new ArrayList<Integer>();
            for(int i=0; i < board[0].length; i++)
                allElem.add(0);
            int numEmpty = 0;
            for(int row = 0; row < board.length; row++)
                if(board[row][col] == 0){
                    zeroRow = row;
                    numEmpty++;
                }else{
                    allElem.set(board[row][col]-1, 1);
                    for(int rowI = 0; rowI< board.length; rowI++)
                        if(board[rowI][col] == 0)
                            allowed[rowI][col][board[row][col]-1] = 1;
                }
            if(numEmpty == 1){
                //put other element in spot
                int missingNo;
                missingNo = allElem.indexOf(0);
                board[zeroRow][col] = missingNo+1;
                numMoves++;
            }
        }
        
        //by sub-square
        for(int blockRow = 0; blockRow<3; blockRow++){
            for(int blockCol = 0; blockCol<3; blockCol++){
                //for a single sub-square
                ArrayList<Integer> allElem = new ArrayList<Integer>();
                for(int i=0; i < board[0].length; i++)
                    allElem.add(0);
                int numEmpty = 0;
                for(int row = blockRow*3; row < blockRow*3+3; row++)
                    for(int col = blockCol*3; col < blockCol*3+3; col++)
                        if(board[row][col] == 0){
                            zeroRow = row;
                            zeroCol = col;
                            numEmpty++;
                        }else{
                            allElem.set(board[row][col]-1, 1);
                            for(int rowI = blockRow*3; rowI < blockRow*3+3; rowI++)
                                for(int colI = blockCol*3; colI < blockCol*3+3; colI++)
                                    if(board[rowI][colI] == 0)
                                        allowed[rowI][colI][board[row][col]-1] = 1;
                        }
                if(numEmpty == 1){
                    //put other element in spot
                    int missingNo;
                    missingNo = allElem.indexOf(0);
                    board[zeroRow][zeroCol] = missingNo+1;
                    numMoves++;
                }
            }
        }
        
        //find those with only one allowed value
        for(int row=0; row<9; row++)
            for(int col=0; col<9; col++)
                if(board[row][col] == 0){
                    int numAllowedValues = 0, aValue = -1, aRow = -1,aCol = -1;
                    for(int value=0; value<9; value++)
                        if(allowed[row][col][value] == 0){
                            numAllowedValues++;
                            aValue = value+1;
                            aRow = row;
                            aCol = col;
                        }
                    if(numAllowedValues == 1){
                        board[aRow][aCol] = aValue;
                        numMoves++;
                    }
                }
        return numMoves;
    }
    
    public void printBoard(){
        for(int row = 0; row< board.length; row++){
            if(row == 3 || row == 6){
                for(int i = 0; i < board[row].length + 2; i++)
                    System.out.print("-");
                System.out.println();
            }
            for(int col = 0; col < board[row].length; col++){
                if(col == 3 || col == 6)
                    System.out.print("|");
                if(board[row][col] == 0)
                    System.out.print(" ");
                else
                    System.out.print(board[row][col]);
            }
            System.out.println();
        }
    }
}
