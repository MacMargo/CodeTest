package sort;

import java.util.Random;

/**
 * Created by MacMargo on 2017/9/4
 * 插入排序
 * 每一趟将一个待排序的记录，按照其关键字的大小插入到有序队列的合适位置里，直到全部插入完成。
 */

public class InsertSort {
    public static void insertSort(int[] array){
        System.out.format("i=%d:\t",0);
        printPart(array,0,0);

        for(int i=1;i<array.length;i++){
            int j=0;
            int temp = array[i];
            //关键这一步，是准备将之前比他大的都是放到他的后面，然后把当前这个放到位置上
            for(j=i-1;j>=0&&temp<array[j];j--){
                array[j+1] = array[j];
            }
            array[j+1] = temp;
            System.out.format("i=%d:\t",i);
            printPart(array,0,i);
        }
    }
    // 打印序列
    public static void printPart(int[] list, int begin, int end) {
        for (int i = 0; i < begin; i++) {
            System.out.print("\t");
        }
        for (int i = begin; i <= end; i++) {
            System.out.print(list[i] + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // 初始化一个随机序列
        final int MAX_SIZE = 10;
        int[] array = new int[MAX_SIZE];
        Random random = new Random();
        for (int i = 0; i < MAX_SIZE; i++) {
            array[i] = random.nextInt(MAX_SIZE);
        }

        // 调用冒泡排序方法
        InsertSort insert = new InsertSort();
        System.out.print("排序前:\t");
        insert.printPart(array, 0, array.length - 1);
        insert.insertSort(array);
        System.out.print("排序后:\t");
        insert.printPart(array, 0, array.length - 1);
    }

}
