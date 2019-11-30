package sort;

import java.util.Random;

/**
 * Created by MacMargo on 2017/9/4
 * 冒泡排序
 */

public class BubbleSort {
    public static void bubbleSort(int[] list){
        int temp = 0;
        for(int i=0;i<list.length-1;i++){
            for(int j=list.length-1;j>i;j--){
                if(list[j-1]>list[j]){
                    temp = list[j-1];
                    list[j-1] = list[j];
                    list[j] = temp;
                }
            }
            System.out.format("第 %d 趟：\t", i);
            printAll(list);
        }
    }

    public static void bubbleSort_2(int[] list){
        int temp = 0;
        boolean bChange = false;

        for(int i=0;i<list.length-1;i++){
            bChange = false;
            for(int j=list.length-1;j>i;j--){
                if(list[j-1]>list[j]){
                    temp = list[j-1];
                    list[j-1] =list[j];
                    list[j] = temp;
                    bChange = true;
                }
            }
            if(bChange ==false)
                break;

            System.out.format("第 %d 趟：\t", i);
            printAll(list);

        }
    }
    public static void printAll(int[] list) {
        for (int value : list) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args){
        final int Max_Size = 10;
        int[] array = new int[Max_Size];
        Random random = new Random();
        for(int i=0;i<Max_Size;i++){
            array[i] = random.nextInt(Max_Size);
        }

        System.out.print("排序前:\t");
        printAll(array);
        // bubble.bubbleSort(array);
        bubbleSort_2(array);
        System.out.print("排序后:\t");
        printAll(array);
    }
}
