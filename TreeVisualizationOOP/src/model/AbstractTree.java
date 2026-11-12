package model;

import java.util.*;

public abstract class AbstractTree {
    protected TreeNode root;
    protected String treeTypeName;

    public AbstractTree(String treeTypeName) {
        this.treeTypeName = treeTypeName;
    }

    public TreeNode getRoot() { return root; }
    public void setRoot(TreeNode root) { this.root = (root != null) ? root.cloneTree() : null; }
    public String getTreeTypeName() { return treeTypeName; }

    public void createEmpty() {
        this.root = null;
    }

    public boolean contains(int value) {
        return findNode(root, value) != null;
    }

    protected TreeNode findNode(TreeNode current, int value) {
        if (current == null) return null;
        if (current.getValue() == value) return current;
        for (TreeNode child : current.getChildren()) {
            TreeNode found = findNode(child, value);
            if (found != null) return found;
        }
        return null;
    }

    protected TreeNode findParent(TreeNode current, int targetValue) {
        if (current == null) return null;
        for (TreeNode child : current.getChildren()) {
            if (child.getValue() == targetValue) return current;
            TreeNode found = findParent(child, targetValue);
            if (found != null) return found;
        }
        return null;
    }

    public abstract List<AnimationStep> insert(Integer parentValue, int newValue) throws Exception;
    public abstract List<AnimationStep> delete(int value) throws Exception;

    public List<AnimationStep> update(int oldValue, int newValue) throws Exception {
        if (contains(newValue)) throw new Exception("New value " + newValue + " already exists in the tree!");
        TreeNode target = findNode(root, oldValue);
        if (target == null) throw new Exception("Node with value " + oldValue + " not found!");

        List<AnimationStep> steps = new ArrayList<>();
        steps.add(new AnimationStep(root, Set.of(oldValue), 1, "Found node " + oldValue));
        target.setValue(newValue);
        steps.add(new AnimationStep(root, Set.of(newValue), 2, "Updated value to " + newValue));
        return steps;
    }

    public List<AnimationStep> search(int targetValue) {
        List<AnimationStep> steps = new ArrayList<>();
        if (root == null) {
            steps.add(new AnimationStep(null, Set.of(), 0, "Tree is empty!"));
            return steps;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode curr = queue.poll();
            steps.add(new AnimationStep(root, Set.of(curr.getValue()), 1, "Checking node " + curr.getValue()));
            if (curr.getValue() == targetValue) {
                steps.add(new AnimationStep(root, Set.of(curr.getValue()), 2, "Found node " + targetValue + "!"));
                return steps;
            }
            queue.addAll(curr.getChildren());
        }
        steps.add(new AnimationStep(root, Set.of(), 3, "Node " + targetValue + " not found in the tree."));
        return steps;
    }

    public List<AnimationStep> traverse(boolean isBFS) {
        List<AnimationStep> steps = new ArrayList<>();
        if (root == null) {
            steps.add(new AnimationStep(null, Set.of(), 0, "Tree is empty!"));
            return steps;
        }
        Set<Integer> visited = new LinkedHashSet<>();
        if (isBFS) {
            Queue<TreeNode> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode curr = queue.poll();
                visited.add(curr.getValue());
                steps.add(new AnimationStep(root, visited, 2, "BFS visiting node: " + curr.getValue()));
                queue.addAll(curr.getChildren());
            }
        } else {
            dfsHelper(root, visited, steps);
        }
        steps.add(new AnimationStep(root, visited, 4, "Traversal completed: " + visited));
        return steps;
    }

    private void dfsHelper(TreeNode node, Set<Integer> visited, List<AnimationStep> steps) {
        if (node == null) return;
        visited.add(node.getValue());
        steps.add(new AnimationStep(root, visited, 2, "DFS visiting node: " + node.getValue()));
        for (TreeNode child : node.getChildren()) {
            dfsHelper(child, visited, steps);
        }
    }

    protected int[] getMinMaxLeafDepth(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        if (node.getChildren().isEmpty()) return new int[]{0, 0};
        int minD = Integer.MAX_VALUE;
        int maxD = Integer.MIN_VALUE;
        for (TreeNode child : node.getChildren()) {
            int[] res = getMinMaxLeafDepth(child);
            minD = Math.min(minD, res[0] + 1);
            maxD = Math.max(maxD, res[1] + 1);
        }
        return new int[]{minD, maxD};
    }
}