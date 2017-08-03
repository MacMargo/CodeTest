package huawei;

import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 编写一个函数，计算字符串中含有的不同字符的个数。字符在ACSII码范围内(0~127)。不在范围内的不作统计。

 输入描述:
 输入N个字符，字符在ACSII码范围内。


 输出描述:
 输出范围在(0~127)字符的个数。
 示例1
 输入

 abc
 输出

 3
 */
public class 字符个数统计 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String s = scanner.nextLine();
            int len = getLen(s);
            System.out.println(len);
        }
    }
    public static int getLen(String s){
        int[] arr = new int[128];
        for(int i=0;i<s.length();i++){
            arr[s.charAt(i)]=1;
        }
        int len = 0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]==1){
                len++;
            }
        }
        return len;
    }
}
