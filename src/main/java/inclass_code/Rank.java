package inclass_code;

import java.util.Arrays;

public class Rank {
    public static String[] reRank(String[] input){
        if(input == null || input.length == 0){
            return new String[0];
        }
        int i=0;
        int left = 0;
        int right = input.length-1;
        while(i<=right){
            if(input[i].equals("r")){
                String s = input[i];
                input[i] = input[left];
                input[left] = s;
                left++;
                i++;
            }
            else if(input[i].equals("g")){
                i++;
            }
            else if(input[i].equals("b")){
                String s = input[right];
                input[right] = input[i];
                input[i] = s;
                right--;
            }
        }
        return input;
    }

    public static void main(String[] args){
        String[] input = new String[]{"r","r","b","g","b","r","g"};
        Arrays.asList(Rank.reRank(input)).forEach(s->System.out.print(s));
    }
}
