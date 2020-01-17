package Trace;

import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import Trace.utils.CostCalculator;
import Trace.utils.PriorityComparetor;

import java.util.ArrayList;
import java.util.Map;

public class OnePassDriver {
    public static int minCost = Integer.MAX_VALUE;
    public  static OpenListNode min_Context =  null;
    private static final boolean LOG_OUT = false;
    public static String testResult = "";

    public static void repair(ArrayList<String[]> traceList, OpenList openList, ArrayList<ArrayList<String>> modelOrderList, Map<String,String> modelOrder, String traceSection[], Map<String,Integer> traceOrder){
        ArrayList<String[]> originTraceList  =  traceList;
        //循环主体  取最小 切分，在放入
        while(!openList.IsEmpty()){
            //get mini  node context
            OpenListNode minNodeContext = null;
            try{
                minNodeContext = openList.getMinCostNode();
            }catch (Exception e){

            }
            openList.removeNode(minNodeContext);




            if(minNodeContext.getCostF()>=minCost){
                if(LOG_OUT){
                    CharNode[] sampleNodes = minNodeContext.getStrContext();
                    String reuslt = CharNodeTools.getConsoleString(modelOrder,sampleNodes);
                    System.out.print("* skip temp min,cost: "+minNodeContext.getCostG());
                    System.out.println(" | trace: "+reuslt);
                }
                continue;
            }



            /* console code */
            CharNode[] sampleNodes = minNodeContext.getStrContext();
            String reuslt = CharNodeTools.getConsoleString(modelOrder,sampleNodes);
            if(LOG_OUT){
                System.out.print("temp min,cost: "+minNodeContext.getCostG());
                System.out.println(" | trace: "+reuslt);
            }










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

                newOpenListNode.setNode(newReCharIndex,reCharNode);
                //重新计算cost
                //pre init
                int newCostH =  CostCalculator.calcCostH(modelOrderList,newOpenListNode.getStrContext());
                newOpenListNode.setCostG(oldCostG+newCostG);

                if(newCostH==0)
                {
                    //System.out.println("newCostH == 0");
                    //分支遍历结束  并未结束，进一步进行判断
                    if(CostCalculator.isFinished(modelOrder,newOpenListNode.getStrContext())){
                        if(minCost>newOpenListNode.getCostF()){
                            minCost = newOpenListNode.getCostF();
                            min_Context = newOpenListNode;
                            if(LOG_OUT){
                                String rr = CharNodeTools.getConsoleString(modelOrder,newOpenListNode.getStrContext());
                                System.out.println(reTrans+" /end trace:"+rr);
                            }
                        }
                    }else{

                        if(newCostG!=0){
                            newOpenListNode.setCostH(newCostH);
                            //是否存在完成的排序方式
                            openList.add(newOpenListNode);
                            if(LOG_OUT){
                                String rr = CharNodeTools.getConsoleString(modelOrder,newOpenListNode.getStrContext());
                                System.out.println(reTrans+" /end trace:"+rr);
                            }

                        }else{
                        }
                    }


                }else{
                    if(newCostG!=0){
                        newOpenListNode.setCostH(newCostH);
                        //是否存在完成的排序方式
                        openList.add(newOpenListNode);
                        if(LOG_OUT){
                            String rr = CharNodeTools.getConsoleString(modelOrder,newOpenListNode.getStrContext());
                            System.out.println(reTrans+" /end trace:"+rr);
                        }
                    }else{

                    }

                }



            }

           //end while
            if(!openList.IsEmpty()){
                OpenListNode node = null;
                try{
                    node = openList.getMinCostNode();
                }catch (Exception e){

                }

                openList.clear();
                openList.add(node);
            }

        }
    }
}
