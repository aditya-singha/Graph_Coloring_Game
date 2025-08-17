import java.io.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

class ColEdge {
	int u;
	int v;

}

public class ReadGraph {

	public final static boolean DEBUG = false;

	public final static String COMMENT = "//";

	public void giveWarning(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Message");
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
		throw new RuntimeException(message);
	}

	public Object[] processTextfile(File file) {

		File inputfile = file;

		boolean seen[] = null;

		// ! n is the number of vertices in the graph
		int n = -1;

		// ! m is the number of edges in the graph
		int m = -1;

		// ! e will contain the edges of the graph
		ColEdge e[] = null;

		try {
			FileReader fr = new FileReader(inputfile);
			BufferedReader br = new BufferedReader(fr);

			String record = new String();

			// ! THe first few lines of the file are allowed to be comments, staring with a
			// // symbol.
			// ! These comments are only allowed at the top of the file.

			// ! -----------------------------------------
			while ((record = br.readLine()) != null) {
				if (record.startsWith("//"))
					continue;
				break; // Saw a line that did not start with a comment -- time to start reading the
						// data in!
			}

			if (record == null) {
				giveWarning("The file is empty or contains only comments. Please provide valid graph data.");
			}

			if (record.startsWith("VERTICES = ")) {
				if (record.substring(11).matches("\\d+") && Integer.parseInt(record.substring(11)) >= 2) {
					n = Integer.parseInt(record.substring(11));
					if (DEBUG)
						System.out.println(COMMENT + " Number of vertices = " + n);
				} else {
					giveWarning(
							"Number of vertices should be greater than or equal to 2, and should be an integer. Field should not be blank");
				}

			}

			seen = new boolean[n + 1];

			record = br.readLine();

			// same conditions as used for vertices statement

			if (record.startsWith("EDGES = ")) {
				if (record.substring(8).matches("\\d+") && Integer.parseInt(record.substring(8)) >= (n - 1)) {
					m = Integer.parseInt(record.substring(8));
					if (DEBUG)
						System.out.println(COMMENT + " Expected number of edges = " + m);
				} else {
					giveWarning(
							"Number of edges should be greater than or equal to (vertices - 1), and should be an integer. Field should not be blank");
				}

			}

			e = new ColEdge[m];

			for (int d = 0; d < m; d++) {
				if (DEBUG)
					System.out.println(COMMENT + " Reading edge " + (d + 1));
				record = br.readLine();

				String data[] = record.split(" ");
				if (data.length != 2) {
					giveWarning("Error! Malformed edge line: " + record);
				}
				e[d] = new ColEdge();

				// check if each string present as elements in the array formed by splitting the
				// sentence is
				// fully numeric and lies within the vertices 1 to n

				for (String i : data) {

					// reject if string is not fully numeric or it less than 1 or greater than n

					if (!i.matches("\\d+") || Integer.parseInt(i) < 1 || Integer.parseInt(i) > n) {
						giveWarning("Error reading edge " + (d + 1)
								+ ". Please check if the text file correctly contains vertex pairings.");
					}
				}

				e[d].u = Integer.parseInt(data[0]);
				e[d].v = Integer.parseInt(data[1]);

				seen[e[d].u] = true;
				seen[e[d].v] = true;

				if (DEBUG)
					System.out.println(COMMENT + " Edge: " + e[d].u + " " + e[d].v);

			}

			String surplus = br.readLine();
			if (surplus != null) {
				if (surplus.length() >= 2)
					if (DEBUG)
						System.out.println(
								COMMENT + " Warning: there appeared to be data in your file after the last edge: '"
										+ surplus + "'");
			}

		} catch (IOException ex) {
			// catch possible io errors from readLine()
			giveWarning("Error! Problem reading file " + inputfile);
		}

		for (int x = 1; x <= n; x++) {
			if (seen[x] == false) {
				giveWarning(COMMENT + " Warning: vertex " + x + " didn't appear in any edge");
				if (DEBUG)
					System.out.println(COMMENT + " Warning: vertex " + x
							+ " didn't appear in any edge : it will be considered a disconnected vertex on its own.");
			}
		}

		Object[] object = new Object[4];

		object[0] = n;

		object[1] = m;

		object[2] = e;

		int[][] edgeArray = new int[e.length][2];
		for (int i = 0; i < e.length; i++) {
			edgeArray[i][0] = e[i].u;
			edgeArray[i][1] = e[i].v;
		}

		for (int[] edge : edgeArray) {
			if (DEBUG)
				System.out.println(java.util.Arrays.toString(edge));
		}

		object[3] = edgeArray;

		return object;
	}

}