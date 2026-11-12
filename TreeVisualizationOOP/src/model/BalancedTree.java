package model;

import java.util.List;

public class BalancedTree extends GenericTree {
    protected int maxDiff;

    public BalancedTree(int maxDiff) {
        this.maxDiff = maxDiff;
        this.treeTypeName = "Balanced Tree (k = " + maxDiff + ")";
    }

    @Override
    public List<AnimationStep> insert(Integer parentValue, int newValue) throws Exception {
        TreeNode backup = (root != null) ? root.cloneTree() : null;
        List<AnimationStep> steps = super.insert(parentValue, newValue);
        int[] depths = getMinMaxLeafDepth(root);
        if (depths[1] - depths[0] > maxDiff) {
            this.root = backup;
            throw new Exception("Balance condition violated! Leaf depth difference (" 
                    + (depths[1] - depths[0]) + ") exceeds threshold k = " + maxDiff);
        }
        return steps;
    }

    @Override
    public List<AnimationStep> delete(int value) throws Exception {
        TreeNode backup = (root != null) ? root.cloneTree() : null;
        List<AnimationStep> steps = super.delete(value);
        if (root != null) {
            int[] depths = getMinMaxLeafDepth(root);
            if (depths[1] - depths[0] > maxDiff) {
                this.root = backup;
                throw new Exception("Cannot delete because it unbalances the tree (depth diff > " + maxDiff + ")!");
            }
        }
        return steps;
    }
}