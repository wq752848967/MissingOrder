package Trace;


import Trace.pojos.CharNode;
import Trace.pojos.OpenList;
import Trace.pojos.OpenListNode;
import Trace.utils.CharNodeTools;
import Trace.utils.CostCalculator;
import Trace.utils.PriorityComparetor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * A * version
 *
 */
public class TraceDriver {

    private static int minCost = Integer.MAX_VALUE;
    private  static OpenListNode min_Context =  null;
    private static final boolean LOG_OUT = true;
    private static String testResult = "";
    public static void main(String[] args) {
        //错位一个

        Map<String,String> modelOrder = new HashMap<String, String>();
        modelOrder.put("a","1");
        modelOrder.put("b","2");
        modelOrder.put("c","3");
        modelOrder.put("d","4");
        modelOrder.put("e","5");
        modelOrder.put("f","6");


        ArrayList<String> modelOrderList1 = new ArrayList<String>();
        modelOrderList1.add("a");
        modelOrderList1.add("b");
        modelOrderList1.add("c");
        modelOrderList1.add("d");
        modelOrderList1.add("e");
        modelOrderList1.add("f");

        ArrayList<String>[] listArray = new ArrayList[1];
        listArray[0] = modelOrderList1;


        String[] traceSection = new String[6];
        traceSection[0] = "b";
        traceSection[1] = "d";
        traceSection[2] = "a";
        traceSection[3] = "c";
        traceSection[4] = "e";
        traceSection[5] = "f";



        Map<String,Integer> traceOrder = new HashMap<String, Integer>();
        for (int i = 0; i < traceSection.length; i++) {
            traceOrder.put(traceSection[i],i);
        }



        rebuild(listArray,modelOrder,traceSection,traceOrder);
        /* test code */
        if(min_Context!=null){
            OpenListNode sampleNodeContext = min_Context;
            System.out.println(" ");
            System.out.println("_______________________________________________________");
            System.out.println("result console cost info:"+sampleNodeContext.getCostF());
            CharNode[] sampleNodes = sampleNodeContext.getStrContext();
            String reuslt = CharNodeTools.getConsoleString(modelOrder,sampleNodes);
            testResult = reuslt;
            System.out.println(testResult);
        }

    }
    public static void rebuild(ArrayList<String>[] modelOrderList,Map<String,String> modelOrder,String traceSection[], Map<String,Integer> traceOrder){

        //init definition
        OpenList openList = new OpenList();

        //init open list
        //初始化openlist ,以trace中的每一个元素作为划分一句初始化
        for(int i=0;i<traceSection.length;i++){
            int costG = 0;
            String referTrans = traceSection[i];
            boolean[] transRecord = new boolean[traceSection.length];

            /**/
            String referPriority = modelOrder.get(referTrans);
            transRecord[i] = true;
            //切分
            OpenListNode openListNode = new OpenListNode(traceSection.length);
            openListNode.setHistory(transRecord);
            CharNode referNode = new CharNode();
            referNode.setCur(referTrans);
            /**/
            referNode.setIndex(traceOrder.get(referTrans));
            for(int j=0;j<traceSection.length;j++){
                if(i!=j){
                    String cmpTrans = traceSection[j];
                    String cmpPriority = modelOrder.get(cmpTrans);

                    int cmpOrder = PriorityComparetor.compare(referPriority,cmpPriority);
                    //cmpOrder<0  refer前  cmpOrder>0  refer  后
                    if(cmpOrder>0){
                        //cmp < refer
                        if(j<i){
                            CharNode tempNode = new CharNode();
                            tempNode.setCur(cmpTrans);
                            /**/
                            tempNode.setIndex(j);
                            openListNode.setNode(j,tempNode);

                        }else{
                            //add costG
                            costG+=(traceOrder.get(cmpTrans)-traceOrder.get(referTrans));
                            referNode.addPre(cmpTrans);
                        }
                    }else if(cmpOrder<0){
                        //cmp>refer
                        if(j>i){
                            CharNode tempNode = new CharNode();
                            tempNode.setCur(cmpTrans);
                            /**/
                            tempNode.setIndex(traceOrder.get(cmpTrans));
                            openListNode.setNode(j,tempNode);

                        }else{
                            //add costG
                            costG+=(traceOrder.get(referTrans)-traceOrder.get(cmpTrans));
                            referNode.addPost(cmpTrans);
                        }
                    }else{
                        //无顺序关系，保持
                        CharNode tempNode = new CharNode();
                        tempNode.setCur(cmpTrans);
                        /**/
                        tempNode.setIndex(traceOrder.get(cmpTrans));
                        openListNode.setNode(j,tempNode);
                    }

                }

            }
            openListNode.setNode(i,referNode);
            //set costG
            openListNode.setCostG(costG);
            //cal future costH
            int costH  = CostCalculator.calcCostH(modelOrderList,openListNode.getStrContext());


            if(costH == 0){
                if(CostCalculator.isFinished(modelOrder,openListNode.getStrContext())){
                    if(minCost>openListNode.getCostF()){
                        minCost = openListNode.getCostF();
                        min_Context = openListNode;
                    }
                }


//                System.out.println("--------------init------------------");
//                for(CharNode cn:openListNode.getStrContext()){
//                    if(cn!=null){
//                        System.out.println("info:"+cn.getIndex()+" "+cn.getPre()+" ["+cn.getCur()+"]  "+cn.getPost());
//                    }
//                }
//                System.out.println(CostCalculator.isFinished(modelOrder,openListNode.getStrContext()));

            }
            openListNode.setCostH(costH);
            openList.add(openListNode);





        }
        System.out.println("init openlist size:"+openList.size());
        //test code
//        OpenListNode sampleNodeContext = openList.getMinCostNode();
//        System.out.println("info:"+sampleNodeContext.getCostF());
//
//        CharNode[] sampleNodes = sampleNodeContext.getStrContext();
//        String reuslt = CharNodeTools.getConsoleString("abcde",sampleNodes);
//        testResult = reuslt;
//
//        System.out.println(testResult);
        //test code end


        //循环主体  取最小 切分，在放入
        while(!openList.IsEmpty()){
            //get mini  node context
            OpenListNode minNodeContext = openList.getMinCostNode();
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
            System.out.print("temp min,cost: "+minNodeContext.getCostG());
            System.out.println(" | trace: "+reuslt);
//            OpenListNode nnn = openList.getMinCostNode();
//            System.out.print("temp min before "+nnn.getCostG());
//            reuslt = CharNodeTools.getConsoleString(modelOrder,nnn.getStrContext());
//            System.out.println(" | trace: "+reuslt);
            /* console code end */






            //get missing char from this node context


            //暂时不做missing 元素的检索
//            Character[] missingChars = minNodeContext.getMissingOrder(sOrigin);
//            /* console code */
//            System.out.print("missing order item:");
//            for(char c:missingChars){
//                System.out.print(" "+c);
//            }
//            System.out.println("");
//            /* console code end */


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
                OpenListNode newOpenListNode = new OpenListNode(minNodeContext.getStrContext().length,oldHis);
                String reTrans = traceSection[m];
                newOpenListNode.setHistory(m,true);


                String reTransPriority= modelOrder.get(reTrans);
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
                            reCharNode = newCharNode;
                            reCharNode.setCur(reTrans);
                            newReCharIndex = cn.getIndex();
                            reNodeIndex = cn.getIndex();
                            //处理原有节点
                            for(String old_pre_c:preStr){
                                if(PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c))>0){
                                    preList.add(old_pre_c);
                                }else if(!old_pre_c.equals(reTrans)){
                                    postList.add(old_pre_c);
                                }else{
                                    preList.add(old_pre_c);
                                }
                            }
                        }else{
                            //不包含
                            for(String old_pre_c:preStr){
                                if(
                                        (hasReChar) &&  ((PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c)))>0)
                                ){
                                    preList.add(old_pre_c);
                                    newCostG += Math.abs(curCnIndex-reNodeIndex);
                                }
                                else if(
                                        (!hasReChar)&&((PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c)))<0)
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
                            if(
                                    (hasReChar) &&  ((PriorityComparetor.compare(reTransPriority,modelOrder.get(curTrans)))>0)
                            ){
                                preList.add(curTrans);
                                newCostG += Math.abs(curCnIndex-reNodeIndex);
                            }
                            else if(
                                    (!hasReChar)&&((PriorityComparetor.compare(reTransPriority,modelOrder.get(curTrans)))<0)
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
                                if(PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c))>0){
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
                                if(
                                        (hasReChar) &&  ((PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c)))>0)
                                ){

                                    preList.add(old_pre_c);
                                    newCostG += Math.abs(curCnIndex-reNodeIndex);
                                }
                                else if(
                                        (!hasReChar)&&((PriorityComparetor.compare(reTransPriority,modelOrder.get(old_pre_c)))<0)
                                ){

                                    postList.add(old_pre_c);
                                    postCostIndex.add(curCnIndex);
                                }else{

                                    newCharNode.addPost(old_pre_c);
                                    if(minIndexChar==null||(PriorityComparetor.compare(modelOrder.get(old_pre_c),modelOrder.get(minIndexChar))<0)){
                                        minIndexChar = old_pre_c;
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



                reCharNode.setPre(preList);
                reCharNode.setPost(postList);
                int n = 0;

                newOpenListNode.setNode(newReCharIndex,reCharNode);
                //重新计算cost
                int newCostH =  CostCalculator.calcCostH(modelOrderList,newOpenListNode.getStrContext());
                newOpenListNode.setCostG(oldCostG+newCostG);
                if(newCostH==0)
                {
                    //分支遍历结束  并未结束，进一步进行判断
                    if(CostCalculator.isFinished(modelOrder,newOpenListNode.getStrContext())){
                        if(minCost>newOpenListNode.getCostF()){
                            minCost = newOpenListNode.getCostF();
                            min_Context = newOpenListNode;
//                            System.out.println("--------------------------------:"+reTrans+"  "+newCostG+"   "+newCostH+"   "+minNodeContext.getCostG() );
//                            for(CharNode cn:newOpenListNode.getStrContext()){
//                                if(cn!=null){
//                                    System.out.println("info:"+cn.getIndex()+" "+cn.getPre()+" ["+cn.getCur()+"]  "+cn.getPost());
//                                }
//                            }
//                            System.out.println(CostCalculator.isFinished(modelOrder,newOpenListNode.getStrContext()));
                        }
                    }else{
                        if(newCostG!=0){
                            newOpenListNode.setCostH(newCostH);
                            //是否存在完成的排序方式
                            openList.add(newOpenListNode);
                        }
                    }


                }else{
                    if(newCostG!=0){
                        newOpenListNode.setCostH(newCostH);
                        //是否存在完成的排序方式
                        openList.add(newOpenListNode);
                    }

                }



            }
//            System.out.print("temp min end ");
//            reuslt = CharNodeTools.getConsoleString(modelOrder,openList.getMinCostNode().getStrContext());
//            System.out.println(" | trace: "+reuslt);




        }

    }

}
