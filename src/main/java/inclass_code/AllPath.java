package inclass_code;


import java.util.ArrayList;
import java.util.List;

public class AllPath {

    public List<String> binaryTreePaths(TreeNode root){
        List<String> paths = new ArrayList<>();
        if(root==null){
            return paths;
        }

        List<String> leftPath = binaryTreePaths(root.getLeftNode());
        List<String> rightPath = binaryTreePaths(root.getRightNode());

        for(String path:leftPath){
            paths.add(root.getValue()+"->"+path);
        }
        for(String path:rightPath){
            paths.add(root.getValue()+"->"+path);
        }
        return paths;
    }

}
