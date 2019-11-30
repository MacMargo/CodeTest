package algorithm;

import java.util.Hashtable;

public class MyLinkedList {
    Node head = null;

    /**
     * 向链表中插入数据
     * @param d：插入数据的内容
     */
    public void addNode(int d){
        Node newNode = new Node(d);
        if(head==null){
            head = newNode;
            return;
        }
        Node tmp = head;
        while(tmp.next!=null){
            tmp = tmp.next;
        }
        tmp.next = newNode;
    }

    /**
     * @param index:删除第index个节点
     * @return 成功返回true,失败返回false
     */
    public Boolean deleteNode(int index){
        // 判断位置是否正确
        if(index<1||index>listLength()){
            return false;
        }

        // 如果为1，则删除首个节点
        if(index == 1){
            head = head.next;
            return true;
        }

        int i=1;
        Node preNode = head;
        Node curNode = preNode.next;
        while(curNode!=null){
            if(i==index){
                preNode.next = curNode.next;
                return true;
            }
            preNode = curNode;
            curNode = curNode.next;
            i++;
        }
        return true;
    }

    /**
     * @return 返回链表的长度
     */
    public int listLength(){
       int length = 0;
       Node tmp = head;
       while(tmp!=null){
           length++;
           tmp = tmp.next;
       }
       return length;
    }

    /**
     * 对链表进行排序
     * @return 返回排序后的头结点
     */
    public Node orderList(){
        Node nextNode = null;
        int temp = 0;
        Node curNode = head;
        while(curNode.next!=null){
            nextNode = curNode.next;
            while(nextNode!=null){
                if(curNode.data>nextNode.data){
//                    temp = curNode.data;
//                    curNode.data = nextNode.data;
//                    nextNode.data = temp;
                    curNode.data = curNode.data^nextNode.data;
                    nextNode.data = nextNode.data^curNode.data;
                    curNode.data = curNode.data^nextNode.data;
                }
                nextNode = nextNode.next;
            }
            curNode = curNode.next;
        }
        return head;
    }

    public void deleteDuplecateOneMethod(Node head){
        Hashtable<Integer,Integer> table = new Hashtable<>();
        Node tmp = head;
        Node pre = null;
        while(tmp!=null){
            if(table.containsKey(tmp.data))
                pre.next = tmp.next;
            else{
                table.put(tmp.data,1);
                pre = tmp;
            }
            tmp = tmp.next;
        }
    }

    public void deleteDuplecateTwoMethod(Node head){
        Node p = head;
        while(p!=null){
            Node q = p;
            while(q.next!=null){
                if(p.data == q.next.data){
                    q.next = q.next.next;
                }else{
                    q= q.next;
                }
                p = p.next;
            }

        }
    }

    /**
     * 查找链表中倒数第k个元素
     * @param head
     * @param k
     * @return
     */
    public Node findElement(Node head,int k){
        if(k<1||k>this.listLength())
            return null;
        Node p1 = head;
        Node p2 = head;
        for(int i=0;i<k-1;i++)
            p1 = p1.next;
        while(p1!=null){
            p1 = p1.next;
            p2 = p2.next;
        }
        return p2;
    }

    /**
     * 反转链表
     * @param head
     */
    public void reverseIteratively(Node head){
        Node pReverseHead = head;
        Node pNode = head;
        Node pPrev = null;
        while(pNode !=null){
            Node pNext = pNode.next;
            if(pNext == null)
                pReverseHead = pNode;
            pNode.next = pPrev;
            pPrev = pNode;
            pNode = pNext;
        }
        this.head = pReverseHead;
    }

    /**
     * 从尾到头输出单链表
     * @param pListHead
     */
    public void printListReversely(Node pListHead){
        if(pListHead!=null){
            printListReversely(pListHead.next);
            System.out.println(pListHead.data);
        }
    }

    /**
     * 查找链表的中间节点
     * @param head
     * @return
     */
    public Node searchMid(Node head){
        Node p = this.head;
        Node q = this.head;
        while (p!=null && p.next!=null &&p.next.next!=null){
            p = p.next.next;
            q = q.next;
        }
        return q;
    }

    /**
     * 判断是否有环
     * @param head
     * @return
     */
    public boolean isLoop(Node head){
        Node fast = head;
        Node slow = head;
        if(fast == null)
            return false;
        while(fast!=null &&fast.next!=null){
            fast = fast.next.next;
            slow = slow.next;
            if(fast==slow)
                return true;
        }
        return !(fast==null||fast.next==null);
    }

    /**
     * 查找环的入口
     * @param head
     * @return
     */
    public Node findLoopPort(Node head){
        Node slow = head;
        Node fast = head;
        while(fast!=null&&fast.next!=null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow ==fast) break;
        }

        if(fast==null||fast.next ==null)
            return null;
        slow = head;
        while(slow!=fast){
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

    /**
     * 删除节点
     * @param node
     * @return
     */
    public boolean deleteNode(Node node){
        if(node==null ||node.next==null)
            return false;
        int temp = node.data;
        node.data = node.next.data;
        node.next.data = temp;
        node.next = node.next.next;
        return true;
    }

    /**
     * 判断两个链表是否相交
     * @param h1
     * @param h2
     * @return
     */
    public boolean isIntersect(Node h1,Node h2){
        if(h1==null||h2==null)
            return false;
        Node tail1 = h1;
        while (tail1.next!=null)
            tail1 = tail1.next;
        Node tail2 = h2;
        while (tail2.next!=null)
            tail2 = tail2.next;

        return tail1==tail2;
    }

    /**
     * 查找第一个相交的节点
     * @param h1
     * @param h2
     * @return
     */
    public Node getFirstMeetNode(Node h1,Node h2){
        if(h1==null||h2==null)
            return null;
        Node tail1 = h1;
        int len1 = 1;
        while(tail1.next!=null){
            tail1 = tail1.next;
            len1++;
        }
        Node tail2 = h2;
        int len2 = 1;
        while (tail2.next!=null){
            tail2 = tail2.next;
            len2++;
        }
        if(tail1!=tail2)
            return null;

        Node t1 = h1;
        Node t2 = h2;
        if(len1>len2){
            int d = len1-len2;
            while (d!=0){
                t1 = t1.next;
                d--;
            }
        }else{
            int d = len2-len1;
            while(d!=0){
                t2 = t2.next;
                d--;
            }
        }
        while (t1!=t2){
            t1 = t1.next;
            t2 = t2.next;
        }

        return t1;
    }

    /**
     * 打印链表
     */
    public void printList(){
        Node tmp = head;
        while(tmp!=null){
            System.out.println(tmp.data);
            tmp = tmp.next;
        }

    }

    public static  void  main(String[] args){
        MyLinkedList myLinkedList = new MyLinkedList();
        myLinkedList.addNode(5);
        myLinkedList.addNode(3);
        myLinkedList.addNode(1);
        myLinkedList.addNode(3);
        System.out.println("listLen="+ myLinkedList.listLength());
        System.out.println("before order:");
        myLinkedList.printList();
        myLinkedList.orderList();
        System.out.println("after order:");
        myLinkedList.printList();
    }

}

class Node{
    Node next = null;
    int data;
    public Node(int data){
        this.data= data;
    }
}
