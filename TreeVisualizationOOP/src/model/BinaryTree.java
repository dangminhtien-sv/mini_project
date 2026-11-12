package model;

import java.util.List;

public class BinaryTree extends GenericTree {
    public BinaryTree() {
        this.treeTypeName = "Binary Tree";
    }

    @Override
    public List<AnimationStep> insert(Integer parentValue, int newValue) throws Exception {
        if (root != null && parentValue != null) {
            TreeNode parent = findNode(root, parentValue);
            if (parent != null && parent.getChildren().size() >= 2) {
                throw new Exception("Binary Tree allows each node to have at most 2 children!");
            }
        }
        return super.insert(parentValue, newValue);
    }
}