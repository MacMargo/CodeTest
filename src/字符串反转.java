import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 写出一个程序，接受一个字符串，然后输出该字符串反转后的字符串。例如：

 输入描述:
 输入N个字符


 输出描述:
 输出该字符串反转后的字符串
 示例1
 输入

 abcd
 输出

 dcba
 */
public class 字符串反转 {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        String out = "";
        for(int i=str.length()-1;i>=0;i--){
            out+=str.charAt(i);
        }
        System.out.println(out);
    }
}
