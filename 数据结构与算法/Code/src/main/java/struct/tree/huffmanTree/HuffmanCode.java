package struct.tree.huffmanTree;

import lombok.Data;

import java.util.*;

/**
 * 哈夫曼编码
 * @author chenpc
 * @version 1.0
 * @since 2022/4/12/04/12  17:22
 */
public class HuffmanCode {
    public static void main(String[] args) {
        String str = "i like like like java do you like a java";
        byte[] zip = code(str.getBytes());
        byte[] decode = decode(zip);
        String s = new String(decode);
        System.out.println(s);
    }
    /**
     * huffman 译码表
     */
    public static Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();

    /**
     * 解码
     *
     * @param huffmanBytes huffman编码数组
     * @return 解码后源byte数组
     */
    public static byte[] decode(byte[] huffmanBytes) {
        //step1: 转二进制
        StringBuilder binary = toBinary(huffmanBytes);
        //step2: 译码表 k v 对调，根据译码表将二进制进行翻译
        //step2.1 对调译码表
        Map<String, Byte> huffmanDecode = new HashMap(huffmanCodes.size());
        huffmanCodes.forEach((k, v) -> {
            huffmanDecode.put(v, k);
        });
        //step2 进行翻译，保存到list 中
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < binary.length()-1;) {
            int index = i + 1;
            while (true) {
                String key = binary.substring(i, index);
                Byte b = huffmanDecode.get(key);
                if (b == null) {
                    index++;
                } else {
                    list.add(b);
                    i = index;
                    break;
                }
            }
        }
        // list 转byte
        byte[] temp = new byte[list.size()];
        for(int i = 0;i <list.size(); i++) {
            temp[i] = list.get(i);
        }
        return temp;
    }

    /**
     * 转二进制字符串
     *
     * @param huffmanBytes 待解码的byte数组
     * @return 二进制字符串
     */
    public static StringBuilder toBinary(byte[] huffmanBytes) {
        //1. byte 转二进制
        StringBuilder builder = new StringBuilder();
        String str;
        //将byte数组转成二进制的字符串
        for (int i = 0, huffmanBytesLength = huffmanBytes.length; i < huffmanBytesLength; i++) {
            byte huffmanByte = huffmanBytes[i];
            if (i < huffmanBytesLength - 1) {
                str = Integer.toBinaryString(huffmanByte | 256);
                builder.append(str.substring(str.length() - 8));
            } else {
                str = Integer.toBinaryString(huffmanByte);
                builder.append(str);
            }
        }
        return builder;
    }

    /**
     * huffman编码
     * @param bytes 原数组 byte[]
     * @return huffman编码 byte[]
     */
    public static byte[] code(byte[] bytes) {
        //step1: 根据二进制数组构建节点权重列表
        List<HuffmanNode> nodes = getNodes(bytes);
        //step2: 根据节点权重列表构建human树
        HuffmanNode node = createHuffmanTree(nodes);
        //step3: 根据huffman树初始化 huffman译码表
        initHuffmanCodes(node);
        //step4: 编码转换
        StringBuilder stringBuilder = new StringBuilder();
        //遍历bytes数组,将每个byte映射为译码表的数字
        for (byte b : bytes) {
            stringBuilder.append(huffmanCodes.get(b));
        }
        //统计返回新数组的长度,补齐8的倍数位，因为byte类型占8位
        int len = (stringBuilder.length() + 7) / 8;
        //创建 存储压缩后的 byte数组
        byte[] huffmanCodeBytes = new byte[len];
        //记录是第几个byte
        int index = 0;
        int i = 0;
        while (i < stringBuilder.length()) {
            //截取8位，并转为int类型
            String strByte = stringBuilder.substring(i, Math.min(i + 8, stringBuilder.length()));
            //2进制转10进制
            //2转10,第一位是符号位。正数直接计算，负数-1取反
            huffmanCodeBytes[index] = (byte) Integer.parseInt(strByte, 2);
            i += 8;
            index++;
        }
        return huffmanCodeBytes;
    }

    /**
     * 初始化 huffmanCode 用于加密及解密
     *
     * @param node huffmanTree根节点
     */
    public static void initHuffmanCodes(HuffmanNode node) {
        if (node != null) {
            getCodes(node.left, "0", new StringBuilder());
            getCodes(node.right, "1", new StringBuilder());
        }
    }

    /**
     * 功能：将传入的node结点的所有叶子结点的赫夫曼编码得到，并放入到huffmanCodes集合
     *
     * @param node          传入结点
     * @param code          路径： 左子结点是 0, 右子结点 1
     * @param stringBuilder 用于拼接路径
     */
    public static void getCodes(HuffmanNode node, String code, StringBuilder stringBuilder) {
        StringBuilder str = new StringBuilder(stringBuilder);
        //将code 加入到 stringBuilder2
        str.append(code);
        if (node != null) {
            //判断当前node 是叶子结点还是非叶子结点
            if (node.data == null) {
                //向左递归
                getCodes(node.left, "0", str);
                //向右递归
                getCodes(node.right, "1", str);
            } else { //说明是一个叶子结点
                //就表示找到某个叶子结点的最后
                huffmanCodes.put(node.data, str.toString());
            }
        }
    }

    /**
     * 构建HuffmanNode节点列表
     * 将传入的byte数组进行计数，记录不同的byte出现的次数
     * Node中 byte 作为data ,次数作为权重
     *
     * @param bytes 接收字节数组
     * @return HuffmanNode节点列表
     */
    public static List<HuffmanNode> getNodes(byte[] bytes) {
        //创建一个ArrayList
        ArrayList<HuffmanNode> nodes = new ArrayList<HuffmanNode>();
        //遍历bytes,统计每一个byte出现的次数->map[byte,count]
        Map<Byte, Integer> counts = new HashMap<>();
        for (byte b : bytes) {
            Integer count = counts.merge(b, 1, (x, y) -> {
                return x + y;
            });
            counts.put(b, count);
        }
        counts.forEach((data, count) -> {
            nodes.add(new HuffmanNode(data, count));
        });
        return nodes;
    }

    /**
     * 根据huffman节点列表构建huffman树
     *
     * @param nodes 节点列表
     * @return 哈夫曼树
     */
    public static HuffmanNode createHuffmanTree(List<HuffmanNode> nodes) {
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            HuffmanNode leftNode = nodes.get(0);
            HuffmanNode rightNode = nodes.get(1);
            HuffmanNode huffmanNode = new HuffmanNode(null, leftNode.weight + rightNode.weight);
            huffmanNode.left = leftNode;
            huffmanNode.right = rightNode;
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            nodes.add(huffmanNode);
        }
        return nodes.get(0);
    }

    /**
     * 前序遍历的方法
     */
    public static void preOrder(HuffmanNode root) {
        if (root != null) {
            root.preOrder();
        } else {
            System.out.println("赫夫曼树为空");
        }
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