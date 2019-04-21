package leetCodeTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maniansheng
 * @date 2019/4/17
 * @description Given an array of integers, return indices of the two numbers such that they add up to a specific target.
 * <p>
 * <p>
 * Input:  [2, 7, 11, 15], target = 9,
 * Output:  [0, 1]
 * Assumptions:
 * <p>
 * each input would have exactly one solution
 * you may not use the same element twice
 **/

public class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length < 2) {
            return new int[0];
        }
        int[] result = new int[0];
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                result = new int[2];
                result[0] = map.get(target - nums[i]);
                result[1] = i;
                return result;
            }
            map.put(nums[i], i);
        }
        return result;
    }
}
