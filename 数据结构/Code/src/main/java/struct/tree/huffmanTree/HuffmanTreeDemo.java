package struct.tree.huffmanTree;

import algorithm.sort.HeapSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 哈夫曼树
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/4/1/04/01  14:57
 */
public class HuffmanTreeDemo {
    public static void main(String[] args) {
        int[] a = {15, 1, 4, 67, 34, 8, 90, 22};
        HuffManTree huffManTree = new HuffManTree(a);
        huffManTree.preorderTraversal();
    }
}

class HuffManTree extends BinaryTree {
    /**
     * 构建哈夫曼树
     * @param a 数组
     */
    public HuffManTree(int[] a) {
        //数组排序
        int[] sort = HeapSort.sort(a);
        // 转list
        List<Node> nodes = new ArrayList();
        for (int i : sort) {
            nodes.add(new Node(i));
        }
        while (nodes.size() > 1) {
            // 取前2个
            Node left = nodes.get(0);
            Node right = nodes.get(1);
            // 构建父节点
            Node fatherNode = new Node(left.getNo() + right.getNo());
            fatherNode.left = left;
            fatherNode.right = right;
            nodes.remove(left);
            nodes.remove(right);
            nodes.add(fatherNode);
            // list 排序
            Collections.sort(nodes);
        }
        this.root = nodes.get(0);
    }
}

class BinaryTree {
    Node root;

    public BinaryTree() {
    }

    public BinaryTree(Node root) {
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
}

class Node implements Comparable<Node> {
    int no;
    Node left;
    Node right;

    public Node() {
    }

    public Node(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    /**
     * 前序遍历
     */
    public void PreorderTraversal(Node node) {
        assert node != null;
        if (node.left == null && node.right == null) {
            System.out.print("哈夫曼树叶子节点"+node + "\t");
        }else {
            System.out.print("虚拟父节点"+node + "\t");
        }
        if (node.left != null) {
            PreorderTraversal(node.left);
        }
        if (node.right != null) {
            PreorderTraversal(node.right);
        }
    }

    @Override
    public int compareTo(Node o) {
        return no - o.no;
    }

    @Override
    public String toString() {
        return "Node{" +
                "no=" + no +
                '}';
    }
}
