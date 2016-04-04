
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Random;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;


class TSP_ortools {
	static String 
			absolutePathBase= "/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/TSP_Files/", 
			fileName="eil76.tsp",
			filePath=absolutePathBase+=fileName,
			line = "START",
			tspName = fileName.substring(0, fileName.length() - 4);
	
	static int size = 0;
	static boolean openLink=false;
	static String 
			scatter_html = "/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/HTML_Files/scatter.html", 
			solved_html="/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/HTML_Files/solved.html", 
			NN_html="/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/HTML_Files/nearestNeighbor.html";

	/*
	Confirmed files
	test
	eil51 (4 seconds)
	eil76 (28 seconds)
	eil101
	bier127 (65 seconds)
	a280 (18 minutes)
	att532
	 */
	
	static private int[] xs;
	static private int[] ys;
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Reading init data
		boolean initData = true;
		while (initData) {
			line = br.readLine();
//			System.out.println(line);
			if (line.contains("DIMENSION")) {
				String temp = line.substring(12, line.length());
				size = Integer.parseInt(temp);
			} else if (line.equals("NODE_COORD_SECTION")) {
				initData = false;
				br.readLine();
			}

		}
		br.close();
		
		//////////////////////////
		
		long starTime = System.currentTimeMillis();
		
		if (args.length > 0) {
			size = Integer.parseInt(args[0]);
		}

		int forbidden = 0;
		if (args.length > 1) {
			forbidden = Integer.parseInt(args[1]);
		}
		int seed = 0;
		if (args.length > 2) {
			seed = Integer.parseInt(args[2]);
		}

		solve(size, forbidden, seed);
		long finTime = System.currentTimeMillis() - starTime;
		System.out.println(finTime + " milliseconds");
		
	}
	
	static {
		System.loadLibrary("jniortools");
		
	}

	static class RandomManhattan extends NodeEvaluator2 {
		public RandomManhattan(int size, int seed) throws IOException {
			xs = new int[size];
			ys = new int[size];
			
			//Stat data for sizes
			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(filePath)));
			lnr.skip(Long.MAX_VALUE);int totalLines=lnr.getLineNumber() + 1;
			lnr.close();
			
			System.out.println("Solving "+fileName);
			System.out.println(totalLines+" lines in txt");
			System.out.println(size+ " nodes\n");
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(filePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();}
			
			boolean initData=true;
			while(initData) {
				line=br.readLine();
//				System.out.println(line);
				if(line.contains("NODE_COORD_SECTION")) {
					initData=false;
				}
			}
			
//			Reading NODES_COORD_SECTION
			int[] node=new int[size];
			for(int i=0;i<=(size-1);i++) {
				line = br.readLine();
				line = line.replaceAll("\\s+", " ");
				line = line.trim();
				String[] parts = line.split(" ");
				for(int q=0; q<=2;q++) {
						node[i]=Integer.parseInt(parts[0]);
						xs[i]=Integer.parseInt(parts[1]);
						ys[i]=Integer.parseInt(parts[2]);
						
				}
			}
			for(int i=0;i<=xs.length-1;i++) {
//				System.out.print("Node "+(i+1)+" - ");
//				System.out.println("["+xs[i]+", "+ys[i]+"],");
				}
			scatterChart(xs,ys);
		}

		@Override
		public long run(int firstIndex, int secondIndex) {
			return Math.abs(xs[firstIndex] - xs[secondIndex]) + Math.abs(ys[firstIndex] - ys[secondIndex]);
		}		
	}

	static class ConstantCallback extends NodeEvaluator2 {
		@Override
		public long run(int firstIndex, int secondIndex) {
			return 1;
		}
	}

	static void solve(int size, int forbidden, int seed) throws IOException {
		RoutingModel routing = new RoutingModel(size, 1);
		// Setting first solution heuristic (cheapest addition).
		routing.setFirstSolutionStrategy(RoutingModel.ROUTING_PATH_CHEAPEST_ARC);

		// Setting the cost function.
		// Put a permanent callback to the distance accessor here. The callback
		// has the following signature: ResultCallback2<int64, int64, int64>.
		// The two arguments are the from and to node inidices.
		RandomManhattan distances = new RandomManhattan(size, seed);
		routing.setCost(distances);

		// Forbid node connections (randomly).
		Random randomizer = new Random();
		long forbidden_connections = 0;
		while (forbidden_connections < forbidden) {
			long from = randomizer.nextInt(size - 1);
			long to = randomizer.nextInt(size - 1) + 1;
			if (routing.nextVar(from).contains(to)) {
				System.out.println("Forbidding connection " + from + " -> " + to);
				routing.nextVar(from).removeValue(to);
				++forbidden_connections;
			}
		}

		// Add dummy dimension to test API.
		routing.addDimension(new ConstantCallback(), size + 1, size + 1, true, "dummy");

		// Solve, returns a solution if any (owned by RoutingModel).
		Assignment solution = routing.solve();
		int[] solvedRoute=new int[size];
		if (solution != null) {
			// Solution cost.
//			System.out.println(size + " nodes");
//			System.out.println("Cost = " + solution.objectiveValue());
			// Inspect solution.
			// Only one route here; otherwise iterate from 0 to
			// routing.vehicles() - 1
			int route_number = 0;
			
			int counter=0;
			System.out.print("[");
			for (long node = routing.start(route_number); !routing.isEnd(node); node = solution
					.value(routing.nextVar(node))) {
				System.out.print("" + node + ", ");
				solvedRoute[counter]=(int) node;
				counter++;
			}
			System.out.println("0]");
		}
		solvedChart(solvedRoute,solution.objectiveValue());
		NN obj=new NN();
		obj.Neighbor(xs,ys,size,fileName);
	}

	public static void scatterChart(int[] xs, int[] ys) throws IOException {
		String coordinates="";
		for(int i=0;i<=xs.length-1;i++) {
			String coord="["+xs[i]+", "+ys[i]+"],";
			coordinates+=coord+"\n";
		}

		String baseHTML= "<html>\n" + 
				"  <head>\n" + 
				"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" + 
				"    <script type=\"text/javascript\">\n" + 
				"      google.charts.load('current', {'packages':['corechart']});\n" + 
				"      google.charts.setOnLoadCallback(drawChart);\n" + 
				"      function drawChart() {\n" + 
				"        var data = google.visualization.arrayToDataTable([\n" + 
				"          ['X', ''],";
		
		String endHTML= "]);\n" + 
				"\n" + 
				"        var options = {\n" + 
				"          title: 'Traveling Salesman Unsolved Nodes (" +fileName+")',\n" + 
				"			pointSize: 2,\n"+
				"          legend: 'none'\n" + 
				"        };\n" + 
				"\n" + 
				"        var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));\n" + 
				"\n" + 
				"        chart.draw(data, options);\n" + 
				"      }\n" + 
				"    </script>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div>\n" + 
				"  </body>\n" + 
				"</html>";
		baseHTML+=coordinates;
		baseHTML+=endHTML;
		String finalHTML = baseHTML;

		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(scatter_html, false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found, making file");
			PrintWriter writer = new PrintWriter(scatter_html, "UTF-8");
			writer.close();
		}
		outputStream.println(finalHTML);
		outputStream.close();

	}

	public static void solvedChart(int[] solvedRoute, long cost) throws IOException {
		String coordinates="";
		for(int i=0;i<=solvedRoute.length-1;i++) {
			String coord="["+xs[solvedRoute[i]]+", "+ys[solvedRoute[i]]+"],";
			coordinates+=coord+"\n";
		}
		coordinates+="["+xs[solvedRoute[0]]+", "+ys[solvedRoute[0]]+"]";
		System.out.println(coordinates);
		cost=0;
		int x1,y1,x2,y2;
		for(int i=0;i<=xs.length-2;i++) {
			x1=xs[i];
			y1=ys[i];
			x2=xs[i+1];
			y2=ys[i+1];
			cost+=(Math.sqrt(((x1-x2)*(x1-x2)) + ((y1-y2)*(y1-y2))));
		}
		System.out.println("Cost = "+cost);
		
		String baseHTML= "<html>\n" + 
				"  <head>\n" + 
				"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" + 
				"    <script type=\"text/javascript\">\n" + 
				"      google.charts.load('current', {'packages':['corechart']});\n" + 
				"      google.charts.setOnLoadCallback(drawChart);\n" + 
				"      function drawChart() {\n" + 
				"        var data = google.visualization.arrayToDataTable([\n" + 
				"          ['X', ''],\n";
		
		String endHTML= "]);\n" + 
				"\n" + 
				"        var options = {\n" + 
				"          title: 'Traveling Salesman Solved Nodes ("+fileName+") - Cost: "+cost+" Or-Tools Algorithm"
						+ "',\n "+ 
				"			pointSize: 2,\n"+
				"			lineWidth: 1,\n"+
				"          legend: 'none'\n" + 
				"        };\n" + 
				"\n" + 
				"        var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));\n" + 
				"\n" + 
				"        chart.draw(data, options);\n" + 
				"      }\n" + 
				"    </script>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div>\n" + 
				"Path: " + coordinates+
				"  </body>\n" + 
				"</html>";
		baseHTML+=coordinates;
		baseHTML+=endHTML;
		String finalHTML = baseHTML;
		
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(solved_html, false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found, making file.");
			PrintWriter writer = new PrintWriter(solved_html, "UTF-8");
			writer.close();
		}
		outputStream.println(finalHTML);
		outputStream.close();
		
		if (openLink) {
			BrowserAPI(scatter_html);
			BrowserAPI(solved_html);
			BrowserAPI(NN_html);
		}
		
	}
	
	public static void BrowserAPI(String link) throws IOException {
		File htmlFile = new File(link);
		Desktop.getDesktop().browse(htmlFile.toURI());
	}
}
