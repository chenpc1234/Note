package LinkedList;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/23/03/23  15:57
 */
public class DoubleLinkedListDemo {

    public static void main(String[] args) {
        DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
        doubleLinkedList.add(new Dnode(1, 1));
        doubleLinkedList.add(new Dnode(3, 3));
        doubleLinkedList.add(new Dnode(5, 5));
        doubleLinkedList.add(new Dnode(7, 7));
        doubleLinkedList.showList();
        System.out.println("-----------");
        doubleLinkedList.update(new Dnode(5, 55));
        doubleLinkedList.showList();
        System.out.println("-----------");
        doubleLinkedList.delete(7);
        doubleLinkedList.showList();
        System.out.println("-----------");
        doubleLinkedList.addByScore(new Dnode(2, 2));
        doubleLinkedList.showList();
        System.out.println("-----------");
    }
}

class DoubleLinkedList {
    Dnode head = new Dnode(0, null);

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
        node.pre = temp;
    }
    /**
     * 根据排序号添加节点,但是必须保证节点分数是有序的
     *
     * @param node 节点
     */
    public void addByScore(Dnode node) {
        boolean flag = true;
        //判断节点是否有序
        Dnode tempNode = head;
        int order = 0;
        while (tempNode.next != null) {
            if (tempNode.next.order > order) {
                tempNode = tempNode.next;
                order = tempNode.order;
            } else {
                throw new RuntimeException("列表不是有序的");
            }

        }
        Dnode temp = head;
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
            temp.next.pre=node;
            node.pre=temp;
            temp.next = node;
        } else {
            System.out.println("分值重复，不能插入" + node);
        }
    }
    /**
     * 展示链表
     */
    public void showList() {
        Dnode temp = head;
        while (temp.next != null) {
            System.out.println(temp.next);
            temp = temp.next;
        }
    }

    /**
     * 根据排序更新节点信息
     *
     * @param node 节点新数据
     */
    public void update(Dnode node) {
        Dnode temp = head.next;
        while (temp != null) {
            if (temp.order == node.order) {
                temp.data = node.data;
                System.out.println("更新成功");
                return;
            }
            temp = temp.next;
        }
        System.out.println("更新失败");
    }

    /**
     * 根据序号删除节点
     *
     * @param order 序号
     */
    public void delete(int order) {
        if (order == 0) {
            System.out.println("头结点不能删除");
        }
        Dnode tempNode = head.next;
        while (tempNode != null) {
            if (tempNode.order == order) {
                tempNode.pre.next = tempNode.next;
                if (tempNode.next!=null){
                    tempNode.next.pre = tempNode.pre;
                }
                System.out.println("删除成功");
                return;
            }
            tempNode = tempNode.next;
        }
        System.out.println("删除失败，当前编号不存在" + order);
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