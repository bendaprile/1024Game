package Project2;


//import F18Project2.NumberGameOriginal;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Hans Dulimarta on Jun 27, 2016.
 */
public class TextUI {
    private NumberSlider game;
    private int[][] grid;
    private static int CELL_WIDTH = 3;
    private static String NUM_FORMAT, BLANK_FORMAT;
    private Scanner inp;

    public TextUI() {
        game = new NumberGameArrayList();

        if (game == null) {
            System.err.println ("*---------------------------------------------*");
            System.err.println ("| You must first modify the UI program.       |");
            System.err.println ("| Look for the first TODO item in TextUI.java |");
            System.err.println ("*---------------------------------------------*");
            System.exit(0xE0);
        }
        game.resizeBoard(4, 4, 1024);
        grid = new int[4][4];

        /* Set the string to %4d */
        NUM_FORMAT = String.format("%%%dd", CELL_WIDTH + 1);

        /* Set the string to %4s, but without using String.format() */
        BLANK_FORMAT = "%" + (CELL_WIDTH + 1) + "s";
        inp = new Scanner(System.in);
    }

    private void renderBoard() {
        game.loadTilesFromGrid();
        game.refreshGrid();
        grid = game.getGrid();
        /* reset all the 2D array elements to ZERO */
//        for (int k = 0; k < grid.length; k++)
//            for (int m = 0; m < grid[k].length; m++)
//                grid[k][m] = 0;

        // =========================================================================
        /* fill in the 2D array using information for non-empty tiles */
        // TO DO .....
        // =========================================================================


        /* Print the 2D array using dots and numbers */
        for (int k = 0; k < grid.length; k++) {
            for (int m = 0; m < grid[k].length; m++)
                if (grid[k][m] == 0)
                    System.out.printf (BLANK_FORMAT, ".");
                else
                    System.out.printf (NUM_FORMAT, grid[k][m]);
            System.out.println();
        }
    }

    /**
     * The main loop for playing a SINGLE game session. Notice that
     * the following method contains NO GAME LOGIC! Its main task is
     * to accept user input and invoke the appropriate methods in the
     * game engine.
     */
    public void playLoop() {
        /* Place the first two random tiles */

        game.placeRandomValue();
        game.placeRandomValue();
        renderBoard();

        /* To keep the right margin within 75 columns, we split the
           following long string literal into two lines
         */
        System.out.println ("Slide direction (W, S, Z, A), " +
                "[U]ndo or [Q]uit? ");

while(game.getStatus() == GameStatus.IN_PROGRESS)
{
    Scanner keyboard = new Scanner(System.in);
    String dir = keyboard.next();
    dir = dir.toLowerCase();
    boolean moved = false;
    switch(dir)
    {
        case ("w"):
            moved = game.slide(SlideDirection.UP);
            break;
        case ("s"):
            moved = game.slide(SlideDirection.RIGHT);
            break;
        case ("z"):
            moved = game.slide(SlideDirection.DOWN);
            break;
        case ("a"):
            moved =game.slide(SlideDirection.LEFT);
            break;
        case("u"):
            try {
                game.undo();
                moved = true;
            }
            catch (IllegalStateException exp) {
                System.out.println("ERROR: CAN NOT UNDO PAST START");

            }
            break;
        case ("q"):
            System.exit(0);
            break;
        default: System.out.println("ERROR: " + dir +" is NOT A MOVE\nSlide direction (W, S, Z, A), " +
                    "[U]ndo or [Q]uit? ");

    }

        renderBoard();

}


        // =========================================================================
        // loop on:
        //        Get user input and slide up, down, left, right
        //        renderBoard
        //    TO DO
        // =========================================================================


        /* Almost done.... */
        switch (game.getStatus()) {
            case IN_PROGRESS:
                System.out.println ("Thanks for playing!");
                break;
            case USER_WON:
                System.out.println ("Congratulation!");
                break;
            case USER_LOST:
                System.out.println ("Sorry....!");
                break;

        }
    }

    public static void main(String[] arg) {
        TextUI t = new TextUI();
        t.playLoop();
    }
}


