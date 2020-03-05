package repair;

import Loop.LoopUtils;
import Trace.OnePassDriver;
import Trace.TraceDriver;
import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import model.ModelAnalysis;
import model.ModelLoader;
import model.ModelPriotity;
import model.pojos.PetriNet;
import model.pojos.Transition;
import simulation.ModelGenerate;
import simulation.RandomTrace;
import simulation.TraceGenerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnePass_Repair {
    public static void main(String[] args) {
        //解析模型
//        PetriNet net = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/withoutLoop/test3-11.xml");
//        //循环检测
//        ModelAnalysis.findLoop(net);
//        //模型transition优先级标注
//        ModelPriotity.makePriority(net);
//
//
//        /*
//         * 模型信息输出
//         * */
//
////        System.out.println("");
////        System.out.println("===================");
////        //String[] trace = {"T0","T4","T2","T1","T6","T8","T1","T3","T5","T6","T7","T1","T3","T5","T6","T8"};
////        //ArrayList<String[]> results  = LoopUtils.devideBranchSection(net,trace);
////        for (String t:net.getpMap().keySet()){
////            System.out.println("place:"+t);
////            for (PNode p:net.getpMap().get(t).getNext()){
////                System.out.print(p.getId());
////            }
////            System.out.println("");
////        }
//
//
//        /*
//        优先级信息输出
//        * */
////        for(String key:net.gettMap().keySet()){
////            System.out.println(key+" "+net.gettMap().get(key).getPriority());
////        }
//
//
//        /*循环信息输出*/
////        for(PLoop pl : net.getLoops()){
////            System.out.println("loop:"+pl.getStart()+"  "+pl.getEnd()+"   t:"+pl.getTransition());
////        }
//
//
//        //trace            0     1     2    3   4     5    6    7    8
//        String[] trace = {"T9","T10","T0","T1","T2","T3","T8","T4","T6"};
        int nodeCount = 35;
        double per = 0.3;
        int tanCount = 1;
        PetriNet net = ModelGenerate.generateSerialModel(nodeCount);
        long startTime=System.currentTimeMillis();
        for (int k = 0; k < 100; k++) {

            String[] trace = TraceGenerate.generateSerialTrace(nodeCount);
            trace = RandomTrace.random(per,trace);
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


            OpenList openList = new OpenList();
            if(net.getLoops().size()>0){
                //存在循环
                ArrayList<String[]> traceList  = LoopUtils.devideBranchSection(net,trace);
                for(int i=0;i<traceList.size();i++){
                    String[] t =  traceList.get(i);
                    OpenListNode openListNode = TraceDriver.initOpenList(t,modelOrderList,i);
                    openList.add(openListNode);
                }
                try{
                    TraceDriver.rebuild(traceList,openList,modelOrderList,modelOrder,trace, traceOrder);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                //不存在循环
                //调用：
                OpenListNode openListNode = TraceDriver.initOpenList(trace,modelOrderList,-1);
                openList.add(openListNode);
                OnePassDriver.repair(tanCount,null,openList,modelOrderList,modelOrder,trace, traceOrder);
            }
            if(OnePassDriver.min_Context!=null){
                OpenListNode sampleNodeContext = OnePassDriver.min_Context;
                System.out.println(" ");
                System.out.println("_______________________________________________________");
                System.out.println("result console cost info:"+sampleNodeContext.getCostF());
                CharNode[] sampleNodes = sampleNodeContext.getStrContext();
                String reuslt = CharNodeTools.getConsoleString(modelOrder,sampleNodes);
                OnePassDriver.testResult = reuslt;
                System.out.println(OnePassDriver.testResult);
            }else{
                System.out.println("min context == null");
            }
        }
        long endTime=System.currentTimeMillis();
        System.out.println("");
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

    }
}
