package huawei;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 输入一个int型整数，按照从右向左的阅读顺序，返回一个不含重复数字的新的整数。

 输入描述:
 输入一个int型整数


 输出描述:
 按照从右向左的阅读顺序，返回一个不含重复数字的新的整数
 示例1
 输入

 9876673
 输出

 37689
 */
public class 提取不重复的正数 {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        String sb = "";
        List<Integer> list = new ArrayList<>();
        while(num>=1){
            int temp = num%10;
            if(!list.contains(temp)){
                sb+=temp;
                list.add(temp);
            }
            num=num/10;
        }
        System.out.println(sb);

    }
}
