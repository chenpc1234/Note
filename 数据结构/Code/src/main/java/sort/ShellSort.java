package sort;

import java.util.Arrays;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/27/03/27  15:35
 */
public class ShellSort {
    public static void main(String[] args) {
        int[] a = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0, 11, 12, 13, 14, 16, 54};
        sort2(a);
    }

    /**
     * 交换式希尔排序 .分组内部使用的冒泡排序
     *
     * @param a 数组
     */
    public static void sort(int[] a) {
        // 记录第几轮
        int count = 0;
        //临时变量
        int temp;
        // 分组,第一次分[a.length/2]组、第二次分[a.length/4]组  [x]=x取整
        for (int g = a.length / 2; g >= 1; g = g / 2) {
            // 每组有 a.length/[a.length/g] 个元素
            // 从每组的第二个元素开始比较，直到数组末尾
            for (int i = g; i < a.length; i++) {
                // 分组内排序： j 从本组第一个元素开始，依次与后边的比对（冒泡）
                for (int j = i - g; j >= 0; j = j - g) {
                    // 拿本组第x个元素与 下一个元素比对
                    if (a[j] > a[j + g]) {
                        temp = a[j];
                        a[j] = a[j + g];
                        a[j + g] = temp;
                    }
                }
            }
            count++;
            System.out.println("第" + count + "轮分组后----" + Arrays.toString(a));
        }
        System.out.println(Arrays.toString(a));
    }

    /**
     * 交换式希尔排序 .分组内部使用的冒泡排序
     *
     * @param a 数组
     */
    public static void sort2(int[] a) {
        // 记录第几轮
        int count = 0;
        //临时变量
        int temp;
        // 分组,第一次分[a.length/2]组、第二次分[a.length/4]组  [x]=x取整
        for (int g = a.length / 2; g >= 1; g = g / 2) {
            // 每组有 a.length/[a.length/g] 个元素
            // 从每组的第二个元素开始比较，直到数组末尾
            for (int i = g; i < a.length; i++) {
                // 分组内排序：使用插入的方式
                {
                    int index = i; // 定义下标
                    int value = a[i]; // 当前数字
                    // 从有序队列的尾 a[index] 向前找
                    // 如果当前值 > = 此分组的前一个值 a[index - g],不用处理了
                    if (value < a[index - g]) {
                        //从有序队列的尾 a[index] 向前找
                        // 直到找到比当前元素大的数或者找到头，即找插入的点在那里
                        while (index - g >= 0 && value < a[index - g]) {
                            //上一个元素a[index - g]大,后移
                            a[index] = a[index - g];
                            //继续向前
                            index -= g;
                        }
                        //找到了插入当前数字到合适的位置
                        a[index] = value;
                    }
                }
            }
            count++;
            System.out.println("第" + count + "轮分组后----" + Arrays.toString(a));
        }
        System.out.println(Arrays.toString(a));
    }
}
