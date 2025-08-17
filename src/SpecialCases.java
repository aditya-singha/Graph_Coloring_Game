import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpecialCases {
    private int numberOfVertices; // We will need the number of vertices of the graph. We will be able to choose
                                  // this number when creating the graph
    private ArrayList<ArrayList<Integer>> adj;

    public SpecialCases(int numberOfVertices) { // Constructor
        this.numberOfVertices = numberOfVertices;
        adj = new ArrayList<>(numberOfVertices); // The size of the ArrayList will be the number of vertices
        for (int i = 0; i < numberOfVertices; i++) {
            adj.add(new ArrayList<>()); // Adding ArrayLists to the ArrayList
        }
    }

    public void addEdge(int startVertex, int endVertex) { // Method to add the edges between vertices
        // Adding an edge between "startVertex" and "endVertex", and viceversa, as it is an undirected graph, so it is bidirectional
        
        adj.get(startVertex - 1).add(endVertex - 1);
        adj.get(endVertex - 1).add(startVertex - 1);
    }

    public boolean isTree() { // Method to check whether the graph is a tree, so there must be exactly numberOfVertices - 1 edges
        int edgeCount = 0; // Finding the number of edges
        for (int i = 0; i < numberOfVertices; i++) {
            edgeCount += adj.get(i).size();
        }

        edgeCount /= 2; // Because we add an edge between "startVertex" and "endVertex" and viceversa,
                        // we get double the number of edges, so we divide by 2

        if (edgeCount != numberOfVertices - 1) { // There must be exactly numberOfVertices - 1 edges
            return false;
        } else {
            return true;
        }
    }

    public boolean isComplete() { // Method to check whether a graph is complete, so there must be exactly
                                  // (numberOfVertices * (numberOfVertices - 1)) / 2 edges
        int edgeCount = 0; // Finding the number of edges
        for (int i = 0; i < numberOfVertices; i++) {
            edgeCount += adj.get(i).size();
        }

        edgeCount /= 2; // Because we add an edge between "startVertex" and "endVertex" and viceversa,
                        // we get double the number of edges, so we divide by 2

        if (edgeCount != (numberOfVertices * (numberOfVertices - 1)) / 2) { // There must be exactly (numberOfVertices *
                                                                            // (numberOfVertices - 1)) / 2 edges
            return false;
        } else {
            return true;
        }
    }

    public boolean isStar() {
        int n = numberOfVertices - 1;
        int centralNodeCount = 0;
        int peripheralNodeCount = 0;

        for (List<Integer> neighbors : adj) {
            int degree = neighbors.size();
            if (degree == n) {
                centralNodeCount++;
            } else if (degree == 1) {
                peripheralNodeCount++;
            }
        }

        return centralNodeCount == 1 && peripheralNodeCount == n;
    }

    public boolean isWheel() {
        if (numberOfVertices < 4)
            return false;
        int center = -1;
        for (int i = 0; i < numberOfVertices; i++) {
            if (adj.get(i).size() == numberOfVertices - 1) {
                center = i;
                break;
            }
        }
        if (center == -1)
            return false;
        for (int i = 0; i < numberOfVertices; i++) {
            if (i != center && adj.get(i).size() != 3) {
                return false;
            }
        }
        return true;
    }

    public boolean isCycle() {
        boolean[] visited = new boolean[numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            if (!visited[i] && hasCycle(i, visited, -1)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycle(int v, boolean[] visited, int parent) {
        visited[v] = true;
        for (int neighbor : adj.get(v)) {
            if (!visited[neighbor]) {
                if (hasCycle(neighbor, visited, v)) {
                    return true;
                }
            } else if (neighbor != parent) {
                return true;
            }
        }
        return false;
    }

    public boolean isBipartite() {
        int[] colors = new int[numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            colors[i] = -1;
        }
        for (int i = 0; i < numberOfVertices; i++) {
            if (colors[i] == -1 && !isBipartiteBFS(i, colors)) {
                return false;
            }
        }
        return true;
    }

    private boolean isBipartiteBFS(int src, int[] colors) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(src);
        colors[src] = 0;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : adj.get(v)) {
                if (colors[neighbor] == -1) {
                    colors[neighbor] = 1 - colors[v];
                    queue.add(neighbor);
                } else if (colors[neighbor] == colors[v]) {
                    return false;
                }
            }
        }
        return true;
    }

}