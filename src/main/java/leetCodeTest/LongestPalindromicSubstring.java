package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/6/30
 * @description 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
 * <p>
 * 示例 1：
 * <p>
 * 输入: "babad"
 * 输出: "bab"
 * 注意: "aba" 也是一个有效答案。
 * 示例 2：
 * <p>
 * 输入: "cbbd"
 * 输出: "bb"
 **/

public class LongestPalindromicSubstring {
    private int start = 0;
    private int end = 0;

    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        for (int i = 0; i < s.length(); i++) {
            updateLongest(s, i, i);
            if (i != s.length() - 1 && s.charAt(i) == s.charAt(i + 1)) {
                updateLongest(s, i, i + 1);
            }
        }
        return s.substring(start, end + 1);
    }

    private void updateLongest(String s, int left, int right) {
        while (left >= 0 && right <= s.length()) {
            if (s.charAt(left) != s.charAt(right)) {
                break;
            }
            if (right - left > end - start) {
                start = left;
                end = right;
            }
            left--;
            right++;
        }
    }
}

