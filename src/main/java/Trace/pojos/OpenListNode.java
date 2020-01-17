package Trace.pojos;

public class OpenListNode {
    //F = G + H
    private int costF;
    private int costG;
    private int costH;
    private boolean[] historyFlag;
    private int originTraceIndex  = -1;
    private CharNode[] strContext;

    public OpenListNode(int strLength,boolean[] his,int originTraceIndex) {
        this.strContext = new CharNode[strLength];
        this.historyFlag = new boolean[his.length];
        this.originTraceIndex = originTraceIndex;
        for(int i=0;i<his.length;i++){
            this.historyFlag[i] = his[i];
        }
    }

    public int getOriginTraceIndex() {
        return originTraceIndex;
    }

    public void setOriginTraceIndex(int originTraceIndex) {
        this.originTraceIndex = originTraceIndex;
    }

    public OpenListNode(int strLength,int index) {
        this.originTraceIndex = index;
        this.strContext = new CharNode[strLength];
    }
    public boolean[] getHis(){
        return historyFlag;
    }
    public void setHistory(boolean[] his){
        this.historyFlag = his;
    }
    public void setHistory(int index,boolean b){
        if(this.historyFlag!=null){
            this.historyFlag[index] = b;
        }
    }
    public CharNode[] getStrContext() {
        return strContext;
    }

    public void setStrContext(CharNode[] strContext) {
        this.strContext = strContext;
    }

//    public Character[] getMissingOrder(String origin){
////        List<Character> charList = new ArrayList<Character>();
////        char[] originChars = origin.toCharArray();
////        int originIndex = 0;
////
////        for(int i=0;i<strContext.length;i++){
////
////            //消除pre
////            //或者没有
////            //mid 元素错位
////            CharNode node = strContext[i];
////            if(node!=null){
////               //pre
////                String preStr = node.getPre();
////                if((preStr!=null)&&(preStr.length()>0)){
////                    while(preStr.length()>0){
////                        //preStr missing order all
////                        if((preStr.indexOf(originChars[originIndex]))==-1){
////                            int maxIndex = -1;
////                            for(char c:preStr.toCharArray()){
////                                charList.add(c);
////                                maxIndex = maxIndex<origin.indexOf(c)?origin.indexOf(c):maxIndex;
////                            }
////                            if(maxIndex<originIndex){
////                                break;
////                            }else{
////                                for(int j=originIndex;j<=maxIndex;j++){
////                                    charList.add(originChars[j]);
////                                }
////                                originIndex = maxIndex+1;
////                                break;
////                            }
////                        }else{
////
////                            preStr = preStr.replaceAll(originChars[originIndex]+"","");
////                            originIndex++;
////                        }
////
////
////                    }
////                }
////                //cur
////                if(originIndex>=origin.length()){
////                    break;
////                }
////                if(node.getCur().length()>0){
////                    char curChar = node.getCur().trim().toCharArray()[0];
////                    if(originChars[originIndex]!=curChar){
////                        //missing order originIndex  -> indexof(cur)
////                        int missingEndIndex = origin.indexOf(curChar);
////                        if(missingEndIndex>originIndex){
////                            for(int j=originIndex;j<=missingEndIndex;j++){
////                                charList.add(originChars[j]);
////                            }
////                            originIndex = missingEndIndex+1;
////                        }
////
////                    }else{
////                        originIndex++;
////                    }
////                }
////
////                // post
////                if(originIndex>=origin.length()){
////                    break;
////                }
////                String postStr =  node.getPost();
////                if((postStr!=null)&&(postStr.length()>0)){
////
////                    while(postStr.length()>0){
////                        if((postStr.indexOf(originChars[originIndex]))==-1){
////                            int missingEndIndex = -1;
////                            char[] postChars =  postStr.toCharArray();
////                            for(char c:postChars)
////                            {
////                                missingEndIndex = missingEndIndex<origin.indexOf(c)?origin.indexOf(c):missingEndIndex;
////                            }
////                            if(missingEndIndex>originIndex){
////                                for(int j=originIndex;j<=missingEndIndex;j++){
////                                    charList.add(originChars[j]);
////                                }
////                                originIndex = missingEndIndex+1;
////                            }
////
////                            break;
////                        }else{
////                            postStr = postStr.replaceAll(originChars[originIndex]+"","");
////                            originIndex++;
////                        }
////                    }
////
////                }
////
////            }
////        }
////        return charList.toArray(new Character[charList.size()]);
////    }

    public void setNode(int index, CharNode node){
        strContext[index] = node;
    }
    public int getCostF() {
        return costG+costH;
    }

    public void setCostF(int costF) {
        this.costF = costF;
    }

    public int getCostG() {
        return costG;
    }

    public void setCostG(int costG) {
        this.costG = costG;
    }

    public int getCostH() {
        return costH;
    }

    public void setCostH(int costH) {
        this.costH = costH;
    }
}
