package LinkedList;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/23/03/23  15:57
 */
public class DoubleLinkedListDemo {

}
class DoubleLinkedList{
    Dnode head =new Dnode(0, null);

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void add(Dnode node) {
        Dnode temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node;
        node.pre=temp;
    }
}

/**
 * 定义一个带排序的节点(也可不带)
 */
class Dnode {
    int order;
    Object data;
    Dnode next;
    Dnode pre;

    public Dnode(int order, Object data) {
        this.order = order;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Dnode{" +
                "order=" + order +
                ", data=" + data +
                '}';
    }
}