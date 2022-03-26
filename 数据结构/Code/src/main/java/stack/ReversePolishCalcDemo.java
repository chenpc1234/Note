package stack;


import java.util.Stack;

/**
 * 逆波兰计算器
 *
 * @author chenpc
 * @version 1.0
 * @since 2022/3/25/03/25  14:39
 */
public class ReversePolishCalcDemo {
    public static void main(String[] args) {
        String str = "( 3   + 4 ) * (15 - 6)";
        // 获取后缀表达式
        String reverseString = toReverseString(str);
        // 计算后缀表达式
        double value = calc(reverseString);
        System.out.println(value);
    }

    /**
     *  计算后缀表达式
     * @param reverseString 后缀表达式
     * @return 计算结果
     */
    private static double calc(String reverseString) {
        String[] s = reverseString.split(" ");
        Stack<String> stack = new Stack<>();
        for (String s1 : s) {
            if (isNumber(s1)) {
                stack.push(s1);
            } else {
                double num2 = Double.parseDouble(stack.pop());
                double num1 = Double.parseDouble(stack.pop());
                switch (s1) {
                    case "+":
                        stack.push((num1 + num2) + "");
                        break;
                    case "-":
                        stack.push((num1 - num2) + "");
                        break;
                    case "*":
                        stack.push((num1 * num2) + "");
                        break;
                    case "/":
                        stack.push((num1 / num2) + "");
                        break;
                    default:
                        throw new RuntimeException("符号不对：" + s1);
                }
            }
        }
        return Double.parseDouble(stack.pop());
    }

    /**
     *  中缀表达式 转后缀表达式
     * @param str 中缀表达式
     * @return 后缀表达式
     */
    private static String toReverseString(String str) {
        String[] split = getStrArray(str);
        Stack<String> s1 = new Stack<>();
        Stack<String> s2 = new Stack<>();
        for (String s : split) {
            if (isNumber(s)) {
                //数字
                s2.push(s);
            } else if (isSymbol(s)) {
                // 运算符
                if (couldPushToS1(s1, s)) {
                    s1.push(s);
                } else {
                    while (!couldPushToS1(s1, s)) {
                        s2.push(s1.pop());
                    }
                    s1.push(s);
                }
            } else {
                // 括号
                if ("(".equals(s)) {
                    s1.push(s);
                }else{
                    //如果是右括号“)”，则依次弹出s1栈顶的运算符，并压入s2，直到遇到左括号为止，此时将这一对括号丢弃
                    while(!s1.peek().equals("(")){
                        s2.push(s1.pop());
                    }
                    s1.pop();
                }
            }

        }
        while (!s1.empty()){
            s2.push(s1.pop());
        }
        String retSrt="";
        while (!s2.empty()){
            retSrt = s2.pop()+" "+retSrt;
        }
        return retSrt;
    }

    /**
     *  字符串过滤
     * @param str 字符串
     * @return 中缀数组
     */
    private static String[] getStrArray(String str) {
        // 1.(3.8+4)*5-6 ( 3.8 + 4 )*5-6

        str = str.replaceAll("\\s+","");
        str = str.replace("(", "( ");
        str = str.replace(")", " )");
        str = str.replace("+", " + ");
        str = str.replace("-", " - ");
        str = str.replace("*", " * ");
        str = str.replace("/", " / ");
        return str.split(" ");
    }

    /**
     * 运算符优先级比较 优先级高返回true
     *
     * @param peek 被比较运算符
     * @param s    比较运算符
     * @return 运算符s优先级是否高
     */
    private static boolean isHigh(String peek, String s) {
        if (peek.equals("*") || peek.equals("/")) {
            return false;
        }
        if (s.equals("+") || s.equals("-")) {
            return false;
        }
        return true;
    }

    /**
     * 判断是不是double类型数字
     */
    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否是运算符
     * @param s 运算符
     * @return true = 是运算符
     */
    public static boolean isSymbol(String s) {
        return s.matches("\\+|-|\\*|/");
    }
    /**
     *  判断能否加入s1 栈
     * @param s1 s1 栈
     * @param s 加入的字符
     * @return 能加入 =true
     */
    public static boolean couldPushToS1(Stack<String> s1, String s) {
        return s1.empty() || s1.peek().equals("(") || isHigh(s1.peek(), s);
    }
}
