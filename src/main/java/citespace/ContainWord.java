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

public class ContainWord {
    public static void main(String[] args){
        File file  = new File("C:\\Users\\Administrator\\Desktop\\input\\download_476.txt");
        String word = "零售";
        int frequency = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if(tempString.contains("K1")){
                    String[] tempLast = tempString.split(" ")[1].split(";");
                    for(String s:tempLast){
                        if(s.contains(word)){
                            frequency++;
                            System.out.println(s);
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

        System.out.println(frequency);

    }
}
