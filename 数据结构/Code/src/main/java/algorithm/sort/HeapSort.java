package algorithm.sort;

import java.util.Arrays;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/4/5/04/05  15:50
 */
public class HeapSort {
    public static void main(String[] args) {
        int[] a = {80, 100, 66, 90, 55, 12, 86};
        sort(a);
    }

    public static void sort(int[] a) {
        System.out.println("堆排序");
        int max;
        for (int i = a.length / 2 - 1; i >= 0; i--) {
            adjustHeap(a, i, a.length);
            System.out.println("第" + i + "次调整后：" + Arrays.toString(a));
        }
        // 此时已经是一个大顶堆 ，将堆顶元素a[0] 与数组末尾元素a[length-1]互换
        // 互换之后其他的非叶子节点都满足大顶堆的要求，只有根节点a[0] 需要调整
        // 循环互换并调整根节点
        for (int j = a.length - 1; j > 0; j--) {
            max = a[0];
            a[0] = a[j];
            a[j] = max;
            adjustHeap(a, 0, j);
        }
        System.out.println(Arrays.toString(a));
    }

    /**
     * 把数组调整为大顶堆
     *
     * @param a      要调整的数组
     * @param i      需要调整的非叶子节点的索引
     * @param length 需要调整多少个
     */
    public static void adjustHeap(int[] a, int i, int length) {
        // 记录当前节点的值
        int value = a[i];
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {
            // 右节点在比较范围内，且右节点大于左节点,k++ 让k变为右子节点
            if (k + 1 < length && a[k] < a[k + 1]) {
                k++;
            }
            if (a[k] > value) {
                // k节点 比当前节点数大，进行交换
                // 交换之后，会影响k的子树。
                // i=k,前节点指向子节点k,调整k与k的左右子节点。
                a[i] = a[k];
                i = k;
            } else {
                break;
            }
        }
        a[i] = value;
    }

}

