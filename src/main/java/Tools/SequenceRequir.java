package Tools;

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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SequenceRequir {
    private static String modelFoldpath = "/Users/wangqi/Desktop/bpmn/test/data/add/";
    private static String seqFoldpath = "/Users/wangqi/Desktop/bpmn/test/data/add_log/";
    public static void main(String[] args) {
        //获取模型
        File file = new File(modelFoldpath);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if(tempList[i].getName().equals(".DS_Store")){
                continue;
            }
            String modelPath = modelFoldpath+tempList[i].getName();
            System.out.println("begin:"+modelPath);
            PetriNet net  = ModelLoader.load(modelPath);
            //循环检测
            ModelAnalysis.findLoop(net);
            //模型transition优先级标注
            ModelPriotity.makePriority(net);
            //获取seq
            long startTime=System.currentTimeMillis();
            String seqPath  = seqFoldpath+tempList[i].getName()+".seq";
            String seqString = "";
            File seqFile = new File(seqPath);
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

            String[] trace = seqString.split(",");

            //String[] trace = {"trans_2","trans_4","trans_1","trans_6","trans_0","trans_5"};
            int traceLength = trace.length;
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{

                OpenListNode openListNode = TraceDriver.initOpenList(trace,modelOrderList,-1);
                openList.add(openListNode);
                try{
                    TraceDriver.rebuild(null,openList,modelOrderList,modelOrder,trace, traceOrder);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


            if(TraceDriver.min_Context!=null) {
                OpenListNode sampleNodeContext = TraceDriver.min_Context;
                System.out.println(" ");
                System.out.println("_______________________________________________________");
                System.out.println("result console cost info:" + sampleNodeContext.getCostF());
                CharNode[] sampleNodes = sampleNodeContext.getStrContext();
                String reuslt = CharNodeTools.getTraceString(modelOrder, sampleNodes);
                TraceDriver.testResult = reuslt;
                System.out.println("r:" + TraceDriver.testResult);
                TraceDriver.clear();
                //output
//                String outFolder =  "/Users/wangqi/Desktop/bpmn/test/data/log_data/"+traceLength+"/";
//                String outPath = outFolder+tempList[i].getName()+".seq";
//                File outFile = new File(outPath);
//                File folderFile = new File(outFolder);
//                try {
//                    if(!folderFile.exists()){
//                        folderFile.mkdir();
//                        //System.out.println("mkdir");
//                    }
//                    outFile.createNewFile();
//                    PrintWriter out = new PrintWriter(outPath);
//
//                    out.print(reuslt);
//                    out.flush();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
            }else{
                System.out.println("result null");
            }

        }

    }
}
