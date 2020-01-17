package simulation;

import java.util.*;

public class RandomTrace {
    public static String[] random(double percentage,String[] trace){
        Set<Integer> randomIndex= new HashSet<Integer>();
        Random r = new Random();
        int randomCount = (int)(trace.length *  percentage);
        int traceLength = trace.length;
        while(randomIndex.size()<randomCount){
            int rIndex = r.nextInt(traceLength);
            if(!randomIndex.contains(rIndex)){
                randomIndex.add(rIndex);
            }
        }
        int removeCount = 0;
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> randomList = new LinkedList<String>();
        for(int i=0;i<traceLength;i++){
            if( randomIndex.contains(i) && removeCount<randomCount){
                removeCount++;
                randomList.add(trace[i]);
            }else{
                list.add(trace[i]);
            }
        }
        while(!randomList.isEmpty()){
            int nextIndex = r.nextInt(list.size());
            String tran =  randomList.poll();
            list.add(nextIndex,tran);
        }
        String[] newTrace  = new String[traceLength];
        int index =  0;
        for(String tran:list){
            newTrace[index] = tran;
            index++;
        }
        return  newTrace;
    }
}
