package model;

import model.pojos.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelAnalysis {
    public static ArrayList<ArrayList<String>> getModelOrderList(PetriNet net){
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Set<String> hisSet = new HashSet<String>();
        ArrayList<String> order = new ArrayList<String>();
        Place startPlace = (Place)net.getStartPlace();
        dfsModelOrder(startPlace,order,result,hisSet,net);
        return result;

    }
    public static void dfsModelOrder(PNode pnode,ArrayList<String> order,ArrayList<ArrayList<String>> result,Set<String> hisSet,PetriNet net){
        if(pnode.getType()==PNodeType.TRANSITION){
            if(hisSet.contains(pnode.getId())){
                //已经添加过
                if(order.size()>1){
                    result.add(order);
                }
                return;
            }else{
                //未添加过
                order.add(pnode.getId());
                hisSet.add(pnode.getId());
                dfsModelOrder(pnode.getNext().get(0),order,result,hisSet,net);
                if(pnode.getNext().size()>1){
                    for(int i=1;i<pnode.getNext().size();i++){
                        dfsModelOrder(pnode.getNext().get(i),new ArrayList<String>(),result,hisSet,net);
                    }
                }
            }
        }else if(pnode.getType()==PNodeType.PLACE){
            if(pnode.getNext().size()==0){
                if(order.size()>1){
                    result.add(order);
                }
                return;
            }else if(pnode.getNext().size()==1){

                dfsModelOrder(pnode.getNext().get(0),order,result,hisSet,net);
            }else{
                int index = 0;
                if(net.getLoopTransition(pnode.getId())!=null){
                    String tId = net.getLoopTransition(pnode.getId());
                    for (int i = 0; i < pnode.getNext().size(); i++) {
                        if(!pnode.getNext().get(i).getId().equals(tId)){
                            index  = i;
                            break;
                        }
                    }

                }
                dfsModelOrder(pnode.getNext().get(index),order,result,hisSet,net);
                 for(int i=0;i<pnode.getNext().size();i++){
                     if(i==index){
                         continue;
                     }
                     dfsModelOrder(pnode.getNext().get(i),new ArrayList<String>(),result,hisSet,net);
                 }

            }
        }

    }
    public static ArrayList<PLoop> findLoop(PetriNet net){
        ArrayList<PLoop> resultLoop = new ArrayList<PLoop>();
        Map<String, Transition> tMap = net.gettMap();
        Set<String> recSet = new HashSet<String>();
        Set<String> repeatHis = new HashSet<String>();
        PNode startPlace = net.getStartPlace();
        dfsLoop(repeatHis,(Place)startPlace,recSet,resultLoop,tMap);
        net.setLoops(resultLoop);
        return resultLoop;
    }
    public static void dfsLoop(Set<String> repeatHis,Place p,Set<String> recSet,ArrayList<PLoop> resultLoop,Map<String, Transition> tMap){
        if(recSet.contains(p.getId())){
            String startPlaceId =  p.getId();
            String endPlaceId = tMap.get(p.getPreId()).getPreId();
            if(!repeatHis.contains(startPlaceId+"@"+endPlaceId)){
                PLoop tempL = new PLoop(startPlaceId,tMap.get(p.getPreId()).getId(),endPlaceId);
                resultLoop.add(tempL);
                repeatHis.add(startPlaceId+"@"+endPlaceId);
            }

            return;
        }else{
            recSet.add(p.getId());
            for(PNode pnode:p.getNext()){
                //transition
                for(PNode item:pnode.getNext()){
                    //place
                    dfsLoop(repeatHis,(Place) item,recSet,resultLoop,tMap);

                }
            }
            recSet.remove(p.getId());

        }

    }

    public static void main(String[] args) {
        PetriNet net = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/loop/loop1-1-9.xml");
//        for(String key:net.gettMap().keySet()){
//            System.out.println(net.gettMap().get(key).getId()+"  "+net.gettMap().get(key).getPreId());
//        }
        ArrayList<PLoop> res = findLoop(net);
        for(PLoop p:res){
            System.out.println(p.getStart()+"  "+p.getEnd()+"  "+p.getTransition());
        }


        //modelOrderList test
//        ArrayList<ArrayList<String>> result = getModelOrderList(net);
//        for(ArrayList<String> r:result){
//            System.out.println(" ");
//            for (int i = 0; i < r.size(); i++) {
//                System.out.print(r.get(i)+" " );
//            }
//            System.out.println("");
//        }

    }
}
