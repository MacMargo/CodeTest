package leetCodeTest;

/**
 * @author maniansheng
 * @date 2019/7/14
 * @description 将两个有序链表合并为一个新的有序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
 * <p>
 * 示例：
 * <p>
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 **/

public class MergeTwoSortedLists {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (null == l1) {
            return l2;
        }
        if (null == l2) {
            return l1;
        }
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        while (null != l1 && null != l2) {
            ListNode tmp = null;
            if (l1.val < l2.val) {
                tmp = l1;
                l1 = l1.next;
                tmp.next = null;
            } else {
                tmp = l2;
                l2 = l2.next;
                tmp.next = null;
            }
            curr.next = tmp;
            curr = curr.next;
        }
        if (l1 != null) {
            curr.next = l1;
        }
        if (l2 != null) {
            curr.next = l2;
        }
        return dummy.next;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
