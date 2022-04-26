package algorithm.sort;

import java.util.Arrays;

/**
 * 快速排序
 * @author chenpc
 * @version 1.0
 * @since 2022/3/27/03/27  21:35
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] a = {8, 22, -23, -23, -24, -23, -23, -23, -23, -23, -23, 0, -2, 21, 3123, 11, 42};
        int[] b = {1, 0, -1, 0, 1, 0, -1};
        sort(a);
    }

    public static void sort(int[] a) {
        sort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }

    /**
     * 中间作为基数
     *
     * @param a     数组
     * @param left  左侧起始点
     * @param right 右侧结束点
     * @return 排序后数组
     */
    public static void sort(int[] a, int left, int right) {
        if (left >= right){
            return;
        }
        int l = left;
        int r = right;
        int value = a[(left + right) / 2];
        int[] b = new int[right + 1 - left];
        int temp;
        // 左侧逐渐右移  右侧逐渐左移
        while (l < r) {
            //左侧找比base 大的数
            while (a[l] < value) {
                l++;
            }
            //右侧找比base小的数
            while (a[r] > value) {
                r--;
            }
            //说明左右都指向了基数位置
            if (l == r) {
                break;
            }
            //左右值互换
            temp = a[l];
            a[l] = a[r];
            a[r] = temp;
            // 此时 a[r] >= base，并且r右边的全部都大于base
            // 此时 a[l] <= base，并且l左边的全部都小于base
            // 右侧指针左移
            r--;
            // 左侧指针右移
            l++;
        }
        // index 图示  [left -> r-1] l=r [l+1 - >right]
        // l与r 相等说明指向了基数值 即a[l]=base,但不一定等于baseIndex 看看例如
        // {base,base,base,x,x} 或者  {x,x,base,base,base}
        // 两种情况 baseIndex =2
        // 但不一定指向baseIndex 第一个 l=r=1 ，第二个 l=r=3,
        // 此时可以跳过基数值去进行左右两侧的递归排序
        if (l == r) {
            // ||-23, -23, -23, -2, 0,||
            // 计算基于几点的偏移量
            int baseIndex = (left + right) / 2;
            l = l < baseIndex ? baseIndex : l;
            r = r > baseIndex ? baseIndex : r;
            // 向右递归  从左指针 l+1 ->  right
            // 向左递归  从左指针 left <- r-1
            l++;
            r--;
        } else {
            //r 一定小于 l [left-r] 是左数组 [l-right]是右数组
            // 并且 r +1 =l
        }
        // 向左递归 ,r逐渐变小，直到左侧起点
        sort(a, left, r);
        sort(a, l, right);
    }

    /**
     * 左侧作为基数
     *
     * @param array 数组
     * @param left  左侧起始点
     * @param right 右侧结束点
     */
    public static void sortBaseOnLeft(int[] array, int left, int right) {
        if (left > right) {
            return;
        }
        // base中存放基准数
        int base = array[left];
        int i = left;
        int j = right;
        while (true) {
            // 先从右边开始往左找，直到找到比base值小的数
            while (array[j] >= base && i < j) {
                j--;
            }
            // 再从左往右边找，直到找到比base值大的数
            while (array[i] <= base && i < j) {
                i++;
            }
            // 当i 与j 还没有相遇时，元素互换
            if (i < j) {
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
            // 当i 与 j 相遇时退出
            if (i == j) {
                break;
            }
        }
        // 将基准数放到中间的位置（基准数归位）
        array[left] = array[i];
        array[i] = base;
        // 递归，继续向基准的左右两边执行和上面同样的操作，基准值不动
        sortBaseOnLeft(array, left, i - 1);
        sortBaseOnLeft(array, i + 1, right);
    }

    /**
     * 右侧作为基数
     *
     * @param array 数组
     * @param left  左侧起始点
     * @param right 右侧结束点
     */
    public static void sortBaseOnRight(int[] array, int left, int right) {
        if (left > right) {
            return;
        }
        // base中存放基准数
        int base = array[right];
        int l = left;
        int r = right;
        while (true) {
            // 从左往右边找，直到找到比base值大的数
            while (array[l] <= base && l < r) {
                l++;
            }
            // 先从右边开始往左找，直到找到比base值小的数
            while (array[r] >= base && l < r) {
                r--;
            }
            // 当i 与j 还没有相遇时，元素互换
            if (l < r) {
                int tmp = array[l];
                array[l] = array[r];
                array[r] = tmp;
            }
            // 当i 与 j 相遇时退出
            if (l == r) {
                break;
            }
        }
        // 将基准数放到中间的位置（基准数归位）
        array[right] = array[r];
        array[r] = base;
        // 递归，继续向基准的左右两边执行和上面同样的操作，基准值不动
        sortBaseOnRight(array, left, r - 1);
        sortBaseOnRight(array, r + 1, right);
    }

}
