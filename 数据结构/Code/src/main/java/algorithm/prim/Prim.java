package algorithm.prim;

import java.util.Arrays;

/**
 * 普利姆算法- 计算最小生成树
 * @author chenpc
 * @version 1.0
 * @since 2022/4/26/04/26  09:36
 */
public class Prim {
    public static final int N = Integer.MAX_VALUE;
    public static void main(String[] args) {
        char[] datas = new char[]{'A','B','C','D','E','F','G'};
        int vertex = datas.length;
        //邻接矩阵的关系使用二维数组表示,N两个点不联通
        int [][] weight=new int[][]{
                {N,5,7,N,N,N,2},
                {5,N,N,9,N,N,3},
                {7,N,N,N,8,N,N},
                {N,9,N,N,N,4,N},
                {N,N,8,N,N,5,4},
                {N,N,N,4,5,N,6},
                {2,3,N,N,4,6,N},};

        //创建MGraph对象
        MGraph graph = new MGraph(vertex,datas,weight);
        graph.getMSTByPrim(1);
    }
}

/**
 * 图
 */
class MGraph {
    /**
     *表示图的节点个数
     */

    int vertex;
    /**
     *存放结点数据
     */
    char[] data;
    /**
     * 邻接矩阵
     */
    int[][] weight;

    /**
     * @param vertex 顶点个数
     * @param data   顶点集合
     * @param weight 邻接矩阵
     */
    public MGraph(int vertex,char[] data, int[][] weight) {
        this.vertex = vertex;
        this.data = data;
        this.weight = weight;
    }
    /**
     *显示图的邻接矩阵
     */
    public void showGraph() {
        for(int[] link: this.weight) {
            System.out.println(Arrays.toString(link));
        }
    }
    /**
     * 编写prim算法，得到最小生成树
     * @param v 表示从图的第几个顶点开始生成'A'->0 'B'->1...
     */
    public void getMSTByPrim(int v) {
        //visited[] 标记结点(顶点)是否被访问过
        int visited[] = new int[this.vertex];
        //把当前这个结点标记为已访问
        visited[v] = 1;
        // 记录出发顶点下标
        int from = -1;
        // 记录到达顶点下标
        int to = -1;
        // 记录树的总权值
        int totalWeight =0;
        int minWeight = Integer.MAX_VALUE;
        //因为有vertex个顶点，普利姆算法结束后，有vertex-1条边，遍历vertex-1 次取N 条边
        int num = vertex -1;
        while (num > 0){
            num --;
            // i结点表示被访问过的结点
            for(int i = 0; i < this.vertex; i++) {
                // i被访问过
                if (visited[i] == 1) {
                    for(int j = 0; j< this.vertex;j++){
                        if (visited[j] == 0 && this.weight[i][j] < minWeight) {
                            minWeight = this.weight[i][j];
                            from = i;
                            to = j;
                        }
                    }
                }
            }
            totalWeight+=minWeight;
            System.out.println("边<" + data[from] + "," + data[to] + "> 权值:" + minWeight);
            //将当前这个结点标记为已经访问
            visited[to] = 1;
            //minWeight 重新设置为最大值 10000
            minWeight = Integer.MAX_VALUE;
        }
        System.out.println(totalWeight);
    }
}