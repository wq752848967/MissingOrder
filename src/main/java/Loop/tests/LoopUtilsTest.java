package Loop.tests;

import Loop.LoopUtils;
import model.ModelAnalysis;
import model.ModelLoader;
import model.ModelPriotity;
import model.pojos.PLoop;
import model.pojos.PetriNet;

import java.util.ArrayList;

public class LoopUtilsTest {
    public static void main(String[] args) {
        PetriNet net = ModelLoader.load("/Users/wangqi/Desktop/bpmn/bxml/loop01.xml");
        ModelPriotity.makePriority(net);
        ModelAnalysis.findLoop(net);

        System.out.println("");
        System.out.println("===================");
        String[] trace = {"T0","T4","T2","T1","T6","T8","T1","T3","T5","T6","T7","T1","T3","T5","T6","T8"};
        ArrayList<String[]> results  = LoopUtils.devideBranchSection(net,trace);
        for (String[] t:results){
            System.out.println("trace:");
            for (String s:t){
                System.out.print(s+"  ");
            }
            System.out.println("");
        }
    }
}
