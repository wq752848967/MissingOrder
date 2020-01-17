package Trace.utils;

import Trace.pojos.CharNode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

public class CharNodeTools {
    public static String getConsoleString(Map<String,String> modelOrder, CharNode[] nodes){
        String consoleString = "";
        int originIndex =  0;
        TreeSet<SortNode> sortList = new TreeSet<SortNode>(new Comparator<SortNode>() {
            public int compare(SortNode o1, SortNode o2) {
                int r = PriorityComparetor.compare("","",o1.getPriority(),o2.getPriority());
                return r==0?(o1.getValue().compareTo(o2.getValue())):r;
            }
        });

        Arrays.sort(nodes, new Comparator<CharNode>() {
            public int compare(CharNode o1, CharNode o2) {
                if(o1==null){
                    return -1;
                }
                if(o2==null){
                    return 1;
                }
                return o1.getIndex()-o2.getIndex();
            }
        });
        for(CharNode n:nodes){

            if(n==null){
                continue;
            }
            int tempAddCount = 0;
            int n_index = n.getIndex();
            String tempConsole=n_index+":";
            //pre

            if(n.getPre().size()>0){


                for(String c:n.getPre()){
                    String tempPriority = modelOrder.get(c);
                    if(c.contains("-")){
                        tempPriority = modelOrder.get(c.split("-")[0]);
                    }
                    SortNode node = new SortNode(tempPriority,c);
                    sortList.add(node);

                }
                while(!sortList.isEmpty()){

                    SortNode node = sortList.first();
                    sortList.remove(node);
                    tempConsole+=node.getValue()+" ";
                    tempAddCount++;
                }
            }
            if(n.getCur().length()>0){
                tempConsole+="["+n.getCur()+"]";
                tempAddCount++;
            }



            //post
            if(n.getPost().size()>0){
                for(String c:n.getPost()){
                    String tempPriority = modelOrder.get(c);
                    if(c.contains("-")){
                        tempPriority = modelOrder.get(c.split("-")[0]);
                    }
                    SortNode node = new SortNode(tempPriority,c);
                    sortList.add(node);

                }
                while(!sortList.isEmpty()){
                    SortNode node = sortList.first();
                    sortList.remove(node);
                    tempConsole+=node.getValue()+" ";
                    tempAddCount++;
                }
            }
            if(tempAddCount>0){
                consoleString+=tempConsole+" ";
            }

        }
        return consoleString;
    }
}
