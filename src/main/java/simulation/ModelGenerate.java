package simulation;

import model.pojos.PNodeType;
import model.pojos.PetriNet;
import model.pojos.Place;
import model.pojos.Transition;

import java.util.HashMap;
import java.util.Map;

public class ModelGenerate {
    public static PetriNet generateSerialModel(int nodeCount){
        Map<String, Place> pMap = new HashMap<String, Place>();
        Map<String, Transition> tMap = new HashMap<String, Transition>();
        Place prePlace = new Place("P0", PNodeType.PLACE);
        Place startPlace = prePlace;
        pMap.put(startPlace.getId(),startPlace);
        for(int i=0;i<nodeCount;i++){
            //cur tran
            Transition t = new Transition("T"+i,PNodeType.TRANSITION);
            tMap.put(t.getId(),t);
            t.setPriority(""+i);
            t.setPreId(prePlace.getId());
            prePlace.getNext().add(t);
            //next
            int nextId = i+1;
            Place nextPlace = new Place("P"+nextId,PNodeType.PLACE);
            pMap.put(nextPlace.getId(),nextPlace);
            t.getNext().add(nextPlace);
            nextPlace.setPreId(t.getId());
            prePlace = nextPlace;


        }
        PetriNet net = new PetriNet(startPlace,prePlace);
        net.settMap(tMap);
        net.setpMap(pMap);;
        return net;
    }
}
