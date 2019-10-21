package Project2;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GUI1024Panel extends JPanel {

    //Creates many JLabels that display the gameBoard, and other in game statistics
    private JLabel[][] gameBoardUI;
    private JLabel slidesMadeLabel;
    private JLabel highScoreLabel;
    private JLabel numGamesLabel;
    private JLabel scoreLabel;
    private JLabel[] blankLabel;

    //Creates an instance of the NumberGameArrayList class in GUI1024Panel called gameLogic
    private NumberGameArrayList gameLogic;

    //Creates five menu items for various purposes
    private JMenuItem undoItem;
    private JMenuItem resizeItem;
    private JMenuItem winningScoreItem;
    private JMenuItem resetItem;
    private JMenuItem quitItem;


//    private JButton left;
//    private JButton right;
//    private JButton up;
//    private JButton down;

    //Creates many private integers that are used in htis class
    private int slidesMade = 0;
    private int numGamesPlayed = 0;
    private int score = 0;
    private int highScore = 0;
    private int height = 4;
    private int width = 4;
    private int winningValue = 1024;

    public GUI1024Panel(JMenuItem undo, JMenuItem resize, JMenuItem winningScore, JMenuItem reset, JMenuItem quit) {
        gameLogic = new NumberGameArrayList();
        gameLogic.resizeBoard(height, width, winningValue);

        setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        setLayout(new GridLayout(gameLogic.getHeight() + 1, gameLogic.getWidth()));

        //Initiates the JLabels created and says what they will display
        gameBoardUI = new JLabel[gameLogic.getHeight()][gameLogic.getWidth()];
        slidesMadeLabel = new JLabel("Slides Made: " + Integer.toString(slidesMade));
        highScoreLabel = new JLabel("High Score: " + Integer.toString(highScore));
        numGamesLabel = new JLabel("Number of Games: " + Integer.toString(numGamesPlayed));
        scoreLabel = new JLabel("Score: " + Integer.toString(score));
        blankLabel = new JLabel[gameLogic.getWidth()];
//        left = new JButton("LEFT");
//        right = new JButton("RIGHT");
//        up = new JButton("UP");
//        down = new JButton("DOWN");


        //Sets the instance variables to the JMenuItems sent into this class by GUI1024.java
        undoItem = undo;
        resizeItem = resize;
        winningScoreItem = winningScore;
        resetItem = reset;
        quitItem = quit;

        //Adds the four JLabels to the panel in the correct order
        add(slidesMadeLabel);
        add(highScoreLabel);
        add(numGamesLabel);
        add(scoreLabel);


        //Adds an extra blank JLabel at the top right when the
        for (int b = 4; b < gameLogic.getWidth(); b++) {
            blankLabel[b] = new JLabel();
            add(blankLabel[b]);
        }

        //Sets the font and creates the GameBoardUI
        Font myTextFont = new Font(Font.SERIF, Font.BOLD, 40);
        for (int k = 0; k < gameBoardUI.length; k++)
            for (int m = 0; m < gameBoardUI[k].length; m++) {
                gameBoardUI[k][m] = new JLabel();
                gameBoardUI[k][m].setFont(myTextFont);
                gameBoardUI[k][m].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                gameBoardUI[k][m].setPreferredSize(new Dimension(100, 100));
                add(gameBoardUI[k][m]);
            }
//        add(left);
//            add(up);
//            add(right);
//            add(down);
        gameLogic.reset();
        updateBoard();
        setFocusable(true);
        addKeyListener(new SlideListener());

        //Creates two JTextFields that are used by the JPanel myPanel
        JTextField heightField = new JTextField(5);
        JTextField widthField = new JTextField(5);

        //Creates a new JPanel called myPanel and places the JTextFields in it
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Height:"));
        myPanel.add(heightField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Width:"));
        myPanel.add(widthField);

        ActionListener aListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //If the JMenu quit button is clicked the application will terminate
                if (quitItem == event.getSource()) {
                    System.exit(0);
                }

                //If the JMenu Undo button is clicked, the Undo method in GUI1024Panel is called and the board is updated
                if (undoItem == event.getSource()) {
                    try {
                        //System.out.println("Attempt to undo");
                        if(slidesMade > 0) {
                            slidesMade = slidesMade - 1;
                            gameLogic.undo();
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Can't undo beyond the first move");
                        }
                        //moved = true;
                        updateBoard();
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Can't undo beyond the first move");

                    }
                }

                //If the winningScoreItem button is clicked a Dialog box opens up requesting the user to enter a new Winning score
                //If the winning score entered is numeric the score to win the game will be updated
                if (winningScoreItem == event.getSource()) {
                    String winningScore = JOptionPane.showInputDialog("Enter a new Winning Score?");
                    if (isNumeric(winningScore)) {
                        winningValue = Integer.parseInt(winningScore);
                        gameLogic.setWinningValue(winningValue);
                        JOptionPane.showMessageDialog(null, "Winning Score set to " + winningValue);
                    } else {
                        JOptionPane.showMessageDialog(null, "Please Enter a Numeric Value.");
                    }
                }

                //If the resizeItem button is clicked a Dialog box will open up requesting the user to enter a new Height and Width for the game
                //If the height and width entered are numeric the game will be reset and a new panel will be created with the new dimensions
                if (resizeItem == event.getSource()) {
                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Please Enter Height and Width", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String heightVal = heightField.getText();
                        String widthVal = widthField.getText();
                        if (isNumeric(heightVal) && isNumeric(widthVal)) {
                            removeAll();
                            gameLogic.reset();
                            updateBoard();
                            height = Integer.parseInt(heightVal);
                            width = Integer.parseInt(widthVal);
                            createPanel();
                            slidesMade = 0;
                            numGamesPlayed = numGamesPlayed + 1;
                            numGamesLabel.setText("Number of Games: " + Integer.toString(numGamesPlayed));
                        } else {
                            JOptionPane.showMessageDialog(null, "Please Enter a Numeric Value for Height and Width.");
                        }
                    }
                }

                //If the resetItem button is clicked, the game will be completely reset and a new game will begin
                if (resetItem == event.getSource()) {
                    int resp = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the game?", "Attention!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        slidesMade = 0;
                        gameLogic.reset();
                        updateBoard();
                        numGamesPlayed = numGamesPlayed + 1;
                        numGamesLabel.setText("Number of Games: " + Integer.toString(numGamesPlayed));
                    }
                }

//                if(left == event.getSource()){
//                    System.out.println("left");
//                    updateBoard();
//                }
//                if(right == event.getSource()){
//                    System.out.println("right");
//                    updateBoard();
//                }
//                if(up == event.getSource()){
//                    System.out.println("up");
//                    updateBoard();
//                }
//                if(down == event.getSource()){
//                    System.out.println("down");
//                    updateBoard();
//                }
            }
        };

        //Adds action listeners to all of the JMenuItems
        quitItem.addActionListener(aListener);
        undoItem.addActionListener(aListener);
        resizeItem.addActionListener(aListener);
        winningScoreItem.addActionListener(aListener);
        resetItem.addActionListener(aListener);
//        left.addActionListener(aListener);
//        right.addActionListener(aListener);
//        up.addActionListener(aListener);
//        down.addActionListener(aListener);
    }

    //This class is used to create a new panel when the resizeItem is used in the JMenu
    public void createPanel() {
        gameLogic.resizeBoard(height, width, winningValue);

        setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        setLayout(new GridLayout(gameLogic.getHeight() + 1, gameLogic.getWidth()));

        //Initiates the JLabels created and says what they will display
        gameBoardUI = new JLabel[gameLogic.getHeight()][gameLogic.getWidth()];
        slidesMadeLabel = new JLabel("Slides Made: " + Integer.toString(slidesMade));
        highScoreLabel = new JLabel("High Score: " + Integer.toString(highScore));
        numGamesLabel = new JLabel("Number of Games: " + Integer.toString(numGamesPlayed));
        scoreLabel = new JLabel("Score: " + Integer.toString(score));
        blankLabel = new JLabel[gameLogic.getWidth()];

        //Adds the four JLabels to the panel in the correct order
        add(slidesMadeLabel);
        add(highScoreLabel);
        add(numGamesLabel);
        add(scoreLabel);

        //Adds an extra blank JLabel at the top right when the
        for (int b = 4; b < gameLogic.getWidth(); b++) {
            blankLabel[b] = new JLabel();
            add(blankLabel[b]);
        }

        //Sets the font and creates the GameBoardUI
        Font myTextFont = new Font(Font.SERIF, Font.BOLD, 40);
        for (int k = 0; k < gameBoardUI.length; k++)
            for (int m = 0; m < gameBoardUI[k].length; m++) {
                gameBoardUI[k][m] = new JLabel();
                gameBoardUI[k][m].setFont(myTextFont);
                gameBoardUI[k][m].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                gameBoardUI[k][m].setPreferredSize(new Dimension(100, 100));
                add(gameBoardUI[k][m]);
            }

        gameLogic.reset();
        updateBoard();
        setFocusable(true);
        //addKeyListener(new SlideListener());
    }

    //The Undo method subtracts one from the slidesMade integer and calls the undo method in the NumberGameArrayList class
    public void Undo() {
        slidesMade = slidesMade - 1;
        gameLogic.undo();
    }

    //The updateBoard method is used to update the GUI board based on what has been calculated in the NumberGameArrayList class
    private void updateBoard() {
        slidesMadeLabel.setText("Slides Made: " + Integer.toString(slidesMade));
        score = gameLogic.getScore();
        scoreLabel.setText("Score: " + Integer.toString(gameLogic.getScore()));

        //If the current score is greater than the highscore, the highscore will be set to the current score
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + Integer.toString(highScore));
        }

        //All cells are set to nothing and are made transparent
        for (JLabel[] row : gameBoardUI)
            for (JLabel s : row) {
                s.setText("");
                s.setOpaque(false);
            }

        //Creates a new ArrayList of cells called out and fills it with all the non empty tiles. If the ArrayList is null a message is displayed
        ArrayList<Cell> out = gameLogic.getNonEmptyTiles();
        if (out == null) {
            JOptionPane.showMessageDialog(null, "Incomplete implementation getNonEmptyTiles()");
            return;
        }

        //Checks each cell in the out ArrayList and gives them their correct value and corresponding color
        for (Cell c : out) {
            JLabel z = gameBoardUI[c.row][c.column];
            z.setText(String.valueOf(Math.abs(c.value)));

            //Depending on the value of the cell, the background color will be changed
            if (c.value == 2) {
                z.setBackground(new Color(0xFF, 0xE5, 0xD9));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 4) {
                z.setBackground(new Color(0xFF, 0xCA, 0xD4));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 8) {
                z.setBackground(new Color(0xFF, 176, 172));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 16) {
                z.setBackground(new Color(0xFF, 193, 137));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 32) {
                z.setBackground(new Color(194, 123, 75));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 64) {
                z.setBackground(new Color(209, 60, 20));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 128) {
                z.setBackground(new Color(209, 176, 26));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 256) {
                z.setBackground(new Color(198, 209, 22));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 512) {
                z.setBackground(new Color(161, 209, 93));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            } else if (c.value == 1024) {
                z.setBackground(new Color(131, 203, 209));
                z.setForeground(Color.darkGray);
                z.setOpaque(true);
            }
        }
    }

    private class SlideListener implements KeyListener, ActionListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

            //Depending on the what key is pressed, the gameLogic slide method will be called for that direction
            boolean moved = false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    moved = gameLogic.slide(SlideDirection.UP);
                    break;
                case KeyEvent.VK_LEFT:
                    moved = gameLogic.slide(SlideDirection.LEFT);
                    break;
                case KeyEvent.VK_DOWN:
                    moved = gameLogic.slide(SlideDirection.DOWN);
                    break;
                case KeyEvent.VK_RIGHT:
                    moved = gameLogic.slide(SlideDirection.RIGHT);
                    break;

                //If the U button is clicked, the undo method in NumberGameArrayList is called
                case KeyEvent.VK_U:
                    try {
                        //System.out.println("Attempt to undo");
                        if(slidesMade > 0) {
                            slidesMade = slidesMade - 1;
                            gameLogic.undo();
                        }
                        else{
                                JOptionPane.showMessageDialog(null, "Can't undo beyond the first move");
                        }
                        //moved = true;
                        updateBoard();
                    } catch (IllegalStateException exp) {
                        JOptionPane.showMessageDialog(null, "Can't undo beyond the first move");
                        moved = false;
                    }
            }

            //Specifies what happens if a move is detected
            if (moved) {
                slidesMade = slidesMade + 1;
                updateBoard();

                //If the user has won the game, a dialog box will pop up telling the user they won and asking if they want to play again
                if (gameLogic.getStatus().equals(GameStatus.USER_WON)) {
                    int resp = JOptionPane.showConfirmDialog(null, "You Won! Do you want to play again?", "You Won!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        slidesMade = 0;
                        gameLogic.reset();
                        updateBoard();
                        numGamesPlayed = numGamesPlayed + 1;
                        numGamesLabel.setText("Number of Games: " + Integer.toString(numGamesPlayed));
                    } else {
                        System.exit(0);
                    }

                    //If the user loses the game a dialog box will pop up telling the user they have lost and asking if they would like to play again
                } else if (gameLogic.getStatus().equals(GameStatus.USER_LOST)) {
                    int resp = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Car Over!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.YES_OPTION) {
                        slidesMade = 0;
                        gameLogic.reset();
                        updateBoard();
                        numGamesPlayed = numGamesPlayed + 1;
                        numGamesLabel.setText("Number of Games: " + Integer.toString(numGamesPlayed));
                    } else {
                        System.exit(0);
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    //This method checks if the given string is Numeric. If it is the method returns true
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}

