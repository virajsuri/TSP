import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NN {
	public void Neighbor(int[] xs, int[] ys, int size, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		int x1,y1,x2,y2;
		String startHTML="";
		double distance=1000000;
		ArrayList<Integer> xsList = new ArrayList<Integer>(size);
		ArrayList<Integer> ysList = new ArrayList<Integer>(size);
		for(int i=0;i<=xs.length-1;i++) {
			xsList.add(xs[i]);
			ysList.add(ys[i]);
		}
		System.out.println(xsList);
		System.out.println(ysList);
		
		//Finding highest X value
		int highestX=0,highestY=0;
		for(int i=0; i<=xsList.size()-1;i++) {
			if(xsList.get(i)>highestX) {
				highestX=xsList.get(i);
				highestY=ysList.get(i);
			}
		}
		x1=highestX;
		y1=highestY;
		System.out.println(highestX + " ,"+highestY);
		
		
		double cost=0;
		while (xsList.size() != 1) {
			int indextoRemove=0;
			distance=1000000;
			double tempDistance=0;
			for (int i = 0; i <= xsList.size() - 2; i++) {
				x2=xsList.get(i+1);
				y2=ysList.get(i+1);
				tempDistance=distance(x1,x2,y1,y2);
				System.out.println(tempDistance);
				if(tempDistance<distance) {
					distance=tempDistance;
					indextoRemove=i;
				}
				x1=xsList.get(indextoRemove);
				y1=ysList.get(indextoRemove);
				
			}
				System.out.print("Next closest point is at ");
				System.out.print("["+xsList.get(indextoRemove)+","+ysList.get(indextoRemove)+"] , ");
				System.out.println(distance + " away");
				cost+=distance;
				distance=1000000;
				startHTML+="["+xsList.get(indextoRemove)+","+ysList.get(indextoRemove)+"],\n";
				
				
				
				xsList.remove(indextoRemove);
				ysList.remove(indextoRemove);
			
			
		}
		
		
		
//		System.out.println(startHTML);
		String aaa= "<html>\n" + 
				"  <head>\n" + 
				"    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" + 
				"    <script type=\"text/javascript\">\n" + 
				"      google.charts.load('current', {'packages':['corechart']});\n" + 
				"      google.charts.setOnLoadCallback(drawChart);\n" + 
				"      function drawChart() {\n" + 
				"        var data = google.visualization.arrayToDataTable([\n" + 
				"          ['X', ''],\n";
		aaa+=startHTML;
		String bbb= "]);\n" + 
				"\n" + 
				"        var options = {\n" + 
				"          title: 'Traveling Salesman Solved Nodes ("+fileName+") - Cost: "+cost+"',\n "+ 
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
				"  </body>\n" + 
				"</html>";
		aaa+=bbb;
//		System.out.println(startHTML);
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream("/htmlFiles/nearestNeighbor.html", false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found, making file.");
			PrintWriter writer = new PrintWriter("/htmlFiles/nearestNeighbor.html", "UTF-8");
			writer.close();
		}
		outputStream.println(aaa);
		outputStream.close();
		
	}
	
	public double distance(int x1,int x2,int y1,int y2) {
		double distance = (Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
		return distance;
	}
}
