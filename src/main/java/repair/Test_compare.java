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

public class Test_compare {
    public static void main(String[] args) {
        //解析模型
        for(int j=0;j<1;j++){
            PetriNet net  = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/withoutLoop/test3-25.xml");
            //循环检测
            ModelAnalysis.findLoop(net);
            //模型transition优先级标注
            ModelPriotity.makePriority(net);
            long startTime=System.currentTimeMillis();


            //trace            0     1     2    3   4     5    6    7    8
            String[] trace = {"T0","T1","T2","T3","T5","T4","T6","T7","T9","T11","T12","T14","T13","T15","T16","T17"};
            trace = RandomTrace.random(0.9,trace);
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
                try{
                    TraceDriver.rebuild(null,openList,modelOrderList,modelOrder,trace, traceOrder);
                }catch (Exception e){
                    e.printStackTrace();
                }

                openListNode = TraceDriver.initOpenList(trace,modelOrderList,-1);
                openList.add(openListNode);
                OnePassDriver.repair(null,openList,modelOrderList,modelOrder,trace, traceOrder);

            }

            OpenListNode sampleNodeContext = TraceDriver.min_Context;
//            System.out.println(" ");
//            System.out.println("_______________________A*______________________________");
//            System.out.println("result console cost info:"+sampleNodeContext.getCostF());
//            CharNode[] sampleNodes = sampleNodeContext.getStrContext();
//            String reuslt = CharNodeTools.getConsoleString(modelOrder,sampleNodes);
//            TraceDriver.testResult = reuslt;
//            System.out.println(TraceDriver.testResult);

            OpenListNode sampleNodeContext2 = OnePassDriver.min_Context;
//            System.out.println(" ");
//            System.out.println("_______________________OnePass___________________________");
//            System.out.println("result console cost info:"+sampleNodeContext2.getCostF());
//            CharNode[] one_sampleNodes = sampleNodeContext2.getStrContext();
//            String one_reuslt = CharNodeTools.getConsoleString(modelOrder,one_sampleNodes);
//            TraceDriver.testResult = one_reuslt;
//            System.out.println(one_reuslt);
            int costA = sampleNodeContext.getCostF();
            int costD = sampleNodeContext2.getCostF();
            if(costA==costD){
                System.out.println("==  costA:"+costA+"   costD:"+costD);
            }else{
                System.out.println("==================================");
                System.out.println("!=  costA:"+costA+"   costD:"+costD);
                System.out.println("==================================");
            }

            long endTime=System.currentTimeMillis();
            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        }
    }
}
