import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NN {
	public void Neighbor(int[] xs, int[] ys, int size, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		int x1,y1,x2,y2,indextoRemove=0;

		String coordinates="";
		double distance=1000000;
		ArrayList<Integer> xsList = new ArrayList<Integer>(size);
		ArrayList<Integer> ysList = new ArrayList<Integer>(size);
		
		for(int i=0;i<=xs.length-1;i++) {
			xsList.add(xs[i]);
			ysList.add(ys[i]);
		}

		
		//Finding highest X value
		int highestX=0,highestY=0;
		for(int i=0; i<=xsList.size()-1;i++) {
			if(xsList.get(i)>highestX) {
				highestX=xsList.get(i);
				highestY=ysList.get(i);
				indextoRemove=i;
			}
		}
		x1=highestX;
		y1=highestY;
		coordinates+="["+highestX+", "+highestY+"],"+"\n";
		xsList.remove(indextoRemove);
		ysList.remove(indextoRemove);
		

		double cost=0;
		while (xsList.size() != 0) {
			
			distance=1000000;
			double tempDistance=0;
			
			for (int i = 0; i <= xsList.size() - 1; i++) {
				x2=xsList.get(i);
				y2=ysList.get(i);
				tempDistance=distance(x1,x2,y1,y2);
				if(tempDistance<distance) {
					distance=tempDistance;
					indextoRemove=i;
					
				}
			}
//				System.out.print("Next closest point is at ");
//				System.out.print("["+xsList.get(indextoRemove)+","+ysList.get(indextoRemove)+"] , ");
//				System.out.println(distance + " away");
				cost+=distance;
				distance=1000000;
				coordinates+="["+xsList.get(indextoRemove)+","+ysList.get(indextoRemove)+"],\n";
				
				xsList.remove(indextoRemove);
				ysList.remove(indextoRemove);
				
		}
		coordinates+="["+highestX+", "+highestY+"]";
		System.out.println("\n"+"NN Coordinates:");
		System.out.println(coordinates);
		System.out.println("Cost = "+Math.round(cost));
		
		makeNNURL(coordinates, fileName,cost);
		
	}
	
	public double distance(int x1,int x2,int y1,int y2) {
		double distance = (Math.sqrt(  ((x1-x2)*(x1-x2)) + ((y1-y2)*(y1-y2))  )  );
		return distance;
	}
	
	public void makeNNURL(String coordinates, String fileName, double cost) throws FileNotFoundException, UnsupportedEncodingException {
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
				"          title: 'Traveling Salesman Solved Nodes ("+fileName+") - Cost: "+Math.round(cost)+" - Nearest Neighbor Algorithm',\n "+ 
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
			outputStream = new PrintWriter(new FileOutputStream("/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/HTML_Files/nearestNeighbor.html", false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found, making file.");
			PrintWriter writer = new PrintWriter("/Users/suriv/Desktop/workspace/TravelingSalesman_ORT/HTML_Files/nearestNeighbor.html", "UTF-8");
			writer.close();
		}
		outputStream.println(finalHTML);
		outputStream.close();
	}
}
