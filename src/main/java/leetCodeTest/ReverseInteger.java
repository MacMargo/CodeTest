package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/6/30
 * @description 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 123
 * 输出: 321
 *  示例 2:
 * <p>
 * 输入: -123
 * 输出: -321
 * 示例 3:
 * <p>
 * 输入: 120
 * 输出: 21
 * 注意:
 * <p>
 * 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−231,  231 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
 * <p>
 * 思路：每次取余将最后一位取出来，放在首位，注意检查是否溢出，还有就是负数取余还是负数
 **/

public class ReverseInteger {
    public int reverse(int x) {
        boolean flag = (x > 0) ? true : false;
        int result = 0;
        while (x / 10 != 0 || x % 10 != 0) {
            int val = result * 10 + Math.abs(x % 10);
            if (val / 10 != result) {
                return 0;
            }
            result = val;
            x /= 10;
        }
        return flag ? result : -result;
    }
}
