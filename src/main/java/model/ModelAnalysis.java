package model;

import model.pojos.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelAnalysis {
    public static ArrayList<PLoop> findLoop(PetriNet net){
        ArrayList<PLoop> resultLoop = new ArrayList<PLoop>();
        Map<String, Transition> tMap = net.gettMap();
        Set<String> recSet = new HashSet<String>();
        Set<String> repeatHis = new HashSet<String>();
        PNode startPlace = net.getStartPlace();
        dfs(repeatHis,(Place)startPlace,recSet,resultLoop,tMap);
        net.setLoops(resultLoop);
        return resultLoop;
    }
    public static void dfs(Set<String> repeatHis,Place p,Set<String> recSet,ArrayList<PLoop> resultLoop,Map<String, Transition> tMap){
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
                    dfs(repeatHis,(Place) item,recSet,resultLoop,tMap);

                }
            }
            recSet.remove(p.getId());

        }

    }

    public static void main(String[] args) {
        PetriNet net = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/loop01.xml");
//        for(String key:net.gettMap().keySet()){
//            System.out.println(net.gettMap().get(key).getId()+"  "+net.gettMap().get(key).getPreId());
//        }
        ArrayList<PLoop> res = findLoop(net);
        for(PLoop p:res){
            System.out.println(p.getStart()+"  "+p.getEnd());
        }
    }
}
