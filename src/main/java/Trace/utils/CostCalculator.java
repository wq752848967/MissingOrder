package Trace.utils;

import Trace.pojos.CharNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CostCalculator {
    public static int calcCostH(ArrayList<String>[] modelOrderList, CharNode[] strContext){
        int costH = 0;
        //pre init
        Map<String,Integer> map = new HashMap<String, Integer>();
        for(int i=0;i<strContext.length;i++){
            CharNode node = strContext[i];
            if(node!=null){
                //System.out.println("ctx info:"+node.getPre()+" "+node.getCur()+" "+node.getPost());
                if(node.getPre()!=null){
                    ArrayList<String> preChars = node.getPre();
                    for(String c:preChars){
                        map.put(c,node.getIndex());
                    }
                }

                if(node.getCur().length()>0){
                    String curChar = node.getCur();
                    map.put(curChar,node.getIndex());
                }

                if(node.getPost()!=null){
                    ArrayList<String> postChars = node.getPost();

                    for(String c:postChars){
                        map.put(c,node.getIndex());
                    }
                }


            }
        }
        //calc
        for(ArrayList<String> orderItem:modelOrderList){
            for(int i=0;i<orderItem.size()-1;i++){
                String cur = orderItem.get(i);
                String next = orderItem.get(i+1);
                //System.out.println("err:"+cur);
                int curIndex = map.get(cur);
                int nextIndex = map.get(next);

                if(curIndex>nextIndex){
                    costH+=Math.abs(curIndex-nextIndex);
                }
            }

        }


        return costH;
    }
    public static boolean isFinished(Map<String,String> modelOrder, CharNode[] nodes){
        boolean finished = true;
        String curMaxPri = null;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i]!=null){
                CharNode cn = nodes[i];
                //check
                String curMinPri = null;
                String preMaxPri = curMaxPri;
                //pre
                if(cn.getPre()!=null){
                    for(String item:cn.getPre()){
                        String itemPri  = modelOrder.get(item);
                        if(curMaxPri==null||(PriorityComparetor.compare(curMaxPri,itemPri)<0)){
                            curMaxPri = itemPri;
                        }
                        if(curMinPri==null||(PriorityComparetor.compare(curMinPri,itemPri)>0)){
                            curMinPri = itemPri;
                        }
                    }
                }
                //cn
                if(cn.getCur().length()>0){
                    String itemPri  = modelOrder.get(cn.getCur());

                    if(curMaxPri==null||(PriorityComparetor.compare(curMaxPri,itemPri)<0)){
                        curMaxPri = itemPri;
                    }
                    if(curMinPri==null||(PriorityComparetor.compare(curMinPri,itemPri)>0)){
                        curMinPri = itemPri;
                    }
                    //System.out.println(curMinPri+"  "+itemPri);
                }
                //post
                if(cn.getPost()!=null){
                    for(String item:cn.getPost()){
                        String itemPri  = modelOrder.get(item);
                        if(curMaxPri==null||(PriorityComparetor.compare(curMaxPri,itemPri)<0)){
                            curMaxPri = itemPri;
                        }
                        if(curMinPri==null||(PriorityComparetor.compare(curMinPri,itemPri)>0)){
                            curMinPri = itemPri;
                        }
                    }
                }
                //System.out.println("pri:"+cn.getIndex()+" "+curMaxPri+"  "+curMinPri);
                if((preMaxPri!=null&&curMinPri!=null)&&(PriorityComparetor.compare(preMaxPri,curMinPri)>0)){
                    return false;
                }
            }
        }
        return finished;
    }

    //unit test driver
    public static void main(String[] args) {
        String origin = "abcdef";
        String cur = "cdefab";
        char[] curChars = cur.toCharArray();
        CharNode[] strContext = new CharNode[origin.length()];

        for(int i=0;i<cur.length();i++){
            CharNode node = new CharNode();
            node.setCur(curChars[i]+"");
            strContext[i] = node;
        }
        //System.out.println(calcCostH(origin,strContext));
    }
}
