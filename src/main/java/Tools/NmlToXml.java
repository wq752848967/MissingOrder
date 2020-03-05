package Tools;

import model.ModelAnalysis;
import model.pojos.PNodeType;
import model.pojos.PetriNet;
import model.pojos.Place;
import model.pojos.Transition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NmlToXml {
    public static void main(String[] args) {
        String path = "/Users/wangqi/Desktop/bpmn/Effa/demo/parall.xml";
        String fileName = "loop"+System. currentTimeMillis()+".xml";
        fileName = "parall.pnml";
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<pnml>\n" +
                "  <net id=\"net1\" type=\"http://www.pnml.org/version-2009/grammar/pnmlcoremodel\">\n" +
                "    <name>\n" +
                "      <text>@name</text>\n" +
                "    </name>\n" +
                "    <page id=\"n0\">\n";
        content += load(path);
        content+="</page>\n" +
                "  </net>\n" +
                "</pnml>";

        String outPath = "/Users/wangqi/Desktop/bpmn/Effa/demo/"+fileName;
        File outFile = new File(outPath);

        try {

            outFile.createNewFile();
            PrintWriter out = new PrintWriter(outPath);

            out.print(content);
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }



    }
    public static String load(String path){
        Map<String, Place> pMap = new HashMap<String, Place>();
        Map<String, Transition> tMap = new HashMap<String, Transition>();
        PetriNet net = null;
        StringBuilder resultBuilder = new StringBuilder();
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(path);
            NodeList pList = d.getElementsByTagName("place");
            NodeList tList = d.getElementsByTagName("transition");
            NodeList arcList = d.getElementsByTagName("arc");
            String endId = "11";
            resultBuilder.append(transPlace(pList,endId));
            resultBuilder.append(transTran(tList));
            resultBuilder.append(transArc(arcList,endId));



        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBuilder.toString();
    }
    public static String transArc(NodeList pList,String endId){
        String result = "";
        String pstart = "P0";
        String demo = "<arc id=\"@id\" source=\"@source\" target=\"@target\">\n" +
                "        <name>\n" +
                "          <text>1</text>\n" +
                "        </name>\n" +
                "        <arctype>\n" +
                "          <text>normal</text>\n" +
                "        </arctype>\n" +
                "      </arc>\n";
        String pend = "P"+endId;
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            //String aId = node.getAttributes().getNamedItem("id").getNodeValue();
            String sId = node.getAttributes().getNamedItem("source").getNodeValue();
            String tId = node.getAttributes().getNamedItem("target").getNodeValue();
            if(sId.equals(pstart)){
                sId = "pstart";
            }else{
                String preStr = "place_";
                if(sId.contains("T")){
                    preStr = "trans_";
                }
                sId = preStr+sId.substring(1);
            }
            if(tId.equals(pend)){
                tId = "pend";
            }else{
                String preStr = "place_";
                if(tId.contains("T")){
                    preStr = "trans_";
                }
                tId = preStr+tId.substring(1);
            }
            String arc = demo.replace("@id","arc_"+i);
            arc = arc.replace("@source",sId);
            arc = arc.replace("@target",tId);
            result+=arc;
        }
        return result;
    }
    public static String transTran(NodeList pList){
        String result = "";
        String demo = "<transition id=\"@id\">\n" +
                "        <name>\n" +
                "          <text>@id</text>\n" +
                "        </name>\n" +
                "        <graphics>\n" +
                "          <position x=\"440.0\" y=\"150.0\"/>\n" +
                "          <dimension x=\"25\" y=\"25\"/>\n" +
                "        </graphics>\n" +
                "        <fill color=\"#FFFFFF\"/>\n" +
                "      </transition>\n";
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            String pId = node.getAttributes().getNamedItem("id").getNodeValue();

            pId = "trans_"+pId.substring(1,pId.length());

            String splace = demo.replace("@id",pId);
            result+=splace;
        }
        return result;
    }

    public static String transPlace(NodeList pList,String endId){

        String result = "";
        String demo = "<place id=\"@id\">\n" +
                "        <name>\n" +
                "          <text>@id</text>\n" +
                "        </name>\n" +
                "        <graphics>\n" +
                "          <position x=\"120.0\" y=\"270.0\"/>\n" +
                "          <dimension x=\"25\" y=\"25\"/>\n" +
                "        </graphics>\n" +
                "        <initialMarking>\n" +
                "          <text>1</text>\n" +
                "        </initialMarking>\n" +
                "      </place>\n";
        for (int i = 0; i <pList.getLength() ; i++) {
            Node node = pList.item(i);
            String pId = node.getAttributes().getNamedItem("id").getNodeValue();
            if(pId.equals("P0")){
                //pstart
                pId = "pstart";
            }else if(pId.equals("P"+endId)){
                //pend
                pId = "pend";
            }else{
                pId = "place_"+pId.substring(1,pId.length());
            }
            String splace = demo.replace("@id",pId);
            result+=splace;
        }
        return result;

    }
}
