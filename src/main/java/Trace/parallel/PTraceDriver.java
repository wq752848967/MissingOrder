package Trace.parallel;

import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import Trace.utils.CostCalculator;
import Trace.utils.PriorityComparetor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class PTraceDriver {
    public static int minCost = Integer.MAX_VALUE;
    public  static OpenListNode min_Context =  null;
    private static final boolean LOG_OUT = false;
    public static String testResult = "";
    private static OpenList p_openList = null;
    private static int treadCount = 5;
    private static int count = 0;
    private static ReentrantLock lock = new ReentrantLock();
    public static OpenListNode getListNode(){
        lock.lock();
        OpenListNode minNodeContext = null;
        while (!p_openList.IsEmpty()){
            try{
                minNodeContext = p_openList.getMinCostNode();
            }catch (Exception e){

            }
            p_openList.removeNode(minNodeContext);
            if(minNodeContext.getCostF()>=minCost){
                continue;
            }else{
                break;
            }
        }

        lock.unlock();
        return minNodeContext;
    }
    public static void addListNode(OpenListNode listNode){
        lock.lock();
        p_openList.add(listNode);
        lock.unlock();
    }
    public static void setMinInfo(int cost,OpenListNode node){
        lock.lock();
        minCost = cost;
        min_Context = node;
        lock.unlock();
    }
    public static void addResult(){
        lock.lock();
        count++;
        lock.unlock();
    }
    public static void rebuild(ArrayList<String[]> traceList, OpenList openList,
                               ArrayList<ArrayList<String>> modelOrderList, Map<String,String> modelOrder,
                               String traceSection[], Map<String,Integer> traceOrder)throws Exception{






        long startTime = System.currentTimeMillis();
        //并行
        p_openList = openList;
        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        for (int i = 0; i < treadCount; i++) {
            executor.submit(new DriverTask(traceSection,modelOrder,traceList,modelOrderList));
        }
        while(true) {
            if (count==treadCount) {
                long end = System.currentTimeMillis();
                System.out.println("3个线程执行用时: " + (end - startTime) + "ms");
                executor.shutdown();
                break;
            }
            Thread.sleep(1000);

        }





    }
}
