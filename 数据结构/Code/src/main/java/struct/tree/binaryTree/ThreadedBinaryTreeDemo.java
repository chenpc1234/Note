package struct.tree.binaryTree;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/4/5/04/05  14:39
 */
public class ThreadedBinaryTreeDemo {

    public static void main(String[] args) {
        ThreadedBinaryTree binaryTree = new ThreadedBinaryTree();
        TNode root = new TNode(1, "root");
        TNode node1 = new TNode(2, "node1");
        TNode node2 = new TNode(3, "node2");
        TNode node3 = new TNode(4, "node3");
        TNode node4 = new TNode(5, "node4");
        binaryTree.setRoot(root);
        root.left = node1;
        root.right = node2;
        node2.left = node3;
        node2.right = node4;
        /**
         *
         *      root
         *  node1     node2
         *         node3    node4
         *
         *
         */
        binaryTree.threaded();
        /**
         * 中序遍历  node1. left=null right =root-1
         * root.left =node1 right =node2
         * node3.left=root-1 right =node2-1
         * node2.left =node3 right =node4
         * node4.left =node2-1 right = null
         */
        binaryTree.inorderTraversal();
    }
}

class ThreadedBinaryTree {
    TNode root;
    TNode pre;

    public void threaded(){
        threaded(root);
    }
    //中序线索化
    public void threaded(TNode tNode) {
        if (tNode == null) {
            return;
        }
        // 线索化左子树
        threaded(tNode.left);
        //当前节点的左子节点为空。将当前节点的前驱节点改为当前节点的父节点。
        if (tNode.left == null) {
            tNode.left = pre;
            tNode.leftType = 1;
        }
        //pre 是当前节点的前驱节点。
        // 如果当前节点的前驱节点的右子节点为空，则前驱节点的后继节点是当前节点
        if (pre!=null &&pre.right == null) {
            pre.right = tNode;
            pre.rightType = 1;
        }
        //处理完当前节点后，使得当前节点作为下一个待处理节点的前驱节点
        pre = tNode;
        //线索化右子树
        threaded(tNode.right);
    }
    public void inorderTraversal(){
        TNode temp =root;
        while (temp !=null){
            while (temp.getLeftType()==0){
                temp =temp.getLeft();
            }
            System.out.println(temp);
            while (temp.getRightType()==1){
                temp =temp.right;
                System.out.println(temp);
            }
            temp=temp.right;
        }
    }
    public void setRoot(TNode root) {
        this.root = root;
    }
}

class TNode {
    int no;
    Object data;
    TNode left;
    TNode right;
    int leftType = 0; // 0 =子节点(左子节点) 1= 线索节点（前驱节点）
    int rightType = 0;// 0 =子节点(右子节点) 1= 线索节点（后继节点）

    public TNode() {
    }

    public TNode(int no, Object data) {
        this.no = no;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Node{" +
                "no=" + no +
                ", data=" + data +
                '}';
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public TNode getLeft() {
        return left;
    }

    public void setLeft(TNode left) {
        this.left = left;
    }

    public TNode getRight() {
        return right;
    }

    public void setRight(TNode right) {
        this.right = right;
    }

    public int getLeftType() {
        return leftType;
    }

    public void setLeftType(int leftType) {
        this.leftType = leftType;
    }

    public int getRightType() {
        return rightType;
    }

    public void setRightType(int rightType) {
        this.rightType = rightType;
    }

    /**
     * 中序遍历
     */
    public void inorderTraversal(Node node) {
        assert node != null;
        if (node.left != null) {
            inorderTraversal(node.left);
        }
        System.out.print(node + "\t");
        if (node.right != null) {
            inorderTraversal(node.right);
        }
    }
}

