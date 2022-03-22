package LinkedList;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/22/03/22  16:28
 */
public class SingleLinkedListDemo {
    public static void main(String[] args) {
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        singleLinkedList.addByScore(new Node(1, 111));

        singleLinkedList.addByScore(new Node(3, 333));
        singleLinkedList.add(new Node(4, 444));
        singleLinkedList.addByScore(new Node(5, 555));
        singleLinkedList.add(new Node(12, 222));
        singleLinkedList.update(new Node(12, 88888));
        singleLinkedList.addByScore(new Node(12, 222));

    }
}

class SingleLinkedList {
    /**
     * 定义头节点
     */
    Node head = new Node(0, null);

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void add(Node node) {
        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node;
    }

    /**
     * 根据排序号添加节点,但是必须保证节点分数是有序的
     *
     * @param node 节点
     */
    public void addByScore(Node node) {
        boolean flag = true;
        //判断节点是否有序
        Node tempNode = head;
        int order = 0;
        while (tempNode.next != null) {
            if (tempNode.next.order > order) {
                tempNode = tempNode.next;
                order = tempNode.order;
            } else {
                throw new RuntimeException("列表不是有序的");
            }

        }


        Node temp = head;
        while (temp.next != null) {
            if (node.order == temp.next.order) {
                flag = false;
                break;
            }
            if (node.order < temp.next.order) {
                break;
            }
            temp = temp.next;
        }
        if (flag) {
            node.next = temp.next;
            temp.next = node;
        } else {
            System.out.println("分值重复，不能插入" + node);
        }
    }

    /**
     * 根据排序更新节点信息
     * @param node 节点新数据
     */
    public void update(Node node) {
        Node tempNode = head;
        while (tempNode.next != null) {
            if (tempNode.next.order == node.order) {
                tempNode.next.data =node.data;
                System.out.println("更新成功");
                return;
            }
            tempNode=tempNode.next;
        }
        System.out.println("更新失败");
    }
    public void delete(Node node){
        this.delete(node.order);
    }
    public void delete(int order){
        if (order ==0 ){
            System.out.println("头结点不能删除");
        }
        Node tempNode = head;
        while (tempNode.next != null) {
            if (tempNode.next.order == order) {
                tempNode.next =tempNode.next.next;
                System.out.println("删除成功");
                return;
            }
            tempNode=tempNode.next;
        }
        System.out.println("删除失败，当前编号不存在"+order);
    }
}

/**
 * 定义一个带排序的节点(也可不带)
 */
class Node {
    int order;
    Object data;
    Node next;

    Node(int order, Object data) {
        this.order = order;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Node{" +
                "order=" + order +
                ", data=" + data +
                '}';
    }
}