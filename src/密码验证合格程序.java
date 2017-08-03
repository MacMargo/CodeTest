import java.util.Scanner;

/**
 * Created by MacMargo on 2017/7/10.
 * 题目描述
 密码要求:

 1.长度超过8位

 2.包括大小写字母.数字.其它符号,以上四种至少三种

 3.不能有相同长度超2的子串重复

 说明:长度超过2的子串

 输入描述:
 一组或多组长度超过2的子符串。每组占一行
 输出描述:
 如果符合要求输出：OK，否则输出NG
 示例1
 输入

 021Abc9000
 021Abc9Abc1
 021ABC9000
 021$bc9000
 输出

 OK
 NG
 NG
 OK
 */
public class 密码验证合格程序 {
    public static void main(String[]args){
        Scanner scan=new Scanner(System.in);
        while(scan.hasNextLine()){
            String tempString=scan.nextLine();
            System.out.println(tf(tempString));

        }
    }
    public static String tf(String pw){
        if(pw.length()<=8){
            return ("NG");

        }
        int []count =new int[4];

        for(int i=0;i<pw.length()-3;i++){
            if (pw.substring(i+3,pw.length()).contains(pw.substring(i,i+3))){
                return("NG");
            }
        }


        for(int i=0;i<pw.length();i++){

            if('A'<=pw.charAt(i)&&pw.charAt(i)<='Z'){
                count[0]=1;
            }
            if('a'<=pw.charAt(i)&&pw.charAt(i)<='z'){
                count[1]=1;
            }
            if('0'<=pw.charAt(i)&&pw.charAt(i)<='9'){
                count[2]=1;
            }
            else{
                count[3]=1;
            }

        }
        if(count[0]+count[1]+count[2]+count[3]<3){
            return("NG");

        }

        return("OK");
    }
}
