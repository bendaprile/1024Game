package Project2;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
* NumberGameArrayList houses the logic for modifying 1024 game values.
 *
* @author Benjamin D'Aprile and Justin Knapp
* @version Fall 2018
* */
public class NumberGameArrayList implements NumberSlider {

    /**
     * The following are variable used throughout
     * NumberGameArrayList
     */
    private ArrayList<Cell> Tiles; // arrayList for housing all the current non zero board pieces
    private ArrayList<Cell> Temp; //arrayList used for sorting
    private ArrayList<Cell> TempCompare; // arrayList used for sorting
    private int score; // this games score
    private int highScore; //this sessions highScore
    private int[] scores; //array to store each moves score
    private int loc = 0; // location of current move in scores array
    private int height = 4; //default height of board
    private int width = 4; //default width of board
    private int  winningValue = 1024; //default tile needed to win
    private int[][] grid; // a 2d array for displaying and updating the board
    private Stack<int[][]> moves; // a stack for storing past boards

    /**********************************
     * public getter method for height
     *
     * @return height
     **********************************/
    public int getHeight() {
        return height;
    }

    /**********************************
     * public getter method for width
     *
     * @return width
     **********************************/
    public int getWidth() {
        return width;
    }

    /**********************************
     * public getter method for score
     *
     * @return score
     **********************************/
    public int getScore() {
        return score;
    }

    /****************************************
     * private setter method for score
     *
     * @param score used to update the score
     ****************************************/
    private void setScore(int score) {
        this.score = score;
    }

    /****************************************
     * private setter method for height
     *
     * @param height new desired board height
     *****************************************/
    private void setHeight(int height) {
        this.height = height;
    }

    /********************************************
     * private setter method for width
     * @param width the new desired board width
     *******************************************/
    private void setWidth(int width) {
        this.width = width;
    }

    /***************************************************************
     * public setter that changes the value needed to win.
     *
     * @param winningValue an int with the new desired winning value
     ****************************************************************/
    public void setWinningValue(int winningValue) {
        this.winningValue = winningValue;
    }

    @Override
    public int[][] getGrid(){
        return grid;
    }

    /*********************************************************************
     * Resize board does everything but reset the game back to normal
     *
     * @param height the number of rows in the board
     * @param width the number of columns in the board
     * @param winningValue the value that must appear on the board to
     *                     win the game
     *******************************************************************/
    @Override
    public void resizeBoard (int height, int width, int winningValue) {
        setWinningValue(winningValue); //sets the winning value
        setHeight(height); //changes the height
        setWidth(width); //changes the width
        scores = new int[500];// creates the score array with 500 spots.
        grid = new int[height][width];//creates a new grid
        Tiles = new ArrayList<>(); //creates all the arraylists.
        TempCompare  = new ArrayList<>();
        Temp = new ArrayList<>();
        moves = new Stack<>();//creates a new stack
    }

    @Override
    public void reset() {
    while(!(moves.empty())) {
        moves.pop();
    }
    for(int i = 0; i < scores.length; i++)
    {
        scores[i] = 0;
    }

        score = 0;
        Tiles.clear();
        eraseGrid();
        for(int i = 0; i < 2; i++){
            Tiles.add(placeRandomValue());
        }
        sortTiles();
    }

    /* The setValues() method is needed for testing */
    @Override
    public void setValues (final int[][] input) {
        for (int rcurr = 0; rcurr < height; rcurr++)
            for (int ccurr = 0; ccurr < width; ccurr++)
            {
                grid[rcurr][ccurr] = input[rcurr][ccurr];
            }
        loadTilesFromGrid();


    }

    @Override
    public Cell placeRandomValue() {
        int r, c;
        boolean foundEmpty = false;

        for (int[] row : grid)
            for (int x : row)
                if (x == 0) {
                    foundEmpty = true;
                    break;
                }
        if (!foundEmpty)
            throw new IllegalStateException("No");
        do {
            r = (int)(Math.random() * grid.length );
            c = (int)(Math.random() * grid[0].length );
        }while (grid[r][c] != 0);
        int n = (int)(Math.random() * 2 + 1);
        n = n * 2;

        grid[r][c] = n;
        Cell val = new Cell();
        val.row = r;
        val.column = c;
        val.value = grid[r][c];
        Tiles.add(val);
        return val;
    }

    /*************************************************
     * Slide handles the broad logic of moving a tile
     *
     * @param dir move direction of the tiles
     *
     * @return moveMade if a valid move was made
     **************************************************8*/
    @Override
    public boolean slide(SlideDirection dir) {

        boolean moveMade = false;//resets moveMade to false to guard against error

        //Copies Grid over to Array
        int[][] gradX = new int[grid.length][grid[0].length];
        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[0].length; c++)
                gradX[r][c] = grid[r][c];


        moves.push(gradX);//puts the newest gradX onto the top of the stack
        scores[loc] = getScore();//takes the current score and saves it in score at number of slides
        loc++;//increments location of scores up one

        /*switch case statement that uses dir to decide which move to make.
         depending on the passed direction, a method will be called to move the values in tiles
         the methods return true if a move was made and false if not.
        */
        switch (dir) {
            case UP:
                moveMade = slideUp();
                break;
            case DOWN:
                moveMade = slideDown();
                break;
            case LEFT:
                moveMade = slideLeft();
                break;
            case RIGHT:
                moveMade = slideRight();
                break;
        }


        refreshGrid();//empties and refills grid
        /*if a move was made during the switch case, place a random value.
        if the score updated inside of the methods called during switch case is higher
        the high score, set the highScore to score*/
        if(moveMade){
            placeRandomValue();
            if(score >= highScore)
            {
                highScore = score;
            }
        }

        return moveMade;//returns if a valid move was made during this method.
    }

    private boolean slideUp(){
        boolean wasMoveMade = false; //error protection
        ArrayList<Cell> NewTiles = new ArrayList<>(); //creates a spot to store the old board
        for(int c = 0; c < width; c++)//goes through all the columns
        {
            Temp.clear();//clear the temp arraylist
            boolean isSorted = false; //error protection before while
            boolean slid = false;//error protection before while
            //fills a temp array list with the values from only one column.
            for(int curr = 0; curr < Tiles.size(); curr++) {
                if (Tiles.get(curr).column == c) {
                    Temp.add(Tiles.get(curr));
                }
            }

            //sorts the arraylist by slot location
            while(!isSorted)
            {
                isSorted = true;
                for(int i = 0; i < Temp.size() - 1; i++ ){
                    if(Temp.get(i).row > Temp.get(i + 1).row)
                    {
                        isSorted = false;
                        Cell holder;
                        holder = Temp.get(i);
                        Temp.set(i,Temp.get(i + 1));
                        Temp.set(i + 1,holder);
                    }
                }

            }
            // slides all the tiles in the dir specified
            while(!slid)
            {
                slid = true;
                for(int r = 0; r < Temp.size(); r++)
                {
                    if(Temp.get(r).row != r)
                    {
                        wasMoveMade = true;
                        slid = false;
                        Temp.get(r).row = (Temp.get(r).row) - 1;
                    }
                }
            }

            //combining adjacent tiles if fits needs
            for(int i = 0; i < (Temp.size() - 1); i++)
            {
                if(Temp.get(i).value == Temp.get(i + 1).value)
                {
                    wasMoveMade = true; // let us know a move was made
                    Temp.get(i).value = Temp.get(i).value * 2; // combine the values of the bricks
                    score = score + Temp.get(i).value;// add the value of the bricks to score
                    Temp.remove(i + 1);//remove the old brick
                }
            }

            //sliding the new smaller arraylist to where its suppose to be
            slid = false;
            while(!slid)
            {
                slid = true;
                for(int i = 0; i < Temp.size(); i++)
                {
                    if(Temp.get(i).row != i)
                    {

                        slid = false;
                        Temp.get(i).row = (Temp.get(i).row) - 1;
                    }
                }
            }
            //add temp to an arraylist where the values will wait
            for(int i = 0; i < Temp.size(); i++)
            {
                NewTiles.add(Temp.get(i));
            }
        }//updates the tiles from the place where they waited
        Tiles.clear();
        for(int i = 0; i < NewTiles.size(); i++)
        {
            Tiles.add(NewTiles.get(i));
        }
        return wasMoveMade;//return if a move was made.
    }
    private boolean slideLeft(){
            boolean wasMoveMade = false;

            ArrayList<Cell> NewTiles = new ArrayList<>();
            for(int r = 0; r < height; r++)//goes through all the rows.
            {
                Temp.clear();//clear the temp arraylist
                boolean isSorted = false;
                boolean slid = false;
                for(int curr = 0; curr < Tiles.size(); curr++) {
                    if (Tiles.get(curr).row == r) {
                        Temp.add(Tiles.get(curr));
                    }
                }
                while(!isSorted)
                {
                    isSorted = true;
                    for(int i = 0; i < Temp.size() - 1; i++ ){
                       if(Temp.get(i).column > Temp.get(i + 1).column)
                       {
                          isSorted = false;
                           Cell holder;
                          holder = Temp.get(i);
                          Temp.set(i,Temp.get(i + 1));
                          Temp.set(i + 1,holder);
                       }
                    }

                }
                while(!slid)
                {
                    slid = true;
                    for(int i = 0; i < Temp.size(); i++)
                    {
                        if(Temp.get(i).column != i)
                        {
                            slid = false;
                            wasMoveMade = true;
                            Temp.get(i).column = (Temp.get(i).column) - 1;
                        }
                    }
                }
                for(int i = 0; i < (Temp.size() - 1); i++)
                {
                    if(Temp.get(i).value == Temp.get(i + 1).value)
                    {
                        wasMoveMade = true;
                        Temp.get(i).value = Temp.get(i).value * 2;
                        score = score + Temp.get(i).value;
                        Temp.remove(i + 1);
                    }
                }
                slid = false;
                while(!slid)
                {
                    slid = true;
                    for(int i = 0; i < Temp.size(); i++)
                    {
                        if(Temp.get(i).column != i)
                        {

                            slid = false;
                            Temp.get(i).column = (Temp.get(i).column) - 1;
                        }
                    }
                }
                for(int i = 0; i < Temp.size(); i++)
                {
                    NewTiles.add(Temp.get(i));
                }
            }
            Tiles.clear();
            for(int i = 0; i < NewTiles.size(); i++)
            {
                Tiles.add(NewTiles.get(i));
            }
            return wasMoveMade;
        }
    private boolean slideDown() {


            boolean wasMoveMade = false;

            ArrayList<Cell> NewTiles = new ArrayList<>();

            for(int c = 0; c < width; c++)//goes through all the rows.
            {
                Temp.clear();//clear the temp arraylist
                TempCompare.clear();
                boolean isSorted = false;
                boolean slid = false;
                for (int curr = 0; curr < Tiles.size(); curr++) {
                    if (Tiles.get(curr).column == c) {
                        Cell newholder = new Cell();
                        Temp.add(Tiles.get(curr));
                        newholder.column = (Tiles.get(curr).column);
                        newholder.row = (Tiles.get(curr).row);
                        newholder.value = (Tiles.get(curr).value);
                        TempCompare.add(newholder);

                    }
                }

                if (Temp.size() > 0) {
                    while (!isSorted) {
                        isSorted = true;
                        for (int i = 0; i < Temp.size() - 1; i++) {
                            if (Temp.get(i).row > Temp.get(i + 1).row) {
                                Cell holder;
                                isSorted = false;
                                holder = Temp.get(i);
                                Temp.set(i, Temp.get(i + 1));
                                Temp.set(i + 1, holder);
                            }
                        }

                    }
                    isSorted = false;
                    while (!isSorted) {
                        isSorted = true;
                        for (int i = 0; i < TempCompare.size() - 1; i++) {
                            if (TempCompare.get(i).row > TempCompare.get(i + 1).row) {
                                Cell holder;
                                isSorted = false;
                                holder = TempCompare.get(i);
                                TempCompare.set(i, TempCompare.get(i + 1));
                                TempCompare.set(i + 1, holder);
                            }
                        }

                    }
                    while (!slid) {
                        slid = true;
                        for (int i = 0; i < Temp.size(); i++) {
                            if (Temp.get(i).row != i) {
                                slid = false;
                                //wasMoveMade = true;
                                Temp.get(i).row = (Temp.get(i).row) - 1;
                            }
                        }
                    }
                    int a = (height - 1) - (Temp.get(Temp.size() - 1).row);

                    for (int i = 0; i < Temp.size(); i++) {
                        Temp.get(i).row = (Temp.get(i).row) + a;
                    }

                    //find a more elegant way to slide these than to slide them all up and measure the distance between the last one and the bottom and shift all of them.


                   for (int i = (Temp.size() - 1); i > 0; i--) {
                        if (Temp.get(i).value == Temp.get(i - 1).value) {
                            Temp.get(i).value = Temp.get(i).value * 2;
                            score = score + Temp.get(i).value;
                            Temp.remove(i - 1);
                            i--;
                        }
                    }
                    slid = false;
                    while (!slid) {
                        slid = true;
                        for (int i = 0; i < Temp.size(); i++) {
                            if (Temp.get(i).row != i) {
                                slid = false;
                                Temp.get(i).row = (Temp.get(i).row) - 1;
                            }
                        }
                    }
                    a = (height - 1) - (Temp.get(Temp.size() - 1).row);

                    for (int i = 0; i < Temp.size(); i++) {
                        Temp.get(i).row = (Temp.get(i).row) + a;
                    }
                    for (int i = 0; i < Temp.size(); i++) {
                        NewTiles.add(Temp.get(i));
                    }
                    if(TempCompare.size() != Temp.size())
                    {
                        wasMoveMade = true;
                    }
                    else
                    {
                        for(int i = 0; i < TempCompare.size(); i++)
                        {
                            if(TempCompare.get(i).row != Temp.get(i).row)
                            {
                                wasMoveMade = true;
                            }
                        }
                    }
                }
            }

            Tiles.clear();
            for(int i = 0; i < NewTiles.size(); i++)
            {
                Tiles.add(NewTiles.get(i));
            }
            return wasMoveMade;
        }
    private boolean slideRight() {
        boolean wasMoveMade = false;

        ArrayList<Cell> NewTiles = new ArrayList<>();

        for (int r = 0; r < height; r++)//goes through all the rows.
        {
            Temp.clear();//clear the temp arraylist
            TempCompare.clear();
            boolean isSorted = false;
            boolean slid = false;
            for (int curr = 0; curr < Tiles.size(); curr++) {
                if (Tiles.get(curr).row == r) {
                    Cell newholder = new Cell();
                    Temp.add(Tiles.get(curr));
                    newholder.column = (Tiles.get(curr).column);
                    newholder.row = (Tiles.get(curr).row);
                    newholder.value = (Tiles.get(curr).value);
                    TempCompare.add(newholder);

                }
            }

            if (Temp.size() > 0) {
                while (!isSorted) {
                    isSorted = true;
                    for (int i = 0; i < Temp.size() - 1; i++) {
                        if (Temp.get(i).column > Temp.get(i + 1).column) {
                            Cell holder = new Cell();
                            isSorted = false;
                            holder = Temp.get(i);
                            Temp.set(i, Temp.get(i + 1));
                            Temp.set(i + 1, holder);
                        }
                    }

                }
                isSorted = false;
                while (!isSorted) {
                    isSorted = true;
                    for (int i = 0; i < TempCompare.size() - 1; i++) {
                        if (TempCompare.get(i).column > TempCompare.get(i + 1).column) {
                            Cell holder = new Cell();
                            isSorted = false;
                            holder = TempCompare.get(i);
                            TempCompare.set(i, TempCompare.get(i + 1));
                            TempCompare.set(i + 1, holder);
                        }
                    }

                }
                while (!slid) {
                    slid = true;
                    for (int i = 0; i < Temp.size(); i++) {
                        if (Temp.get(i).column != i) {
                            slid = false;
                            //wasMoveMade = true;
                            Temp.get(i).column = (Temp.get(i).column) - 1;
                        }
                    }
                }
                int a = (width - 1) - (Temp.get(Temp.size() - 1).column);

                for (int i = 0; i < Temp.size(); i++) {
                    Temp.get(i).column = (Temp.get(i).column) + a;
                }

                //find a more elegant way to slide these than to slide them all up and measure the distance between the last one and the bottom and shift all of them.


                for (int i = (Temp.size() - 1); i > 0; i--) {
                    if (Temp.get(i).value == Temp.get(i - 1).value) {
                        Temp.get(i).value = Temp.get(i).value * 2;
                        score = score + Temp.get(i).value;
                        Temp.remove(i - 1);
                        i--;
                    }
                }
                slid = false;
                while (!slid) {
                    slid = true;
                    for (int i = 0; i < Temp.size(); i++) {
                        if (Temp.get(i).column != i) {
                            slid = false;
                            Temp.get(i).column = (Temp.get(i).column) - 1;
                        }
                    }
                }
                a = (width - 1) - (Temp.get(Temp.size() - 1).column);

                for (int i = 0; i < Temp.size(); i++) {
                    Temp.get(i).column = (Temp.get(i).column) + a;
                }
                for (int i = 0; i < Temp.size(); i++) {
                    NewTiles.add(Temp.get(i));
                }
                if (TempCompare.size() != Temp.size()) {
                    wasMoveMade = true;
                } else {
                    for (int i = 0; i < TempCompare.size(); i++) {
                        if (TempCompare.get(i).column != Temp.get(i).column) {
                            wasMoveMade = true;
                        }
                    }
                }
            }
        }

        Tiles.clear();
        for (int i = 0; i < NewTiles.size(); i++) {
            Tiles.add(NewTiles.get(i));
        }
        return wasMoveMade;
    }




    @Override
    public ArrayList<Cell> getNonEmptyTiles() {
	    ArrayList cells = new ArrayList<Cell>();
	    for (int r = 0; r < grid.length; r++)
	        for (int c = 0; c < grid[0].length; c++)
	            if (grid[r][c] != 0) {
	                Cell cel = new Cell();
	                cel.row = r;
	                cel.column = c;
	                cel.value = grid[r][c];
	                cells.add(cel);

                }
                return cells;

    }
    @Override
    public GameStatus getStatus() {
        loadTilesFromGrid();
        if(checkWinningValue())
        {
            return GameStatus.USER_WON;
        }
        else if(Tiles.size() == (height * width))
        {
            if(!possibleMoves())
            {
                return GameStatus.USER_LOST;
            }
        }
        return  GameStatus.IN_PROGRESS;
    }
    @Override
    public void undo() {
        eraseGrid();
            try {
                grid = moves.pop();
                scores[loc] = 0;
                loc--;
                setScore(scores[loc]);
                loadTilesFromGrid();
            }
            catch(EmptyStackException e1)
            {
                loc = 0;
                throw new IllegalStateException();
            }

    }

    /******************************************************
     * a method to fill the grid with the values from Tiles
     *****************************************************/
    @Override
    public void updateGrid() {
        for(Cell thisCell : Tiles)
        {
            grid[thisCell.row][thisCell.column] = thisCell.value;
        }
    }

    /*************************************************
     * A method to load Tiles form the values in grid
     *************************************************/
    @Override
    public void loadTilesFromGrid(){
        Tiles.clear();
        ArrayList<Cell> out = getNonEmptyTiles();
        Tiles.addAll(out);

    }

    /*****************************************************
     * A method to place all Tiles in order by row,column
     ********************************************************/
    @Override
    public void sortTiles(){
        eraseGrid();
        updateGrid();
        Tiles.clear();
        loadTilesFromGrid();
    }

    /*********************************************
     * A method to call eraseGrid and updateGrid
     **********************************************/
    @Override
    public void refreshGrid(){
        eraseGrid();
        updateGrid();
}

    /************************************************
     * a method to set all the values in grid to zero
     ************************************************/
    @Override
    public void eraseGrid(){
        //the following goes through all possible values of grid
        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[0].length; c++)
            {
                grid[r][c] = 0;
            }
    }

    /**************************************************************
     * Goes through all of grid and checks if a move can be made.
     * This method is only called if all grid spots are full
     *
     * @return true or false if a mode can be made.
     ****************************************************************/
    private boolean possibleMoves(){

        //go through each value in grid.
        for(int c = 0; c < grid.length; c++)
            for(int r = 0; r < grid[0].length; r++)
            {
                if((r + 1) < grid.length)//dont go out of bounds
                {
                    if(grid[r][c] == grid[r+1][c]) //if the value below is equal
                    {
                        return true; //stop the method and return true
                    }
                }
                if((c + 1) < grid[0].length){//dont go out of bounds
                    if(grid[r][c] == grid[r][c + 1])// if the value to the right is equal
                    {
                        return true; //stop the method and return true
                    }
                }
            }
        return false;//no moves are possible
    }

    /***************************************************************
     * goes through each tile and looks to see if its value matches
     * the winning value tile
     *
     * @return true if the tile to win is on the board
     ****************************************************************/
    private boolean checkWinningValue() {
        for(Cell thisCell : Tiles)//go through all tiles
        {
            if(thisCell.value == winningValue)
            {
                return true;
            }
        }
        return false;
    }
}
