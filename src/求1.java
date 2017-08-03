import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 输入一个int型的正整数，计算出该int型数据在内存中存储时1的个数。

 输入描述:
 输入一个整数（int类型）


 输出描述:
 这个数转换成2进制后，输出1的个数
 示例1
 输入

 5
 输出

 2
 */
public class 求1 {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            int n=scanner.nextInt();
            int count=0;
            while (n!=0){
                count++;
                n=n&(n-1);
            }
            System.out.println(count);
        }
    }
}
