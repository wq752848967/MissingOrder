//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Graph implements Serializable, Cloneable {
    private static final long serialVersionUID = 6712637811832637633L;
    protected int id;
    protected ArrayList<Node> nodes = new ArrayList();
    protected ArrayList<Arc> arcs = new ArrayList();

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Graph() {
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Arc> getArcs() {
        return this.arcs;
    }

    public void setEdges(ArrayList<Arc> arcs) {
        this.arcs = arcs;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void deleteNode(Node node) {
        this.nodes.remove(node);
    }

    public void addArc(Arc arc) {
        this.arcs.add(arc);
    }

    public void addArc(Node n1, Node n2) {
        Arc a = new Arc();
        a.setSource(n1);
        a.setTarget(n2);
        this.arcs.add(a);
    }

    public void deleteArc(Arc arc) {
        this.arcs.remove(arc);
    }

    public void deleteArc(Node n1, Node n2) {
        this.deleteArc(new Arc(n1, n2));
    }

    public Node getNodeById(String id) {
        Iterator var3 = this.nodes.iterator();

        while(var3.hasNext()) {
            Node node = (Node)var3.next();
            //System.out.println(id+"  "+node.getID());
            if (node.getID().equalsIgnoreCase(id)) {
                return node;
            }
        }

        return null;
    }

    public Arc getArcByST(String source, String target) {
        Iterator var4 = this.arcs.iterator();

        Arc arc;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            arc = (Arc)var4.next();
        } while(!arc.getSource().getID().equalsIgnoreCase(source) || !arc.getTarget().getID().equalsIgnoreCase(target));

        return arc;
    }

    public String toString() {
        return this.nodes.toString() + "|" + this.arcs.toString();
    }



    public void findIndegree() {
        Iterator var2 = this.getNodes().iterator();

        while(var2.hasNext()) {
            Node node = (Node)var2.next();
            node.setIndegree(0);
        }

        var2 = this.getArcs().iterator();

        while(var2.hasNext()) {
            Arc arc = (Arc)var2.next();
            Node target = arc.getTarget();
            target.setIndegree(target.getIndegree() + 1);
        }

    }

    public boolean[][] getAdjacentMatrix() {
        boolean[][] adjacentMatrix = new boolean[this.getNodes().size()][this.getNodes().size()];

        for(int i = 0; i < this.getNodes().size(); ++i) {
            for(int j = 0; j < this.getNodes().size(); ++j) {
                if (i == j) {
                    adjacentMatrix[i][j] = false;
                } else {
                    Iterator var5 = this.getArcs().iterator();

                    while(var5.hasNext()) {
                        Arc arc = (Arc)var5.next();
                        if (arc.getSource().getID().equalsIgnoreCase(((Node)this.getNodes().get(i)).getID()) && arc.getTarget().getID().equalsIgnoreCase(((Node)this.getNodes().get(j)).getID())) {
                            adjacentMatrix[i][j] = true;
                        }
                    }
                }
            }
        }

        return adjacentMatrix;
    }
}
