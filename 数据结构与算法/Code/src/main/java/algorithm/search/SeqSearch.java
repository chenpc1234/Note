package algorithm.search;

import java.util.ArrayList;
import java.util.List;

/**
 * 线性查找
 * @author chenpc
 * @version 1.0
 * @since 2022/3/30/03/30  14:54
 */
public class SeqSearch {
    public static void main(String[] args) {
        int[] a = {1,2,3,4,5,5,6,7,8,8,9,12,14};
        int index = searchFirst(a, 5);
        System.out.println(index);
        List list = searchAll(a, 5);
        System.out.println(list);
    }

    /**
     * 查找数字出现的第一个下标
     * @param a 数组
     * @param value 数字
     * @return 下标
     */
    public static int searchFirst(int[] a ,int value){
        for (int i = 0; i < a.length; i++) {
            if(a[i] == value){
                return i;
            }
        }
        return -1;
    }
    /**
     * 查找数字出现的所有下标
     * @param a 数组
     * @param value 数字
     * @return 下标集合
     */
    public static List searchAll(int[] a , int value){
        List res = new ArrayList();
        for (int i = 0; i < a.length; i++) {
            if(a[i] == value){
                res.add(i);
            }
        }
        return res;
    }
}
