package Project2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI1024 {

    private static GUI1024Panel gui1024Panel;

    public static void main(String arg[]){
        JMenu fileMenu;
        JMenuItem quitItem;
        JMenuItem undoItem;
        JMenuItem resizeItem;
        JMenuItem scoreItem;
        JMenuItem resetItem;
        JMenuBar menus;

        fileMenu = new JMenu("File");
        undoItem = new JMenuItem("Undo");
        resizeItem = new JMenuItem("Resize Board");
        scoreItem = new JMenuItem("Change Winning Score");
        resetItem = new JMenuItem("Reset Game");
        quitItem = new JMenuItem ("Quit");

        fileMenu.add(undoItem);
        fileMenu.add(resizeItem);
        fileMenu.add(scoreItem);
        fileMenu.add(resetItem);
        fileMenu.add(quitItem);
        menus = new JMenuBar();

        menus.add(fileMenu);

        ActionListener aListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                            }
        };

        quitItem.addActionListener(aListener);
        undoItem.addActionListener(aListener);

        JFrame gui = new JFrame ("Welcome to 1024!");
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUI1024Panel panel = new GUI1024Panel(undoItem, resizeItem, scoreItem, resetItem, quitItem);
        panel.setFocusable(true);
        gui.getContentPane().add(panel);
        gui.setSize(600,600);
        gui.setJMenuBar(menus);
        gui.setVisible(true);
    }
}