package struct.tree.huffmanTree;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/4/12/04/12  17:22
 */
public class HuffmanCode {
    public static void main(String[] args) {
        String str ="i like you java";
        List<HuffmanNode> nodes = getNodes(str.getBytes());
        nodes.forEach(System.out::println);

    }


    /**
     *  构建HuffmanNode节点列表
     *  将传入的byte数组进行计数，记录不同的byte出现的次数
     *  Node中 byte 作为data ,次数作为权重
     * @param bytes 接收字节数组
     * @return HuffmanNode节点列表
     */
    private static List<HuffmanNode> getNodes(byte[] bytes) {
        //创建一个ArrayList
        ArrayList<HuffmanNode> nodes = new ArrayList<HuffmanNode>();
        //遍历bytes,统计每一个byte出现的次数->map[byte,count]
        Map<Byte, Integer> counts = new HashMap<>();
        for (byte b : bytes) {
            Integer count = counts.merge(b, 1, (x,y)-> {
                return x+y;
            });
            counts.put(b,count);
        }
        counts.forEach((data, count) -> {
            nodes.add(new HuffmanNode(data,count));
        });
        return nodes;
    }
}

@Data
class HuffmanNode implements Comparable<HuffmanNode> {
    Byte data;
    int weight;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(Byte data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "HuffmanNode{" +
                "data=" + data +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return weight - o.getWeight();
    }

    /**
     * 前序遍历
     */
    public void preOrder() {
        System.out.println(this);
        if (this.left != null) {
            this.left.preOrder();
        }
        if (this.right != null) {
            this.right.preOrder();
        }
    }
}