package queue;

/**
 * @author chenpc
 * @version 1.0
 * @since 2022/3/21/03/21  16:15
 */
public class ArrayQueueDemo {
    public static void main(String[] args) {
        ArrayQueue arrayQueue = new ArrayQueue(10);
        arrayQueue.add(1);
        arrayQueue.add(2);
        arrayQueue.add(3);
        arrayQueue.add(4);
        arrayQueue.add(5);
        arrayQueue.add(6);
        arrayQueue.add(7);
        arrayQueue.add(8);
        arrayQueue.add(9);
        arrayQueue.add(0);
        System.out.println(arrayQueue.remove());
        arrayQueue.add(11);
        System.out.println(arrayQueue.front);
        System.out.println(arrayQueue.rear);
        arrayQueue.showQueue();
        System.out.println();
        System.out.println(arrayQueue.size());
    }

    /**
     * 环形数组模拟队列
     * 尾针和头针不能指向数组同一下标，保证环形数组有方向。尾针节点的数据为无效数据
     * 添加数据时，尾针处添加数据，同时尾针后移
     */
    static class ArrayQueue {
        private int maxSize; //队列容量
        private int front; // 队列头针
        private int rear;   //队列尾针
        private int[] data; //数组存放数据

        // 有参构造器
        public ArrayQueue(int size) {
            maxSize = size + 1;
            data = new int[maxSize];
            front = 0;
            rear = 0;
        }

        //判断队列是否满了
        public boolean isFull() {
            return (rear + 1) % maxSize == front;
        }

        //判断队列是否为空
        public boolean isEmpty() {
            return front == rear; //队列头指针 = 队列尾指针
        }

        //添加数据
        public void add(int value) {
            if (isFull()) {
                throw new RuntimeException("队列满了");
            }
            data[rear] = value;
            rear = (rear + 1) % maxSize;
        }

        //获取并移除队列头数据
        public int remove() {
            if (isEmpty()) {
                throw new RuntimeException("队列为空");
            }
            int temp = data[front];
            front = (front + 1) % maxSize;
            return temp;
        }

        //获取队列头数据
        public int element() {
            if (isEmpty()) {
                throw new RuntimeException("队列为空");
            }
            return data[front];
        }

        //获取队列长度
        public int size() {
//            if (front <= rear){
//                return rear-front;
//            }else{
//                return rear+maxSize-front;
//            }
            return (rear + maxSize - front) % maxSize;
        }

        //展示队列数据
        public void showQueue() {
            if (isEmpty()) {
                throw new RuntimeException("队列为空");
            }
            if (front < rear) {
                for (int i = front; i < rear; i++) {
                    System.out.print(data[i] + "\t");
                }
            } else {
                for (int i = front; i < maxSize; i++) {
                    System.out.print(data[i] + "\t");
                }
                for (int i = 0; i < rear; i++) {
                    System.out.print(data[i] + "\t");
                }
            }
        }

    }
}
