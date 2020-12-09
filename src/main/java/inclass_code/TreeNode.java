package inclass_code;

public class TreeNode{
    private int value;
    private TreeNode leftNode;
    private TreeNode rightNode;

    public TreeNode(int value, TreeNode leftNode, TreeNode rightNode) {
        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public TreeNode(int value) {
        this.value = value;
        leftNode = rightNode = null;
    }

    public int getValue() {
        return value;
    }

    public TreeNode getLeftNode() {
        return leftNode;
    }

    public TreeNode getRightNode() {
        return rightNode;
    }
}
