package repair;

import Loop.LoopUtils;
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
import simulation.RandomTrace;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RealDataTest {
    private static int RUN_TEST_TIME = 1;
    private static long SLEEP_GAP  = 10;
    public static void main(String[] args) {
        double[] missingOrderRates = {0.2};
        String modelPath = "/Users/wangqi/Desktop/bpmn/test/data/temp2/";
        String logPath = "/Users/wangqi/Desktop/bpmn/test/data/log_data/";
        int minSeqLength = 0;
        int maxSeqLength = 50;
        int seqGap = 5;
        for(double mOrderRate:missingOrderRates){
            for(int i=0;i<maxSeqLength;i=i+seqGap){
                //
                long result = repair(modelPath,logPath,i,i+seqGap,mOrderRate);


                System.out.println(mOrderRate+"-["+i+","+(i+seqGap)+"]-result time: "+result);
            }
        }
    }
    public static long repair(String modelPath,String logPath,int minLength,int maxLength,double rate){
        //检索 所有的模型，然后跑 10次
        long endTime  = 0;
        long startTime = 0;
        long sumTimeCost = 0;
        File file = new File(logPath);
        File[] tempList = file.listFiles();
        int gapSeqCount = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].getName().equals(".DS_Store")) {
                continue;
            }
            //范围
            int seqLength = Integer.parseInt(tempList[i].getName());
            if(seqLength>maxLength || seqLength<minLength){
                continue;
            }
            gapSeqCount++;
            String curLogPath = logPath+seqLength+"/";
            File curLogsFolder = new File(curLogPath);
            File[] seqFileList = curLogsFolder.listFiles();
            String seqString = "";
            long tempSeqTime = 0;
            for(File seqFile:seqFileList){
                //读取序列
                BufferedReader reader = null;
                StringBuffer sbf = new StringBuffer();
                try {
                    reader = new BufferedReader(new FileReader(seqFile));
                    String tempStr;
                    while ((tempStr = reader.readLine()) != null) {
                        sbf.append(tempStr);
                    }
                    reader.close();
                    seqString = sbf.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                //读入模型
                String curModelPath = modelPath+seqFile.getName().substring(0,seqFile.getName().length()-4);


                //
                File testFile = new File(curModelPath);
                if(!testFile.exists()){
                    continue;
                }

                //
                PetriNet net  = ModelLoader.load(curModelPath);
                //循环检测
                ModelAnalysis.findLoop(net);
                //模型transition优先级标注
                ModelPriotity.makePriority(net);
                //获取seq


                String[] trace = seqString.split(",");

                //String[] trace = {"trans_2","trans_4","trans_1","trans_6","trans_0","trans_5"};

                //修复
                Map<String,Integer> traceOrder =  new HashMap<String, Integer>();
                for (int k = 0; k < trace.length; k++) {
                    traceOrder.put(trace[k],k);
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

                //修复


                int count = 0;
                startTime=System.currentTimeMillis();
                for (int j = 0; j < RUN_TEST_TIME; j++) {
                    try{
                        Thread.sleep(SLEEP_GAP);
                    }catch (Exception e){e.printStackTrace();}
                    trace = RandomTrace.random(rate,trace);
                    if(net.getLoops().size()>0){
                        //存在循环
                        ArrayList<String[]> traceList  = LoopUtils.devideBranchSection(net,trace);
                        for(int k=0;k<traceList.size();k++){
                            String[] t =  traceList.get(k);
                            OpenListNode openListNode = TraceDriver.initOpenList(t,modelOrderList,k);
                            openList.add(openListNode);
                        }
                        try{
                            TraceDriver.rebuild(traceList,openList,modelOrderList,modelOrder,trace, traceOrder);
                            count++;
                        }catch (Exception e){
                            //System.out.println("mod:"+curModelPath);
                            //e.printStackTrace();
                        }
                    }else{

                        OpenListNode openListNode = TraceDriver.initOpenList(trace,modelOrderList,-1);
                        openList.add(openListNode);
                        try{
                            TraceDriver.rebuild(null,openList,modelOrderList,modelOrder,trace, traceOrder);
                            count++;
                        }catch (Exception e){
                            //System.out.println("mod:"+curModelPath);
                            //e.printStackTrace();
                        }
                    }
                    if(TraceDriver.min_Context!=null) {
//                        OpenListNode sampleNodeContext = TraceDriver.min_Context;
//                        System.out.println(" ");
//                        System.out.println("_______________________________________________________");
//                        System.out.println("result console cost info:" + sampleNodeContext.getCostF());
//                        CharNode[] sampleNodes = sampleNodeContext.getStrContext();
//                        String reuslt = CharNodeTools.getTraceString(modelOrder, sampleNodes);
//                        TraceDriver.testResult = reuslt;
//                        System.out.println("r:" + TraceDriver.testResult);
                        TraceDriver.clear();
                    }else{
                        TraceDriver.clear();
                        //System.out.println("result null:"+curModelPath);
                    }
                }
                endTime=System.currentTimeMillis();
                if(count!=0){
                    tempSeqTime+=(endTime-startTime-(RUN_TEST_TIME*SLEEP_GAP))/count;
                }
                else{
                    tempSeqTime = 0;
                    System.out.println("this length = 0:"+seqLength+"  "+minLength+"/"+maxLength);
                }




            }
            if(seqFileList.length!=0){
                sumTimeCost += tempSeqTime/seqFileList.length;
                System.out.println(seqLength+"| 一个长度的时间："+(tempSeqTime/seqFileList.length));
                //System.out.println(sumTimeCost+"   "+gapSeqCount);
            }



        }
        if(gapSeqCount==0){
            return 0;
        }
        return sumTimeCost/gapSeqCount;
    }
}
