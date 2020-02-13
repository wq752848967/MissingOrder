package Trace.parallel;

import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import Trace.utils.CostCalculator;
import Trace.utils.PriorityComparetor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class DriverTask implements Runnable {

    String traceSection[] = null;
    Map<String,String> modelOrder = null;
    ArrayList<String[]> originTraceList = null;
    ArrayList<ArrayList<String>> modelOrderList = null;
    public DriverTask(String traceSection[], Map<String,String> modelOrder,ArrayList<String[]> traceList
    ,ArrayList<ArrayList<String>> modelOrderList) {
        this.traceSection = traceSection;
        this.modelOrder  = modelOrder;
        originTraceList = traceList;
        this.modelOrderList = modelOrderList;
    }

    public void run() {
        HashSet<String> costHis = new HashSet<String>();
        OpenListNode minNodeContext = null;
        int count = 0;
        boolean flag = true;
        while(flag){
            minNodeContext = PTraceDriver.getListNode();
            if(minNodeContext==null){
                try {
                    Thread.sleep(50);
                    count++;
                }catch (Exception e){}
                if(count>=50){
                    flag = false;
                }
                continue;
            }
            //System.out.println("temp:"+minNodeContext.getCostF()+"/"+minNodeContext.getCostG()+"/"+minNodeContext.getCostH());
            /* console code */
            CharNode[] sampleNodes = minNodeContext.getStrContext();
            //重新切分 ，重新放入
            CharNode[] charNodes = minNodeContext.getStrContext();
            int oldCostG = minNodeContext.getCostG();
            boolean[] oldHis = minNodeContext.getHis();
            for(int m=0;m<traceSection.length;m++){
                if(oldHis[m]){
                    continue;
                }

                //System.out.println("Splite trans:"+traceSection[m]);
                //获取切分元素集合
                int originTraceIndex = minNodeContext.getOriginTraceIndex();
                OpenListNode newOpenListNode = new OpenListNode(minNodeContext.getStrContext().length,oldHis,originTraceIndex);
                //未打标元素和打标元素之间转换
                String reTrans = null;
                if(originTraceList==null){
                    reTrans = traceSection[m];
                }else{
                    reTrans = originTraceList.get(originTraceIndex)[m];
                }


                newOpenListNode.setHistory(m,true);


                String reTransPriority= null;
                if(reTrans.contains("-")){
                    reTransPriority = modelOrder.get(reTrans.split("-")[0]);
                }else{
                    reTransPriority = modelOrder.get(reTrans);
                }

                int newCostG = 0;
                int newReCharIndex =  -1;

                CharNode reCharNode =  null;
                ArrayList<String> preList =  new ArrayList<String>();
                ArrayList<String> postList = new ArrayList<String>();
                boolean hasReChar = false;
                int reNodeIndex = Integer.MIN_VALUE;
                ArrayList<Integer> postCostIndex =  new ArrayList<Integer>();
                for(int i=0;i<charNodes.length;i++)
                {

                    CharNode cn = charNodes[i];

                    if(cn==null){
                        continue;
                    }
                    if(minNodeContext.getCostG()==5 && (reTrans.equals("T2-1"))){
                        if(minNodeContext.getStrContext()[9].getPost().contains("T6-2")){
                            if(i==9){
                                System.out.println("");
                                System.out.println("1");
                            }
                        }

                    }
                    CharNode newCharNode = new CharNode();
                    newCharNode.setIndex(cn.getIndex());
                    boolean isContains = false;
                    int curCnIndex = cn.getIndex();




                    //pre
                    if(cn.getPre().size()>0){
                        ArrayList<String> preStr = cn.getPre();
                        if(preStr.contains(reTrans)){
                            //包含
                            isContains = true;
                            hasReChar = true;
                            reCharNode = newCharNode;
                            reCharNode.setCur(reTrans);
                            newReCharIndex = cn.getIndex();
                            reNodeIndex = cn.getIndex();
                            //处理原有节点
                            for(String old_pre_c:preStr){
                                String comId = old_pre_c.split("-")[0];

                                if(PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId))>0){
                                    preList.add(old_pre_c);
                                }else if(!old_pre_c.equals(reTrans)){
                                    postList.add(old_pre_c);
                                }else{
                                    if(!old_pre_c.equals(reTrans)){
                                        preList.add(old_pre_c);
                                    }

                                }
                            }
                        }else{
                            //不包含
                            for(String old_pre_c:preStr){
                                String comId = old_pre_c.split("-")[0];
                                if(
                                        (hasReChar) &&  ((PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId)))>0)
                                ){
                                    preList.add(old_pre_c);
                                    newCostG += Math.abs(curCnIndex-reNodeIndex);
                                }
                                else if(
                                        (!hasReChar)&&((PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId)))<0)
                                ){
                                    postList.add(old_pre_c);
                                    postCostIndex.add(curCnIndex);
                                }else{
                                    newCharNode.addPre(old_pre_c);
                                }
                            }
                        }
                    }

                    //cur
                    if(isContains){
                        //在pre中找到reTrans
                        postList.add(cn.getCur());
                    }else{
                        if(cn.getCur().length()>0){
                            if(cn.getCur().equals(reTrans)){
                                hasReChar = true;
                                reNodeIndex = curCnIndex;
                                reCharNode = newCharNode;
                                newReCharIndex  = curCnIndex;
                            }

                            String curTrans =  cn.getCur();
                            String comId = curTrans.split("-")[0];

                            if(
                                    (hasReChar) &&  ((PriorityComparetor.compare(reTrans,curTrans,reTransPriority,modelOrder.get(comId)))>0)
                            ){
                                preList.add(curTrans);
                                newCostG += Math.abs(curCnIndex-reNodeIndex);
                            }
                            else if(
                                    (!hasReChar)&&((PriorityComparetor.compare(reTrans,curTrans,reTransPriority,modelOrder.get(comId)))<0)
                            ){
                                postList.add(curTrans);
                                postCostIndex.add(curCnIndex);
                            }else {
                                newCharNode.setCur(curTrans);
                            }

                        }
                    }

                    //post
                    if(cn.getPost().size()>0){
                        ArrayList<String> postStr = cn.getPost();
                        //包含
                        if(postStr.contains(reTrans)){
                            hasReChar = true;
                            reNodeIndex = cn.getIndex();
                            if(newCharNode.getCur().length()>0){
                                preList.add(newCharNode.getCur());
                            }
                            newCharNode.setCur(reTrans);
                            reCharNode = newCharNode;
                            newReCharIndex = cn.getIndex();
                            //处理原有list
                            for(String old_pre_c:postStr){
                                String comId = old_pre_c.split("-")[0];
                                if(PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId))>0){
                                    preList.add(old_pre_c);
                                }else if(!old_pre_c.equals(reTrans)){
                                    postList.add(old_pre_c);
                                }else{
                                    preList.add(old_pre_c);
                                }
                            }
                        }else{
                            //不包含
                            String addPost = "";
                            String minIndexChar = null;
                            for(String old_pre_c:postStr){
                                String comId = old_pre_c.split("-")[0];
                                if(
                                        (hasReChar) &&  ((PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId)))>0)
                                ){

                                    preList.add(old_pre_c);
                                    newCostG += Math.abs(curCnIndex-reNodeIndex);
                                }
                                else if(
                                        (!hasReChar)&&((PriorityComparetor.compare(reTrans,old_pre_c,reTransPriority,modelOrder.get(comId)))<0)
                                ){

                                    postList.add(old_pre_c);
                                    postCostIndex.add(curCnIndex);
                                }else{

                                    newCharNode.addPost(old_pre_c);
                                    if(minIndexChar==null||(PriorityComparetor.compare(reTrans,old_pre_c,modelOrder.get(comId),modelOrder.get(minIndexChar))<0)){
                                        minIndexChar = comId;
                                    }
                                }
                            }
                            if(newCharNode.getCur().length()==0){
                                newCharNode.setCur(minIndexChar);
                                postList.remove(minIndexChar);
                            }


                        }
                    }

                    newOpenListNode.setNode(newCharNode.getIndex(),newCharNode);
                }


                //补全costG
                for(int index:postCostIndex){
                    newCostG+=Math.abs(index-reNodeIndex);
                }






                if(newCostG==0){
                    //无用切分
                    continue;
                }
                //add net replace set
                for(String preItem:preList){
                    reCharNode.getPre().add(preItem);
                }
                for(String postItem:postList){
                    reCharNode.getPost().add(postItem);
                }
//                reCharNode.setPre(preList);
//                reCharNode.setPost(postList);

                newOpenListNode.setNode(newReCharIndex,reCharNode);
                //重新计算cost
                //pre init
                int newCostH =  CostCalculator.calcCostH(modelOrderList,newOpenListNode.getStrContext());
                int newSumCostG=oldCostG+newCostG;
                newOpenListNode.setCostG(newSumCostG);

                String newKey = newSumCostG+"/"+newCostH;
                if(costHis.contains(newKey)){
                    //System.out.println("skkkkkkk");
                    continue;
                }else {
                    costHis.add(newKey);
                }
//                String traceKey = CharNodeTools.getConsoleString(modelOrder,newOpenListNode.getStrContext());
//                if (traceHis.containsKey(traceKey)){
//                    continue;
//                }else{
//                    traceHis.put(traceKey,newSumCostG);
//                }


                if(newCostH==0)
                {
                    //System.out.println("newCostH == 0");
                    //分支遍历结束  并未结束，进一步进行判断
                    if(CostCalculator.isFinished(modelOrder,newOpenListNode.getStrContext())){
                        if(PTraceDriver.minCost>newOpenListNode.getCostF()){
                            PTraceDriver.setMinInfo(newOpenListNode.getCostF(),newOpenListNode);
                        }
                    }else{

                        if(newCostG!=0){
                            newOpenListNode.setCostH(newCostH);
                            //是否存在完成的排序方式
                            PTraceDriver.addListNode(newOpenListNode);


                        }else{
                        }
                    }


                }else{
                    if(newCostG!=0){
                        newOpenListNode.setCostH(newCostH);
                        //是否存在完成的排序方式
                        PTraceDriver.addListNode(newOpenListNode);
                    }else{

                    }

                }



            }
        }
        System.out.println("end 1");
        PTraceDriver.addResult();


    }
}
