package algorithm.sort;

import java.util.Arrays;

/**
 * 选择排序
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/3/26/03/26  14:52
 */
public class SelectSort {
    public static void main(String[] args) {
        //任意数组
        int[] a = {-11, 2, 43, 14, 5, 36};
        a = sort(a);
        System.out.println(Arrays.toString(a));
    }

    public static int[] sort(int[] a) {
        for (int i = 0; i < a.length; i++) {
            // 定义min为 需要遍历的第一个数 记录下标
            int min = a[i];
            int index = i;
            for (int j = i + 1; j < a.length; j++) {
                //从 遍历a[i] 之后的所有元素，找到最小值之后与a[i] 互换
                if (a[j] < min) {
                    min = a[j];
                    index = j;
                }
            }
            //把 a [i] 与 a[index]互换
            a[index] = a[i];
            a[i] = min;
        }
        return a;
    }
}
