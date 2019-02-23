package algorithm;

/**
 * 数据排序
 */
public class ArraySort {
    // 冒泡排序
    public static void bubbleSort(int[] array){
        boolean flag = true;
        for(int i=0;i<array.length;i++){
            printArray(array);
            flag = false;
            for(int j=0;j<array.length-1-i;j++){
                if(array[j]>array[j+1]){
                    int temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                    flag = true;
                }
            }
        }
    }

    // 简单选择排序
    public static void selectSort(int[] array){
        for(int i=0;i<array.length;i++){
            printArray(array);
            int mink = i;
            for(int j=i+1;j<array.length;j++){
                if(array[j]<array[mink])
                    mink = j;
            }

            if(mink!=i){
                int temp = array[mink];
                array[mink] = array[i];
                array[i] = temp;
            }
        }
    }

    // 插入排序
    public static void insertSort(int[] array){
        int j;
        for(int i=1;i<array.length;i++){
            printArray(array);
            int temp = array[i];
            j = i-1;
            while(j>-1&&temp<array[j]){
                array[j+1] = array[j];
                j--;
            }
            array[j+1] = temp;
        }
    }

    // 归并排序
    public static void mergeSort(int[] array){
        if(array.length>1){
            int length1 = array.length/2;
            int[] array1 = new int[length1];
            System.arraycopy(array,0,array1,0,length1);
            mergeSort(array1);

            int length2 = array.length-length1;
            int[] array2 = new int[length2];
            System.arraycopy(array,length1,array2,0,length2);
            mergeSort(array2);

            int[] datas = merge(array1,array2);
            System.arraycopy(datas,0,array,0,array.length);
        }
    }
    public static int[] merge(int[] list1,int[] list2){
        int[] list3 = new int[list1.length+list2.length];
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        while(count1<list1.length&&count2<list2.length){
            if(list1[count1]<list2[count2]){
                list3[count3++] = list1[count1++];
            }else{
                list3[count3++] = list2[count2++];
            }
        }

        while(count1<list1.length){
            list3[count3++] = list1[count1++];
        }

        while(count2<list2.length){
            list3[count3] = list2[count2++];
        }

        return list3;
    }

    // 快速排序
    public static void quickSort(int[] mdata,int start,int end){
        if(end>start){
            int pivotIndex = quickSortPartition(mdata,start,end);
            quickSort(mdata,start,pivotIndex-1);
            quickSort(mdata,pivotIndex+1,end);
        }
    }
    public static int quickSortPartition(int[] list,int first,int last){
        int pivot = list[first];
        int low = first+1;
        int high = last;

        while(high>low){
            while(low<=high&&list[low]<=pivot)
                low++;
            while(low<=high&&list[high]>pivot)
                high--;
            if(high>low){
                int temp = list[high];
                list[high] = list[low];
                list[low] = temp;
            }
        }

        while(high>first&&list[high]>=pivot)
            high--;

        if(pivot>list[high]){
            list[first] = list[high];
            list[high] = pivot;
            return high;
        }else{
            return first;
        }
    }

    public static void printArray(int[] array){
        for(int i=0;i<array.length;i++){
            System.out.print(array[i]+" ");
        }
        System.out.println("");
    }

    public static void main(String[] args){
        int[] array = {5,4,9,8,7,6,0,1,3,2};
        //bubbleSort(array);
        //selectSort(array);
        //insertSort(array);
        //mergeSort(array);
        quickSort(array,0,array.length-1);
        printArray(array);
    }
}
