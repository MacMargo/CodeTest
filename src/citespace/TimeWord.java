package citespace;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by MacMargo on 2018/1/27
 */

public class TimeWord {
    public static void main(String[] args){
        File file  = new File("C:\\Users\\Administrator\\Desktop\\input\\download_476.txt");
        Map<String,Map<String,Integer>> map = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if(tempString.contains("YR")){
                    String tempKey = tempString.split(" ")[1];
                    reader.readLine();
                    reader.readLine();
                    tempString = reader.readLine();
                    String[] tempValues = tempString.split(" ")[1].split(";");
                    if(!map.containsKey(tempKey)){
                        Map<String,Integer> tempMap = new HashMap<>();
                        for(String keyword:tempValues){
                            if(tempMap.containsKey(keyword)){
                                tempMap.put(keyword,tempMap.get(keyword)+1);
                            }else{
                                tempMap.put(keyword,1);
                            }
                        }
                        map.put(tempKey,tempMap);
                    }else{
                        Map<String,Integer> tempMap = map.get(tempKey);
                        for(String keyword:tempValues){
                            if(tempMap.containsKey(keyword)){
                                tempMap.put(keyword,tempMap.get(keyword)+1);
                            }else{
                                tempMap.put(keyword,1);
                            }
                        }
                        map.put(tempKey,tempMap);
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


        for(String s:map.keySet()){
            System.out.println(s);
            //将map.entrySet()转换成list
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.get(s).entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    //return o1.getValue().compareTo(o2.getValue());
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            for (Map.Entry<String, Integer> mapping : list) {
                System.out.println(mapping.getKey() + ":" + mapping.getValue());
            }

            System.out.println("***************************");
            System.out.println();
        }

    }
}
