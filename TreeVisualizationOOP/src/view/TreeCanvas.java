package view;

import model.TreeNode;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TreeCanvas extends JPanel {
    private TreeNode root;
    private Set<Integer> highlightedNodes = new HashSet<>();
    private final int NODE_RADIUS = 22;

    public TreeCanvas() {
        setBackground(Color.WHITE);
    }

    public void updateState(TreeNode root, Set<Integer> highlighted) {
        this.root = root;
        this.highlightedNodes = (highlighted != null) ? highlighted : new HashSet<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root == null) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.drawString("Tree is empty. Please select 'Insert' to add nodes!", 40, 50);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTree(g2, root, getWidth() / 2, 50, getWidth() / 4);
    }

    private void drawTree(Graphics2D g2, TreeNode node, int x, int y, int xOffset) {
        int childCount = node.getChildren().size();
        if (childCount > 0) {
            int startX = x - xOffset * (childCount - 1) / 2;
            for (int i = 0; i < childCount; i++) {
                TreeNode child = node.getChildren().get(i);
                int childX = (childCount == 1) ? x : startX + i * xOffset;
                int childY = y + 80;

                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x, y, childX, childY);

                drawTree(g2, child, childX, childY, Math.max(xOffset / 2, 35));
            }
        }

        if (highlightedNodes.contains(node.getValue())) {
            g2.setColor(new Color(255, 204, 0));
        } else {
            g2.setColor(new Color(100, 181, 246));
        }
        g2.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        String text = String.valueOf(node.getValue());
        FontMetrics fm = g2.getFontMetrics();
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(text, x - fm.stringWidth(text) / 2, y + fm.getAscent() / 3);
    }
}