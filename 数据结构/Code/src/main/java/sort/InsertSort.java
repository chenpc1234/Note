package sort;

import java.util.Arrays;

/**
 * 插入排序
 * @author chenpc
 * @version 1.0
 * @since 2022/3/27/03/27  10:34
 */
public class InsertSort {
    public static void main(String[] args) {
        int[] a = {1, -2, 13, 4, 55, 26, 7};
        insertSort2(a);
    }

    public static void insertSort(int[] a) {
        //int[] a = {1, -2, 13, 4, 55, 26, 7};
        // 从第二个开始 到最后一个进行插入排序
        for (int i = 1; i < a.length; i++) {
            //int indexValue = a[i-1];
            int index = i - 1; // 有序列表的尾
            int value = a[i]; // 当前数字
            // 从有序队列的尾 a[index] 向前找,
            for (int j = index; j >= 0; j--) {
                // 如果当前数字小于队列尾 ,将队列尾后移一个位置
                if (value < a[j]) {
                    a[j+1] =a[j];
                }else {
                    //如果当前数字不小于队列尾，即找到当前数字的位置了,在j后边
                    index = j+1;
                    break;
                }
            }
            //找到当前数字合适的坐标 进行更换
            a[index] =value;
        }
        System.out.println(Arrays.toString(a));
    }
    public static void insertSort2(int[] a) {
        //int[] a = {1, -2, 13, 4, 55, 26, 7};
        // 从第二个开始 到最后一个进行插入排序
        for (int i = 1; i < a.length; i++) {
            //int indexValue = a[i-1];
            int index = i - 1; // 有序列表的尾
            int value = a[i]; // 当前数字
            // 从有序队列的尾 a[index] 向前找
            while (index>= 0&&value <a[index]){
                    a[index+1] =a[index];
                    index --;
            }
            assert index+1!=i;
            a[index+1] =value;
        }
        System.out.println(Arrays.toString(a));
    }
}
