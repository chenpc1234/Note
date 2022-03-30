package struct.linkedList;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/24/03/24  11:10
 */
public class Josephus {

    public static void main(String[] args) {
        JosephusLinkedList josephusLinkedList = new JosephusLinkedList(5);
        josephusLinkedList.show(2);
    }

}

class JosephusLinkedList{
    JosephusNode first = new JosephusNode(1);
    /**
     * 构造长度为num的单向循环链表
     * @param num 长度
     */
    public JosephusLinkedList(int num) {
        if (num <= 0){
           throw new RuntimeException("链表长度至少为1");
        }
        first.next=first;
        JosephusNode temp=first;
        for (int i = 2; i <= num; i++) {
            JosephusNode josephusNode = new JosephusNode(i);
            temp.next=josephusNode;
            josephusNode.next=first;
            temp=temp.next;
        }
    }
    /**
     * 展示
     * @param n
     */
    public void show(int n){
        int a =1;
        JosephusNode temp =first;
        while (temp.next!=temp){
            if(a%n==(n-1)){
                System.out.print("要出去的节点是"+temp.next+"\t");
                temp.next =temp.next.next;
            }else{
                temp=temp.next;
            }
            a++;
        }
        System.out.println("幸运节点数是"+temp);
    }
}

class JosephusNode{
    int num;
    JosephusNode next;

    public int getNum() {
        return num;
    }

    public JosephusNode(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "JosephusNode{" +
                "num=" + num +
                '}';
    }
}
