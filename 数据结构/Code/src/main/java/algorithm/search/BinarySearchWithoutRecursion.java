package algorithm.search;

/**
 *
 * 非递归的二分查找算法
 * @author chenpc
 * @version 1.0
 * @since 2022/4/22/04/22  09:52
 */
public class BinarySearchWithoutRecursion {
    public static void main(String[] args) {
        int[] a = {1,2,3,4,5,5,6,7,8,8,9,12,14};
        int index = searchFirst(a, -5);
        System.out.println(index);
    }

    private static int searchFirst(int[] a, int i) {
        int left  =0 ;
        int right =a.length-1;
        while (left <= right){
            int mid = left+right/2;
            if (a[mid] == i){
                return mid;
            }else if (a[mid] >i){
                right = mid-1;
            }else {
                left =mid+1;
            }
        }
        return -1;
    }
}
