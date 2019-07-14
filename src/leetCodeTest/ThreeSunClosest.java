package leetCodeTest;

import java.util.Arrays;

/**
 * @author maniansheng
 * @date 2019/7/13
 * @description 给定一个包括 n 个整数的数组 nums 和 一个目标值 target。找出 nums 中的三个整数，
 * 使得它们的和与 target 最接近。返回这三个数的和。假定每组输入只存在唯一答案。
 * <p>
 * 例如，给定数组 nums = [-1，2，1，-4], 和 target = 1.
 * <p>
 * 与 target 最接近的三个数的和为 2. (-1 + 2 + 1 = 2).
 * <p>
 **/

public class ThreeSunClosest {
    /**
     * Three sum closest int.
     *
     * @param nums   the nums
     * @param target the target
     * @return the int
     */
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        long diff = (long) Integer.MAX_VALUE * 2;
        int result = 0;
        for (int i = 0; i < nums.length - 2; i++) {
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                long val = nums[i] + nums[left] + nums[right];
                if (Math.abs(val - target) < Math.abs(diff)) {
                    diff = val - target;
                    result = (int) val;
                }
                if (val == target) {
                    return target;
                } else if (val > target) {
                    right--;
                } else {
                    left++;
                }
            }
        }
        return result;
    }
}
