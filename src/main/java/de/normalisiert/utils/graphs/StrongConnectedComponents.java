//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.normalisiert.utils.graphs;

import java.util.Vector;

public class StrongConnectedComponents {
    private int[][] adjListOriginal = null;
    private int[][] adjList = null;
    private boolean[] visited = null;
    private Vector stack = null;
    private int[] lowlink = null;
    private int[] number = null;
    private int sccCounter = 0;
    private Vector currentSCCs = null;

    public StrongConnectedComponents(int[][] adjList) {
        this.adjListOriginal = adjList;
    }

    public SCCResult getAdjacencyList(int node) {
        this.visited = new boolean[this.adjListOriginal.length];
        this.lowlink = new int[this.adjListOriginal.length];
        this.number = new int[this.adjListOriginal.length];
        this.visited = new boolean[this.adjListOriginal.length];
        this.stack = new Vector();
        this.currentSCCs = new Vector();
        this.makeAdjListSubgraph(node);

        for(int i = node; i < this.adjListOriginal.length; ++i) {
            if (!this.visited[i]) {
                this.getStrongConnectedComponents(i);
                Vector nodes = this.getLowestIdComponent();
                if (nodes != null && !nodes.contains(new Integer(node)) && !nodes.contains(new Integer(node + 1))) {
                    return this.getAdjacencyList(node + 1);
                }

                Vector[] adjacencyList = this.getAdjList(nodes);
                if (adjacencyList != null) {
                    for(int j = 0; j < this.adjListOriginal.length; ++j) {
                        if (adjacencyList[j].size() > 0) {
                            return new SCCResult(adjacencyList, j);
                        }
                    }
                }
            }
        }

        return null;
    }

    private void makeAdjListSubgraph(int node) {
        this.adjList = new int[this.adjListOriginal.length][0];

        for(int i = node; i < this.adjList.length; ++i) {
            Vector successors = new Vector();

            int j;
            for(j = 0; j < this.adjListOriginal[i].length; ++j) {
                if (this.adjListOriginal[i][j] >= node) {
                    successors.add(new Integer(this.adjListOriginal[i][j]));
                }
            }

            if (successors.size() > 0) {
                this.adjList[i] = new int[successors.size()];

                for(j = 0; j < successors.size(); ++j) {
                    Integer succ = (Integer)successors.get(j);
                    this.adjList[i][j] = succ;
                }
            }
        }

    }

    private Vector getLowestIdComponent() {
        int min = this.adjList.length;
        Vector currScc = null;

        for(int i = 0; i < this.currentSCCs.size(); ++i) {
            Vector scc = (Vector)this.currentSCCs.get(i);

            for(int j = 0; j < scc.size(); ++j) {
                Integer node = (Integer)scc.get(j);
                if (node < min) {
                    currScc = scc;
                    min = node;
                }
            }
        }

        return currScc;
    }

    private Vector[] getAdjList(Vector nodes) {
        Vector[] lowestIdAdjacencyList = (Vector[])null;
        if (nodes != null) {
            lowestIdAdjacencyList = new Vector[this.adjList.length];

            int i;
            for(i = 0; i < lowestIdAdjacencyList.length; ++i) {
                lowestIdAdjacencyList[i] = new Vector();
            }

            for(i = 0; i < nodes.size(); ++i) {
                int node = (Integer)nodes.get(i);

                for(int j = 0; j < this.adjList[node].length; ++j) {
                    int succ = this.adjList[node][j];
                    if (nodes.contains(new Integer(succ))) {
                        lowestIdAdjacencyList[node].add(new Integer(succ));
                    }
                }
            }
        }

        return lowestIdAdjacencyList;
    }

    private void getStrongConnectedComponents(int root) {
        ++this.sccCounter;
        this.lowlink[root] = this.sccCounter;
        this.number[root] = this.sccCounter;
        this.visited[root] = true;
        this.stack.add(new Integer(root));

        int next;
        for(next = 0; next < this.adjList[root].length; ++next) {
            int w = this.adjList[root][next];
            if (!this.visited[w]) {
                this.getStrongConnectedComponents(w);
                this.lowlink[root] = Math.min(this.lowlink[root], this.lowlink[w]);
            } else if (this.number[w] < this.number[root] && this.stack.contains(new Integer(w))) {
                this.lowlink[root] = Math.min(this.lowlink[root], this.number[w]);
            }
        }

        if (this.lowlink[root] == this.number[root] && this.stack.size() > 0) {

            Vector scc = new Vector();

            do {
                next = (Integer)this.stack.get(this.stack.size() - 1);
                this.stack.remove(this.stack.size() - 1);
                scc.add(new Integer(next));
            } while(this.number[next] > this.number[root]);

            if (scc.size() > 1) {
                this.currentSCCs.add(scc);
            }
        }

    }

    public static void main(String[] args) {
        boolean[][] adjMatrix = new boolean[10][];

        for(int i = 0; i < 10; ++i) {
            adjMatrix[i] = new boolean[10];
        }

        adjMatrix[0][1] = true;
        adjMatrix[1][2] = true;
        adjMatrix[2][0] = true;
        adjMatrix[2][6] = true;
        adjMatrix[3][4] = true;
        adjMatrix[4][5] = true;
        adjMatrix[4][6] = true;
        adjMatrix[5][3] = true;
        adjMatrix[6][7] = true;
        adjMatrix[7][8] = true;
        adjMatrix[8][6] = true;
        adjMatrix[6][1] = true;
        int[][] adjList = AdjacencyList.getAdjacencyList(adjMatrix);
        StrongConnectedComponents scc = new StrongConnectedComponents(adjList);

        for(int i = 0; i < adjList.length; ++i) {
            System.out.print("i: " + i + "\n");
            SCCResult r = scc.getAdjacencyList(i);
            if (r != null) {
                Vector[] al = scc.getAdjacencyList(i).getAdjList();

                for(int j = i; j < al.length; ++j) {
                    if (al[j].size() > 0) {
                        System.out.print("j: " + j);

                        for(int k = 0; k < al[j].size(); ++k) {
                            System.out.print(" _" + al[j].get(k).toString());
                        }

                        System.out.print("\n");
                    }
                }

                System.out.print("\n");
            }
        }

    }
}
