package Loop;

import Loop.utils.PermutationUtil;
import model.pojos.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class LoopUtils {
    public static ArrayList<String[]> devideBranchSection(PetriNet net,String[] trace){
        /*
        * 循环开始遍历，非分支，直接添加
        * 分支：
        * 1.直至结束 无其他分支  基础案例
        * 2.某几个分支里面嵌套了分支 增强案例
        * 计划，先实现基础案例
        * */
        ArrayList<String[]> results = new ArrayList<String[]>();

        //1.截取分支部分
        for(PLoop loop:net.getLoops()){
            //a.find branch start
            LinkedList<PNode> queue = new LinkedList<PNode>();
            String startPlaceId = loop.getStart();
            String endPlaceId = loop.getEnd();
            Place startplace = net.getpMap().get(startPlaceId);
            boolean first = true;
            //获取当前循环次数
            int loopCount = 0;
            String loopTransId = loop.getTransition();
            for(String itemId:trace){
                if(itemId.equals(loopTransId)){
                    loopCount++;
                }
            }


            while(queue.size()>0||first){
                first = false;
                while((startplace==null)||(!startplace.getId().equals(endPlaceId))){
                    if(startplace==null&&queue.size()==0){
                        break;
                    }
                    while(startplace==null&& queue.size()>0){
                        startplace = (Place)queue.poll();
                    }
                    if(startplace==null){
                        continue;
                    }
                    if(startplace.getNext()!=null&&startplace.getNext().size()>1){
                        break;
                    }else{
                        if(startplace.getNext()!=null){
                            Transition t = (Transition)startplace.getNext().get(0);
                            for(PNode pnode:t.getNext()){
                                if(!pnode.getId().equals(endPlaceId)){
                                    queue.offer(pnode);
                                }
                                startplace = null;
                            }
                        }
                    }
                }
                if(startplace==null){
                    //无循环直接跳出
                    results.add(trace);
                    break;
                }
                //System.out.println("branch start:"+startplace.getId()+"  "+startplace.getNext().size());
                //startPlace is the branch entrance
                ArrayList<String[]> branchs = new ArrayList<String[]>();
                if(startplace!=null){
                    Place startBranchPlace =  startplace;
                    String endBranchId = findBranchEnd(startBranchPlace);
                    //System.out.println("end Branch:"+endBranchId);
                    for(PNode pnode:startBranchPlace.getNext()){
                        String[] tranSet = getAllTransition((Transition)pnode,endBranchId);
                        branchs.add(tranSet);
                    }

                }
                //需要对branch进行扩充
                ArrayList<String[]> addBranch = new ArrayList<String[]>();
                for(String[] branch:branchs){
                    int count = 0;
                    for(String t:trace){
                        if(t.equals(branch[0])){
                            count++;
                        }
                    }
                    for (int i = 0; i < count-1; i++) {
                        String[] newBranch = new String[branch.length];
                        System.arraycopy(branch,0,newBranch,0,branch.length);
                        addBranch.add(newBranch);
                    }

                }
                //分支 删减
                for(int i=0;i<branchs.size();i++){
                    String[] branch = branchs.get(i);
                    int count = 0;
                    for(String t:trace){
                        if(t.equals(branch[0])){
                            count++;
                        }
                    }
                    if(count == 0){
                        branchs.remove(i);
                    }


                }
                for(String[] branch:addBranch){
                    branchs.add(branch);
                }
//                for (String[] b:branchs){
//                    System.out.println("branch:");
//                    for (String s:b){
//                        System.out.print(s+" ");
//                    }
//                    System.out.println("");
//                }


                //2.归属问题
                ArrayList<String> permuList = PermutationUtil.getAllPermutation(loopCount+1);


                //results 已有
                ArrayList<String[]> tempResult = new ArrayList<String[]>();
                if(results.size()==0){
                    results.add(trace);
                }
                for (String[] traceItem:results){

                    for (String permu:permuList) {
                        //loop 的一种分配归属
                        String[] perIndex = permu.substring(1,permu.length()).split("@");
                        String[] newTrace = new String[traceItem.length];
                        System.arraycopy(traceItem,0,newTrace,0,trace.length);
                        for (int i = 0; i < perIndex.length; i++) {
                            //每一次循环
                            String[] branchTrans =  branchs.get(i);
                            String branchString = "";
                            for(String t:branchTrans){
                                branchString+="@"+t;
                            }
                            //System.out.println("branch marking:"+branchString+"  "+i);

                            for (int j = 0; j < trace.length; j++) {
                                String item = newTrace[j];
                                if(branchString.contains(item)){
                                    branchString = branchString.replace(item,"");
                                    newTrace[j] = newTrace[j]+"-"+(perIndex[i]);
                                }
                            }

                        }
                        tempResult.add(newTrace);

                    }
                    results = tempResult;

                }

            }
            System.out.println("end while");
            //给非循环元素打标
            //loop start / end
            markLoop(results,loop,loopCount+1,net.getpMap().get(loop.getStart()));


        }
        return results;
    }
    private static void markLoop(ArrayList<String[]> results,PLoop pLoop,int loopCount,Place startPlace){

        //获取当前循环所有元素
        String loopAllTransition = "";
        LinkedList<PNode> queue = new LinkedList<PNode>();
        queue.offer(startPlace);
        PNode temp = null;
        while(queue.size()>0){

            temp = queue.poll();
            if(temp.getType()==PNodeType.TRANSITION){
                if(!loopAllTransition.contains(temp.getId())){
                    loopAllTransition+="@"+temp.getId();
                }
            }
            else{
                if(temp.getId().equals(pLoop.getEnd())){
                    continue;
                }
            }
            for(PNode pn:temp.getNext()){
                if(!pn.getId().equals(pLoop.getEnd())){
                     queue.offer(pn);
                }
            }
        }


        for(String[] tempTrace:results){
            for (int i = 0; i < loopCount; i++) {
                //System.out.println("loopAllTransition:"+loopAllTransition+"  "+i);
                boolean loopTranFlag = true;
                String referString = new String(loopAllTransition);
                for(int j=0;j<tempTrace.length;j++){
                    //item = transition
                    String item = tempTrace[j];
                    if(item.contains("-")){
                        continue;
                    }
                    if(referString.contains(item)){
                        tempTrace[j]+="-"+i;
                        referString = referString.replace(item,"");
                    }
                    if(loopTranFlag && item.equals(pLoop.getTransition())){
                        tempTrace[j]+="-"+i;
                        loopTranFlag = false;
                    }
                }
            }
        }

    }

    private static String[] getAllTransition(Transition pnode,String endBranchId){
        //
        ArrayList<String> res = new ArrayList<String>();
        getTransition(pnode,endBranchId,res);
        return res.toArray(new String[res.size()]);
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
    private static String findBranchEnd(Place startBranchPlace){

       String startPriority = startBranchPlace.getNext().get(0).getPriority();
       PNode endTransition = startBranchPlace.getNext().get(0).getNext().get(0).getNext().get(0);
       //System.out.println("startPriority:"+startPriority+"  "+endTransition.getPriority()+  "  "+endTransition.getId());
       while(startPriority.length()<=endTransition.getPriority().length()){
            endTransition = endTransition.getNext().get(0).getNext().get(0);
       }
       return endTransition.getPreId();
    }
    public static void main(String[] args) {

    }
}
