package model;

import model.pojos.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {
    public static void main(String[] args) {
        PetriNet net = load("/Users/wangqi/Desktop/bpmn/bxml/Idea.pptx.xml");
        System.out.println("pMap:");
        for(String key:net.getpMap().keySet()){
            System.out.print("id:"+net.getpMap().get(key).getId());
            System.out.println("  next:"+net.getpMap().get(key).getNext().size());
        }
    }
    public static PetriNet load(String path){
        Map<String, Place> pMap = new HashMap<String, Place>();
        Map<String, Transition> tMap = new HashMap<String, Transition>();
        PetriNet net = null;
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(path);
            NodeList pList = d.getElementsByTagName("place");
            NodeList tList = d.getElementsByTagName("transition");
            NodeList arcList = d.getElementsByTagName("arc");
            loadPlace(pList,pMap);
            loadTransition(tList,tMap);
            net = buildPetriNet(arcList,pMap,tMap);
            ModelAnalysis.findLoop(net);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return net;
    }
    public static PetriNet buildPetriNet(NodeList pList,Map<String,Place> pMap,Map<String,Transition> tMap){
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            //String aId = node.getAttributes().getNamedItem("id").getNodeValue();
            String sId = node.getAttributes().getNamedItem("source").getNodeValue();
            String tId = node.getAttributes().getNamedItem("target").getNodeValue();
            PNode source = pMap.get(sId);
            if(source==null){
                source = tMap.get(sId);
            }
            PNode target = pMap.get(tId);
            if (target==null){
                target = tMap.get(tId);
            }
            //System.out.println("arc: "+sId+" to "+tId);
            source.getNext().add(target);
            target.setPreId(source.getId());
        }
        PNode startNode = pMap.get("P0");
        PNode endNode = pMap.get("P"+(pMap.size()-1));
        PetriNet net = new PetriNet(startNode,endNode);
        net.setpMap(pMap);
        net.settMap(tMap);
        return net;

    }
    public static void loadTransition(NodeList pList,Map<String,Transition> tMap){
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            String tId = node.getAttributes().getNamedItem("id").getNodeValue();
            //System.out.println("put transition:"+tId);
            tMap.put(tId,new Transition(tId, PNodeType.TRANSITION));
        }
    }

    public static void loadPlace(NodeList pList,Map<String,Place> pMap){
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            String pId = node.getAttributes().getNamedItem("id").getNodeValue();
            pMap.put(pId,new Place(pId, PNodeType.PLACE));
        }

    }

}
