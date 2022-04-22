package algorithm.kmp;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/4/22/04/22  17:23
 */
public class KMP {
    public static void main(String[] args) {
        String find ="abcabcd";
        char[] chars = find.toCharArray();
        int[]next = next(chars);
    }

    private static int[] next(char[] chars) {
        int[] next =new int[chars.length];
        for (int i = 0; i < chars.length; i++) {

            //next[i] =
        }
        return next;
    }
}
