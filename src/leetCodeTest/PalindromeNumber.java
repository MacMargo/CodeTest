package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/6/30
 * @description 判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 121
 * 输出: true
 * 示例 2:
 * <p>
 * 输入: -121
 * 输出: false
 * 解释: 从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
 * 示例 3:
 * <p>
 * 输入: 10
 * 输出: false
 * 解释: 从右向左读, 为 01 。因此它不是一个回文数。
 * 进阶:
 * <p>
 * 你能不将整数转为字符串来解决这个问题吗？
 * 取余，负数肯定不是
 * <p>
 **/

public class PalindromeNumber {
    public boolean isPalindrome(int x) {
        // 如果是负数，肯定不是
        if (x < 0) {
            return false;
        }
        long org = x;
        long reverse = 0;
        while (x / 10 != 0 || x % 10 != 0) {
            reverse = reverse * 10 + x % 10;
            x /= 10;
        }
        return reverse == org;
    }
}
