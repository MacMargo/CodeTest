package algorithm;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Main {

/** 请完成下面这个函数，实现题目要求的功能 **/
    /** 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^  **/
    static int QuizReview(String[] scoreList) {
        int res = 0;
        Map<String,List<String>> map1 = new HashMap<>();
        Map<String,List<String>> map2 = new HashMap<>();
        Map<String,List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        for(int i=0;i<scoreList.length;i++){
            String[] temp = scoreList[i].split(" ");
            String tempString1 = temp[5];
            String tempString2 = temp[6];
            String tempString3 = temp[7];
            String tempString4 = temp[3];
            String tempString5 = temp[0];

            getMap(map1,tempString1,tempString4);

            getMap(map2,tempString2,tempString4);

            getMap(map1,tempString3,tempString4);

            if(!list.contains(tempString4)){
                list.add(tempString4);
            }

            if(!list1.contains(tempString5)){
                list1.add(tempString5);
            }
        }

        Set<String> set1 = map1.keySet();
        for(String num:set1){
            res+=map1.get(num).size();
        }
        res+=set1.size();

        Set<String> set2 = map2.keySet();
        for(String num:set2){
            res+=map2.get(num).size();
        }

        Set<String> set = map.keySet();
        for(String num:set){
            res+=map.get(num).size();
        }
        res+=set.size();

        res+=list.size()+1;

        res+=list1.size();

        return res;
    }

    public static void getMap(Map<String,List<String>> map,String in1,String in2){
        if(!map.keySet().contains(in1)){
            List<String> list2 = new ArrayList<>();
            list2.add(in2);
            map.put(in1,list2);
        }else{
            if(!map.get(in1).contains(in2)){
                map.get(in1).add(in2);
            }
        }
        //return map;
    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        int res;

        int _scoreList_size = 0;
        _scoreList_size = Integer.parseInt(in.nextLine().trim());
        String[] _scoreList = new String[_scoreList_size];
        String _scoreList_item;
        for(int _scoreList_i = 0; _scoreList_i < _scoreList_size; _scoreList_i++) {
            try {
                _scoreList_item = in.nextLine();
            } catch (Exception e) {
                _scoreList_item = null;
            }
            _scoreList[_scoreList_i] = _scoreList_item;
        }
//        _scoreList[0]="张三 6 3 语文 78 王大叔 刘大姐 李大哥";
//        _scoreList[1]="李四 6 3 数学 78 王大叔 刘二姐 李大哥";
//        _scoreList[2]="赵五 6 2 数学 78 王二叔 刘三姐 李二哥";
        res = QuizReview(_scoreList);
        System.out.println(String.valueOf(res));

    }
}

