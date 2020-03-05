package repair;

import Loop.LoopUtils;
import Trace.OnePassDriver;
import Trace.TraceDriver;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
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

public class Test_compare_Deeky {
    public static void main(String[] args) {
        //解析模型
        int nodeCount =40;
        double per = 0.8;
        int onepassCount =5;
        long startTime=System.currentTimeMillis();
        int unCorrectCount = 0;
        for(int j=0;j<10;j++){
            try {
                Thread.sleep(1000);
            }catch (Exception e){}
            PetriNet net = ModelGenerate.generateSerialModel(nodeCount);

            //trace            0     1     2    3   4     5    6    7    8
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
                try{
                    TraceDriver.rebuild(null,openList,modelOrderList,modelOrder,trace, traceOrder);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("A* end");
                openListNode = TraceDriver.initOpenList(trace,modelOrderList,-1);
                openList.add(openListNode);
                OnePassDriver.repair(onepassCount,null,openList,modelOrderList,modelOrder,trace, traceOrder);

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
            if(costA!=costD){
                System.out.println(costA+"/"+costD);
                unCorrectCount++;
            }


        }
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        System.out.println("count:"+unCorrectCount);
    }
}
