package model;

import model.pojos.PNode;
import model.pojos.PNodeType;
import model.pojos.PetriNet;
import Trace.utils.PriorityComparetor;

public class ModelPriotity {

    public static void makePriority(PetriNet net){
        mergePriority(net,net.getStartPlace(),"-1",-1);
    }
    public static void mergePriority(PetriNet net,PNode node,String priority,int subIndex){

        if(node.getType()== PNodeType.PLACE){
            //System.out.println("node Id:"+node.getId()+"  "+net.findLoopEnd(node.getId()));
            if(net.findLoopEnd(node.getId())){
                //循环 反向边的起点
                //System.out.println("** in reverse:"+node.getId());
                String loopTransitionId = net.getLoopTransition(node.getId());
                priority = handleLoop(net.gettMap().get(loopTransitionId),priority);
                for(PNode nitem:node.getNext()){
                    if(nitem.getId()!=loopTransitionId){
                        mergePriority(net,nitem,priority,subIndex);
                    }
                }

            }else{
                //串行
                if(node.getNext().size()==1){
                    mergePriority(net,node.getNext().get(0),priority,subIndex);
                }else if(node.getNext().size()>1){
                    //分支
                    int nextSubIndex = 0;
                    for(PNode nItem:node.getNext()){
                        mergePriority(net,nItem,priority,nextSubIndex);
                        nextSubIndex++;
                    }
                }
            }


        }
        else if(node.getType()==PNodeType.TRANSITION){
            //System.out.println("transition:"+node.getId());
            String newPriority = "";
            int preIndex = -1;
            if(subIndex>=0){
                //这是分支
//                if((priority.length()==1)&&(Integer.parseInt(priority)<0)){
//                    preIndex = -1;
//                }else{
//                    int index_d = priority.lastIndexOf(",");
//                    int index_h = priority.lastIndexOf("-");
//                    String spChcar = ",";
//                    //System.out.println("indexd:"+index_d+"  "+index_h+"  "+priority);
//                    if(index_d>index_h){
//                        // last one ,
//                        preIndex = Integer.parseInt(priority.substring(index_d+1,priority.length()));
//
//                    }else if(index_d<index_h){
//                        spChcar = "-";
//                        preIndex = Integer.parseInt(priority.substring(index_d+1,index_h));
//                    }else{
//                        preIndex = Integer.parseInt(priority);
//                    }
//                }



                int curIndex = preIndex+1;
                newPriority = priority+","+subIndex+"-0";
                if(node.getPriority().length()>0){
                    newPriority = replacePriority(node.getPriority(),newPriority);


                }
                node.setPriority(newPriority);

            }else{
                //上一个优先级递增即可
                //System.out.println(node.getId()+" in just add:"+priority);
                if((priority.length()==2)&&(Integer.parseInt(priority)<0)){
                    newPriority = "0";
                }else{
                    int index_d = priority.lastIndexOf(",");
                    int index_h = priority.lastIndexOf("-");
                    if(index_d<0 && index_h<0){
                        newPriority = Integer.parseInt(priority)+1+"";
                    }else{
                        int maxPreIndex =  Math.max(index_d,index_h);
                        String prePri = priority.substring(0,maxPreIndex+1);
                        String priFormat = ",";
                        String spChcar = ",";
                        if(index_d<index_h){
                            spChcar = "-";
                            priFormat = "";
                        }
                        int pLeng = priority.split(spChcar).length-1;
                        preIndex = Integer.parseInt(priority.split(spChcar)[pLeng]);
                        int curIndex = preIndex+1;
                        newPriority = prePri+priFormat+curIndex;
                    }

                    //System.out.println(node.getId()+" newPri:"+newPriority);
                }


                if(node.getPriority().length()>0){
                    newPriority = replacePriority(node.getPriority(),newPriority);
                }
                //System.out.println("node:"+node.getId()+"  p:"+newPriority);
                node.setPriority(newPriority);
            }
            //System.out.println("end transition:"+node.getId()+" "+newPriority);
            //下一个
            if(node.getNext().size()>1){
                for(int i=0;i<node.getNext().size();i++){
                    mergePriority(net,node.getNext().get(i),newPriority,i);
                }
            }else{
                mergePriority(net,node.getNext().get(0),newPriority,-1);
            }

        }
    }
    public static String handleLoop(PNode node,String priority){
        String newPriority = "";
        int index_d = priority.lastIndexOf(",");
        int index_h = priority.lastIndexOf("-");
        int maxPreIndex =  Math.max(index_d,index_h);
        if(index_d<0 && index_h<0){
            newPriority = Integer.parseInt(priority)+1+"";
            node.setPriority(newPriority);
        }else{
            String prePri = priority.substring(0,maxPreIndex+1);
            String priFormat = ",";
            String spChcar = ",";
            if(index_d<index_h){
                spChcar = "-";
                priFormat = "";
            }
            int pLeng = priority.split(spChcar).length-1;
            int preIndex = Integer.parseInt(priority.split(spChcar)[pLeng]);
            int curIndex = preIndex+1;
            newPriority = prePri+priFormat+curIndex;
            node.setPriority(newPriority);
        }

        //System.out.println("handel loop:"+newPriority);
        return newPriority;
    }
    public static String replacePriority(String originPri,String replaceOri){
        //System.out.println("replace:"+originPri+"  "+replaceOri);


        if(PriorityComparetor.compare(originPri,replaceOri)>0){
            if(originPri.length()<replaceOri.length()){
                return originPri;
            }
        }
        if(PriorityComparetor.compare(originPri,replaceOri)<0){
            if(originPri.length()>replaceOri.length()){
                return replaceOri;
            }
        }
        String resOri = ",";
        while(originPri.length()!=replaceOri.length()){
            if(originPri.length()>replaceOri.length()){
                originPri = originPri.substring(0,originPri.lastIndexOf(","));
            }else{
                replaceOri = replaceOri.substring(0,replaceOri.lastIndexOf(","));
            }
        }

        String subOri = originPri.split(",")[originPri.split(",").length-1];
        String subReplace = replaceOri.split(",")[replaceOri.split(",").length-1];
        //System.out.println("sub ori:"+subOri+"  "+subReplace);

        while(!subOri.equals(subReplace)){
            originPri = originPri.substring(0,originPri.lastIndexOf(","));
            replaceOri = replaceOri.substring(0,replaceOri.lastIndexOf(","));
             subOri = originPri.split(",")[originPri.split(",").length-1];
             subReplace = replaceOri.split(",")[replaceOri.split(",").length-1];
        }

        if(originPri.contains(",")){
            String lasPri = originPri.split(",")[originPri.split(",").length-1];
            originPri = originPri.substring(0,originPri.lastIndexOf(","));
            if(lasPri.contains("-")){
                int nextPri = Integer.parseInt(lasPri.split("-")[1])+2;
                lasPri = lasPri.substring(0,subOri.lastIndexOf("-"));
                resOri += lasPri+"-"+nextPri;
            }else{
                resOri += Integer.parseInt(lasPri)+1;
            }
            originPri+=resOri;
        }else{
            resOri = Integer.parseInt(originPri)+2+"";
            originPri=resOri;
        }


        //System.out.println("replace:"+originPri+" "+replaceOri);
        //System.out.println("r result:"+originPri);
        return originPri;
    }




    public static void main(String[] args) {
        PetriNet net = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/test01.xml");
        //System.out.println(net.getpMap().get("P0").getNext().get(0));

        makePriority(net);
        for(String key:net.gettMap().keySet()){
            System.out.print("id:"+net.gettMap().get(key).getId());
            System.out.println("  next:"+net.gettMap().get(key).getPriority());
        }
    }
}
