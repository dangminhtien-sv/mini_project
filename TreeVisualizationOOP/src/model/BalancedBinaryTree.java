package model;

import java.util.List;

public class BalancedBinaryTree extends BalancedTree {
    public BalancedBinaryTree(int maxDiff) {
        super(maxDiff);
        this.treeTypeName = "Balanced Binary Tree (k = " + maxDiff + ")";
    }

    @Override
    public List<AnimationStep> insert(Integer parentValue, int newValue) throws Exception {
        if (root != null && parentValue != null) {
            TreeNode parent = findNode(root, parentValue);
            if (parent != null && parent.getChildren().size() >= 2) {
                throw new Exception("Balanced Binary Tree allows at most 2 children per node!");
            }
        }
        return super.insert(parentValue, newValue);
    }
}