package algorithm.horse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 马踏棋盘-骑士周游- 基于图的深度优先遍历
 * @author chenpc
 * @version 1.0
 * @since 2022/4/27/04/27  15:36
 */
public class HorseChessboard {
    /**
     * 棋盘X 轴最大值
     */

    public static int X;
    /**
     * 棋盘Y 轴最大值
     */
    public static int Y;

    /**
     * 创建一个数组，标记棋盘的各个位置是否被访问过
     */
    private static boolean[] visited;
    /**
     * 如果为true,表示成功
     */
    private static boolean finished;

    public static void main(String[] args) {
        X = 8;
        Y = 8;
        // 初始化棋盘
        int[][] chessboard = new int[X][Y];
        //0~63  默认全部未访问
        visited = new boolean[X * Y];
        // 出发点X 坐标
        int point_X = 1;
        // 出发点Y 坐标
        int point_Y = 1;
        traversalChessboard(chessboard, point_X - 1, point_Y - 1, 1);
        for (int[] ints : chessboard) {
            System.out.println(Arrays.toString(ints));
        }


    }

    /**
     * @param chessboard 棋盘
     * @param x          出发点的横坐标
     * @param y          出发点的纵坐标
     * @param step       步数
     */
    private static void traversalChessboard(int[][] chessboard, int x, int y, int step) {
        chessboard[y][x] = step;
        //标记该位置已经访问
        visited[y * X + x] = true;
        //获取当前位置可以走的下一个位置的集合
        ArrayList<Point> ps = next(new Point(x, y));
        sort(ps);
        while (!ps.isEmpty()){
            Point remove = ps.remove(0);
            // 如果没有被访问过
            if (!visited[remove.y * X + remove.x]){
                traversalChessboard(chessboard, remove.x, remove.y, step+1);
            }
        }
        if(step < X * Y && !finished ) {
            chessboard[y][x] = 0;
            visited[y * X + x] =  false;
        } else {
            finished = true;
        }

    }

    private static void sort(ArrayList<Point> ps) {
        ps.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                //获取到o1的下一步的所有位置个数
                int count1 = next(o1).size();
                //获取到o2的下一步的所有位置个数
                int count2 = next(o2).size();
                if(count1 < count2) {
                    return -1;
                } else if (count1 == count2) {
                    return 0;
                } else {
                    return 1;
                }
            }

        });
    }

    /**
     * 得到下一步  可以走的点
     * @param point 当前点
     * @return 下一步点的集合
     */
    private static ArrayList<Point> next(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        Point next = new Point();
        //0
        if ((next.x = point.x + 2) < X && (next.y = point.y - 1) >= 0) {
            points.add(new Point(next));
        }
        //1
        if ((next.x = point.x + 2) < X && (next.y = point.y + 1) < Y) {
            points.add(new Point(next));
        }
        //2
        if ((next.x = point.x + 1) < X && (next.y = point.y + 2) < Y) {
            points.add(new Point(next));
        }
        //3
        if ((next.x = point.x - 1) >= 0 && (next.y = point.y + 2) < Y) {
            points.add(new Point(next));
        }
        //4
        if ((next.x = point.x - 2) >= 0 && (next.y = point.y + 1) < Y) {
            points.add(new Point(next));
        }
        //5
        if ((next.x = point.x - 2) >= 0 && (next.y = point.y - 1) >= 0) {
            points.add(new Point(next));
        }
        //6
        if ((next.x = point.x - 1) >= 0 && (next.y = point.y - 2) >= 0) {
            points.add(new Point(next));
        }
        //7
        if ((next.x = point.x + 1) < X && (next.y = point.y - 2) >= 0) {
            points.add(new Point(next));
        }

        return points;
    }
}
