//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;

import de.normalisiert.utils.graphs.ElementaryCyclesSearch;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MyPetriNet extends Graph implements Serializable, Cloneable {
    private static final long serialVersionUID = 3201629470197559118L;
    public Node sourcePlace;
    public Node sinkPlace;
    public ArrayList<Node> places;
    public ArrayList<Node> transitions;
    public ArrayList<Node> backs;
    public ArrayList<Node> exits;
    public ArrayList<List<Node>> f1;
    public ArrayList<List<Node>> f2;
    private HashMap<String, Node> transMap = new HashMap();
    public boolean hasCycle = false;
    public boolean isHasIntersection = false;

    public ArrayList<Node> getBack_transitions() {
        return this.backs;
    }

    public MyPetriNet() {
        this.initPetri();
    }

    public void initPetri() {
        this.sourcePlace = new Node();
        this.sinkPlace = new Node();
        this.transitions = new ArrayList();
        this.places = new ArrayList();
        this.backs = new ArrayList();
        this.exits = new ArrayList();
        this.f1 = new ArrayList();
        this.f2 = new ArrayList();
    }

    public ArrayList<Node> getPlaces() {
        return this.places;
    }

    public ArrayList<Node> getTransitons() {
        return this.transitions;
    }

    public boolean isChoiceNode(Node n) {
        if (n.getType().equals("transition")) {
            return false;
        } else {
            int j = 0;

            for(int i = 0; i < this.arcs.size(); ++i) {
                if (((Arc)this.arcs.get(i)).getSource() == n) {
                    ++j;
                }
            }

            if (j > 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void initTransMap() {
        Iterator var2 = this.getTransitons().iterator();

        while(var2.hasNext()) {
            Node node = (Node)var2.next();
            this.transMap.put(node.getID(), node);
        }

    }

    public HashMap<String, Node> getTransMap() {
        return this.transMap;
    }

    public boolean isChoicePlace(Place n) {
        int j = 0;

        for(int i = 0; i < this.arcs.size(); ++i) {
            if (((Arc)this.arcs.get(i)).getSource() == n) {
                ++j;
            }
        }

        if (j > 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean HasChoiceNode() {
        for(int i = 0; i < this.nodes.size(); ++i) {
            if (this.isChoiceNode((Node)this.nodes.get(i))) {
                return true;
            }
        }

        return false;
    }

    public boolean enabled(Node n) {
        Iterator var3 = n.getPredecessors().iterator();

        while(var3.hasNext()) {
            Node node = (Node)var3.next();
            if (node.gettoken() <= 0) {
                return false;
            }
        }

        return true;
    }

    public void fire(Node n) {
        Iterator var3 = n.getPredecessors().iterator();

        Node node;
        while(var3.hasNext()) {
            node = (Node)var3.next();
            node.multoken();
        }

        var3 = n.getSuccessors().iterator();

        while(var3.hasNext()) {
            node = (Node)var3.next();
            node.addtoken();
        }

    }

    public void initToken() {
        Iterator var2 = this.getPlaces().iterator();

        while(var2.hasNext()) {
            Node node = (Node)var2.next();
            if (node.equals(this.getSourcePlace())) {
                node.settoken(1);
            } else {
                node.settoken(0);
            }
        }

    }

    public void showDegree() {
        Iterator var2 = this.nodes.iterator();

        while(var2.hasNext()) {
            Node node = (Node)var2.next();
            System.out.println(node + "\t" + "���" + node.getIndegree() + "����" + node.getOutdegree());
        }

    }

    public Node getSourcePlace() {
        return this.sourcePlace;
    }

    public Node getSinkPlace() {
        return this.sinkPlace;
    }

    public void computePetri() {
        this.initPetri();
        Iterator var2 = this.getNodes().iterator();

        Node node;
        while(var2.hasNext()) {
            node = (Node)var2.next();
            if (node != null) {
                node.setIndegree(0);
                node.setOutdegree(0);
                if (node.getType().equalsIgnoreCase("place")) {
                    this.places.add(node);
                } else {
                    this.transitions.add(node);
                }

                node.predecessors = new ArrayList();
                node.successors = new ArrayList();
            }
        }

        var2 = this.getArcs().iterator();

        while(var2.hasNext()) {
            Arc arc = (Arc)var2.next();
            Node source = this.getNodeById(arc.getSource().getID());
            Node target = this.getNodeById(arc.getTarget().getID());
            source.setOutdegree(source.getOutdegree() + 1);
            target.setIndegree(target.getIndegree() + 1);
            source.getSuccessors().add(target);
            target.getPredecessors().add(source);
        }

        var2 = this.getPlaces().iterator();

        while(var2.hasNext()) {
            node = (Node)var2.next();
            if (node.getIndegree() == 0) {
                this.sourcePlace = node;
            }

            if (node.getOutdegree() == 0) {
                this.sinkPlace = node;
            }
        }

        List<MyPetriNet> test = this.getAllCycle();
        if (test.size() > 0) {
            this.hasCycle = true;
            //System.out.println("cyc is trues:"+test.size());
            //System.out.println(test.toString());
        }

        List<List<Node>> circuits = new ArrayList();
        Iterator var17 = test.iterator();

        while(var17.hasNext()) {
            MyPetriNet p = (MyPetriNet)var17.next();
            //System.out.println(p.getNodes().toString());
            List<Node> circuit = new ArrayList();

            for(int i = 0; i < p.getNodes().size(); ++i) {
                Node n = (Node)p.getNodes().get(i);
                if (n.getOutdegree() > 1 && n.getType().equals("place")) {

                    circuit.clear();
                    circuit.addAll(p.getNodes().subList(i, p.getNodes().size()));
                    circuit.addAll(p.getNodes().subList(0, i));
                }
            }

            circuits.add(circuit);
        }

        var17 = circuits.iterator();

        while(true) {
            while(var17.hasNext()) {
                List<Node> circuit = (List)var17.next();
                System.out.println(circuit.toString());
                Node loop_start = (Node)circuit.get(0);
                Node loop_end = null;

                for(int i = 1; i < circuit.size(); ++i) {
                    Node temp = (Node)circuit.get(i);
                    if (temp.getIndegree() > 1 && temp.getType().equalsIgnoreCase("place")) {
                        loop_end = temp;
                        break;
                    }
                }

                //List<Node> f2s = circuit.subList(1,circuit.indexOf(loop_end) );
                List<Node> f2s = new ArrayList<Node>();
                for(int uu=1;uu<circuit.indexOf(loop_end);uu++){
                    f2s.add(circuit.get(uu));
                }
                List<Node> f1s = circuit.subList(circuit.indexOf(loop_end) + 1, circuit.size());
                int index;
                if ((index = this.f2.indexOf(f2s)) != -1) {
                    ((List)this.f1.get(index)).addAll(f1s);
                } else {
                    this.f1.add(f1s);
                    this.f2.add(f2s);
                    this.backs.add((Node)circuit.get(1));
                    Iterator var11 = loop_start.getSuccessors().iterator();

                    while(var11.hasNext()) {
                        Node n = (Node)var11.next();
                        if (!this.backs.contains(n)) {
                            this.exits.add(n);
                            break;
                        }
                    }
                }
            }
            System.out.println("f1.size:"+this.f1.toString());
            this.isHasIntersection = this.hasIntersection(this.f1);
            System.out.println("isHasIntersection:"+this.isHasIntersection);
            return;
        }
    }

    public void showInfo() {
        System.out.println("##########petriNet info##########");
        System.out.println("sourcePlace:" + this.sourcePlace);
        System.out.println("sinkPlace:" + this.sinkPlace);
        System.out.println("transitions:" + this.transitions);
        System.out.println("places:" + this.places);
        System.out.println("f1" + this.f1);
        System.out.println("f2" + this.f2);
        System.out.println("backs" + this.backs);
        System.out.println("exits" + this.exits);
        System.out.println("s1�н���" + this.isHasIntersection);
        System.out.println("##################################");
    }



    public boolean hasIntersection(List<List<Node>> circuits) {
        if (circuits.size() < 2) {
            return false;
        } else {
            for(int a = 0; a < circuits.size(); a++) {
                for(int b = a + 1; b < circuits.size(); b++) {
                    List<Node> ca = (List)circuits.get(a);
                    List<Node> cb = (List)circuits.get(b);
                    Iterator var7 = ca.iterator();
                    System.out.println("ca:"+ca.toString());
                    System.out.println("cb:"+cb.toString());
                    while(var7.hasNext()) {
                        Node node = (Node)var7.next();
                        if (node.getType().equalsIgnoreCase("place") && cb.contains(node)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public List<MyPetriNet> getAllCycle() {
        List<MyPetriNet> graphs = new ArrayList();
        String[] nodes = new String[this.getNodes().size()];

        for(int i = 0; i < this.getNodes().size(); ++i) {
            nodes[i] = ((Node)this.getNodes().get(i)).getID();
        }

        ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(this.getAdjacentMatrix(), nodes);
        List cycles = ecs.getElementaryCycles();

        for(int i = 0; i < cycles.size(); ++i) {
            MyPetriNet graph = new MyPetriNet();
            List cycle = (List)cycles.get(i);
            int index;
            int length;
            Node source;
            if ((index = cycle.indexOf("T-ADD")) != -1) {
                length = cycle.size();
                int j = index;

                do {
                    if (j == cycle.size()) {
                        j = 0;
                    }

                    source = this.getNodeById((String)cycle.get(j % length));
                    Node target = this.getNodeById((String)cycle.get((j + 1) % length));
                    graph.addNode(source);
                    graph.addArc(source, target);
                    ++j;
                } while(j % cycle.size() != index);
            } else {
                for(length = 0; length < cycle.size() - 1; ++length) {
                    Node source1 = this.getNodeById((String)cycle.get(length));
                    Node source2 = this.getNodeById((String)cycle.get(length + 1));
                    graph.addNode(source1);
                    graph.addArc(source1, source2);
                }

                graph.addNode(this.getNodeById((String)cycle.get(cycle.size() - 1)));
                graph.addArc(this.getNodeById((String)cycle.get(cycle.size() - 1)), this.getNodeById((String)cycle.get(0)));
            }

            graphs.add(graph);
        }
        //System.out.println("cycles size:"+graphs.size());
        return graphs;
    }

    public int hashCode() {
        int hashCode = 0;

        Node n;
        Iterator var3;
        for(var3 = this.getNodes().iterator(); var3.hasNext(); hashCode += n.hashCode()) {
            n = (Node)var3.next();
        }

        Arc arc;
        for(var3 = this.getArcs().iterator(); var3.hasNext(); hashCode += arc.hashCode()) {
            arc = (Arc)var3.next();
        }

        return hashCode & 2147483647;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MyPetriNet)) {
            return false;
        } else {
            MyPetriNet pn = (MyPetriNet)obj;
            if (this.getNodes().size() == pn.getNodes().size() && this.getArcs().size() == pn.getArcs().size()) {
                Iterator var4 = this.getNodes().iterator();

                while(var4.hasNext()) {
                    Node node = (Node)var4.next();
                    if (pn.getNodeById(node.getID()) == null) {
                        return false;
                    }
                }

                var4 = this.getArcs().iterator();

                while(var4.hasNext()) {
                    Arc arc = (Arc)var4.next();
                    if (pn.getArcByST(arc.getSource().getID(), arc.getTarget().getID()) == null) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }
}
