package leetCodeTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maniansheng
 * @date 2019/7/14
 * @description 给出 n 代表生成括号的对数，请你写出一个函数，
 * 使其能够生成所有可能的并且有效的括号组合。
 * <p>
 * 例如，给出 n = 3，生成结果为：
 * <p>
 * [
 * "((()))",
 * "(()())",
 * "(())()",
 * "()(())",
 * "()()()"
 * ]
 * <p>
 **/

public class GenerateParentheses {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        if (n < 0) {
            return result;
        }
        dfsHelper(0, 0, n, "", result);
        return result;
    }

    private void dfsHelper(int left, int right, int n, String state, List<String> result) {
        if (left == n && right == n) {
            result.add(state);
        }
        if (left < n) {
            dfsHelper(left + 1, right, n, state + "(", result);
        }
        // 裁剪，这一步很关键，是不断迭代的基础 ( (( (((
        if (right < left) {
            dfsHelper(left, right + 1, n, state + ")", result);
        }
    }
}
