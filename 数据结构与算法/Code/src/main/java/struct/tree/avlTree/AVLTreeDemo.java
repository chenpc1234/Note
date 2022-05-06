package struct.tree.avlTree;

import lombok.Data;

/**
 * 二叉平衡树
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/4/19/04/19  10:37
 */
public class AVLTreeDemo {
    public static void main(String[] args) {
        //int[] a = {4,3,6,5,7,8};
        //int[] a = {10,12,8,9,7,6};
        int[] a = {10,11,7,6,8,9};
        AVLTree avlTree = new AVLTree();
        for (int i = 0; i < a.length; i++) {
            avlTree.addNode(new Node(a[i]));
        }
        avlTree.inorderTraversal();
        System.out.println(avlTree.root.rightHeight());
        System.out.println(avlTree.root.leftHeight());
    }
}

class AVLTree {
    Node root;

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void addNode(Node node) {
        if (root == null) {
            root = node;
        } else {
            root.addNode(node);
        }

    }

    /**
     * 中序遍历
     */
    public void inorderTraversal() {
        System.out.print("中序遍历：");
        if (root != null) {
            root.inorderTraversal(root);
        } else {
            System.out.println("空树");
        }
        System.out.println("");
    }

    /**
     * 查找节点
     *
     * @param no 节点值
     */
    public Node searchNode(int no) {
        if (root == null) {
            return null;

        }
        return root.searchNode(no);
    }

    /**
     * 删除节点
     */
    public void delNode(Node node) {
        if (root == null || node == null) {
            System.out.println("无法删除");
            return;
        }
        //遍历找到 父节点
        if (root.no == node.no) {
            //删除的节点是父节点
            //找到替代节点
            Node repNode = findRepNode(node);
            if (repNode != null) {
                //找到替代节点父节点
                Node repFatherNode = findFatherNode(root, repNode);
                //替代节点父节点删除替代节点
                if (repFatherNode.left.no == repNode.no) {
                    repFatherNode.left = null;
                } else {
                    repFatherNode.right = null;
                }
                //根节点与替代节点互换
                repNode.right = root.right;
                repNode.left = root.left;
                root = repNode;
            } else {
                root = null;
            }
        } else {
            //查找到删除节点的父节点
            Node fatherNode = findFatherNode(root, node);
            // 查找到删除节点的替代节点
            Node repNode = findRepNode(node);
            if (repNode != null) {
                // 查找到删除节点的替代节点的父节点
                Node repFatherNode = findFatherNode(root, repNode);
                //替代节点删除
                if (repFatherNode.left == repNode) {
                    repFatherNode.left = null;
                } else {
                    repFatherNode.right = null;
                }
            }
            if (fatherNode.left == node) {
                fatherNode.left = repNode;
            } else {
                fatherNode.right = repNode;
            }
            //替代节点替换删除节点
            if (repNode != null) {
                repNode.left=node.left;
                repNode.right=node.right;
            }
        }
    }

    /**
     * 查找删除节点的替代节点
     * @param node 删除的节点
     * @return 替代节点
     */
    private Node findRepNode(Node node) {
        // 删除的是叶节点  不需要替代
        if (node.left == null && node.right == null) {
            return null;
        } else if (node.left == null) {
            // 左节点为空，右节点不空，右节点为替代节点
            return node.right;
        } else if (node.right == null) {
            // 右节点为空，左节点不空，左节点为替代节点
            return node.left;
        } else {
            // 查找右子树最小的节点作为替代节点
            return findRightMin(node.right);
        }

    }

    /**
     * 查找当前节点所在数的最小节点
     * @param right 当前根节点
     * @return 最小节点
     */
    private Node findRightMin(Node right) {
        while (right.left != null) {
            right = right.left;
        }
        if (right.right == null) {
            return right;
        } else {
            return findRightMin(right.right);
        }
    }

    /**
     * 查找节点的父节点
     * @param father 父节点
     * @param node 查找节点
     * @return 父节点
     */
    private Node findFatherNode(Node father, Node node) {
        Node left = father.left;
        Node right = father.right;
        if ((left != null && left.no == node.no) || (right != null && right.no == node.no)) {
            return father;
        }
        if (father.no > node.no) {
            return findFatherNode(left, node);
        } else {
            return findFatherNode(right, node);
        }
    }
}

@Data
class Node {
    int no;
    Node left;
    Node right;

    public Node(int i) {
        this.no = i;
    }

    /**
     * 当前节点为根节点的树的高度
     *
     * @return 高度
     */
    public int height() {
        return Math.max(leftHeight(),rightHeight() ) + 1;
    }

    /**
     * 左子树的高度
     */
    public int leftHeight() {

        return left == null ? 0 : left.height();

    }

    /**
     * 柚子树的高度
     */
    public int rightHeight() {
        return right == null ? 0 : right.height();
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
     * 添加节点
     *
     * @param node 节点
     */
    public void addNode(Node node) {
        if (node == null) {
            return;
        }
        if (this.no > node.no) {
            if (this.left == null) {
                this.left = node;
            } else {
                this.left.addNode(node);
            }
        } else {
            if (this.right == null) {
                this.right = node;
            } else {
                this.right.addNode(node);
            }
        }

        if (rightHeight() -leftHeight() >1){
            // 右子树的右子树的高度 > 右子树的右子树高度
            if (this.right!=null && this.right.leftHeight()>this.right.rightHeight()){
                this.right.rightRotate();
            }
            leftRotate();
        }
        if (leftHeight() -rightHeight() >1){
            // 左子树的右子树的高度 > 左子树的左子树高度
            if (this.left!=null && this.left.rightHeight()>this.left.leftHeight()){
                this.left.leftRotate();
            }
            rightRotate();
        }
    }

    /**
     * 当前节点进行左旋转
     *  1. 创建一个新节点，与当前节点一致
     *  2. 新节点的左子节点指向 当前节点的左子节点
     *  3. 新节点的的右子节点 设置为 当前节点的右节点的左节点
     *  4. 当前节点 设置为右子节点
     *  5. 当前节点的左子节点为新节点
     */
    public void leftRotate(){
        Node node =new Node(no);
        node.left =this.left;
        node.right =this.right.left;

        this.no =this.right.no;
        this.right=this.right.right;
        this.left=node;
    }

    /**
     * 当前节点进行右旋转
     *  1. 创建一个新节点，与当前节点一致
     *  2. 新节点的右子节点指向 当前节点的右子节点
     *  3. 新节点的的左子节点 设置为 当前节点的左节点的右节点
     *  4. 当前节点 设置为左子节点
     *  5. 当前节点的右子节点为新节点
     */
    public void rightRotate(){
        Node node =new Node(no);
        node.right =this.right;
        node.left =this.left.right;

        this.no =this.left.no;
        this.left=this.left.left;
        this.right=node;
    }


    @Override
    public String toString() {
        return "Node{" +
                "no=" + no +
                '}';
    }

    public Node searchNode(int no) {
        if (this.no == no) {
            return this;
        }
        if (this.no > no) {
            if (this.left == null) {
                System.out.println("不存在值为" + no + "的节点");
                return null;
            } else {
                return this.left.searchNode(no);
            }
        } else {
            if (this.right == null) {
                System.out.println("不存在值为" + no + "的节点");
                return null;
            } else {
                return this.right.searchNode(no);
            }
        }
    }
}
