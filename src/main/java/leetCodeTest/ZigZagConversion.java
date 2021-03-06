package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/6/30
 * @description 将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
 * <p>
 * 比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：
 * <p>
 * L   C   I   R
 * E T O E S I I G
 * E   D   H   N
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。
 * <p>
 * 请你实现这个将字符串进行指定行数变换的函数：
 * <p>
 * string convert(string s, int numRows);
 * 示例 1:
 * <p>
 * 输入: s = "LEETCODEISHIRING", numRows = 3
 * 输出: "LCIRETOESIIGEDHN"
 * 示例 2:
 * <p>
 * 输入: s = "LEETCODEISHIRING", numRows = 4
 * 输出: "LDREOEIIECIHNTSG"
 * 解释:
 * <p>
 * L     D     R
 * E   O E   I I
 * E C   I H   N
 * T     S     G
 **/

public class ZigZagConversion {
    public static void main(String[] args) {
        ZigZagConversion zigZagConversion = new ZigZagConversion();
        System.out.println(zigZagConversion.convert("LEETCODEISHIRING", 3));
    }

    public String convert(String s, int numRows) {
        if (s == null || s.length() <= numRows || numRows <= 1) {
            return s;
        }
        int step = numRows * 2 - 2;
        char[] sc = s.toCharArray();
        StringBuilder[] sb = new StringBuilder[numRows];
        for (int i = 0; i < sb.length; i++) {
            sb[i] = new StringBuilder();
        }
        for (int i = 0; i < sc.length; i++) {
            int index = i % step;
            if (index < numRows) {
                sb[index].append(sc [i]);
            } else {
                sb[step - index].append(sc[i]);
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sb.length; i++) {
            result.append(sb[i]);
        }
        return result.toString();
    }
}
