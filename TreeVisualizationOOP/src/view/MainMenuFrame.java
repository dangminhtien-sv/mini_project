package view;

import model.*;
import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("Tree Data Structures Visualization - OOP Project");
        setSize(600, 480);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        JLabel title = new JLabel("TREE DATA STRUCTURES VISUALIZATION", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Object-Oriented Programming Mini-Project", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        header.add(title);
        header.add(subtitle);
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton btnGeneric = new JButton("1. Generic Tree");
        JButton btnBinary = new JButton("2. Binary Tree");
        JButton btnBalanced = new JButton("3. Balanced Tree");
        JButton btnBalancedBinary = new JButton("4. Balanced Binary Tree");

        Font btnFont = new Font("Arial", Font.BOLD, 14);
        for (JButton b : new JButton[]{btnGeneric, btnBinary, btnBalanced, btnBalancedBinary}) {
            b.setFont(btnFont);
            centerPanel.add(b);
        }

        btnGeneric.addActionListener(e -> openVisualizer(new GenericTree()));
        btnBinary.addActionListener(e -> openVisualizer(new BinaryTree()));
        btnBalanced.addActionListener(e -> {
            Integer k = askBalanceThreshold();
            if (k != null) openVisualizer(new BalancedTree(k));
        });
        btnBalancedBinary.addActionListener(e -> {
            Integer k = askBalanceThreshold();
            if (k != null) openVisualizer(new BalancedBinaryTree(k));
        });

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        JButton btnHelp = new JButton("Help");
        JButton btnQuit = new JButton("Quit");

        btnHelp.addActionListener(e -> showHelp());
        btnQuit.addActionListener(e -> confirmQuit());

        bottomPanel.add(btnHelp);
        bottomPanel.add(btnQuit);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmQuit();
            }
        });
    }

    private Integer askBalanceThreshold() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter max depth difference threshold between leaf nodes (k >= 0):", "1");
        if (input == null) return null;
        try {
            int k = Integer.parseInt(input.trim());
            if (k < 0) throw new NumberFormatException();
            return k;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a non-negative integer!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void openVisualizer(AbstractTree tree) {
        this.setVisible(false);
        new VisualizerFrame(tree, this).setVisible(true);
    }

    private void showHelp() {
        String helpText = "PROJECT OBJECTIVE:\n"
                + "Visualize basic operations on 4 types of tree data structures.\n\n"
                + "USER GUIDE:\n"
                + "1. Select one of the 4 tree types from the main menu.\n"
                + "2. For balanced trees, enter the maximum allowed leaf depth difference (k).\n"
                + "3. In the visualizer screen, choose an operation (Create, Insert, Delete, Update, Traverse, Search).\n"
                + "4. Observe the tree canvas and executing pseudo-code. Use player controls to Pause/Play, step through execution, or Undo/Redo.";
        JOptionPane.showMessageDialog(this, helpText, "User Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmQuit() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to quit the application?", "Confirm Quit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}