package struct.tree;

/**
 * 二叉树
 * @author chenpc
 * @version 1.0
 * @since 2022/4/1/04/01  14:57
 */
public class BinaryTreeDemo {
    public static void main(String[] args) {
        /**
         *
         *      root
         *  node1     node2
         *         node3    node4
         *
         *
         */
        BinaryTree binaryTree = new BinaryTree();
        Node root = new Node(1, "root");
        Node node1 = new Node(2, "node1");
        Node node2 = new Node(3, "node2");
        Node node3 = new Node(4, "node3");
        Node node4 = new Node(5, "node4");
        binaryTree.setRoot(root);
        root.left = node1;
        root.right = node2;
        node2.left = node3;
        node2.right = node4;
        binaryTree.preorderTraversal();
        binaryTree.inorderTraversal();
        binaryTree.postorderTraversal();
        System.out.println(binaryTree.search(5, BinaryTree.SEARCH_TYPE_POST));
        binaryTree.delNode(5);
        System.out.println(binaryTree.search(5, BinaryTree.SEARCH_TYPE_POST));
    }
}

class BinaryTree {
    Node root;
    public static final String SEARCH_TYPE_PRE = "pre";
    public static final String SEARCH_TYPE_IN = "in";
    public static final String SEARCH_TYPE_POST = "post";

    public BinaryTree() {
    }

    public BinaryTree(Node root) {
        this.root = root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void preorderTraversal() {
        System.out.print("前序遍历：");
        if (root != null) {
            root.PreorderTraversal(root);
        } else {
            System.out.println("空树");
        }
        System.out.println("");
    }

    public void inorderTraversal() {
        System.out.print("中序遍历：");
        if (root != null) {
            root.inorderTraversal(root);
        } else {
            System.out.println("空树");
        }
        System.out.println("");
    }

    public void postorderTraversal() {
        System.out.print("后序遍历：");
        if (root != null) {
            root.postorderTraversal(root);
        } else {
            System.out.println("空树");
        }
        System.out.println("");
    }

    public Node search(int i, String searchType) {
        if (this.root != null) {
            switch (searchType) {
                case SEARCH_TYPE_PRE:
                    System.out.print("前序遍历查找：");
                    return this.root.PreorderSearch(i);
                case SEARCH_TYPE_IN:
                    System.out.print("中序遍历查找：");
                    return this.root.inorderSearch(i);
                case SEARCH_TYPE_POST:
                    System.out.print("后序遍历查找：");
                    return this.root.postorderSearch(i);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public void delNode(int no) {
        if (root != null) {
            if (root.no == no) {
                root = null;
            } else {
                root.delete(no);
            }
        } else {
            System.out.println("空树不能删除");
        }
    }
}

class Node {
    int no;
    Object data;
    Node left;
    Node right;

    public Node() {
    }

    public Node(int no, Object data) {
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

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }


    /**
     * 前序遍历
     */
    public void PreorderTraversal(Node node) {
        assert node != null;
        System.out.print(node + "\t");
        if (node.left != null) {
            PreorderTraversal(node.left);
        }
        if (node.right != null) {
            PreorderTraversal(node.right);
        }
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

    /**
     * 后序遍历
     */
    public void postorderTraversal(Node node) {
        assert node != null;
        if (node.left != null) {
            postorderTraversal(node.left);
        }
        if (node.right != null) {
            postorderTraversal(node.right);
        }
        System.out.print(node + "\t");
    }

    /**
     * 前序遍历查找
     */
    public Node PreorderSearch(int no) {
        Node res = null;
        if (this.no == no) {
            return this;
        }
        if (this.left != null) {
            res = this.left.PreorderSearch(no);
        }
        if (res != null) {
            return res;
        }
        if (this.right != null) {
            res = this.right.PreorderSearch(no);
        }
        return res;
    }

    /**
     * 中序遍历查找
     */
    public Node inorderSearch(int no) {
        Node res = null;
        if (this.left != null) {
            res = this.left.PreorderSearch(no);
        }
        if (res != null) {
            return res;
        }
        if (this.no == no) {
            return this;
        }
        if (this.right != null) {
            res = this.right.PreorderSearch(no);
        }
        return res;
    }

    /**
     * 后序遍历查找
     */
    public Node postorderSearch(int no) {
        Node res = null;
        if (this.left != null) {
            res = this.left.PreorderSearch(no);
        }
        if (res != null) {
            return res;
        }
        if (this.right != null) {
            res = this.right.PreorderSearch(no);
        }
        if (res != null) {
            return res;
        }
        if (this.no == no) {
            return this;
        }
        return null;
    }

    /**
     * 删除指定节点
     *
     * @param no 待删除节点
     */
    public void delete(int no) {
        if (this.left != null && this.left.no == no) {
            this.left = null;
            return;
        }
        if (this.right != null && this.right.no == no) {
            this.right = null;
            return;
        }
        if (this.left != null) {
            this.left.delete(no);
        }
        if (this.right != null) {
            this.right.delete(no);
        }
    }

    /**
     * 删除指定节点，不删除子树，左子节点替代当前节点
     *  右子节点？？？
     *
     * @param no 待删除节点
     */
    public void deleteWithoutOtherNode(int no) {
        Node delNode = null;
        if (this.left != null && this.left.no == no) {
            delNode = this.left;
            if (delNode.left != null) {
                // 待删除节点的左子节点升级
                this.left = delNode.left;
                if (delNode.right != null) {
                    //todo 待删除节点的右子节点如何？
                }
            } else if (delNode.right != null) {
                this.left = delNode.right;
            } else {
                this.left = null;
            }
            return;
        }
        if (this.right != null && this.right.no == no) {
            delNode = this.right;
            if (delNode.left != null) {
                this.right = delNode.left;
                if (delNode.right != null) {
                    //todo
                }
            } else if (delNode.right != null) {
                this.right = delNode.right;
            } else {
                this.right = null;
            }
            return;
        }
        if (this.left != null) {
            this.left.delete(no);
        }
        if (this.right != null) {
            this.right.delete(no);
        }
    }
}
