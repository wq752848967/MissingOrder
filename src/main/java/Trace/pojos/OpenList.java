package Trace.pojos;

import java.util.Comparator;
import java.util.TreeSet;

public class OpenList {
    public OpenList(){
        openlist = new TreeSet<OpenListNode>(new Comparator<OpenListNode>() {
            public int compare(OpenListNode o1, OpenListNode o2) {
                if (o1.getCostF() != o2.getCostF()) {
                    return o1.getCostF() - o2.getCostF();
                } else {
                    CharNode[] ctx1 = o1.getStrContext();
                    CharNode[] ctx2 = o2.getStrContext();
                    for (int i = 0; i < o1.getStrContext().length; i++) {
                        if (ctx1[i] != null && ctx2[i] != null) {
                            if (!ctx1[i].getPre().equals(ctx2[i].getPre())) {
                                return -1;
                            }
                            if (!ctx1[i].getCur().equals(ctx2[i].getCur())) {
                                return -1;
                            }
                            if (!ctx1[i].getPost().equals(ctx2[i].getPost())) {
                                return -1;
                            }

                        } else if (ctx1[i] == null && ctx2[i] == null) {
                            continue;
                        } else {
                            return -1;
                        }
                    }
                }
                return 0;
            }

        });
    }

    private TreeSet<OpenListNode> openlist = null;

    public int size(){
        return openlist.size();
    }
    public void add(OpenListNode node){
        openlist.add(node);
    }
    public boolean IsEmpty(){
        return openlist.isEmpty();
    }
    public void removeNode(OpenListNode node){
         openlist.remove(node);
    }
    public OpenListNode getMinCostNode(){
        OpenListNode node =  openlist.first();
        openlist.remove(node);
        return node;
    }
}
