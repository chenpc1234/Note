package algorithm.sort;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author chenpc
 * @version 1.0
 * @since 2022/3/26/03/26  14:33
 */
public class BubbleSort {

    public static void main(String[] args) {
        //任意数组
        int [] a = {-11,2,43,14,5,36};
        a =sort(a);
        System.out.println(Arrays.toString(a));
    }
    public static int[] sort(int [] a){
        int temp= 0;

        for (int i = a.length-1 ; i >0 ; i--) {

            // 从0开始，依次比较a[j]与a[j+1] 的大小, 并交换
            // j+1 是最后一个 那么 j 最大能与 a.length-2
            boolean flag = false; // 默认没有元素互换
            for (int j = 0; j < i; j++) {
                if (a[j]>a[j+1]){
                    flag =true;  //发生了元素互换
                    temp =a[j];
                    a[j] =a[j+1];
                    a[j+1]=temp;
                }
            }
            //每轮交换确定最大一个值，并且位于最后,下一次只需要确认到确认值之前

            // 如果一轮中都没有 进行元素互换，那么可以提前结束
            if (!flag) {
                break;
            }
        }
        return  a;
    }
}
