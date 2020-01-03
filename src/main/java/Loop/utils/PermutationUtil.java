package Loop.utils;

import java.util.ArrayList;

public class PermutationUtil {
    public static ArrayList<String> getAllPermutation(int size){
        int[] arr = new int[size];
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i <size ; i++) {
            arr[i] = i;
        }
        perum(arr,0,arr.length-1,res);
        return res;
    }
    public static void perum(int [] arr,int p,int q,ArrayList<String> res){
        // for循环将数组中所有的元素和第一个元素进行交换。然后进行全排列。
        // 递归结束条件
        if(p == q){
            //  一次递归结束。将整个数组打印出来
            String per = "";
            for (int i = 0; i < arr.length; i++) {
                per+="@"+arr[i];
            }
            per.substring(1,per.length());
            res.add(per);
        }
        for(int i =p ;i <= q;i++){
            swap(arr,i,p);
            // 把剩下的元素都做全排列。
            perum(arr,p+1,q,res);
            // 然后再交换回去，数组还原，保证下一次不会有重复交换。
            swap(arr,i,p);
        }
    }
    public static void swap(int [] arr,int i,int j){
        // 交换函数
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
