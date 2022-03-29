package sort;

import java.util.Arrays;

/**
 * 基数排序，桶排序
 * @author chenpc
 * @version 1.0
 * @since 2022/3/29/03/29  10:32
 */
public class RadixSort {
    public static void main(String[] args) {
        int[] a = {11, 2, 143, 5, 36};
        sort(a);
        double[] b = {-11.05, -11.02, 11.1, 2, 143, 14, 5, 36};
        sortDoubleWithMinus(b);
    }

    /**
     * 排序
     * @param a 数组
     */
    public static void sort(int[] a) {
        // 1. 定义10个桶，每个桶最多放a.length个数
        int[][] bucket = new int[10][a.length];
        // 统计每个桶放了多少个数
        int[] bucketCount = new int[10];
        // 2. 定义轮数，数字除以 10^n 再对10取模得到当前位置的数
        int n = 0;
        while (true){
            System.out.println("第"+n+"轮");
            // 除数
            int divisor = (int) Math.pow(10, n);
            // 统计轮数
            n++;
            for (int j = 0; j < a.length; j++) {
                //取模
                int element = a[j]/divisor % 10;
                // 放入对应的桶中
                bucket[element][bucketCount[element]] = a[j];
                // 对应桶数+1
                bucketCount[element]++;
            }
            // 如果全被放入了0号桶，结束
            if (bucketCount[0] == a.length){
                break;
            }
            // 取10个桶的数据 放回原数组
            int index = 0;
            for (int j = 0; j < 10; j++) {
                // 判断桶里有没有数字
                if (bucketCount[j] != 0) {
                    // 桶里有数，放回原数组
                    for (int k = 0; k < bucketCount[j]; k++) {
                        a[index] = bucket[j][k];
                        index++;
                    }
                    // 桶里的数字放回数组后，清空桶
                    bucketCount[j] = 0;
                }
            }
            System.out.println(Arrays.toString(a));
        }
        System.out.println(Arrays.toString(a));
    }

    /**
     * 对带负数的进行排序
     * @param a 数组
     */
    public static void sortWithMinus(int[] a) {
        // 1. 计算最大数是多少位的
        int max = a[0];
        int min = a[0];
        for (int i = 0; i < a.length; i++) {
            max = max > a[i] ? max : a[i];
            min = min < a[i] ? min : a[i];
        }
        int maxLength = Math.max((Math.abs(max) + "").length(), (Math.abs(min) + "").length());
        System.out.println("最大长度是：" + maxLength);
        // 2. 定义20个桶，每个桶最多放a.length个数
        int[][] bucket = new int[20][a.length];
        // 统计每个桶放了多少个数
        int[] bucketCount = new int[20];
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < a.length; j++) {
                int element = a[j] / (int) Math.pow(10, i) % 10;
                element = element + 10;
                // 放入对应的桶中
                bucket[element][bucketCount[element]] = a[j];
                // 对应桶数+1
                bucketCount[element]++;
            }
            // 取10个桶的数据 放回原数组
            int index = 0;
            for (int j = 0; j < 20; j++) {
                // 判断桶里有没有数字
                if (bucketCount[j] != 0) {
                    // 桶里有数，放回原数组
                    for (int k = 0; k < bucketCount[j]; k++) {
                        a[index] = bucket[j][k];
                        index++;
                    }
                    // 桶里的数字放回数组后，清空桶
                    bucketCount[j] = 0;
                }
            }
            System.out.println(Arrays.toString(a));
        }
        System.out.println(Arrays.toString(a));
    }

    /**
     *  对double类型进行排序
     * @param a 排序的数组
     */
    public static void sortDouble(double[] a) {
        int length = 0;
        for (double v : a) {
            length = length > ((v + "").length() - (v + "").indexOf(".")) ? length : ((v + "").length() - (v + "").indexOf("."));
        }
        System.out.println(length);
        for (double v : a) {
            v = v * Math.pow(10, length);
        }
        sortDoubleWithMinus(a);
        for (double v : a) {
            v = v / Math.pow(10, length);
        }
        System.out.println("-----------" + Arrays.toString(a));
    }

    public static void sortDoubleWithMinus(double[] a) {
        // 1. 计算最大数是多少位的
        double max = a[0];
        double min = a[0];
        for (int i = 0; i < a.length; i++) {
            max = max > a[i] ? max : a[i];
            min = min < a[i] ? min : a[i];
        }
        int maxLength = Math.max((Math.abs(max) + "").length(), (Math.abs(min) + "").length());
        max = maxLength - 3;
        System.out.println("最大长度是：" + maxLength);
        // 2. 定义20个桶，每个桶最多放a.length个数
        double[][] bucket = new double[20][a.length];
        // 统计每个桶放了多少个数
        int[] bucketCount = new int[20];
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < a.length; j++) {
                int element = (int) a[j] / (int) Math.pow(10, i) % 10;
                element = element + 10;
                // 放入对应的桶中
                bucket[element][bucketCount[element]] = a[j];
                // 对应桶数+1
                bucketCount[element]++;
            }
            // 取10个桶的数据 放回原数组
            int index = 0;
            for (int j = 0; j < 20; j++) {
                // 判断桶里有没有数字
                if (bucketCount[j] != 0) {
                    // 桶里有数，放回原数组
                    for (int k = 0; k < bucketCount[j]; k++) {
                        a[index] = bucket[j][k];
                        index++;
                    }
                    // 桶里的数字放回数组后，清空桶
                    bucketCount[j] = 0;
                }
            }
            System.out.println(Arrays.toString(a));
        }
        System.out.println(Arrays.toString(a));
    }
}
