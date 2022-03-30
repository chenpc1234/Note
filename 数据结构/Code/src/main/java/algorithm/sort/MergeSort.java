package algorithm.sort;

import java.util.Arrays;

/**
 * 归并排序
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/3/29/03/29  09:50
 */
public class MergeSort {
    public static void main(String[] args) {
        int[] a = {-11, 2, 43, 14, 5, 36};
        sort(a);
    }

    /**
     * 归并排序
     *
     * @param a 待排序数组
     */
    public static void sort(int[] a) {
        // 创建临时数组，用于保存数据
        int[] temp = new int[a.length];
        mergeSort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }

    /**
     * 排序
     *
     * @param a     待排序数组
     * @param left  数组左指针
     * @param right 数组右指针
     *              //@param temp  临时数组
     */
    public static void mergeSort(int[] a, int left, int right) {
        //当左指针小于右指针时，进行分割
        if (left < right) {
            int mid = (left + right) / 2;
            // 分离左侧数组 {left -->mid}
            mergeSort(a, left, mid);
            // 分离右侧数组 {mid+1--->right}
            mergeSort(a, mid + 1, right);
            // 进行排序
            merge(a, left, right);
        }
    }

    /**
     * 左右数组 排序
     *
     * @param a     排序
     * @param left  左数组起点
     * @param right 右数组终点
     */
    private static void merge(int[] a, int left, int right) {
        int[] temp = new int[right - left + 1];
        int mid = (left + right) / 2;
        // 数组 {left -->mid}{mid+1--->right}
        // i 指针 用于遍历左数组j指针，用于遍历右数组
        int i = left;
        int j = mid + 1;
        // t 用于保存temp临时数组时记录下标位置
        int t = 0;
        // 左右必须有一个遍历完成
        while (i <= mid && j <= right) {
            //将小的数据依次放入临时数组
            if (a[i] < a[j]) {
                temp[t] = a[i];
                i++;
                t++;
            } else {
                temp[t] = a[j];
                j++;
                t++;
            }
        }
        // 左侧有剩余的
        while (i <= mid) {
            temp[t] = a[i];
            t++;
            i++;
        }
        if (j <= right) {
            temp[t] = a[j];
            t++;
            j++;
        }
        for (int value : temp) {
            a[left] = value;
            left++;
        }
        //temp{0 ->t-1}拷贝到a {left -> right}
//        while (t > 0) {
//            t--;
//            a[right] = temp[t];
//            right--;
//        }
//       或者如下写法
//        t = 0;
//        while ( left <= right) {
//            a[left] = temp[t];
//            t++;
//            left++;
//        }
    }

}
