//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.normalisiert.utils.graphs;

import java.util.List;
import java.util.Vector;

public class ElementaryCyclesSearch {
    private List cycles = null;
    private int[][] adjList = null;
    private Object[] graphNodes = null;
    private boolean[] blocked = null;
    private Vector[] B = null;
    private Vector stack = null;

    public ElementaryCyclesSearch(boolean[][] matrix, Object[] graphNodes) {
        this.graphNodes = graphNodes;
        this.adjList = AdjacencyList.getAdjacencyList(matrix);
    }

    public List getElementaryCycles() {
        this.cycles = new Vector();
        this.blocked = new boolean[this.adjList.length];
        this.B = new Vector[this.adjList.length];
        this.stack = new Vector();
        StrongConnectedComponents sccs = new StrongConnectedComponents(this.adjList);
        int s = 0;

        while(true) {
            SCCResult sccResult = sccs.getAdjacencyList(s);
            if (sccResult == null || sccResult.getAdjList() == null) {
                return this.cycles;
            }

            Vector[] scc = sccResult.getAdjList();
            s = sccResult.getLowestNodeId();

            for(int j = 0; j < scc.length; ++j) {
                if (scc[j] != null && scc[j].size() > 0) {
                    this.blocked[j] = false;
                    this.B[j] = new Vector();
                }
            }

            this.findCycles(s, s, scc);
            ++s;
        }
    }

    private boolean findCycles(int v, int s, Vector[] adjList) {
        boolean f = false;
        this.stack.add(new Integer(v));
        this.blocked[v] = true;

        int i;
        int w;
        for(i = 0; i < adjList[v].size(); ++i) {
            w = (Integer)adjList[v].get(i);
            if (w != s) {
                if (!this.blocked[w] && this.findCycles(w, s, adjList)) {
                    f = true;
                }
            } else {
                Vector cycle = new Vector();

                for(int j = 0; j < this.stack.size(); ++j) {
                    int index = (Integer)this.stack.get(j);
                    cycle.add(this.graphNodes[index]);
                }

                this.cycles.add(cycle);
                f = true;
            }
        }

        if (f) {
            this.unblock(v);
        } else {
            for(i = 0; i < adjList[v].size(); ++i) {
                w = (Integer)adjList[v].get(i);
                if (!this.B[w].contains(new Integer(v))) {
                    this.B[w].add(new Integer(v));
                }
            }
        }

        this.stack.remove(new Integer(v));
        return f;
    }

    private void unblock(int node) {
        this.blocked[node] = false;
        Vector Bnode = this.B[node];

        while(Bnode.size() > 0) {
            Integer w = (Integer)Bnode.get(0);
            Bnode.remove(0);
            if (this.blocked[w]) {
                this.unblock(w);
            }
        }

    }
}
