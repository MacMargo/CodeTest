package leetCodeTest;

/**
 * Auther maniansheng
 * Date 2019/4/22
 * Description
 * Given a string, find the length of the longest substring without repeating characters.
 *
 * Example 1:
 *
 * Input: "abcabcbb"
 * Output: 3
 * Explanation: The answer is "abc", with the length of 3.
 * Example 2:
 *
 * Input: "bbbbb"
 * Output: 1
 * Explanation: The answer is "b", with the length of 1.
 * Example 3:
 *
 * Input: "pwwkew"
 * Output: 3
 * Explanation: The answer is "wke", with the length of 3.
 *              Note that the answer must be a substring, "pwke" is a subsequence and not a substring.
 */

public class LongestSubStringWithoutRepeatingChar {
    class Solution {
        public int lengthOfLongestSubstring(String s) {
            if (s == null || s.length() == 0) {
                return 0;
            }
            int result = 0;
            int start = 0, end = 0;
            char[] sc = s.toCharArray();
            boolean[] hash = new boolean[256];
            while (end < sc.length) {
                while (end < sc.length && !hash[sc[end]]) {
                    hash[sc[end]] = true;
                    end++;
                }
                result = Math.max(result, end - start);
                hash[sc[start]] = false;
                start++;
            }
            return result;
        }
    }
}
