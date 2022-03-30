package struct.stack;



/**
 * 使用带头尾节点的链表模拟栈
 * @author chenpc
 * @version 1.0
 * @since 2022/3/25/03/25  09:26
 */
public class LinkedListStackDemo {
    public static void main(String[] args) {
        LinkedListStack linkedListStack = new LinkedListStack();
        linkedListStack.push(111);
        linkedListStack.push(222);
        linkedListStack.push(333);
        System.out.println(linkedListStack.pop());
        System.out.println(linkedListStack.pop());
        System.out.println(linkedListStack.pop());
        System.out.println(linkedListStack.pop());
    }

}
class LinkedListStack{
    LinkedList2 list2 =new LinkedList2();
    public void push(int value){
        list2.addLast(new Node(value));
    }
    public Object pop(){
        Node data = null;
        try {
            data = list2.getAndRemoveLast();
            return data.data;
        } catch (Exception e) {
            System.out.println("栈空了");
        }
        return null;
    }
}
class LinkedList2 {
    int size =0;
    Node first=new Node(null);
    Node last=new Node(null);;
    public LinkedList2() {
        first.next=last;
        last.pre=first;
    }

    public void addLast(Node node) {
        last.pre.next=node;
        node.pre=last.pre;
        node.next=last;
        last.pre=node;
    }
    public void addFirst(Node node) {
        first.next.pre=node;
        node.next=first.next;
        node.pre=first;
        first.next=node;
    }
    public boolean isEmpty(){
        return first.next == last;
    }
    public Node getAndRemoveFirst(){
        if (isEmpty()){
            throw  new RuntimeException("链表为空");
        }
        Node temp = first.next;
        temp.next.pre=first;
        first.next=temp.next;
        return temp;
    }
    public Node getAndRemoveLast(){
        if (isEmpty()){
            throw  new RuntimeException("链表为空");
        }
        Node temp = last.pre;
        temp.pre.next=last;
        last.pre=temp.pre;
        return temp;
    }
}

/**
 * 定义一个带排序的节点(也可不带)
 */
class Node {
    Object data;
    Node next;
    Node pre;

    public Node(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                '}';
    }
}
