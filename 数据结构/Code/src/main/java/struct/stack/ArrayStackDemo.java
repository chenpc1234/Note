package struct.stack;

/**
 * 使用数组模拟栈
 * @author chenpc
 * @version 1.0
 * @since 2022/3/24/03/24  15:41
 */
public class ArrayStackDemo {
    public static void main(String[] args) {

        ArrayStack arrayStack = new ArrayStack(10);
        for (int i = 0; i < 10; i++) {
            arrayStack.push(i);
            if (i%3==0){
                arrayStack.pop();
            }
        }
        arrayStack.show();
    }

}
class ArrayStack {
    int [] data;
    int top =-1;
    int maxSize;

    public ArrayStack(int maxSize) {
        this.maxSize = maxSize;
        data = new int[maxSize];
    }
    public boolean isFull(){
        return top==maxSize-1;
    }

    public void push(int value){
        if (isFull()) {
            throw new RuntimeException("栈满了,无法入栈");
        }
        top++;
        data[top] =value;
    }
    public boolean isEmpty(){
        return top==-1;
    }

    public int pop(){
        if (isEmpty()) {
            throw new RuntimeException("栈空了,无法出栈");
        }
        int value =data[top];
        top--;
        return value;
    }

    public void show(){
        for (int i = top; i >= 0; i--) {
            System.out.print(data[i]+"\t");
        }
    }
}

