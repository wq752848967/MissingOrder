package Tools;

import simulation.RandomTrace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class StringToXes {
    public static void main(String[] args) {
        String[] trace={"trans_0","trans_1","trans_3","trans_4","trans_5"
                ,"trans_2","trans_6","trans_7","trans_8","trans_9"};
        String fileName="parall.xes";
        trace = RandomTrace.random(0.0,trace);

        for(String s:trace){
            System.out.print(s+ " ");
        }
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<Log>\n" +
                "  <trace id=\"0\">\n";
        String demo = "<event>\n" +
                "      <String key=\"org:resource\" value=\"UNDEFINED\"/>\n" +
                "      <date key=\"time:timestamp\" value=\"2008-12-09T08:21:01.527+01:00\"/>\n" +
                "      <String key=\"concept:name\" value=\"@name\"/>\n" +
                "      <String key=\"lifecycle:transition\" value=\"receive\"/>\n" +
                "      <String key=\"input\" value=\"null\"/>\n" +
                "      <String key=\"output\" value=\"null\"/>\n" +
                "    </event>\n";
        for(String s : trace){
            String event = demo.replace("@name",s);
            content+=event;
        }
        content+="  </trace>\n" +
                "  \n" +
                " \n" +
                "</Log>\n";
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
}
