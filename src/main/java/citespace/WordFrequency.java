package citespace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by MacMargo on 2018/1/27
 */

public class WordFrequency {
    public static void main(String[] args){
        File file  = new File("C:\\Users\\Administrator\\Desktop\\input\\download_476.txt");
        Map<String,Integer> map = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if(tempString.contains("K1")){
                    String[] tempLast = tempString.split(" ")[1].split(";");
                    for(String s:tempLast){
                        if(map.keySet().contains(s)){
                            map.put(s,map.get(s)+1);
                        }else{
                            map.put(s,1);
                        }
                    }

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        //将map.entrySet()转换成list
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                //return o1.getValue().compareTo(o2.getValue());
                return o2.getKey().compareTo(o1.getKey());
            }
        });

        for (Map.Entry<String, Integer> mapping : list) {
            System.out.println(mapping.getKey() + ":" + mapping.getValue());
        }
    }
}
