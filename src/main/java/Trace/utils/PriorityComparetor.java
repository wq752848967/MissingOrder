package Trace.utils;

public class PriorityComparetor {
    public static int compare(String id,String comId,String p1,String p2){
        String[] p1_arr = null;
        String[] p2_arr = null;
        if(id!=null && comId!=null){
            if(id.contains("-")&&comId.contains("-")){
                int id_index = Integer.parseInt(id.split("-")[1]);
                int comId_index = Integer.parseInt(comId.split("-")[1]);
                if(id_index!=comId_index){
                    return id_index-comId_index;
                }
            }
        }

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
        if(p1.split(",").length==p2.split(",").length){
            return 0;
        }
        return p1.split(",").length-p2.split(",").length;

    }

    public static void main(String[] args) {
        String p1 = "";
        String p2 = "1";
        String p3 = "1,1-0";
        System.out.println(compare("","",p2,p3));
    }
}
