//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.normalisiert.utils.graphs;

import java.util.Vector;

public class AdjacencyList {
    public AdjacencyList() {
    }

    public static int[][] getAdjacencyList(boolean[][] adjacencyMatrix) {
        int[][] list = new int[adjacencyMatrix.length][];

        for(int i = 0; i < adjacencyMatrix.length; ++i) {
            Vector v = new Vector();

            int j;
            for(j = 0; j < adjacencyMatrix[i].length; ++j) {
                if (adjacencyMatrix[i][j]) {
                    v.add(new Integer(j));
                }
            }

            list[i] = new int[v.size()];

            for(j = 0; j < v.size(); ++j) {
                Integer in = (Integer)v.get(j);
                list[i][j] = in;
            }
        }

        return list;
    }
}
