package huawei;

import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by MacMargo on 2017/7/6.
 * 题目描述
 数据表记录包含表索引和数值，请对表索引相同的记录进行合并，即将相同索引的数值进行求和运算，输出按照key值升序进行输出。

 输入描述:
 先输入键值对的个数
 然后输入成对的index和value值，以空格隔开


 输出描述:
 输出合并后的键值对（多行）
 示例1
 输入

 4
 0 1
 0 2
 1 2
 3 4
 输出

 0 3
 1 2
 3 4
 */

public class 合并表记录 {
    public static void main(String[] args) {
        Scanner str = new Scanner(System.in);
        SortedMap<Integer,Integer> map = new TreeMap<>();
        int n = Integer.parseInt(str.nextLine());
        for (int i = 0;i<n;i++){
            String[] mid = str.nextLine().split("\\s+");
            addPare(map,mid);
        }
        System.out.println(mapToString(map));
    }

    private static String mapToString(SortedMap<Integer, Integer> map) {
        StringBuilder builder = new StringBuilder();
        for(SortedMap.Entry<Integer,Integer>e:map.entrySet()){
            builder.append(e.getKey()).append(" ").append(e.getValue()).append("\r");
        }
        return builder.toString();
    }

    private static void addPare(SortedMap<Integer, Integer> map, String[] mid) {
        int key = Integer.parseInt(mid[0]);
        int value = Integer.parseInt(mid[1]);
        if(map.containsKey(key)){
            map.put(key, map.get(key) + value);
        }else{
            map.put(key, value);
        }
    }
}
