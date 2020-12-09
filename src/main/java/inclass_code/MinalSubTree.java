package inclass_code;

public class MinalSubTree {
    private int minSum;
    private TreeNode minRoot;

    public TreeNode findSubTree(TreeNode root){
        minSum = Integer.MAX_VALUE;
        minRoot = null;
        getSum(root);
        return minRoot;
    }

    private int getSum(TreeNode root){
        if(root==null){
            return 0;
        }

        int sum = getSum(root.getLeftNode())+getSum(root.getRightNode())+root.getValue();
        if(sum < minSum){
            minSum = sum;
            minRoot = root;
        }

        return sum;
    }


}


