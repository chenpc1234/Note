package struct.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author 陈鹏程
 */
public class Graph {
    /**
     * 存储顶点集合
     */
    private ArrayList<Node> vertexList;
    /**
     * 存储图对应的邻结矩阵
     */
    private int[][] edges;
    /**
     * 表示边的数目
     */
    private int numOfEdges;
    public static void main(String[] args) {

		//结点的个数
        int n = 8;
		//创建图对象
		Graph graph = new Graph(n);
        String[] vertexes = {"1", "2", "3", "4", "5", "6", "7", "8"};
        //循环的添加顶点
        for (String vertex : vertexes) {
            graph.insertVertex(vertex);
        }
        //构建邻接矩阵
        graph.insertEdge(0, 1, 1);
        graph.insertEdge(0, 2, 1);
        graph.insertEdge(1, 3, 1);
        graph.insertEdge(1, 4, 1);
        graph.insertEdge(3, 7, 1);
        graph.insertEdge(4, 7, 1);
        graph.insertEdge(2, 5, 1);
        graph.insertEdge(2, 6, 1);
        graph.insertEdge(5, 6, 1);
        //显示一把邻结矩阵
        graph.showGraph();
		System.out.println(graph.numOfEdges);
		//测试一把，我们的dfs遍历是否ok
        System.out.println("深度遍历");
    	graph.dfsPrint();
		// A->B->C->D->E [1->2->4->8->5->3->6->7]
        System.out.println("广度优先!");
        graph.bfsPrint();
        // A->B->C->D-E [1->2->3->4->5->6->7->8]

    }

	/**
	 * 构造方法
	 * @param n 顶点个数
	 */
	public Graph(int n) {
        //初始化矩阵和vertexList
        edges = new int[n][n];
        vertexList = new ArrayList<Node>(n);
        numOfEdges = 0;

    }
    /**
	 * 得到第一个邻接结点的下标 w
     * @param index 当前顶点的下标
     * @return 如果存在就返回对应的下标，否则返回-1
     */
    public int getFirstNeighbor(int index) {
    	// 遍历顶点所在行，判断下标所在行哪个是 1
        for (int j = 0; j < vertexList.size(); j++) {
            if (edges[index][j] > 0) {
                return j;
            }
        }
        return -1;
    }

	/**
	 * 根据前一个邻接结点的下标来获取下一个邻接结点
	 * @param v1 当前顶点
	 * @param v2 邻接节点
	 * @return 下一个邻接节点
	 */
    public int getNextNeighbor(int v1, int v2) {
        for (int j = v2 + 1; j < vertexList.size(); j++) {
            if (edges[v1][j] > 0) {
                return j;
            }
        }
        return -1;
    }

	/**
	 *深度优先遍历算法
	 * @param i 顶点下标
	 */
    private void dfs( int i) {
        //首先我们访问该结点,输出
		Node node =vertexList.get(i);
        System.out.print(node.getName() + "->");
        //将结点设置为已经访问
		node.isVisited = true;
        //查找顶点i的第一个邻接结点
        int adjacentIndex = getFirstNeighbor(i);
        // 当相邻节点存在时
        while (adjacentIndex != -1) {
        	//下一个节点没有被访问则进行深度优先遍历
			Node adjacentNode = vertexList.get(adjacentIndex);
			if (!adjacentNode.isVisited) {
                dfs(adjacentIndex);
            }else {
				// 查找下一个节点
				adjacentIndex = getNextNeighbor(i, adjacentIndex);
			}
        }
    }

	/**
	 * 打印深度优先
	 */
	public void dfsPrint() {
    	// 循环是可能有的节点是单独的节点，没有任何相邻节点
		// 或者图由两个图构成
        for (int i = 0; i < vertexList.size(); i++) {
			Node node = vertexList.get(i);
			// 节点没有被访问过，对节点进行深度优先遍历
			if (!node.isVisited) {
				dfs(i);
            }
        }
    }

	/**
	 * 对一个结点进行广度优先遍历的方法
	 * @param i 顶点下标
	 */
	private void bfs(int i) {
		//访问结点，输出结点信息
		Node node = vertexList.get(i);
		//标记为已访问
		node.isVisited= true;
		System.out.print(node.getName() + "=>");

        int queueIndex; // 表示队列的头结点对应下标
        int adjacentIndex; // 邻接结点w

        //队列，记录结点访问的顺序
        LinkedList queue = new LinkedList();
        //将结点加入队列
        queue.addLast(i);

        while (!queue.isEmpty()) {
            //取出队列的头结点下标
			queueIndex = (Integer) queue.removeFirst();
            //得到头邻接结点的下标 w
			adjacentIndex = getFirstNeighbor(queueIndex);
			//如果存在邻接节点
            while (adjacentIndex != -1) {
            	//得到相邻节点
				Node adjacentNode = vertexList.get(adjacentIndex);
				// 没有访问过
                if (!adjacentNode.isVisited) {
                    System.out.print(adjacentNode.getName() + "=>");
                    //标记已经访问
					adjacentNode.isVisited = true;
                    //加入队列 记录访问顺序
                    queue.addLast(adjacentIndex);
                }else {
                	//访问过之后，取下一个相邻节点
					adjacentIndex = getNextNeighbor(queueIndex, adjacentIndex);
				}

            }
        }

    }

	/**
	 * 遍历所有的结点，都进行广度优先搜索
	 */
    public void bfsPrint() {
        for (int i = 0; i < vertexList.size(); i++) {
        	Node node =vertexList.get(i);
            if (!node.isVisited) {
                bfs(i);
            }
        }
    }


	/**
	 * 显示图对应的邻接矩阵
	 */
	public void showGraph() {
        for (int[] link : edges) {
            System.err.println(Arrays.toString(link));
        }
    }


	/**
	 * 插入结点
	 * @param vertex 节点名称
	 */
	public void insertVertex(String vertex) {
        vertexList.add(new Node(vertex));
    }
    /**
     * @param v1     表示点的下标即使第几个顶点  "A"-"B" "A"->0 "B"->1
     * @param v2     第二个顶点对应的下标
     * @param weight 表示
     */
    public void insertEdge(int v1, int v2, int weight) {
        edges[v1][v2] = weight;
        edges[v2][v1] = weight;
        numOfEdges++;
    }
}

@Data
class Node {
    String name;
    boolean isVisited;

    public Node(String name) {
        this.name = name;
        this.isVisited = false;
    }
}
