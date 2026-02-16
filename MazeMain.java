public class MazeMain {
    public static void main(String[] args){

        //2D array. 0 means open and 1 means walls.
        int[][] mazeGrid = {
                {0,1,1,1,0,0,0,1,1},
                {0,0,0,0,1,0,0,0,0},
                {1,0,1,0,1,0,1,0,1},
                {0,1,1,0,1,0,0,1,1},
                {0,1,0,1,1,1,0,1,1},
                {1,1,1,0,0,1,1,0,1}
        };
        //Creates the Maze object, Maze class stores and manages the maze.
        Maze labyrinth = new Maze(mazeGrid);
        //Creates the solver.
        MazeSolver solver =  new MazeSolver(labyrinth);

        //Starts recursion at position 0.
        if (solver.traverse(0,0))
            System.out.println("Solution found");//Returns true
        else
            System.out.println("Can't solve maze");//Returns false
        //Prints the maze after solving it showing the path marks.
        System.out.println(labyrinth);
    }

    //Inner class to represent the maze
    static class Maze {

        //Fixed constants.
        private static final int OPEN = 0;
        private static final int TRIED = 2;
        private static final int PATH = 3;

        public int[][] grid; //Holds the maze
        public int endRow, endColumn; //Stores last position

        //Constructor - runs when Maze is created.
        public Maze(int[][] gridInput){
            grid = gridInput; //Save the maze into the object - Line 14

            endRow = grid.length - 1;
            endColumn = grid[0].length - 1;
        }
        //Method to mark position as visited - position can fail.
        public void tryPosition(int row, int col) {
            grid[row][col] = TRIED;
        }
        //BASE CASE - Checks if it reached the exit.
        public boolean solved(int row, int col) {
            //returns true only if row & col are both the last.
            return (row == endRow && col == endColumn);
        }
        //Method to mark the VALID path
        public void markPath(int row, int col) {

            grid[row][col] = PATH;
        }
        //If all true, it's a valid move
        public boolean validPosition(int row, int col) {
            return row >= 0 && row < grid.length &&
                    col >= 0 && col < grid[0].length &&
                    grid[row][col] == OPEN;
        }
        //Prints the maze
        public String toString() {
            String result = "\n";

            for (int row = 0; row < grid.length; row++) {
                for (int column = 0; column < grid[row].length; column++)
                    result += grid[row][column] + "";
                result += "\n";
            }
            return result;
        }
    }
    //Inner class to solve the maze
    static class MazeSolver{
        private final Maze maze; //Stores the final result.

        public MazeSolver(Maze maze){
            this.maze = maze;
        }
        //Recursive Method
        public boolean traverse(int row, int column){
            boolean done = false;
            if (maze.validPosition(row, column)){ //legal position
                maze.tryPosition(row, column); //already tried path
                if (maze.solved(row, column)){ //base case - exit reached
                    done = true; //Solution found

                //if not start searching
                } else{
                    if (!done)
                        done = traverse(row-1,column); //up
                    if (!done)
                        done = traverse(row,column-1); //left
                    if (!done)
                        done = traverse(row+1,column); // down
                    if (!done)
                        done = traverse(row,column+1);//right
                }
                if (done)
                    maze.markPath(row,column); //show solution path
            }
            return done;
        }
    }
}
