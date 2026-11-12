package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VisualizerFrame extends JFrame {
    private AbstractTree tree;
    private TreeCanvas canvas;
    private JList<String> codeList;
    private DefaultListModel<String> codeListModel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton btnPlayPause;

    private List<AnimationStep> currentSteps = new ArrayList<>();
    private int currentStepIndex = -1;
    private javax.swing.Timer animationTimer;
    private boolean isPlaying = false;

    private Stack<TreeNode> undoStack = new Stack<>();
    private Stack<TreeNode> redoStack = new Stack<>();

    public VisualizerFrame(AbstractTree tree, JFrame mainMenu) {
        this.tree = tree;
        setTitle("Visualization - " + tree.getTreeTypeName());
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(235, 240, 245));
        JButton btnBack = new JButton("<- Back");
        btnBack.addActionListener(e -> {
            if (animationTimer != null) animationTimer.stop();
            mainMenu.setVisible(true);
            dispose();
        });

        JLabel lblType = new JLabel("Tree Type: " + tree.getTreeTypeName());
        lblType.setFont(new Font("Arial", Font.BOLD, 14));

        String[] ops = {"Create (Empty)", "Insert", "Delete", "Update", "Traverse (BFS)", "Traverse (DFS)", "Search"};
        JComboBox<String> opCombo = new JComboBox<>(ops);
        JButton btnExecute = new JButton("Execute");
        btnExecute.addActionListener(e -> handleOperation((String) opCombo.getSelectedItem()));

        topBar.add(btnBack);
        topBar.add(lblType);
        topBar.add(new JLabel("   |   Operation:"));
        topBar.add(opCombo);
        topBar.add(btnExecute);
        add(topBar, BorderLayout.NORTH);

        canvas = new TreeCanvas();
        codeListModel = new DefaultListModel<>();
        codeList = new JList<>(codeListModel);
        codeList.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeList.setSelectionBackground(new Color(255, 235, 59));
        codeList.setSelectionForeground(Color.BLACK);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Pseudo-Code Execution"));
        rightPanel.add(new JScrollPane(codeList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, rightPanel);
        splitPane.setDividerLocation(680);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" Ready. Please select an operation from the toolbar.");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel progressWrapper = new JPanel(new BorderLayout());
        progressWrapper.add(statusLabel, BorderLayout.NORTH);
        progressWrapper.add(progressBar, BorderLayout.SOUTH);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnPrev = new JButton("|< Prev Step");
        btnPlayPause = new JButton("Play / Pause");
        JButton btnNext = new JButton("Next Step >");
        JButton btnUndo = new JButton("Undo");
        JButton btnRedo = new JButton("Redo");

        btnPrev.addActionListener(e -> stepBackward());
        btnNext.addActionListener(e -> stepForward());
        btnPlayPause.addActionListener(e -> togglePlayPause());
        btnUndo.addActionListener(e -> undo());
        btnRedo.addActionListener(e -> redo());

        controlsPanel.add(btnPrev);
        controlsPanel.add(btnPlayPause);
        controlsPanel.add(btnNext);
        controlsPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlsPanel.add(btnUndo);
        controlsPanel.add(btnRedo);

        bottomPanel.add(progressWrapper, BorderLayout.NORTH);
        bottomPanel.add(controlsPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        animationTimer = new javax.swing.Timer(800, e -> {
            if (currentStepIndex < currentSteps.size() - 1) {
                stepForward();
            } else {
                isPlaying = false;
                animationTimer.stop();
            }
        });
    }

    private void saveStateForUndo() {
        undoStack.push(tree.getRoot() != null ? tree.getRoot().cloneTree() : null);
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No operations to Undo!");
            return;
        }
        redoStack.push(tree.getRoot() != null ? tree.getRoot().cloneTree() : null);
        tree.setRoot(undoStack.pop());
        canvas.updateState(tree.getRoot(), Set.of());
        statusLabel.setText(" Action undone successfully.");
    }

    private void redo() {
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No operations to Redo!");
            return;
        }
        undoStack.push(tree.getRoot() != null ? tree.getRoot().cloneTree() : null);
        tree.setRoot(redoStack.pop());
        canvas.updateState(tree.getRoot(), Set.of());
        statusLabel.setText(" Action redone successfully.");
    }

    private void handleOperation(String op) {
        try {
            animationTimer.stop();
            isPlaying = false;
            List<AnimationStep> steps = new ArrayList<>();

            switch (op) {
                case "Create (Empty)":
                    saveStateForUndo();
                    tree.createEmpty();
                    loadPseudoCode(new String[]{"1. root = null;", "2. // Initialize empty tree"});
                    steps.add(new AnimationStep(null, Set.of(), 0, "Created a new empty tree."));
                    break;

                case "Insert":
                    Integer parentVal = null;
                    if (tree.getRoot() != null) {
                        String pStr = JOptionPane.showInputDialog(this, "Enter Parent Node Value:");
                        if (pStr == null) return;
                        parentVal = Integer.parseInt(pStr.trim());
                    }
                    String vStr = JOptionPane.showInputDialog(this, "Enter New Node Value (integer):");
                    if (vStr == null) return;
                    int newVal = Integer.parseInt(vStr.trim());

                    saveStateForUndo();
                    loadPseudoCode(new String[]{
                        "1. Node newNode = new Node(newVal);",
                        "2. if (root == null) root = newNode;",
                        "3. Node parent = findNode(root, parentVal);",
                        "4. parent.addChild(newNode);"
                    });
                    steps = tree.insert(parentVal, newVal);
                    break;

                case "Delete":
                    String delStr = JOptionPane.showInputDialog(this, "Enter Node Value to Delete:");
                    if (delStr == null) return;
                    int delVal = Integer.parseInt(delStr.trim());

                    saveStateForUndo();
                    loadPseudoCode(new String[]{
                        "1. if (root == null) return;",
                        "2. Node target = findNode(root, val);",
                        "3. if (target == root) root = null;",
                        "4. else parent.removeChild(target);"
                    });
                    steps = tree.delete(delVal);
                    break;

                case "Update":
                    String oldStr = JOptionPane.showInputDialog(this, "Enter Current Node Value:");
                    if (oldStr == null) return;
                    String updStr = JOptionPane.showInputDialog(this, "Enter New Value:");
                    if (updStr == null) return;

                    saveStateForUndo();
                    loadPseudoCode(new String[]{
                        "1. if (contains(newVal)) throw Error;",
                        "2. Node target = findNode(root, oldVal);",
                        "3. target.setValue(newVal);"
                    });
                    steps = tree.update(Integer.parseInt(oldStr.trim()), Integer.parseInt(updStr.trim()));
                    break;

                case "Traverse (BFS)":
                    loadPseudoCode(new String[]{
                        "1. Queue<Node> q = new LinkedList<>(); q.add(root);",
                        "2. while (!q.isEmpty()) {",
                        "3.    Node curr = q.poll(); visit(curr);",
                        "4.    q.addAll(curr.getChildren());",
                        "5. }"
                    });
                    steps = tree.traverse(true);
                    break;

                case "Traverse (DFS)":
                    loadPseudoCode(new String[]{
                        "1. void dfs(Node node) {",
                        "2.    if (node == null) return;",
                        "3.    visit(node);",
                        "4.    for (Node child : node.getChildren()) dfs(child);",
                        "5. }"
                    });
                    steps = tree.traverse(false);
                    break;

                case "Search":
                    String sStr = JOptionPane.showInputDialog(this, "Enter Value to Search:");
                    if (sStr == null) return;
                    loadPseudoCode(new String[]{
                        "1. Queue<Node> q = new LinkedList<>(); q.add(root);",
                        "2. while (!q.isEmpty()) {",
                        "3.    if (curr.val == target) return FOUND;",
                        "4. } return NOT_FOUND;"
                    });
                    steps = tree.search(Integer.parseInt(sStr.trim()));
                    break;
            }

            if (!steps.isEmpty()) {
                this.currentSteps = steps;
                this.currentStepIndex = -1;
                isPlaying = true;
                animationTimer.start();
                stepForward();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (!undoStack.isEmpty()) undoStack.pop(); 
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Notice", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadPseudoCode(String[] lines) {
        codeListModel.clear();
        for (String line : lines) codeListModel.addElement(line);
    }

    private void renderCurrentStep() {
        if (currentStepIndex < 0 || currentStepIndex >= currentSteps.size()) return;
        AnimationStep step = currentSteps.get(currentStepIndex);
        canvas.updateState(step.getTreeSnapshot(), step.getHighlightedNodes());
        statusLabel.setText(" Step " + (currentStepIndex + 1) + "/" + currentSteps.size() + ": " + step.getDescription());
        
        int progress = (int) (((double) (currentStepIndex + 1) / currentSteps.size()) * 100);
        progressBar.setValue(progress);

        if (step.getHighlightedCodeLine() < codeListModel.size()) {
            codeList.setSelectedIndex(step.getHighlightedCodeLine());
        }
    }

    private void stepForward() {
        if (currentStepIndex < currentSteps.size() - 1) {
            currentStepIndex++;
            renderCurrentStep();
        }
    }

    private void stepBackward() {
        animationTimer.stop();
        isPlaying = false;
        if (currentStepIndex > 0) {
            currentStepIndex--;
            renderCurrentStep();
        }
    }

    private void togglePlayPause() {
        if (currentSteps.isEmpty()) return;
        if (isPlaying) {
            animationTimer.stop();
            isPlaying = false;
        } else {
            if (currentStepIndex >= currentSteps.size() - 1) currentStepIndex = -1;
            animationTimer.start();
            isPlaying = true;
        }
    }
}