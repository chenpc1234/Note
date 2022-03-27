package recursion;

import java.util.Arrays;

/**
 * 递归解决八皇后问题
 * @author chenpc
 * @version 1.0
 * @since 2022/3/26/03/26  11:49
 */
public class EightQueens {

    int maxSize = 8;
    /**
     * 8位长度的数组，分别表示第一行到第八行皇后的位置
     */
    int[] arr = new int[maxSize];
    /**
     * 统计解法
     */
    static int count = 0;
    /**
     * 在判断时错误的次数
     */
    static int error = 0;
    /**
     * 在判断时，成功的次数
     */
    static int success=0;
    public static void main(String[] args) {
        new EightQueens().put(0);
        System.out.println(count);
        System.out.println(error);
        System.out.println(success);
    }
    /**
     * 检查第 N个皇后 和之前的 皇后是否冲突
     * @param n 第N个皇后在int[] arr的下标
     * @return true = 不冲突
     */
    public boolean check(int n) {
        /**
         *
         * 循环判断之前的 arr[0] ~ arr[n-1] 是否与 a[n] 冲突，冲突的三种情况
         * 1. arr[i] == arr[n] 表示在同一列
         * 2. n - i = arr[n] - arr[i] 表示 arr[n]在arr[i] ↘ 列
         * 3. n - i = arr[i] - arr[n] 表示 arr[n]在arr[i] ↙ 列
         */
        for (int i = 0; i < n; i++) {
            if (arr[i] == arr[n] || Math.abs(n - i) == Math.abs(arr[n] - arr[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 放置第N个皇后
     *
     * @param n
     */
    public void put(int n) {
        /**
         * n=8 说明开始放arr[8] 此时已经成功放了8个，游戏结束
         */
        if (n == maxSize) {
            count++;
            System.out.println(Arrays.toString(arr));
            return;
        }
        //依次放入皇后.并判断是否冲突
        // n表示放置的第几个皇后  i表示放在第几列
        for (int i = 0; i < maxSize; i++) {
            arr[n] = i;
            // 如果放完之后，检查当前皇后和之前的都不冲突，则放置下一个
            if (check(n)) {
                success++;
                put(n + 1);
            }
            //如果冲突 , i++ 当前皇后放在下一个位置
            error ++;
        }
    }

}
