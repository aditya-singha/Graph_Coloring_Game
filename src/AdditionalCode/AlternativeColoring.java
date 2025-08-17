// This is additional code created by Mihai. It has not been integrated in the final product


// package AdditionalCode;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import org.proj11.casechecking.GraphCase;
// import org.proj11.casechecking.ComponentCaseChecker.CaseResult;
// import org.proj11.casechecking.ComponentCaseChecker.ComponentColoring;
// import org.proj11.graphds.Graph;
// import org.proj11.graphds.GraphColoring;
// import org.proj11.upperbounds.complex.DSaturUB;

// public class AlternativeColoring {
//     private DSaturUB dsaturUB = new DSaturUB();
    
//     public GraphColoring colorGraph(Graph g) {
//         List<Set<Integer>> comps = g.getComponents();
//         for (int i = 0; i < comps.size(); i++) {
//             if (g.getComponentCase(i).caseType() == GraphCase.NONE) {
//                 CaseResult compColoring = colorComponent(g, i);
//                 g.setComponentCase(i, compColoring);
//             }
//         }
//         return mergeColorings(g);
//     }

//     private CaseResult colorComponent(Graph g, int compIdx) {
//         Set<Integer> comp = g.getComponents().get(compIdx);
//         int upperBound = dsaturUB.dsaturOnComponent(g, comp);

//         Map<Integer, Integer> colorMap = null;
//         for(int k = upperBound; k >= 1; k--) {
//             Map<Integer, Integer> nextMap = findColoring(g, compIdx, k);
//             if (nextMap != null) {
//                 colorMap = nextMap;
//             } else {
//                 return new CaseResult(GraphCase.COLORED, new ComponentColoring(compIdx, colorMap, k+1));
//             }
//         }
//         return new CaseResult(GraphCase.COLORED, new ComponentColoring(compIdx, new HashMap<>(), 0));
//     }

//     private Map<Integer, Integer> findColoring(Graph g, int compIdx, int k) {
//         Set<Integer> comp = g.getComponents().get(compIdx);

//         // Initialize domains
//         Map<Integer, Set<Integer>> domains = new HashMap<>();
//         for (Integer v : comp) {
//             domains.put(v, new HashSet<>());
//             for (int i = 0; i < k; i++) {
//                 domains.get(v).add(i);
//             }
//         }

//         // Initialize color assignment
//         Map<Integer, Integer> assignment = new HashMap<>();
//         for (Integer v : comp) {
//             assignment.put(v, -1); // -1 == uncolored
//         }
        
//         boolean foundSolution = backtrack(g, comp, domains, assignment, 0); // Also keeps coloring in assignment
//         if (foundSolution) {
//             return assignment;
//         } else {
//             return null;
//         }
//     }

//     private boolean backtrack(Graph g,
//                               Set<Integer> comp,
//                               Map<Integer, Set<Integer>> domains,
//                               Map<Integer, Integer> assignment,
//                               int depth) {
        
//         // Base case: all vertices colored
//         if (depth >= comp.size()) {
//             return true;
//         }
//         // Select next variable
//         int vertex = selectVariable(g, comp, domains);
        
//         // Try all values for the variable
//         for (int color : orderDomain(g, vertex, domains)) {
//             if (isConsistent(g, vertex, color, assignment)) {
//                 // Assign value
//                 assignment.put(vertex, color);
//                 // Forward checking
//                 Map<Integer, Set<Integer>> domainsCopy = new HashMap<>();
//                 for (Map.Entry<Integer, Set<Integer>> entry : domains.entrySet()) {
//                     domainsCopy.put(entry.getKey(), new HashSet<>(entry.getValue()));
//                 }
//                 for (Integer neighbor : g.neighborsOf(vertex)) {
//                     if (domainsCopy.get(neighbor).remove(color)) {
//                         if (domainsCopy.get(neighbor).isEmpty()) {
//                             return false;
//                         }
//                     }
//                 }
//                 // Recurse
//                 if (backtrack(g, comp, domainsCopy, assignment, depth + 1)) {
//                     return true;
//                 }
//                 // Unassign value
//                 assignment.put(vertex, -1);
//             }
//         }

//         return false;
//     }

//     /** -- */
//     private List<Integer> orderDomain(Graph g, int vertex, Map<Integer, Set<Integer>> domains) {
//         return new ArrayList<>(domains.get(vertex)); // Default order
//     }

//     /** Selects next variable based on the minimum value heuristic with degree tie-breaker. */
//     private int selectVariable(Graph g, Set<Integer> comp, Map<Integer, Set<Integer>> domains) {
//         int minValueNum = Integer.MAX_VALUE;
//         int minVertex = -1;
//         for (Integer v : comp) {
//             if (domains.get(v).size() == 0) {
//                 continue; // skip already colored vertices
//             }
//             if (domains.get(v).size() < minValueNum) {
//                 minValueNum = domains.get(v).size();
//                 minVertex = v; 
//             } else if (domains.get(v).size() == minValueNum) {
//                 if (g.neighborsOf(v).size() > g.neighborsOf(minVertex).size()) {
//                     minVertex = v;
//                 }
//             }
//         }
//         return minVertex;
//     }

//     private GraphColoring mergeColorings(Graph g) {
//         Map<Integer, Integer> colorMap = new HashMap<>();
//         int chrNum = 0;
//         for (CaseResult c : g.getComponentCases()) {
//             if (c.caseType() == GraphCase.NONE) {
//                 throw new IllegalStateException("Component not colored");
//             }
//             colorMap.putAll(c.coloring().colorMap());
//             chrNum = Math.max(chrNum, c.coloring().compChrNum());
//         }
//         return new GraphColoring(g, colorMap, chrNum);
//     }
// }