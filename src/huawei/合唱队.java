package huawei;

import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/10.
 *
 * 题目描述
 计算最少出列多少位同学，使得剩下的同学排成合唱队形
 说明：
 N位同学站成一排，音乐老师要请其中的(N-K)位同学出列，使得剩下的K位同学排成合唱队形。
 合唱队形是指这样的一种队形：设K位同学从左到右依次编号为1，2…，K，他们的身高分别为T1，T2，…，TK，   则他们的身高满足存在i（1<=i<=K）使得T1<T2<......<Ti-1<Ti>Ti+1>......>TK。
 你的任务是，已知所有N位同学的身高，计算最少需要几位同学出列，可以使得剩下的同学排成合唱队形。


 输入描述:
 整数N
 输出描述:
 最少需要几位同学出列
 示例1
 输入

 8
 186 186 150 200 160 130 197 200
 输出

 4
 */
public class 合唱队 {
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        while(s.hasNext()){
            int n = s.nextInt();
            int temp = 0;
            int[] Inc = new int[n];
            int[] Dec = new int[n];
            int[] arr = new int[n];
            for(int i = 0; i < n; i++){
                arr[i] = s.nextInt();
            }
            Inc[0] = 1;
            for(int i = 1; i < n; i++){
                Inc[i] = 1;
                for(int j = 0; j < i; j++){
                    if(arr[j] < arr[i] && Inc[j] + 1 > Inc[i]){
                        Inc[i] = Inc[j] + 1;
                    }
                }
            }
            Dec[n - 1] = 1;
            for(int i = n -2; i >= 0; i--){
                Dec[i] = 1;
                for(int j = n - 1; j > i; j--){
                    if(arr[j] < arr[i] && Dec[j] + 1 > Dec[i]){
                        Dec[i] = Dec[j] + 1;
                    }
                }
            }
            for(int i = 0; i < n; i++){
                if(Inc[i] + Dec[i] - 1 > temp)
                    temp = Inc[i] + Dec[i] - 1;
            }
            System.out.println(n - temp);
        }
    }
}
