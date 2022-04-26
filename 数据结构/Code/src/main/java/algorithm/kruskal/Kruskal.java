package algorithm.kruskal;

import java.util.Arrays;

/**
 * 克鲁斯卡尔算法  -- 计算带权图的最小生成树
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/4/26/04/26  11:15
 */
public class Kruskal {
    public static final int N = Integer.MAX_VALUE;

    public static void main(String[] args) {
        char[] datas = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G'};
        int vertex = datas.length;
        //邻接矩阵的关系使用二维数组表示,N两个点不联通
        int[][] weight = new int[][]{
                {N, 5, 7, N, N, N, 2},
                {5, N, N, 9, N, N, 3},
                {7, N, N, N, 8, N, N},
                {N, 9, N, N, N, 4, N},
                {N, N, 8, N, N, 5, 4},
                {N, N, N, 4, 5, N, 6},
                {2, 3, N, N, 4, 6, N},};

        //创建MGraph对象
        Graph graph = new Graph(vertex, datas, weight);
        graph.getMSTByKruskal();
    }
}

/**
 * 图
 */
class Graph {
    /**
     * 表示图的节点个数
     */

    int vertex;
    /**
     * 存放结点数据
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
    public Graph(int vertex, char[] data, int[][] weight) {
        this.vertex = vertex;
        this.data = data;
        this.weight = weight;
    }

    /**
     * 显示图的邻接矩阵
     */
    public void showGraph() {
        for (int[] link : this.weight) {
            System.out.println(Arrays.toString(link));
        }
    }

    public int getEdgeNum() {
        int count = 0;
        int[] ends = new int[vertex];
        for (int[] ints : weight) {
            for (int anInt : ints) {
                if (anInt != Integer.MAX_VALUE) {
                    count++;
                }
            }
        }
        return count/2;
    }

    /**
     * 克鲁斯卡尔算法得到最小生成树
     */
    public void getMSTByKruskal() {
        // 初始化终点，终点为自己
        int[] ends = new int[vertex];
        for (int i = 0; i < vertex; i++) {
            ends[i] =i;
        }
        //step1. 获取所有边的集合
        EData[] edges = getEdges();
        //step2. 对边进行排序
        sortEdges(edges);
        //step3. 遍历所有的边,加入集合中
        EData[] rets = new EData[vertex-1];
        int index =0;
        for (EData edge : edges) {
            //获取到第i条边的第一个顶点(起点)
            int p1 = edge.startIndex;
            //获取到第i条边的第2个顶点
            int p2 = edge.endIndex;
            //是否构成回路 p1 和p2的终点不是同一个
            if(ends[p1]!= ends[p2]) {
                // 加入结果集
                rets[index++] = edge;
                // 开始点的p1 ,结束节点是p2
                // 得到p1与p2 连同之后,p1和p2的终点就是Max(ends[p1],ends[p2])
                int maxEnd = Math.max(ends[p1],ends[p2]);
                // 所有以p1 p2 为终点的新终点都是Max(ends[p1],ends[p2])
                for (int i = 0; i < ends.length; i++) {
                    if (ends[i] == p1 || ends[i] ==p2){
                        ends[i] =maxEnd;
                    }
                }
            }
        }
        // step4 展示结果集
        for (EData ret : rets) {
            System.out.println(ret);
        }
    }
    /**
     * 功能：对边进行排序处理, 冒泡排序
     * @param edges 边的集合
     */
    private void sortEdges(EData[] edges) {
        for(int i = 0; i < edges.length - 1; i++) {
            for(int j = 0; j < edges.length - 1 - i; j++) {
                if(edges[j].weight > edges[j+1].weight) {
                    EData tmp = edges[j];
                    edges[j] = edges[j+1];
                    edges[j+1] = tmp;
                }
            }
        }
    }
    /**
     * 功能: 获取图中边，放到EData[] 数组中，后面我们需要遍历该数组
     * 是通过matrix 邻接矩阵来获取
     * EData[]
     */
    private EData[] getEdges() {
        int index = 0;
        EData[] edges = new EData[getEdgeNum()];
        for (int i = 0; i < vertex; i++) {
            for (int j = i + 1; j < vertex; j++) {
                if (weight[i][j] != Integer.MAX_VALUE) {
                    edges[index++] = new EData(data[i], i,data[j],j,weight[i][j]);
                }
            }
        }
        return edges;
    }

}

/**
 * 创建一个类EData ，它的对象实例就表示一条边
 */
class EData {
    char startName;
    int startIndex;
    char endName;
    int endIndex;
    int weight;

    public EData(char startName, int startIndex, char endName, int endIndex, int weight) {
        this.startName = startName;
        this.startIndex = startIndex;
        this.endName = endName;
        this.endIndex = endIndex;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "EData [<" + startName + ", " + endName + ">= " + weight + "]";
    }


}
