package sort;

import java.time.temporal.Temporal;
import java.util.Arrays;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/27/03/27  21:35
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] a = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0, 11, 12, 13, 14, 16, 54};

        sort(a);
    }

    public static void sort(int a[]) {
        sort2(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }

    /**
     * 中间作为基数
     * @param a
     * @param left
     * @param right
     * @return
     */
    public static int[] sort(int a[], int left, int right) {
        int l = left;
        int r = right;
        int centre = (left + right) / 2;
        int value = a[centre];
        // 左侧逐渐右移  右侧逐渐左移
        while (l < r) {
            //
            while (a[l] < value) {
                l = l + 1;
            }
            while (a[r] > value) {
                r = r - 1;
            }
            if (l >= r) {
                break;
            }
            int temp = a[l];
            a[l] = a[r];
            a[r] = temp;

            if (a[l] == value){
                r=r-1;
            }
            if (a[r] == value){
                l=l+1;
            }

            if (l==r ){
                l=l+1;
                r=r-1;
            }
            if (left<r){
                sort(a, left, r);
            }
            if(l<right){
                sort(a,l,right);
            }
        }
        return a;
    }

    /**
     * 左侧作为基数
     * @param array
     * @param left
     * @param right
     */
    public static void sort2(int[] array, int left, int right) {
        if (left > right) {
            return;
        }
        // base中存放基准数
        int base = array[left];
        int i = left;
        int j = right;
        while (i != j) {
            // 顺序很重要，先从右边开始往左找，直到找到比base值小的数
            while (array[j] >= base && i < j) {
                j--;
            }
            // 再从左往右边找，直到找到比base值大的数
            while (array[i] <= base && i < j) {
                i++;
            }
            // 上面的循环结束表示找到了位置或者(i>=j)了，交换两个数在数组中的位置
            if (i < j) {
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        }
        // 将基准数放到中间的位置（基准数归位）
        array[left] = array[i];
        array[i] = base;
        // 递归，继续向基准的左右两边执行和上面同样的操作
        // i的索引处为上面已确定好的基准值的位置，无需再处理
        sort2(array, left, i - 1);
        sort2(array, i + 1, right);
    }

}
