//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;
import java.util.ArrayList;

public class Node {

    protected int DX = 25;
    protected int DY = 25;
    protected String Name;
    protected String ID;
    protected String type;
    protected int indegree = 0;
    protected int outdegree = 0;
    protected int PX;
    protected int PY;
    protected int token = 0;
    private NodePrintType nodePrintType;
    public int missingNum = 0;
    protected ArrayList<Node> predecessors = new ArrayList();
    protected ArrayList<Node> successors = new ArrayList();
    protected boolean petriNet = false;

    public ArrayList<Node> getPredecessors() {
        return this.predecessors;
    }

    public ArrayList<Node> getSuccessors() {
        return this.successors;
    }

    public int getMissingNum() {
        return this.missingNum;
    }

    public void setMissingNum(int missingNum) {
        this.missingNum = missingNum;
    }

    public void initMissingNum() {
        this.missingNum = 0;
    }

    public Node() {
    }

    public Node(String id) {
        this.ID = id;
    }

    public Node(String Name, String type) {
        this.Name = Name;
        this.type = type;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NodePrintType getNodePrintType() {
        return this.nodePrintType;
    }

    public void setNodePrintType(NodePrintType nodePrintType) {
        this.nodePrintType = nodePrintType;
    }

    public int getIndegree() {
        return this.indegree;
    }

    public void setIndegree(int indegree) {
        this.indegree = indegree;
    }

    public int getOutdegree() {
        return this.outdegree;
    }

    public void setOutdegree(int outdegree) {
        this.outdegree = outdegree;
    }

    public int getPX() {
        return this.PX;
    }

    public void setPX(int PX) {
        this.PX = PX;
    }

    public int getPY() {
        return this.PY;
    }

    public void setPY(int PY) {
        this.PY = PY;
    }

    public int getDX() {
        return this.DX;
    }

    public void setDX(int DX) {
        this.DX = DX;
    }

    public int getDY() {
        return this.DY;
    }

    public void setDY(int DY) {
        this.DY = DY;
    }

    public int gettoken() {
        return this.token;
    }

    public void settoken(int i) {
        this.token = i;
    }

    public void multoken() {
        --this.token;
    }

    public void addtoken() {
        ++this.token;
    }

    public boolean isPetriNet() {
        return this.petriNet;
    }

    public void setPetriNet(boolean petriNet) {
        this.petriNet = petriNet;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            Node n = (Node)obj;
            return this.ID.equals(n.ID);
        }
    }

    public boolean isIn(ArrayList<Node> Node) {
        for(int i = 0; i < Node.size(); ++i) {
            if (Node.get(i) == this) {
                return true;
            }
        }

        return false;
    }

    public int hashCode() {
        return this.ID.hashCode();
    }

    public String toString() {
        return this.ID;
    }


}
