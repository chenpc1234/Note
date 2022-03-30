package algorithm.search;

import java.util.ArrayList;
import java.util.List;

/**
 * 插值查找
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/3/30/03/30  14:56
 */
public class InterpolationSearch {
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 5, 6, 7, 8};
        int index = searchFirst(a, 5);
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
        return search(a, 0, a.length - 1, value);
    }

    private static int search(int[] a, int left, int right, int value) {
        if (left > right) {
            return -1;
        }
        int mid = left + ( (value - a[left]) *(right-left)/ (a[right] - a[left]));
        if (a[mid] > value) {
            return search(a, left, mid - 1, value);
        } else if (a[mid] < value) {
            return search(a, mid + 1, right, value);
        } else {
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
        return searchAll(a, 0, a.length - 1, value);
    }

    private static List searchAll(int[] a, int left, int right, int value) {
        List res = new ArrayList();
        if (left > right) {
            return null;
        }
        int mid = left + ( (value - a[left]) *(right-left)/ (a[right] - a[left]));
        if (a[mid] > value) {
            return searchAll(a, left, mid - 1, value);
        } else if (a[mid] < value) {
            return searchAll(a, mid + 1, right, value);
        } else {
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
