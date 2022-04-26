package algorithm.kmp;


/**
 * KMP算法
 * @author chenpc
 * @version 1.0
 * @since 2022/4/22/04/22  17:23
 */
public class KMP {
    public static void main(String[] args) {
        char[] find = "abcabcd".toCharArray();
        char[] base = "abcaacdabbabcabcdd".toCharArray();
        int i = indexByViolent(base, find);
        System.out.println(i);
        int v = indexByKMP(base, find);
        System.out.println(v);
    }

    /**
     * 暴力破解
     */
    private static int indexByViolent(char[] base, char[] find) {
        int i = 0;
        int j = 0;
        int baseLength = base.length;
        int findLength = find.length;
        while (i < baseLength && j < findLength) {
            if (base[i] == find[j]) {
                i++;
                j++;
            } else {
                //i后移一位
                i = i - j + 1;
                //j 归0
                j = 0;
            }
        }
        if (j == findLength) {
            return i - j;
        } else {
            return -1;
        }
    }

    /**
     * KMP查找
     */
    private static int indexByKMP(char[] base, char[] find) {
        int i = 0;
        int j = 0;
        int baseLength = base.length;
        int findLength = find.length;
        int[] maxLength = getMaxLength(find);
        while (i < baseLength && j < findLength) {
            // 当匹配时，同时后移
            if (base[i] == find[j]) {
                i++;
                j++;
            } else if (j == 0) {
                // 当第一个字符就不匹配时,i后移
                i++;
            } else {
                //1 当被匹配的数组第j位字符不匹配,当前已匹配的是0到j-1 共计j个字符
                //1.1 find[0]到find[j-1]与 base[i-j]到base[i-1] 一一对应
                //1.2 find[j]与base[i] 不同
                //2. find[0] 到 find[j-1]的前后缀最大长度是 maxLength[j - 1] ,记作 n
                // 2.1 find[0]到 find[n-1] 与 find[j-1- n] 到 find[j-1] 共计n个字符一一对应
                // 3. 对于base 数组
                //3.1 base[i-1-n] 到 base[i-1] 与 find[0]到 find[n-1] 一一对应，共计n个 无需比较
                //3.2 那么直接将j 定位到 n 处 ，比较 find[n] 与base[i] 及之后的数据即可
                j = maxLength[j - 1];
            }
        }
        if (j == findLength) {
            return i - j;
        } else {
            return -1;
        }
    }

    /**
     * 查找字符串数组的前缀、后缀 相同的最大长度
     *
     * @param str 数组
     * @return
     */
    public static int[] getMaxLength(char[] str) {
        int[] max = new int[str.length];
        max[0] = 0;
        int j = 0;
        for (int i = 1; i < max.length; i++) {
            //如果当前字段和j不相等
            while (str[i] != str[j] && j > 0) {
                j = max[j - 1];
            }
            // 如果后边的出现了
            if (str[i] == str[j]) {
                j++;
            }
            max[i] = j;
        }
        return max;
    }
}
