// This is additional code created by Mihai. It has not been integrated in the final product


// package AdditionalCode;

// import java.util.*;

// /**
//  * A backtracking graph-coloring approach with:
//  * 1) Forward Checking
//  * 2) Simplified Arc Consistency
//  * 3) MRV (Minimum Remaining Values) + Degree tiebreak
//  * 4) Least Constraining Value heuristic
//  * 
//  * Uses:
//  *  - Map<Integer, HashSet<Integer>> for each domain
//  *  - Map<Integer, List<RemovalRecord>> for the change log
//  * 
//  * Where RemovalRecord is a record holding (vertex, color).
//  */
// public class CSPColoring implements ColoringAlgorithm {

//     /** A simple record for (vertex, color) removals */
//     public record RemovalRecord(int vertex, int color) {}

//     // domain[v] => set of possible colors for vertex v
//     private Map<Integer, HashSet<Integer>> domains;
//     // Current color assignment (0 means unassigned)
//     private Map<Integer, Integer> assignment;
//     // changeLog[depth] => list of (vertex, color) that were removed at 'depth'
//     private Map<Integer, List<RemovalRecord>> changeLog;

//     private Graph graph;
//     private int currentDepth; // current recursion

//     private long offsetTime; // time limit
//     private long MAX_DELTA = 30_000; // time limit

//     @Override
//     public GraphColoring colorGraph(Graph g, int lowerBound, int upperBound) {
//         this.graph = g;

//         // Try coloring with color counts from lowerBound..upperBound
//         GraphColoring temp = null;
//         for (int k = upperBound; k >= lowerBound; k--) {
//             offsetTime = System.currentTimeMillis();
//             GraphColoring result = attemptColoring(k);
//             System.out.println("delta " + (System.currentTimeMillis() - offsetTime));
//             if (result != null) {
//                 temp = result;
//             } if (result==null || k == lowerBound) {
//                 return temp; // no solution for k, use k+1
//             }
//         }

//         // No coloring found
//         return new GraphColoring(g, Collections.emptyMap(), -1);
//     }

//    /** Attempt coloring using k colors */
//     private GraphColoring attemptColoring(int k) {
//         initializeDomains(k);

//         // assignment[v] = 0 => not yet assigned
//         assignment = new HashMap<>();
//         for (int v : graph.getVertices()) {
//             assignment.put(v, 0);
//         }

//         // Initialize change log
//         changeLog = new HashMap<>();
//         currentDepth = 0;

//         // Start backtracking
//         boolean success = backtrack(k);
//         if (!success) {
//             return null; 
//         }

//         // Build final color map
//         Map<Integer, Integer> colorMap = new HashMap<>(assignment);
//         int chromaticNumber = colorMap.values().stream().mapToInt(i -> i).max().orElse(k);
//         return new GraphColoring(graph, colorMap, chromaticNumber);
//     }

//     /** Create an initial domain for each vertex: {1, 2, ..., k}. */
//     private void initializeDomains(int k) {
//         domains = new HashMap<>();
//         for (int v : graph.getVertices()) {
//             HashSet<Integer> colors = new HashSet<>();
//             for (int c = 1; c <= k; c++) {
//                 colors.add(c);
//             }
//             domains.put(v, colors);
//         }
//     }

//     /** Backtracking algorithm with forward checking and arc consistency. */
//     private boolean backtrack(int k) {
//         // Time limit check
//         if (System.currentTimeMillis() - offsetTime > MAX_DELTA) {
//             return false;
//         }

//         // Base case: all vertices assigned
//         int unassignedV = getNextVertex();
//         if (unassignedV == -1) {
//             return true; // all assigned
//         }

//         currentDepth++;
//         changeLog.putIfAbsent(currentDepth, new ArrayList<>());

//         // Get the domain of the chosen vertex
//         // We'll order its domain by least constraining value

//         List<Integer> orderedColors = getLeastConstrainingValueOrder(unassignedV);
//         //List<Integer> orderedColors = new ArrayList<>(domains.get(unassignedV));

//         for (int color : orderedColors) {
//             // Assign
//             assignment.put(unassignedV, color);

//             // Forward Checking: remove 'color' from neighbors' domains
//             //
//             List<RemovalRecord> immediateRemovals = new ArrayList<>();
//             boolean forwardCheckOk = forwardCheck(immediateRemovals, unassignedV, color);
//             //boolean arcConsistencyOk = runArcConsistency();

//             if (forwardCheckOk) {
//                 // Recurse
//                 if (backtrack(k)) {
//                     return true;
//                 }
//             }

//             // Undo assignment
//             assignment.put(unassignedV, 0);

//             // Undo forward checking changes at this depth
//             rollbackRemovals(immediateRemovals);
//         }

//         // If no color worked, rollback all changes at this depth and return false
//         rollbackDepth(currentDepth);
//         currentDepth--;
//         return false;
//     }

//     /**
//      * Returns the vertex with the minimum remaining domain size.
//      * If there's a tie, choose the one with the highest degree.
//      * If all assigned, returns -1.
//      */
//     private int getNextVertex() {
//         int chosen = -1;
//         int minDomainSize = Integer.MAX_VALUE;
//         int maxDegree = -1;

//         for (int v : graph.getVertices()) {
//             if (assignment.get(v) == 0) { // unassigned
//                 int domainSize = domains.get(v).size();
//                 if (domainSize < minDomainSize) {
//                     minDomainSize = domainSize;
//                     chosen = v;
//                     maxDegree = graph.degreeOf(v);
//                 } else if (domainSize == minDomainSize) {
//                     // tiebreak by degree
//                     int deg = graph.degreeOf(v);
//                     if (deg > maxDegree) {
//                         chosen = v;
//                         maxDegree = deg;
//                     }
//                 }
//             }
//         }
//         return chosen;
//     }

//     /**
//      * Order the colors in the domain of 'v' by how few domain removals they
//      * cause in neighbors (least constraining value first).
//      */
//     private List<Integer> getLeastConstrainingValueOrder(int v) {
//         // We'll evaluate each color by how many times it appears in neighbors' domains
//         // so that picking a color that appears less in neighbors is more constraining, we want the opposite
//         // Actually, we want to pick the color that removes the fewest possibilities from neighbors' domains

//         HashMap<Integer, Integer> colorConstraints = new HashMap<>();
//         for (int c : domains.get(v)) {
//             int count = 0;
//             // For each neighbor, if neighbor's domain contains c, that means we'd remove c from neighbor
//             for (int w : graph.neighborsOf(v)) {
//                 if (assignment.get(w) == 0) { // only consider unassigned neighbor
//                     if (domains.get(w).contains(c)) {
//                         count++; // This color c would be removed from w
//                     }
//                 }
//             }
//             colorConstraints.put(c, count);
//         }

//         // Sort colors by ascending "count" => least constraining
//         List<Integer> sortedColors = new ArrayList<>(domains.get(v));
//         sortedColors.sort(Comparator.comparingInt(colorConstraints::get));
//         return sortedColors;
//     }

//     /**
//      * Once we assign 'color' to vertex 'v', remove that color from each neighbor's domain.
//      * Returns the list of removals done so we can restore them if needed.
//      */
//     private boolean forwardCheck(List<RemovalRecord> immediateRemovals, int v, int color) {
//         boolean ok = true;
//         for (int w : graph.neighborsOf(v)) {
//             if (assignment.get(w) == 0 && domains.get(w).contains(color)) {
//                 domains.get(w).remove(color);
//                 RemovalRecord rec = new RemovalRecord(w, color);
//                 immediateRemovals.add(rec);
//                 changeLog.get(currentDepth).add(rec);

//                 if (domains.get(w).isEmpty()) {
//                     // Mark failure but do NOT break.
//                     ok = false;
//                 }
//             }
//         }
//         return ok;
//     }

//     /**
//      * Undo a set of removals done at the current depth.
//      */
//     private void rollbackRemovals(List<RemovalRecord> removed) {
//         for (RemovalRecord rec : removed) {
//             domains.get(rec.vertex()).add(rec.color());
//             // Also remove from global changeLog
//             changeLog.get(currentDepth).remove(rec);
//         }
//     }

//     /**
//      * Completely undo all removals at 'depth'.
//      */
//     private void rollbackDepth(int depth) {
//         // pop from changeLog[depth] in reverse insertion order is ideal,
//         // but we can simply re-add them in any order
//         List<RemovalRecord> removedThisDepth = changeLog.get(depth);
//         for (RemovalRecord rec : removedThisDepth) {
//             domains.get(rec.vertex()).add(rec.color());
//         }
//         removedThisDepth.clear();
//     }
// }