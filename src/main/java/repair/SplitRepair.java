package repair;

import Loop.LoopUtils;
import Trace.SplitDriver;
import Trace.TraceDriver;
import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import Trace.utils.CostCalculator;
import model.ModelAnalysis;
import model.ModelLoader;
import model.ModelPriotity;
import model.pojos.*;
import simulation.ModelGenerate;
import simulation.RandomTrace;
import simulation.TraceGenerate;

import java.util.*;

public class SplitRepair {


    public static String repair(ArrayList<ArrayList<String>> modelOrderList,
                                Map<String,String> modelOrder,
                                PetriNet net, PNode startNode, PNode endNode, String[] trace){
        String result = "";
        ArrayList<String[]> traceList  = null;
        List<PLoop> loops =  net.getLoops();
        HashMap<String,PLoop> loopStartMap = new HashMap<String, PLoop>();
        for(PLoop pl:loops){
            loopStartMap.put(pl.getStart(),pl);
        }
        if(loops!=null&&loops.size()>0){
            traceList  = LoopUtils.devideBranchSection(net,trace);
        }
        while(startNode!=null&&(startNode!=endNode)){

           if(startNode.getType()== PNodeType.PLACE){
               //循环判断
               if(loopStartMap.containsKey(startNode.getId())){
                    PLoop pl = loopStartMap.get(startNode.getId());
                    String loopTranId = pl.getTransition();
                    Place  loopEndPlace = net.getpMap().get(pl.getEnd());
                   loopEndPlace.getNext().remove(net.gettMap().get(loopTranId));
                   startNode = loopEndPlace;






                   //存在循环
                   //取出所有划分中，本循环的部分
                   ArrayList<String[]> loopTraceList  = new ArrayList<String[]>();
                   //获得本次循环中所有 event
                   ArrayList<String> curLoopTranSet = getAllParallelTransition(net.getpMap().get(pl.getStart())
                           ,net.getpMap().get(pl.getEnd()));
                   curLoopTranSet.add(pl.getTransition());
                   //筛选元素进行rebuild

                   for(String[] t:traceList){
                        //获取最大和最小索引
                       int minIndex  = Integer.MAX_VALUE;
                       int maxIndex = Integer.MIN_VALUE;
                       for (int i = 0; i < t.length; i++) {
                           if(curLoopTranSet.contains(t[i].split("-")[0])){
                                minIndex = Math.min(minIndex,i);
                                maxIndex = Math.max(maxIndex,i);
                           }
                       }
                       int arrLength  = (maxIndex-minIndex)+1;
                       String[] newTrace = new String[arrLength];
                       for (int i = 0; i < t.length; i++) {
                           String tran = t[i];
                           if(curLoopTranSet.contains(tran.split("-")[0])){
                               newTrace[i-minIndex] = tran;
                           }
                       }
                       loopTraceList.add(newTrace);


                   }
                   OpenList openList = new OpenList();
                   for(int i=0;i<loopTraceList.size();i++){
                       String[] t =  loopTraceList.get(i);
                       OpenListNode openListNode = initLoopOpenList(t,modelOrderList,trace.length,i);
                       openList.add(openListNode);
                   }
                   SplitDriver.rebuild(openList,loopTraceList,modelOrderList,modelOrder,loopTraceList.get(0),null);
                   //处理结果
                   String curResult = CharNodeTools.getTraceString(modelOrder,SplitDriver.min_Context.getStrContext());
                   result+=curResult;



                   continue;
               }


               if(startNode.getNext().size()==1){
                    PNode nextTransition   = startNode.getNext().get(0);
                    startNode = nextTransition;

               }else{
                   //分支
                   //get end
                   String endPlaceId = getEnd(startNode).getPreId();
                   //get start
                   PNode startTransition = null;
                   for(PNode p:startNode.getNext()){
                       String id = p.getId(); //transition
                       for (String event:trace){
                           if (event.equals(id)){
                               startTransition = p;
                               break;
                           }
                       }
                       if(startTransition!=null){
                           break;
                       }
                   }
                   result+=repair(modelOrderList,modelOrder,net,startTransition,net.getpMap().get(endPlaceId),trace);
                   //
                   startNode = endNode;
               }
           }else{
               //transition
               result+=startNode.getId();
               if(startNode.getNext().size()==1){
                    startNode = startNode.getNext().get(0);
               }else{
                   //并发
                    //System.out.println("para start:"+startNode.getId());
                    PNode endParallelNode = getParallelEnd(startNode);
                    //get all trace transition
                    List<String>  tranSets = getAllParallelTransition(startNode,endParallelNode);
                    Map<String,Integer> traceOrder = new HashMap<String, Integer>();
                    Map<String,Integer> nTraceOrder = new HashMap<String, Integer>();
                    int minIndex  = -1;
                    for(int i=0;i<trace.length;i++){
                        String cur = trace[i];
                        if(tranSets.contains(cur)){
                            traceOrder.put(cur,i);
                            if(minIndex<0){
                                minIndex = i;
                            }
                        }
                    }
                    int maxIndex = -1;
                    for(String key:traceOrder.keySet()){
                        int preIndex = traceOrder.get(key);
                        int newIndex = preIndex-minIndex;
                        nTraceOrder.put(key,newIndex);
                        if(maxIndex<newIndex){
                            maxIndex = newIndex;
                        }
                    }
                   //System.out.println("max:"+maxIndex);
                    String[] traceSection =  new String[maxIndex+1];
                    for(String key:nTraceOrder.keySet()){
                       String eventName = key;
                       int index =  nTraceOrder.get(key);
                       traceSection[index] = eventName;
                    }

                    OpenList openList = new OpenList();
                    OpenListNode openListNode = initOpenList(nTraceOrder,modelOrderList,maxIndex);
                    openList.add(openListNode);
                    SplitDriver.clear();
                    SplitDriver.rebuild(openList,null,modelOrderList,modelOrder,traceSection,nTraceOrder);
                    //处理结果
                    String curResult = CharNodeTools.getTraceString(modelOrder,SplitDriver.min_Context.getStrContext());
                    result+=curResult;
                    //rebuild
                   startNode = endParallelNode;
                   //System.out.println("end:"+curResult);
               }

           }


        }
        return  result;
    }
    public ArrayList<String> getAllTransition(PNode start,PNode end){
        ArrayList<String> res = new ArrayList<String>();
        for (PNode sitem:start.getNext()){
            getTransition((Transition) sitem,end.getId(),res);
        }
        return  res;

    }
    private static void getTransition(Transition pnode,String endBranchId,ArrayList<String> res){
        res.add(pnode.getId());
        while(pnode.getNext().size()==1){
            Place place = (Place)pnode.getNext().get(0);
            if(place.getId().equals(endBranchId)){
                pnode = null;
                break;
            }
            //System.out.println("place Id:"+place.getId()+"  "+place.getNext().size());
            pnode = (Transition) place.getNext().get(0);
            res.add(pnode.getId());
        }
        if(pnode!=null){
            for(PNode parellelPlace : pnode.getNext()){
                getTransition((Transition) parellelPlace.getNext().get(0),endBranchId,res);
            }
        }
    }
    public static PNode getEnd(PNode node){
        //node  place
        PNode endNode =  node.getNext().get(0);
        PNode prePlace = node;
        int priLength = endNode.getPriority().length();
        while(priLength<=endNode.getPriority().length()){
            System.out.println("1");
            endNode.getNext().get(0).getNext().get(0);
            prePlace = endNode.getNext().get(0);
        }
        return prePlace;

    }
    public static OpenListNode initLoopOpenList(String[] trace ,ArrayList<ArrayList<String>> modelOrderList,int arrLength,int index){
        OpenListNode openListNode = new OpenListNode(arrLength,index);
        boolean[] transRecord = new boolean[arrLength];
        Arrays.fill(transRecord,true);
        for (int i = 0; i < trace.length;i++) {
            if(trace[i]!=null){
                CharNode cNode = new CharNode();
                cNode.setIndex(i);
                transRecord[i] = false;
                cNode.setCur(trace[i]);
                openListNode.setNode(i,cNode);
            }
        }

        //初始化代价

        openListNode.setHistory(transRecord);
        openListNode.setCostG(0);
        int costH  = CostCalculator.calcCostH(modelOrderList,openListNode.getStrContext());
        openListNode.setCostH(costH);
        return openListNode;
    }
    public static OpenListNode initOpenList(Map<String,Integer> traceOrder ,ArrayList<ArrayList<String>> modelOrderList,int maxIndex){
        OpenListNode openListNode = new OpenListNode(maxIndex+1,-1);
        boolean[] transRecord = new boolean[maxIndex+1];
        Arrays.fill(transRecord,true);
        for(String key:traceOrder.keySet()){
            String eventName = key;
            int index =  traceOrder.get(key);
            CharNode cNode = new CharNode();
            cNode.setIndex(index);
            transRecord[index] = false;
            cNode.setCur(eventName);
            openListNode.setNode(index,cNode);

        }

        //初始化代价

        openListNode.setHistory(transRecord);
        openListNode.setCostG(0);
        int costH  = CostCalculator.calcCostH(modelOrderList,openListNode.getStrContext());
        openListNode.setCostH(costH);
        return openListNode;
    }
    public static ArrayList<String> getAllParallelTransition(PNode start,PNode end){
        LinkedList<PNode> queue = new LinkedList<PNode>();
        ArrayList<String> res = new ArrayList<String>();
        for (PNode node:start.getNext()){
            queue.add(node);
        }

        while(!queue.isEmpty()){
            PNode tempNode = queue.poll();
            if(tempNode.getType()==PNodeType.TRANSITION){
                if(!res.contains(tempNode.getId())){
                    res.add(tempNode.getId());
                }

                for(PNode nex:tempNode.getNext()){
                    if(!nex.getId().equals(end.getId())){
                        queue.add(nex);
                    }

                }
            }else{
                for(PNode nex:tempNode.getNext()){
                    if(!nex.getId().equals(end.getId())){
                        queue.add(nex);
                    }
                }
            }

        }
        return res;

    }
    public static PNode getParallelEnd(PNode node){
        PNode endNode =  node.getNext().get(0).getNext().get(0);
        int priLength = endNode.getPriority().length();

        //System.out.println("2:"+priLength+" "+node.getPriority());
        while(priLength<=endNode.getPriority().length()){

            endNode = endNode.getNext().get(0).getNext().get(0);
        }
        return endNode;
    }
    public static void main(String[] args) {
        int nodeCount = 40;
        double per = 0.8;
        PetriNet net = ModelGenerate.generateSerialModel(nodeCount);
        //PetriNet net  = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/splite/s-60.xml");
        //循环检测
        ModelAnalysis.findLoop(net);
        //模型transition优先级标注
        ModelPriotity.makePriority(net);
        long startTime=System.currentTimeMillis();
        for (int k = 0; k < 10; k++) {
            String[] trace = TraceGenerate.generateSerialTrace(nodeCount);
            trace = RandomTrace.random(per,trace);
            //String[] trace = {"T0","T1","T2","T3","T5","T7","T10","T2","T4","T6","T7","T10","T2","T3","T5","T7","T8","T9"};
            //trace = RandomTrace.random(0.9,trace);
            System.out.println("");
            Map<String,Integer> traceOrder =  new HashMap<String, Integer>();
            for (int i = 0; i < trace.length; i++) {
                traceOrder.put(trace[i],i);
            }
            //获取优先级
            Map<String,String> modelOrder  = new HashMap<java.lang.String, java.lang.String>();
            for(String key:net.gettMap().keySet()){
                Transition tran = net.gettMap().get(key);
                modelOrder.put(tran.getId(),tran.getPriority());
            }
            //获取线性transition列表
            ArrayList<ArrayList<String>> modelOrderList = ModelAnalysis.getModelOrderList(net);

            String res = repair(modelOrderList,modelOrder,net,net.getStartPlace(),net.getEndPlace(),trace);

            System.out.println(res);
        }

        long endTime=System.currentTimeMillis();
        System.out.println("");
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}
