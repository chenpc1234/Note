package struct.linkedList;



import java.util.Stack;

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
        singleLinkedList.showList();
        singleLinkedList.delete(12);
        singleLinkedList.addByScore(new Node(2, 222));
        singleLinkedList.showList();
        System.out.println(singleLinkedList.length());
        System.out.println(singleLinkedList.findDesc(1));
        System.out.println(singleLinkedList.find(5));
        singleLinkedList.reversal();
        System.out.println("--------------------------------");
        singleLinkedList.showList();
        System.out.println("--------------------------------");
        singleLinkedList.showListDesc();
        SingleLinkedList list1 = new SingleLinkedList();
        list1.addByScore(new Node(1, 111));
        list1.addByScore(new Node(13, 333));
        list1.addByScore(new Node(35, 555));
        list1.addByScore(new Node(45, 555));
        list1.addByScore(new Node(55, 555));
        list1.addByScore(new Node(65, 555));
        System.out.println("-------------list1-------------------");
        list1.showList();
        SingleLinkedList list2 = new SingleLinkedList();
        list2.addByScore(new Node(10, 111));
        list2.addByScore(new Node(3, 333));
        list2.add(new Node(24, 444));
        list2.addByScore(new Node(15, 555));
        System.out.println("-------------list2-------------------");
        list2.showList();
        SingleLinkedList merger = SingleLinkedListUtil.merger(list1, list2);
        System.out.println("-------------new list-------------------");
        merger.showList();

    }


}
class SingleLinkedListUtil{
    /**
     * 有序链表合并
     * @param list1 链表1
     * @param list2 链表2
     * @return 新链表
     */
    public static SingleLinkedList merger(SingleLinkedList list1,SingleLinkedList list2){
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        Node temp =list1.head.next;
        Node temp2 =list2.head.next;
        while (true){
            if (temp ==null){
                singleLinkedList.add(temp2);
                break;
            }
            if (temp2 ==null){
                singleLinkedList.add(temp);
                break;
            }
            if(temp.order <= temp2.order){
                singleLinkedList.add(new Node(temp.order, temp.data));
                temp =temp.next;
            }else{
                singleLinkedList.add(new Node(temp2.order, temp2.data));
                temp2=temp2.next;
            }
        }
        return singleLinkedList;
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
     *
     * @param node 节点新数据
     */
    public void update(Node node) {
        Node tempNode = head;
        while (tempNode.next != null) {
            if (tempNode.next.order == node.order) {
                tempNode.next.data = node.data;
                System.out.println("更新成功");
                return;
            }
            tempNode = tempNode.next;
        }
        System.out.println("更新失败");
    }

    /**
     * 删除节点
     *
     * @param node 节点
     */
    public void delete(Node node) {
        this.delete(node.order);
    }

    /**
     * 删除节点
     *
     * @param order 序号
     */
    public void delete(int order) {
        if (order == 0) {
            System.out.println("头结点不能删除");
        }
        Node tempNode = head;
        while (tempNode.next != null) {
            if (tempNode.next.order == order) {
                tempNode.next = tempNode.next.next;
                System.out.println("删除成功");
                return;
            }
            tempNode = tempNode.next;
        }
        System.out.println("删除失败，当前编号不存在" + order);
    }

    /**
     * 展示链表
     */
    public void showList() {
        Node temp = head;
        while (temp.next != null) {
            System.out.println(temp.next);
            temp = temp.next;
        }
    }

    /**
     * 计算长度
     * @return 有效数据长度
     */
    public int length() {
        int length = 0;
        Node temp = head;
        while (temp.next != null) {
            length++;
            temp = temp.next;
        }
        return length;
    }

    /**
     * 查找第x个节点
     * @param num x
     * @return 节点
     */
    public Node find(int num) {
        int a = 0;
        if (num<=0){
            return null;
        }
        Node temp = head;
        while (temp.next != null) {
            a++;
            if (a ==num){
                return temp.next;
            }
            temp = temp.next;
        }
        return null;
    }
    /**
     * 查找倒数第x个节点
     * @param descNum x
     * @return 节点
     */
    public Node findDesc(int descNum) {
        int length = length();

        if (descNum > length || descNum <= 0) {
            return null;
        }
        Node temp = head;
        while (temp.next != null) {
            if (length == descNum) {
                return temp.next;
            }
            length--;
            temp = temp.next;
        }
        return null;
    }

    /**
     * 反转链表
     */
    public void reversal(){
        //定义一个新的头节点
        Node reversHead =new Node(0, null);
        Node newNodeTemp;
        //遍历原来的链表
        Node temp = head.next;
        while (temp!=null){
            // 临时保存下一个节点，用于遍历使用
            newNodeTemp =temp.next;
            // 将节点插入新头节点的后面
            //1. 先代替头结点，将自己的尾针指向头结点尾针指向的地方
            //2. 然后将头结点的尾针指向当前节点
            temp.next=reversHead.next;
            reversHead.next=temp;
            // 循环后推
            temp=newNodeTemp;
        }
        // 将老头结点的尾指针指向新节点的尾指针
        head.next=reversHead.next;
    }
    /**
     * 逆序打印,不进行翻转，使用栈结构
     */
    public void showListDesc(){
        Stack stack =new Stack();
        assert head.next!=null;
        Node temp =head.next;

        while (temp!=null){
            stack.push(temp);
            temp=temp.next;
        }
        while (!stack.empty()){
            System.out.println(stack.pop());
        }
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