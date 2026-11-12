package model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private int value;
    private List<TreeNode> children;

    public TreeNode(int value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public List<TreeNode> getChildren() { return children; }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public void removeChild(TreeNode child) {
        children.remove(child);
    }

    public TreeNode cloneTree() {
        TreeNode copy = new TreeNode(this.value);
        for (TreeNode child : this.children) {
            copy.addChild(child.cloneTree());
        }
        return copy;
    }
}