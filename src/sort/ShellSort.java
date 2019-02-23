package sort;

/**
 * Created by MacMargo on 2017/9/4
 * 希尔排序
 * 把记录按步长 gap 分组，对每组记录采用直接插入排序方法进行排序。
 * 随着步长逐渐减小，所分成的组包含的记录越来越多，当步长的值减小到 1 时，整个数据合成为一组，构成一组有序记录，则完成排序。
 */

public class ShellSort {
    public static void shellSort(int[] array){
        int gap = array.length/2;
        while (1<=gap){
            for(int i=gap;i<array.length;i++){
                int j = 0;
                int temp = array[i];
                for(j=i-gap;j>=0&&temp<array[j];j=j-gap){
                    array[j+gap] = array[j];
                }
                array[j+gap]= temp;
            }
            System.out.format("gap=%d:\t",gap);
            printAll(array);
            gap = gap/2;
        }
    }
    // 打印完整序列
    public static void printAll(int[] list) {
        for (int value : list) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] array = {9, 1, 2, 5, 7, 4, 8, 6, 3, 5};

        // 调用希尔排序方法
        ShellSort shell = new ShellSort();
        System.out.print("排序前:\t\t");
        shell.printAll(array);
        shell.shellSort(array);
        System.out.print("排序后:\t\t");
        shell.printAll(array);
    }
}
