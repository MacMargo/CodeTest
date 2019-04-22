package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/4/17
 * @description You are given two non-empty linked lists representing two non-negative integers.
 * The digits are stored in reverse order and each of their nodes contain a single digit.
 * Add the two numbers and return it as a linked list.
 * <p>
 * You may assume the two numbers do not contain any leading zero, except the number 0 itself.
 * <p>
 * Example:
 * <p>
 * Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
 * Output: 7 -> 0 -> 8
 * Explanation: 342 + 465 = 807.
 **/

public class AddTwoNumbers {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        int carrier = 0;
        ListNode result = new ListNode(0);
        ListNode current = result;
        if (l1 != null && l2 != null) {
            int val = l1.val + l2.val + carrier;
            current.next = new ListNode(val % 10);
            current = current.next;
            carrier = val / 10;
            l1 = l1.next;
            l2 = l2.next;
        }
        while (l1 != null) {
            int val = l1.val + carrier;
            current.next = new ListNode(val % 10);
            current = current.next;
            carrier = val / 10;
            l1 = l1.next;
        }
        while (l2 != null) {
            int val = l2.val + carrier;
            current.next = new ListNode(val % 10);
            current = current.next;
            carrier = val / 10;
            l2 = l2.next;
        }
        if (carrier != 0) {
            current.next = new ListNode(carrier);
        }
        return result.next;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
