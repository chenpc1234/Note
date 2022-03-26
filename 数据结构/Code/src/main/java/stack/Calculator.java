package stack;


/**
 * 栈模拟处理简单的计算器
 * @author chenpc
 * @version 1.0
 * @since 2022/3/24/03/24  15:41
 */
public class Calculator {

    public static void main(String[] args) {

        String calc = "7+5*15+561/11-6";
        CalcStack operStack = new CalcStack(10);
        CalcStack numStack = new CalcStack(11);
        //i 计数器 记录第几个是符号，以及是否遍历完字符串
        int i = 0;
        while (true){
            if(isOper(calc.charAt(i))){
                //运算符前的数字是num
                String num =calc.substring(0, i);
                numStack.push(Integer.parseInt(num));
                //运算符是oper
                int oper =calc.charAt(i);
                //如果是空栈 或者 符号的优先级大于栈顶的优先级 符号添加到符号栈
                if (operStack.isEmpty()||(yxj(oper)-yxj(operStack.peek())>0)){
                    operStack.push(oper);
                }else{
                    //将数字栈顶两个数 使用符号栈顶符号进行运算
                    int num1 = numStack.pop();
                    int num2=numStack.pop();
                    int value = s(num2,num1,operStack.pop());
                    //计算后的值插入数字栈，当前符号插入符号栈
                    numStack.push(value);
                    operStack.push(oper);
                }
                //截取字符串，重置计数器
                calc=calc.substring(i+1, calc.length());
                i=0;
            }else{
                i++; //进行计数
                //如果字符串遍历完了,将剩余字符串(最后一个数字)加入数字栈，结束循环
                if (calc.length()==i){
                    numStack.push(Integer.parseInt(calc));
                    break;
                }
            }
        }
        //遍历符号栈，将剩余的数字进行计算
        while (!operStack.isEmpty()){
            int num1 = numStack.pop();
            int num2=numStack.pop();
            int value = s(num2,num1,operStack.pop());
            numStack.push(value);
        }
        // 数字栈最后的数字就是结果
        numStack.show();

    }
    public static int s(int num1,int num2,int oper) {
        switch (oper){
            case '+':
                return num1+num2;
            case '-':
                return num1-num2;
            case '*':
                return num1*num2;
            case '/':
                return num1/num2;
            default:
                return 0;
        }
    }
    public static boolean isOper(int x) {
        return x == 42 || x == 43 || x == 45 || x == 47;
    }
    public static int yxj(int x){
        if (x == 42||x== 47){
            return 1;
        } else if (x== 43 ||x== 45){
            return 0;
        }else {
            return -1;
        }
    }

}

class CalcStack {
    int[] data;
    int top = -1;
    int maxSize;

    public CalcStack(int maxSize) {
        this.maxSize = maxSize;
        data = new int[maxSize];
    }

    public boolean isFull() {
        return top == maxSize - 1;
    }

    public void push(int value) {
        if (isFull()) {
            throw new RuntimeException("栈满了,无法入栈");
        }
        top++;
        data[top] = value;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("栈空了,无法出栈");
        }
        int value = data[top];
        top--;
        return value;
    }
    public int peek() {
        return data[top];
    }
    public void show(){
        for (int i = top; i >= 0; i--) {
            System.out.print(data[i]+"\t");
        }
    }
}