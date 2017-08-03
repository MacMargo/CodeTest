import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/10.
 * 题目描述
 编写一个程序，将输入字符串中的字符按如下规则排序。
 规则 1 ：英文字母从 A 到 Z 排列，不区分大小写。
 如，输入： Type   输出： epTy
 规则 2 ：同一个英文字母的大小写同时存在时，按照输入顺序排列。
 如，输入： BabA   输出： aABb
 规则 3 ：非英文字母的其它字符保持原来的位置。
 如，输入： By?e   输出： Be?y
 样例：
 输入：
 A Famous Saying: Much Ado About Nothing(2012/8).
 输出：
 A  aaAAbc   dFgghh :  iimM   nNn   oooos   Sttuuuy  (2012/8).

 输入描述:

 输出描述:

 示例1
 输入

 A Famous Saying: Much Ado About Nothing (2012/8).
 输出

 A aaAAbc dFgghh: iimM nNn oooos Sttuuuy (2012/8).
 */
public class 字符串排序 {
    public static void main(String[] args)
    {
        Scanner sca = new Scanner(System.in);
        while (sca.hasNext())
        {
            String str = sca.nextLine();
            char [] cha = str.toCharArray();
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i<26; i++)
            {
                char c = (char)(i + 'A');
                for (int j = 0; j<str.length(); j++)
                {
                    if (cha[j] == c || cha[j] == (char)(c + 32))
                        sb.append(cha[j]);
                }
            }

            for (int k = 0; k<str.length(); k++)
            {
                if (!(cha[k] >= 'A' && cha[k] <= 'Z' || cha[k] >= 'a' && cha[k] <= 'z'))
                    sb.insert(k, cha[k]);
            }
            System.out.println(sb.toString());
        }
    }
}
