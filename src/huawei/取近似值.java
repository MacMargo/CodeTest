package huawei;

import java.util.Scanner;

/**
 * 题目描述
 写出一个程序，接受一个正浮点数值，输出该数值的近似整数值。如果小数点后数值大于等于5,向上取整；小于5，则向下取整。

 输入描述:
 输入一个正浮点数值


 输出描述:
 输出该数值的近似整数值
 示例1
 输入
 5.5
 输出
 6
 */
public class 取近似值 {
    public static void main(String[] args){
        Scanner str = new Scanner(System.in);
        double num = str.nextDouble();
        int result = getResult(num);
        System.out.println(result);

    }

    public static int getResult(double num){
        int i=(int)num;
        return  (num-i)>=0.5?i+1:i;
    }
}
