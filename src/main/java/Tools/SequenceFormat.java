package Tools;

import java.io.*;

public class SequenceFormat {
    private static String path = "/Users/wangqi/Desktop/bpmn/test/data/realdata/my_loop_log/";
    public static void main(String[] args) {
        int maxLengthSeq = Integer.MIN_VALUE;
        int minLengthSeq = Integer.MAX_VALUE;
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String seqString = "";
                File seqFile = new File(path+tempList[i].getName());
                BufferedReader reader = null;
                StringBuffer sbf = new StringBuffer();
                try {
                    reader = new BufferedReader(new FileReader(seqFile));
                    String tempStr;
                    while ((tempStr = reader.readLine()) != null) {
                        sbf.append(tempStr);
                    }
                    reader.close();
                    seqString = sbf.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                //tranform seq
                String[] seqArray = seqString.split(",");
                int seqLength = seqArray.length;
                minLengthSeq = Math.min(minLengthSeq,seqLength);
                maxLengthSeq = Math.max(maxLengthSeq,seqLength);
                String newSeq = "";

                for(String tran:seqArray){
                    tran = tran.trim();
                    tran = "trans_"+tran.substring(4,tran.length());
                    newSeq+=tran+",";
                }
                newSeq  = newSeq.substring(0,newSeq.length()-1);
                //System.out.println(tempList[i]+"  "+newSeq);
                //output to file
                String outFolder =  "/Users/wangqi/Desktop/bpmn/test/data/origin_log_data/";
                String outPath = outFolder+tempList[i].getName();
                File outFile = new File(outPath);
                File folderFile = new File(outFolder);
                try {
                    if(!folderFile.exists()){
                        folderFile.mkdir();
                        //System.out.println("mkdir");
                    }
                    outFile.createNewFile();
                    PrintWriter out = new PrintWriter(outPath);

                    out.print(newSeq);
                    out.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        System.out.println("max:"+maxLengthSeq+"  min:"+minLengthSeq);
    }
}
