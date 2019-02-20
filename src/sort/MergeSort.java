package sort;

/**
 * Created by MacMargo on 2017/9/4
 * 归并排序
 * 归并排序是建立在归并操作上的一种有效的排序算法，该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。
 */

public class MergeSort {
    public static void Merge(int[] array,int low,int mid,int high){
        int i = low;
        int j = mid+1;
        int k = 0;
        int[] array2 = new int[high-low+1];

        while(i<=mid&&j<=high){
            if(array[i]<=array[j]){
                array2[k] = array[i];
                i++;
                k++;
            }else{
                array2[k] = array[j];
                j++;
                k++;
            }
        }
        while(i<=mid){
            array2[k] = array[i];
            i++;
            k++;
        }
        while(j<=high){
            array2[k] = array[j];
            j++;
            k++;
        }
        for(k=0,i=low;i<=high;i++,k++){
            array[i] = array2[k];
        }
    }

    public static void MergePass(int[] array,int gap,int length){
        int i=0;

        for(i=0;i+2*gap-1<length;i=i+2*gap){
            Merge(array,i,i+gap-1,i+2*gap-1);
        }

        if(i+gap-1<length){
            Merge(array,i,i+gap-1,length-1);
        }
    }

    public static int[] sort(int[] array){
        for(int gap = 1;gap<array.length;gap = 2*gap){
            MergePass(array,gap,array.length);
            System.out.print("gap="+gap+":\t");
            printAll(array);
        }
        return array;
    }

    // 打印完整序列
    public static void printAll(int[] array) {
        for (int value : array) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] array = {9, 1, 5, 3, 4, 2, 6, 8, 7};

        System.out.print("排序前:\t\t");
        printAll(array);
        sort(array);
        System.out.print("排序后:\t\t");
        printAll(array);
    }
}
