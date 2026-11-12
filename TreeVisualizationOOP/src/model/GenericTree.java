package model;

import java.util.*;

public class GenericTree extends AbstractTree {
    public GenericTree() { super("Generic Tree"); }

    @Override
    public List<AnimationStep> insert(Integer parentValue, int newValue) throws Exception {
        if (contains(newValue)) throw new Exception("Value " + newValue + " already exists!");
        List<AnimationStep> steps = new ArrayList<>();
        if (root == null) {
            root = new TreeNode(newValue);
            steps.add(new AnimationStep(root, Set.of(newValue), 1, "Created root node " + newValue));
            return steps;
        }
        if (parentValue == null) throw new Exception("Root already exists, please specify a parent node value!");
        TreeNode parent = findNode(root, parentValue);
        if (parent == null) throw new Exception("Parent node " + parentValue + " not found");

        steps.add(new AnimationStep(root, Set.of(parentValue), 2, "Found parent node " + parentValue));
        parent.addChild(new TreeNode(newValue));
        steps.add(new AnimationStep(root, Set.of(parentValue, newValue), 3, "Added node " + newValue + " to parent " + parentValue));
        return steps;
    }

    @Override
    public List<AnimationStep> delete(int value) throws Exception {
        if (root == null) throw new Exception("Tree is currently empty!");
        List<AnimationStep> steps = new ArrayList<>();
        if (root.getValue() == value) {
            steps.add(new AnimationStep(root, Set.of(value), 1, "Deleting root node " + value));
            root = null;
            steps.add(new AnimationStep(null, Set.of(), 2, "Tree is now empty"));
            return steps;
        }
        TreeNode parent = findParent(root, value);
        if (parent == null) throw new Exception("Node " + value + " not found");
        TreeNode target = findNode(parent, value);
        steps.add(new AnimationStep(root, Set.of(value), 1, "Found target node to delete: " + value));
        parent.removeChild(target);
        steps.add(new AnimationStep(root, Set.of(parent.getValue()), 3, "Deleted node " + value + " and its subtree"));
        return steps;
    }
}