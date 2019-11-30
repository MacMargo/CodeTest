package sort;

/**
 * Created by MacMargo on 2017/9/4
 * 实现堆排序
 * 根据初始数组去构造初始堆（构建一个完全二叉树，保证所有的父结点都比它的孩子结点数值大）。
 * 每次交换第一个和最后一个元素，输出最后一个元素（最大值），然后把剩下元素重新调整为大根堆。
 */

public class HeapSort {
    public static void HeapAdjust(int[] array,int parent,int length){
        int temp = array[parent];//temp保存当前父节点
        int child = 2*parent+1;//先获得左孩子

        while(child<length){
            //如果有右孩子节点，并且右孩子节点的值大于左孩子节点，则选取右孩子节点
            if(child+1<length&&array[child]<array[child+1])
                child++;

            //如果父结点的值已经大于孩子节点的值，则直接结束
            if(temp>=array[child])
                break;

            //把孩子节点的值赋给父节点
            array[parent] = array[child];

            //选取孩子节点的左孩子节点，继续向下筛选
            parent = child;
            child = 2*child+1;
        }
        array[parent] = temp;
    }

    public static void heapSort(int[] array){
        //循环建立初始堆
        for(int i=array.length-1;i>=0;i--){
            HeapAdjust(array,i,array.length-1);
        }

        //进行n-1次循环，完成排序
        for(int i=array.length-1;i>0;i--){
            //最后一个元素和第一个元素进行交换
            int temp = array[i];
            array[i] = array[0];
            array[0] = temp;

            //筛选R[0]结点，得到i-1个节点的堆
            HeapAdjust(array,0,i);
            System.out.format("第%d趟:\t",array.length-i);
            printPart(array,0,array.length-1);
        }
    }

    public static void printPart(int[] array,int begin,int end){
        for(int i=0;i<begin;i++){
            System.out.print("\t");
        }
        for(int i=begin;i<=end;i++){
            System.out.print(array[i]+"\t");
        }
        System.out.println();
    }
    public static void main(String[] args){
        int[] array = {1, 3, 4, 5, 2, 6, 9, 7, 8, 0};

        System.out.println("排序前:\t");
        printPart(array,0,array.length-1);
        //heapsort(array,array.length);
        heapSort(array);
        System.out.print("排序后:\t");
        printPart(array,0,array.length-1);
    }

    public static void heapsort(int[] arr,int n){
        for(int i=n/2-1;i>=0;i--){
            heapAdjust(arr,i,n);
        }
        for(int i=0;i<n-1;i++){
            swap(arr,0,n-i-1);
            heapAdjust(arr,0,n-i-1);
        }
    }
    public static void swap(int[] arr,int low,int high){
        int temp = arr[low];
        arr[low] = arr[high];
        arr[high] = temp;
    }
    public static void heapAdjust(int[] arr,int index,int n){
        int temp = arr[index];
        int child = 0;
        while(index*2+1<n){
            child = 2*index+1;
            if(child!=n-1&&arr[child]<arr[child+1]){
                child++;
            }
            if(temp>arr[child]){
                break;
            }else{
                arr[index]= arr[child];
                index = child;
            }
        }
        arr[index] = temp;
    }
}
