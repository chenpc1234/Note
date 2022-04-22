package algorithm.dynamicProgramming;

import java.util.HashMap;

/**
 * 0-1 背包问题
 * @author chenpc
 * @version 1.0
 * @since 2022/4/22/04/22  11:16
 */
public class BackPack {
    public static void main(String[] args) {
        // 背包容量
        int backPackCapacity = 4;
        // 物品数目
        int itemCount = 3;
        //物品信息
        String[] item = {"吉他", "音箱", "笔记本"};
        //物品重量
        int[] w = {1, 4, 3};
        //物品价格
        int[] p = {1500, 3000, 2000};
        // 构建二位数组
        int[][] v = new int[4][5];
        HashMap<String,String> result=new HashMap<>();
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 5; j++) {
                if (w[i-1] > j) {
                    v[i][j] = v[i - 1][j];
                    String oldKey = i-1+","+j;
                    String old = result.get(oldKey);
                    if (old==null){
                        old="";
                    }
                    String key= i+","+j;
                    result.put(key, old);
                } else {
                    int s = j - w[i-1];

                  // v[i][j] = Math.max(p[i-1] + v[i - 1][s], v[i - 1][s]);
                    if(p[i-1] + v[i - 1][s] > v[i - 1][s]){
                        v[i][j] = p[i-1] + v[i - 1][s];
                        String oldKey = i-1+","+s;
                        String old = result.get(oldKey);
                        if (old==null){
                            old="";
                        }
                        String key= i+","+j;
                        result.put(key, old+item[i-1]);
                    }else {
                        v[i][j] =  v[i - 1][s];
                        String oldKey = i-1+","+j;
                        String old = result.get(oldKey);
                        String key= i+","+j;
                        result.put(key, old);
                    }
                }
            }
        }

        result.forEach((s, s2) -> {
            System.out.println(s + "\t" +s2);
        });
    }
}
