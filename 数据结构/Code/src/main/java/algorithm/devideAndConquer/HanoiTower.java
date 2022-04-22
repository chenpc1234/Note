package algorithm.devideAndConquer;

/**
 *
 * 汉诺塔问题
 * @author chenpc
 * @version 1.0
 * @since 2022/4/22/04/22  10:12
 */
public class HanoiTower {
    public static void main(String[] args) {
        move(10, "A", "B", "C");
        System.out.println(count);
    }
static  int count =0;

    public static void move(int n, String a, String b, String c) {
        if (n == 1) {
            count++;
            System.out.println("第" + n + "个盘子 " + a + "---> " + c);
        }
        if (n >= 2) {
            //n-1 个从A --B
            move(n - 1, a, c, b);
            count++;
            // 第N个 A--C
            System.out.println("第" + n + "个盘子 " + a + "---> " + c);
            // n-1个 B--C
            move(n - 1, b, a, c);
        }
    }

}
