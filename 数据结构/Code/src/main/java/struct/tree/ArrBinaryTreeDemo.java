package struct.tree;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/4/1/04/01  17:27
 */
public class ArrBinaryTreeDemo {
    public static void main(String[] args) {
        int[] arr ={1,2,3,4,5,6,7};
        /**
         *      1
         *  2      3
         * 4 5    6  7
         */
        ArrBinaryTree arrBinaryTree = new ArrBinaryTree(arr);
        //1	2	4	5	3	6	7
        arrBinaryTree.traversal(ArrBinaryTree.TYPE_PRE);
        //1	2	4	5	3	6	7
        arrBinaryTree.traversal(ArrBinaryTree.TYPE_IN);
        //1	2	4	5	3	6	7
        arrBinaryTree.traversal(ArrBinaryTree.TYPE_POST);
    }
}
class  ArrBinaryTree{
    public static final String TYPE_PRE = "pre";
    public static final String TYPE_IN = "in";
    public static final String TYPE_POST = "post";

    int[] arr;

    public ArrBinaryTree(int[] arr) {
        this.arr = arr;
    }
    public void traversal(String type){
        switch (type){
            case TYPE_PRE:
                System.out.print("前序遍历: \t");
                preorder(0);
                System.out.println();
                break;
            case TYPE_IN:
                System.out.print("中序遍历: \t");
                inorder(0);
                System.out.println();
                break;
            case TYPE_POST:
                System.out.print("后序遍历: \t");
                postorder(0);
                System.out.println();
                break;
            default:
                System.out.println("不支持");
        }
    }
    public void preorder(int i){
        if (arr == null|| arr.length==0) {
            System.out.println("空树");
            return;
        }
        System.out.print(arr[i]+"\t");
        //向左
        if ( (2*i+1)< arr.length){
            preorder(2*i+1);
        }
        //向左右
        if ( (2*i+2)< arr.length){
            preorder(2*i+2);
        }
    }
    public void inorder(int i){
        if (arr == null|| arr.length==0) {
            System.out.println("空树");
            return;
        }
        //向左
        if ( (2*i+1)< arr.length){
            preorder(2*i+1);
        }
        System.out.print(arr[i]+"\t");
        //向左右
        if ( (2*i+2)< arr.length){
            preorder(2*i+2);
        }
    }
    public void postorder(int i){
        if (arr == null|| arr.length==0) {
            System.out.println("空树");
            return;
        }
        //向左
        if ( (2*i+1)< arr.length){
            preorder(2*i+1);
        }
        //向左右
        if ( (2*i+2)< arr.length){
            preorder(2*i+2);
        }
        System.out.print(arr[i]+"\t");
    }
}