package simulation;

public class TraceGenerate {
    public static String[] generateSerialTrace(int nodeCount){
        String[] trace = new String[nodeCount];
        for(int i=0;i<nodeCount;i++){
            trace[i]  = "T"+i;
        }
        return trace;
    }
}
