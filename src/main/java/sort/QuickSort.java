package sort;

/**
 * Created by MacMargo on 2017/9/4
 * 快速排序
 * 通过一趟排序将要排序的数据分割成独立的两部分：分割点左边都是比它小的数，右边都是比它大的数。
 * 然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
 */

public class QuickSort {
    public static int division(int[] array,int left,int right){
        int base = array[left];
        while (left<right){
            while (left<right&&array[right]>=base)
                right--;
            array[left] = array[right];
            while (left<right&&array[left]<=base)
                left++;
            array[right]=array[left];
        }
        array[left] = base;
        return left;
    }
    public static void quickSort(int[] array,int left,int right){
        if(left<right){
            int base = division(array,left,right);
            System.out.format("base = %d:\t", array[base]);
            printPart(array, left, right);
            quickSort(array,left,base-1);
            quickSort(array,base+1,right);
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
        // 初始化一个序列
        int[] array = {
                1, 3, 4, 5, 2, 6, 9, 7, 8, 0
        };

        // 调用快速排序方法
        System.out.print("排序前:\t\t");
        printPart(array, 0, array.length - 1);
        quickSort(array, 0, array.length - 1);
        System.out.print("排序后:\t\t");
        printPart(array, 0, array.length - 1);
    }
}
