package algorithm.dijkstra;


import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * 迪杰斯特拉算法 -- 计算图内某顶点到其他各个顶点的最短路径
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/4/27/04/27  10:20
 */
public class Dijkstra {
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
        //  graph.showGraph();
        graph.dsj(0);
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

    /**
     * 迪杰斯特拉算法
     *
     * @param index 出发点下标
     */
    public void dsj(int index) {
        //得到index 点到其他顶点的距离
        int[] length = weight[index];
        length[index] = 0;
        // 记录节点是否被访问过了
        int[] vertexes = new int[this.vertex];
        vertexes[index] = 1;
        // 记录节点的前驱节点,初始化为-1 没有任何前驱节点
        int[] pre = new int[this.vertex];
        Arrays.fill(pre, -1);
        // 循环处理vertex-1 次
        for (int i = 0; i < vertex - 1; i++) {
            getMinAndHandle(index, length, vertexes, pre);
        }
        // 节点到
        System.out.println(Arrays.toString(length));
        for (int i = 0; i < vertex; i++) {
            System.out.println("顶点" + data[index] + "到节点" + data[i] + "的最短距离是" + length[i]+
                    "\t\t\t路径是 "+getPath(index, pre, i));
        }
    }

    /**
     * 获取顶点到当前节点的路径
     * @param start 起点
     * @param pre  前置节点
     * @param i 当前节点
     * @return 路径
     */
    private String getPath(int start, int[] pre, int i) {
        StringBuilder path = new StringBuilder();
        if (pre[i] == -1) {
            path.append("当前节点就是起点");
            return path.toString();
        }
        while ( i != start) {
            path.insert(0, "->"+data[i]);
            i = pre[i];
        }
        path.insert(0,data[start]);
        return path.toString();
    }

    /**
     * 得到最短节点并处理
     * @param index 起点
     * @param length 起点到其他节点的路径数组
     * @param vertexes 节点是否被访问数组
     * @param pre 节点前置节点--计算路径使用
     */
    private void getMinAndHandle(int index, int[] length, int[] vertexes, int[] pre) {
        //1. 得到length 中最小的值的下标
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < length.length; i++) {
            // 下标最小并且没有被访问
            if (length[i] < min && vertexes[i] == 0) {
                min = length[i];
                minIndex = i;
            }
        }
        // 标记最短的下标为已访问,更新最短路径
        vertexes[minIndex] = 1;
        // 如果当前节点的前置节点为空
        if (pre[minIndex] == -1) {
            pre[minIndex] = index;
        }
        //得到节点minIndex 的相邻未访问节点
        for (int i = 0; i < weight[minIndex].length; i++) {
            if (weight[minIndex][i] != Integer.MAX_VALUE && vertexes[i] == 0) {
                // 原来到出发点的距离是oldLength
                int oldLength = length[i];
                // 新的到出发点的距离newLength 出发点到minIndex 的距离+出发点到minIndex到相邻节点的距离
                int newLength = length[minIndex] + weight[minIndex][i];
                // 判断并处理
                if (oldLength > newLength) {
                    length[i] = newLength;
                    pre[i] = minIndex;
                }
            }
        }
    }
}
