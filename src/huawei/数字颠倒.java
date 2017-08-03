package huawei;

import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 描述：
 输入一个整数，将这个整数以字符串的形式逆序输出
 程序不考虑负数的情况，若数字含有0，则逆序形式也含有0，如输入为100，则输出为001


 输入描述:
 输入一个int整数


 输出描述:
 将这个整数以字符串的形式逆序输出
 示例1
 输入

 1516000
 输出

 0006151
 */
public class 数字颠倒 {
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
