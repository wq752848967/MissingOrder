package org.nust.heroine;

import model.pojos.PetriNet;
import model.pojos.Place;
import model.pojos.Transition;
import org.nust.heroine.basicstruct.*;
import org.nust.heroine.recovery.Recovery;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import simulation.RandomTrace;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
        //构造petri网



        String path = "/Users/wangqi/Desktop/bpmn/Effa/demo/loop3.xml";

        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        MyPetriNet g = new MyPetriNet();
        ArrayList<Node> nodes = new ArrayList<Node>();
        ArrayList<Arc> arcs = new ArrayList<Arc>();
        Map<String,Node> nMap = new HashMap<String, Node>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(path);
            NodeList pList = d.getElementsByTagName("place");
            NodeList tList = d.getElementsByTagName("transition");
            NodeList arcList = d.getElementsByTagName("arc");
            for(int i=0;i<pList.getLength();i++){
                org.w3c.dom.Node node = pList.item(i);
                String id = node.getAttributes().getNamedItem("id").getNodeValue();
                Node addNode = new Node(id);
                addNode.setType("place");
                nodes.add(addNode);

                nMap.put(id,addNode);
            }
            for(int i=0;i<tList.getLength();i++){
                org.w3c.dom.Node node = tList.item(i);
                String id = node.getAttributes().getNamedItem("id").getNodeValue();

                Node newNode = new Node(id);
                newNode.setType("transition");
                nodes.add(newNode);
                nMap.put(id,newNode);
            }
            for (int i = 0; i <arcList.getLength() ; i++) {
                org.w3c.dom.Node node = arcList.item(i);
                //String aId = node.getAttributes().getNamedItem("id").getNodeValue();
                String sId = node.getAttributes().getNamedItem("source").getNodeValue();
                String tId = node.getAttributes().getNamedItem("target").getNodeValue();
                Arc a = new Arc(nMap.get(sId),nMap.get(tId));
                arcs.add(a);
            }
            g.setEdges(arcs);
            g.setNodes(nodes);
            g.setId(0);
            g.computePetri();
            g.initTransMap();
            System.out.println("1");
            String[] tr={"T0","T1","T2","T4","T6","T8","T10","T12","T1","T3","T5","T7","T9","T10"
                    ,"T11"};

            int errCount = 0;
            for(int i=0;i<1;i++){
                //Thread.sleep(3000);
                tr = RandomTrace.random(0.1,tr);
                ArrayList<String> events  = new ArrayList<String>();
                for(String s:tr){
                    events.add(s);
                }
                Trace trace = new Trace();
                trace.setEvents(events);
                ArrayList<Trace> traceSet = new ArrayList<Trace>();
                traceSet.add(trace);

                Recovery.petriNet  = g;
                Trace result = Recovery.recovery(traceSet).get(0);
                if(result.getEvents().size()!=tr.length){
                    System.out.println(result.getEvents().size());
                    System.out.println(tr.length);
                    errCount++;
                }
                System.out.println("result:"+result.toString());
            }
            System.out.println("errCount:"+errCount);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
