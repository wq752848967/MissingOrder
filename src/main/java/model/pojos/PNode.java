package model.pojos;

import java.util.ArrayList;

public class PNode {
    protected String Id;
    protected PNodeType type;
    protected String Priority="";
    protected String preId = "";
    protected ArrayList<PNode> next = null;

    public PNode(String id, PNodeType type) {
        Id = id;
        this.type = type;
        this.next = new ArrayList<PNode>();
    }

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public PNodeType getType() {
        return type;
    }

    public void setType(PNodeType type) {
        this.type = type;
    }

    public ArrayList<PNode> getNext() {
        return next;
    }

    public void setNext(ArrayList<PNode> next) {
        this.next = next;
    }
}
