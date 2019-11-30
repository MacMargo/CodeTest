package leetCodeTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maniansheng
 * @date 2019/7/13
 * @description 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 * <p>
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 * <p>
 * <p>
 * <p>
 * 示例:
 * <p>
 * 输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 * 说明:
 * 尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。
 **/

public class LatterCombinationOfaPhoneNumber {
    private static final String[] pad = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return result;
        }
        char[] dc = digits.toCharArray();
        dfsHelper(dc, 0, "", result);
        return result;
    }

    private void dfsHelper(char[] dc, int start, String curr, List<String> result) {
        if (start == dc.length) {
            result.add(curr);
            return;
        }
        char[] letters = pad[start - '0'].toCharArray();
        for (char letter : letters) {
            dfsHelper(dc, start + 1, curr + letter, result);
        }
    }
}
