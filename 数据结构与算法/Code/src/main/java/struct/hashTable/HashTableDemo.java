package struct.hashTable;

/**
 * 散列表demo
 * @author chenpc
 * @version 1.0
 * @since 2022/3/31/03/31  14:37
 */
public class HashTableDemo {
    public static void main(String[] args) {
        HashTab hashTab = new HashTab(5);
        hashTab.add(new Emp(1, "1"));
        hashTab.add(new Emp(2, "2"));
        hashTab.add(new Emp(3, "3"));
        hashTab.add(new Emp(4, "4"));
        hashTab.add(new Emp(5, "5"));
        hashTab.add(new Emp(6, "6"));
        hashTab.list();
    //    Emp emp = hashTab.find(5);
      //  System.out.println(emp);
    }
}

class HashTab{
    EmpLinkedList[] arr;
    int maxSize;
    public HashTab(int maxSize) {
        this.maxSize =maxSize;
        arr =new EmpLinkedList[maxSize];
        for (int i = 0; i < maxSize; i++) {
            arr[i] = new EmpLinkedList();
        }
    }
    public void add(Emp emp){
        int i = emp.id % this.maxSize;
        arr[i].add(emp);
    }
    public Emp find(int id ){
        int i = id % this.maxSize;
        return arr[i].findById(id);
    }
    public void list(){
        for (int i = 0; i < this.maxSize; i++) {
            arr[i].list(i);
        }
    }
}
class EmpLinkedList{
    Emp head;
    public void add(Emp emp){
        if (head == null){
            head =emp;
            return;
        }
        Emp temp = head;
        while (temp.next!=null){
            temp = temp.next;
        }
        temp.next =emp;
    }
    public void list(int no ){
        if (head == null){
            System.out.println("第"+no+"条链表为空");
        }
        Emp temp = head;
        System.out.print("第"+no+"条链表为");
        while (temp!=null){
            System.out.print(temp+"\t");
            temp = temp.next;
        }
    }

    public Emp findById(int id){
        if (head == null){
            return null;
        }
        Emp temp = head;
        while (temp.next!=null){
            if (temp.next.id == id){
                return temp.next;
            }
        }
        return null;
    }
}

class Emp{
    int id;
    String name;
    Emp next;

    public Emp(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
