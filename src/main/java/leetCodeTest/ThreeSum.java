package leetCodeTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author maniansheng
 * @date 2019/6/30
 * @description 给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。
 * <p>
 * 注意：答案中不可以包含重复的三元组。
 * <p>
 * 例如, 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 * <p>
 * 满足要求的三元组集合为：
 * [
 * [-1, 0, 1],
 * [-1, -1, 2]
 * ]
 * <p>
 **/

public class ThreeSum {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i != 0 && nums[i] == nums[i + 1]) {
                continue;
            }
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int val = nums[i] + nums[left] + nums[right];
                if (val == 0) {
                    List<Integer> solution = new ArrayList<>();
                    solution.add(nums[i]);
                    solution.add(nums[left]);
                    solution.add(nums[right]);
                    result.add(solution);
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (val > 0) {
                    right--;
                } else {
                    left++;
                }
            }
        }
        return result;
    }
}
