package algorithm.search;

import java.util.ArrayList;
import java.util.List;

/**
 * 斐波那契查找
 * @author chenpc
 * @version 1.0
 * @since 2022/3/30/03/30  14:57
 */
public class FibonacciSearch {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 5, 6, 7, 8};
        int index = searchFirst(a, 8);
        System.out.println(index);
        List list = searchAll(a, 5);
        System.out.println(list);
    }

    /**
     * 查找数字出现的第一个下标
     *
     * @param a     数组
     * @param value 数字
     * @return 下标
     */
    public static int searchFirst(int[] a, int value) {
        int[] fib =new int[a.length];
        fib[0] =1;
        fib[1] =1;
        int i = 2;
        while (true){
            fib[i] =fib[i-1]+fib[i-2];
            if (fib[i] >= a.length){
                break;
            }
            i++;
        }
        // 现在 f[i] >= a.length // 且 f[i] = f[i-2]+f[i-1]
        //分割点是 f[i-2]
        return search(a, value,fib,i,0);
    }

    private static int search(int[] a, int value, int[] fib, int i,int left) {
        if (i < 1){
            return -1;
        }
        int mid = left +fib[i-2];
        if (a[mid] > value){
            return search(a,value,fib, i-2,0);
        }else if (a[mid] < value){
            return search(a,value,fib, i-1,mid);
        }else {
            return mid;
        }
    }
    /**
     * 查找数字出现的所有下标
     *
     * @param a     数组
     * @param value 数字
     * @return 下标集合
     */
    public static List searchAll(int[] a, int value) {
        int[] fib =new int[a.length];
        fib[0] =1;
        fib[1] =1;
        int i = 2;
        while (true){
            fib[i] =fib[i-1]+fib[i-2];
            if (fib[i] >= a.length){
                break;
            }
            i++;
        }
        // 现在 f[i] >= a.length // 且 f[i] = f[i-2]+f[i-1]
        //分割点是 f[i-2]
        return searchAll(a, value,fib,i,0);
    }

    private static List searchAll(int[] a, int value, int[] fib, int i,int left) {
        List res = new ArrayList();
        if (i < 1){
            return res;
        }
        int mid =left+ fib[i-2];
        if (a[mid] > value){
            return searchAll(a,value,fib, i-2,0);
        }else if (a[mid] < value){
            return searchAll(a,value,fib, i-1,left);
        }else {
            int index = mid;
            while (a[index] == value) {
                res.add(index);
                index--;
            }
            index = mid + 1;
            while (a[index] == value) {
                res.add(index);
                index++;
            }
            return res;
        }
    }
}
