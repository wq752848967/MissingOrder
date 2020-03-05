//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.normalisiert.utils.graphs;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class SCCResult {
    private Set nodeIDsOfSCC = null;
    private Vector[] adjList = null;
    private int lowestNodeId = -1;

    public SCCResult(Vector[] adjList, int lowestNodeId) {
        this.adjList = adjList;
        this.lowestNodeId = lowestNodeId;
        this.nodeIDsOfSCC = new HashSet();
        if (this.adjList != null) {
            for(int i = this.lowestNodeId; i < this.adjList.length; ++i) {
                if (this.adjList[i].size() > 0) {
                    this.nodeIDsOfSCC.add(new Integer(i));
                }
            }
        }

    }

    public Vector[] getAdjList() {
        return this.adjList;
    }

    public int getLowestNodeId() {
        return this.lowestNodeId;
    }
}
