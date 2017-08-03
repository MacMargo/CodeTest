import java.util.*;

public class BinaryTree {

    /**
     * BinaryTree 的节点数据结构
     */
    private class TreeNode{
        private int key = 0;
        private String data = null;
        private boolean isVisited = false;
        private TreeNode leftChild = null;
        private TreeNode rightChild = null;

        public TreeNode(){}
        public TreeNode(int key,String data){
            this.key = key;
            this.data = data;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    //获取根节点
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }


    //定义根节点
    private TreeNode root = null;
    public BinaryTree(){
        root = new TreeNode(1,"A");
    }

    /**
     * 创建一棵二叉树
     */
    public void createBinaryTree(TreeNode root){
        TreeNode nodeB = new TreeNode(2,"B");
        TreeNode nodeC = new TreeNode(3,"C");
        TreeNode nodeD = new TreeNode(4,"D");
        TreeNode nodeE = new TreeNode(5,"E");
        TreeNode nodeF = new TreeNode(6,"F");
        root.leftChild = nodeB;
        root.rightChild = nodeC;
        nodeB.leftChild = nodeD;
        nodeB.rightChild = nodeE;
        nodeC.rightChild = nodeF;

    }

    /**
     * 前序遍历
     */
    public void preOrder(TreeNode node){
        if(node != null){
            visited(node);
            preOrder(node.leftChild);
            preOrder(node.rightChild);
        }
    }
    /**
     * 中序遍历
     * @param node
     */

    public void inOrder(TreeNode node){
        if(node != null){
            preOrder(node.leftChild);
            visited(node);
            preOrder(node.rightChild);
        }
    }
    /**
     * 后序遍历
     * @param node
     */

    public void postOrder(TreeNode node){
        if(node != null){
            preOrder(node.leftChild);
            preOrder(node.rightChild);
            visited(node);
        }
    }
    /**
     * 非递归前序遍历
     * @param node
     */

    public void nonRecPreOrder(TreeNode node){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode pNode = node;
        while(pNode != null || stack.size()>0){
            while(pNode != null){
                visited(pNode);
                stack.push(pNode);
                pNode = pNode.leftChild;
            }
            if(stack.size()>0){
                pNode = stack.pop();
                pNode = pNode.rightChild;
            }
        }
    }
    /**
     * 非递归中序遍历
     * @param node
     */

    public void nonRecInOrder(TreeNode node){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode pNode = node;
        while(pNode != null || stack.size()>0){
            while(pNode != null){
                stack.push(pNode);
                pNode = pNode.leftChild;
            }
            if(stack.size()>0){
                pNode = stack.pop();
                visited(pNode);
                pNode = pNode.rightChild;
            }
        }
    }

    /**
     * 非递归后序遍历
     * @param pNode
     */
    public void nonRecPostOrder(TreeNode pNode){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = pNode;
        while(pNode != null){
            //左子树入栈
            while(pNode.leftChild != null){
                stack.push(pNode);
                pNode = pNode.leftChild;
            }
            //当前节点无右子树或者右子树已输出
            while(pNode != null && (pNode.rightChild == null || pNode.rightChild == node)){
                visited(pNode);
                //记录上一个已输出的节点
                node = pNode;
                if(!stack.isEmpty()){
                    pNode = stack.pop();
                }else{
                    return;
                }
            }
            //右子树入栈
            stack.push(pNode);
            pNode = pNode.rightChild;
        }
    }

    private void visited(TreeNode node) {
        node.isVisited = true;
        System.out.println(node.data+","+node.key);
    }

    /**
     * 计算树的高度
     */
    private int height(TreeNode node){
        if(node == null){
            return 0;
        }else{
            int i = height(node.leftChild);
            int j = height(node.rightChild);
            return (i<j)?j+1:i+1;
        }
    }

    /**
     * 计算树的节点数
     * @param node
     * @return 树的节点数
     */

    private int size(TreeNode node){
        if(node == null){
            return 0;
        }else{
            return 1+size(node.leftChild)+size(node.rightChild);
        }
    }


    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        TreeNode root = binaryTree.root;
        binaryTree.createBinaryTree(root);
        System.out.println(binaryTree.height(root));
        System.out.println(binaryTree.size(root));
        binaryTree.preOrder(root);
        System.out.println("*******");
        binaryTree.nonRecPreOrder(root);
        System.out.println("*******");
        binaryTree.nonRecInOrder(root);
        System.out.println("-------------");
        binaryTree.nonRecPostOrder(root);
    }

}

