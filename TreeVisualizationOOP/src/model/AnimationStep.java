package model;

import java.util.HashSet;
import java.util.Set;

public class AnimationStep {
    private TreeNode treeSnapshot;
    private Set<Integer> highlightedNodes;
    private int highlightedCodeLine;
    private String description;

    public AnimationStep(TreeNode root, Set<Integer> highlightedNodes, int codeLine, String description) {
        this.treeSnapshot = (root != null) ? root.cloneTree() : null;
        this.highlightedNodes = new HashSet<>(highlightedNodes);
        this.highlightedCodeLine = codeLine;
        this.description = description;
    }

    public TreeNode getTreeSnapshot() { return treeSnapshot; }
    public Set<Integer> getHighlightedNodes() { return highlightedNodes; }
    public int getHighlightedCodeLine() { return highlightedCodeLine; }
    public String getDescription() { return description; }
}