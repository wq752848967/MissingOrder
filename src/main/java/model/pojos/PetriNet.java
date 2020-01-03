package model.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PetriNet {
    private PNode startPlace = null;
    private PNode endPlace = null;
    Map<String, Place> pMap = new HashMap<String, Place>();
    Map<String, Transition> tMap = new HashMap<String, Transition>();
    ArrayList<PLoop> loops = new ArrayList<PLoop>();
    public PetriNet(PNode startPlace, PNode endPlace) {
        this.startPlace = startPlace;
        this.endPlace = endPlace;
    }

    public String getLoopTransition(String endPlaceId){
        for(PLoop pl:loops){
            if(pl.getEnd().equals(endPlaceId)){
                return pl.getTransition();
            }
        }
        return "";
    }
    public boolean findLoopEnd(String id){
        for(PLoop pl:loops){
            if(pl.getEnd().equals(id)){
                return true;
            }
        }
        return false;
    }
    public ArrayList<PLoop> getLoops() {
        return loops;
    }

    public void setLoops(ArrayList<PLoop> loops) {
        this.loops = loops;
    }

    public PNode getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(PNode startPlace) {
        this.startPlace = startPlace;
    }

    public PNode getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(PNode endPlace) {
        this.endPlace = endPlace;
    }

    public Map<String, Place> getpMap() {
        return pMap;
    }

    public void setpMap(Map<String, Place> pMap) {
        this.pMap = pMap;
    }

    public Map<String, Transition> gettMap() {
        return tMap;
    }

    public void settMap(Map<String, Transition> tMap) {
        this.tMap = tMap;
    }
}
