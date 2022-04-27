package algorithm.floyd;

import java.util.Arrays;

/**
 * 弗洛伊德算法---计算带权图内每个顶点到其他节点的最短距离
 * @author chenpc
 * @version 1.0
 * @since 2022/4/27/04/27  20:57
 */
public class FloydAlgorithm {
	public static final int N = 65535;
	public static void main(String[] args) {
		char[] datas = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G'};
		int vertex = datas.length;
		//邻接矩阵的关系使用二维数组表示,N两个点不联通
		int[][] weight = new int[][]{
				{0, 5, 7, N, N, N, 2},
				{5, 0, N, 9, N, N, 3},
				{7, N, 0, N, 8, N, N},
				{N, 9, N, 0, N, 4, N},
				{N, N, 8, N, 0, 5, 4},
				{N, N, N, 4, 5, 0, 6},
				{2, 3, N, N, 4, 6, 0},};
		
		//创建 Graph 对象
		Graph graph = new Graph(vertex, datas,weight);
		graph.showGraph();
		graph.floyd();
		System.out.println("-------------------------");
		graph.showGraph();
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
	 * 构造方法
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
	 * 弗洛伊德算法，获取每个顶点到其他顶点的最短路径
	 */
	public void floyd() {
		// 构建前驱节点、 前驱节点：节点X到节点Y的全路径中Y之前的那个节点
		int[][] pre = new int[vertex][vertex];
		for (int i = 0; i < vertex; i++) {
			Arrays.fill(pre[i], i);
		}
		//对中间顶点遍历， k 就是中间顶点的下标 [A, B, C, D, E, F, G]
		for(int k = 0; k < weight.length; k++) { //
			//从i顶点开始出发 [A, B, C, D, E, F, G]
			for(int i = 0; i < weight.length; i++) {
				//到达j顶点 // [A, B, C, D, E, F, G]
				for(int j = 0; j < weight.length; j++) {
					// i -> j 的 原始距离是  weight[i][j]
					int oldLength = weight[i][j];
					// 经过中间节点K节点后 i ->k->j 新距离weight[i][k] + weight[k][j];
					int newLength = weight[i][k] + weight[k][j];
					if(newLength < oldLength) {
						weight[i][j] = newLength;//更新距离
						pre[i][j] = pre[k][j];//更新前驱顶点
					}
				}
			}
		}

	}
}

