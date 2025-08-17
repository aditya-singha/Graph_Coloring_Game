import java.util.ArrayList;
import java.util.Arrays;

public class ChromaticNumber {
    private int chromaticNumber = 100000;
    private int[][] lines;

    ArrayList<ArrayList<Integer>> rejectedColors = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> admittedColors = new ArrayList<ArrayList<Integer>>();

    ArrayList<ArrayList<Integer>> colors = new ArrayList<ArrayList<Integer>>();

    public ChromaticNumber(int[][] lines) {
        this.lines = lines;
    }

    @SuppressWarnings("unchecked")
    public void calculateChromaticNumber() {
        ArrayList<Integer> firstSetOfConnected = new ArrayList<Integer>();
        rejectedColors.add(firstSetOfConnected);
        ArrayList<Integer> firstNode = new ArrayList<Integer>();
        admittedColors.add(firstNode);

        int lastNode = getLastNode(lines);
        String connectedNodes[][] = new String[lastNode][2];

        fillConnectedNodes(connectedNodes);

        String sortedNodes[][] = connectedNodes.clone();

        sortConnected(sortedNodes);

        coloring(sortedNodes);

        colors = (ArrayList<ArrayList<Integer>>) admittedColors.clone();

        recoloring(connectedNodes);

        if (admittedColors.size() < colors.size()) {
            colors = (ArrayList<ArrayList<Integer>>) admittedColors.clone();
        }

    }

    private void fillConnectedNodes(String[][] connectedNodes) {
        for (int i = 0; i < lines.length; i++) {

            int left = lines[i][0];
            int right = lines[i][1];

            String a = connectedNodes[left - 1][1];
            String b = connectedNodes[right - 1][1];

            // left - 1 to correct indexing
            if (a == null) {
                connectedNodes[left - 1][0] = String.valueOf(left);
                connectedNodes[left - 1][1] = String.valueOf(right);
            } else {
                connectedNodes[left - 1][1] = new StringBuilder(a).append(",").append(right).toString();
            }

            // right - 1 to correct indexing
            if (b == null) {
                connectedNodes[right - 1][0] = String.valueOf(right);
                connectedNodes[right - 1][1] = String.valueOf(left);
            } else {
                connectedNodes[right - 1][1] = new StringBuilder(b).append(",").append(left).toString();
            }

        }
    }

    private void sortConnected(String[][] sortedNodes) {
        Arrays.sort(sortedNodes, (a, b) -> {
            int i1 = a[1] == null ? 0 : a[1].split(",").length;
            int i2 = b[1] == null ? 0 : b[1].split(",").length;
            return Integer.compare(i2, i1);
        });
    }

    private void coloring(String[][] nodes) {

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i][1] != null) {
                int node = Integer.valueOf(nodes[i][0]);

                String[] array = nodes[i][1].split(",");

                int[] connected = new int[array.length];
                for (int l = 0; l < array.length; l++) {
                    connected[l] = Integer.valueOf(array[l]);
                }

                boolean weHaveColor = false;

                for (int j = 0; j < rejectedColors.size(); j++) {
                    boolean checkConnected = true;
                    for (int n : connected) {
                        checkConnected = checkConnected && !admittedColors.get(j).contains(n);
                        if (!checkConnected) {
                            break;
                        }
                    }

                    boolean checkNode = !rejectedColors.get(j).contains(node);

                    if (checkNode && checkConnected) {
                        for (int p : connected) {
                            if (!rejectedColors.get(j).contains(p)) {
                                rejectedColors.get(j).add(p);
                            }
                        }
                        admittedColors.get(j).add(node);
                        weHaveColor = true;
                        break;
                    }
                }

                if (!weHaveColor) {
                    ArrayList<Integer> a0 = new ArrayList<Integer>();
                    a0.add(node);

                    ArrayList<Integer> a1 = new ArrayList<Integer>();
                    for (int m : connected) {
                        a1.add(m);
                    }

                    admittedColors.add(a0);
                    rejectedColors.add(a1);
                }
            }
        }
    }

    private void recoloring(String connectedNodes[][]) {

        for (int i = 0; i < admittedColors.size() * 1; i++) {

            int test = 0;

            // reordering nodes in such a way that we aim for the optimal result
            int count = admittedColors.get(test).size();
            String reorderNodes[][] = new String[count][2];

            // this is the for loop that reorders colors - one each time
            for (int j = 0; j < count; j++) {
                reorderNodes[j][0] = String.valueOf(admittedColors.get(test).get(j));
                int index = admittedColors.get(test).get(j) - 1;
                reorderNodes[j][1] = connectedNodes[index][1];
            }

            // here we are removing the current position from the admittedColors and the
            // rejectedColors since the colors will be reassigned from scratch
            admittedColors.remove(test);
            rejectedColors.remove(test);

            // we call the coloring process again to facilitate the process mentioned above
            coloring(reorderNodes);

            if (admittedColors.size() < chromaticNumber) {
                chromaticNumber = admittedColors.size();
            }

            // Additional helper function to count how many vertices are assigned to each
            // color
            // We want to understand whether there is a single color that causes an extra,
            // probably unecessary color

            StringBuilder sizes = new StringBuilder();

            for (int g = 0; g < admittedColors.size(); g++) {
                sizes.append(admittedColors.get(g).size()).append(" ");
            }

        }
    }

    private int getLastNode(int[][] lines) {
        int max = lines[0][0];
        for (int i = 0; i < lines.length; i++) {
            int left = lines[i][0];
            int right = lines[i][1];

            if (left > max) {
                max = left;
            }

            if (right > max) {
                max = right;
            }
        }

        return max;
    }

    public int getChromaticNumber() {
        return chromaticNumber;
    }

    public ArrayList<ArrayList<Integer>> getGraphColoring() {
        return admittedColors;
    }
}