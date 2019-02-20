
package sort;

/**
 * Created by MacMargo on 2017/9/
 * 选择排序
 * 从待排序序列中，找到关键字最小的元素；
 * 如果最小元素不是待排序序列的第一个元素，将其和第一个元素互换；
 * 从余下的 N - 1 个元素中，找出关键字最小的元素，重复(1)、(2)步，直到排序结束。
 */

public class SelectSort {
    public static int[] selectSort(int[] array){
        for(int i=0;i<array.length;i++){
            int mink = i;
            for(int j=i+1;j<array.length;j++){
                if(array[j]<array[mink]){
                    mink = j;
                }
            }
            if(mink!=i){
                int temp = array[mink];
                array[mink] = array[i];
                array[i] = temp;
            }
        }
        return array;
    }

    public static void printAll(int[] list) {
        for (int value : list) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] array = {9, 1, 2, 5, 7, 4, 8, 6, 3, 5};

        // 调用选择排序方法
        System.out.print("排序前:\t\t");
        printAll(array);
        selectSort(array);
        System.out.print("排序后:\t\t");
        printAll(array);
    }
}
