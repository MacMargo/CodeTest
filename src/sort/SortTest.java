package sort;

import java.util.Random;

/**
 * Created by MacMargo on 2017/9/19
 */

public class SortTest {
    //冒泡排序
    public static void bubbleSort(int[] array){
        int temp = 0;
        boolean flag = false;
        for(int i=0;i<array.length;i++){
            flag = false;
            for(int j=array.length-1;j>i;j--){
                if(array[j-1]>array[j]){
                    temp = array[j];
                    array[j] = array[j-1];
                    array[j-1] = temp;
                    flag = true;
                }
            }
            if(flag==false){
                break;
            }
        }
    }

    //堆排序
    public static void heapSort(int[] array){
        for(int i=array.length-1;i>=0;i--){
            heapAdjust(array,i,array.length-1);
        }
        for(int i=array.length-1;i>0;i--){
            int temp = array[i];
            array[i] = array[0];
            array[0] = temp;
            heapAdjust(array,0,i);
        }
    }
    public static void heapAdjust(int[] array,int parent,int length){
        int temp = array[parent];
        int child = 2*parent+1;
        while(child<length){
            if(child+1<length&&array[child+1]>array[child])
                child++;
            if(array[child]<=temp)
                break;
            array[parent] = array[child];
            parent = child;
            child = 2*child+1;
        }
        array[parent] = temp;
    }

    //插入排序
    public static void insertSort(int[] array){
        for(int i=1;i<array.length;i++){
            int j = 0;
            int temp = array[i];
            for(j=i-1;j>=0&&array[j]>temp;j--){
                array[j+1] = array[j];
            }
            array[j+1] = temp;
        }
    }

    //归并排序
    public static void mergeSort(int[] array){
        for(int gap = 1;gap<array.length;gap = gap*2){
            mergePass(array,gap,array.length);
        }
    }
    public static void mergePass(int[] array,int gap,int length){
        int i=0;
        for(i=0;i+2*gap-1<length;i=i+2*gap){
            merge(array,i,i+gap-1,i+2*gap-1);
        }
        if(i+gap-1<length){
            merge(array,i,i+gap-1,length-1);
        }
    }
    public static void merge(int[] array,int low,int mid,int high){
        int i=low,j=mid+1,k=0;
        int[] tempArray = new int[high-low+1];
        while(i<=mid&&j<=high){
            if(array[i]<array[j]){
                tempArray[k++] = array[i++];
            }else{
                tempArray[k++] = array[j++];
            }
        }
        while(i<=mid){
            tempArray[k++] = array[i++];
        }
        while(j<=high){
            tempArray[k++] = array[j++];
        }
        for(k=0,i=low;i<=high;i++,k++){
            array[i] = tempArray[k];
        }
    }

    //快速排序
    public static void quickSort(int[] array,int left,int right){
        if(left<right){
            int base = division(array,left,right);
            quickSort(array,left,base);
            quickSort(array,base+1,right);
        }

    }
    public static int division(int[] array,int left,int right){
        int base = array[left];
        while(left<right){
            while(left<right&&array[right]>=base)
                right--;
            array[left] = array[right];
            while(left<right&&array[left]<=base)
                left++;
            array[right] = array[left];
        }
        array[left] = base;
        return left;
    }

    // 基数排序
    public static void radixSort(int[] array,int begin,int end,int digit){
        int radix = 10,i=0,j=0;
        int[] count = new int[radix];
        int[] bucket = new int[end-begin+1];
        for(int d=1;d<digit;d++){
            for(i=0;i<radix;i++){
                count[i] = 0;
            }
            for(i=begin;i<=end;i++){
                j = getDigit(array[i],d);
                count[j]++;
            }
            for(i=1;i<radix;i++){
                count[i] = count[i]+count[i-1];
            }
            for(i=end;i>=begin;i--){
                j = getDigit(array[i],d);
                bucket[--count[j]] = array[i];
            }
            for (i = begin, j = 0; i <= end; i++, j++) {
                array[i] = bucket[j];
            }
        }
    }
    public static int getDigit(int x,int d){
        int[] temp ={1,1,10,100};
        return ((x / temp[d]) % 10);
    }

    //选择排序
    public static void selectSort(int[] array){
        for(int i=0;i<array.length;i++){
            int mink = i;
            for(int j = i+1;j<array.length;j++){
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
    }

    //希尔排序
    public static void shellSort(int[] array){
        int gap = array.length/2;
        while(gap>=1){
            for(int i=gap;i<array.length;i++){
                int j = 0;
                int temp = array[i];
                for(j=i-gap;j>=0&&temp<array[j];j=j-gap){
                    array[j+gap] = array[j];
                }
                array[j+gap]= temp;
            }
            gap = gap/2;
        }
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
        shellSort(array);
        System.out.print("排序后:\t");
        printAll(array);
    }
    public static void printAll(int[] list) {
        for (int value : list) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }


}
