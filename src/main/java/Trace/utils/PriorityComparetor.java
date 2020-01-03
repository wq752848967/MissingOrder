package Trace.utils;

public class PriorityComparetor {
    public static int compare(String p1,String p2){
        String[] p1_arr = null;
        String[] p2_arr = null;
        try{
            p1_arr = p1.split(",");
            p2_arr = p2.split(",");
        }catch (NullPointerException e){
            System.out.println("***********:"+p1+"  "+p2);
        }
        int smallPri = Math.min(p1_arr.length,p2_arr.length);


        for (int i = 0; i < smallPri; i++) {
            String tempP1 = p1_arr[i].split("-")[0];
            String tempP2 = p2_arr[i].split("-")[0];
            if(     (p1_arr[i].contains("-"))
                    &&
                    (p2_arr[i].contains("-"))
            ){
                if(tempP1.compareTo(tempP2)==0){
                    tempP1 = p1_arr[i].split("-")[1];
                    tempP2 = p2_arr[i].split("-")[1];
                    int r = Integer.parseInt(tempP1)-Integer.parseInt(tempP2);
                    if(r!=0){
                        return r;
                    }
                }else{
                    return 0;
                }
            }else {
                int r = Integer.parseInt(tempP1)-Integer.parseInt(tempP2);
                if(r!=0){
                    return r;
                }
            }


        }
        return 0;

    }

    public static void main(String[] args) {
        String p1 = "";
        String p2 = "5";
        String p3 = "3,2-1";
        System.out.println(compare(p2,p3));
    }
}
