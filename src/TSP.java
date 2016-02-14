import java.awt.Desktop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TSP extends JFrame{
	static boolean openBrowserURL=true;
	static ArrayList<String> cities = new ArrayList<String>(), cityX = new ArrayList<String>(),cityY = new ArrayList<String>(), cityVisited = new ArrayList<String>();
	static double distance, x1, y1, x2, y2, currentHighestY = 1000, currentHighestX = 0;
	static int indexToRemove = 0, totalsum = 0;
	static String mapsURL = "https://www.google.com/maps/dir/", line = "", airportChoice = "temp",allAirports_txt = "allAirports.txt", cities_txt = "cities.txt";
	static final boolean[] next= {true};
	
	/*
	Variable Documentation
	 boolean openBrowserURL - boolean to open maps in browser
	 boolean[] next			- boolean for inputting another airport
	 ArrayList cities       - list with the actual airport codes (eg. IAD,LAX)
	 ArrayList cityX        - X coordinate of city matching the cities
	 ArrayList cityY        - Y coordinate of city matching the cities
	 ArrayLsit cityVisited  - Strings of airport codes that have been flagged as visited
	 double x1,x2,y1,y2     - Latitudes and longitudes to find distance()
	 double currentHighestY - Current Y coordinate of city to find distance() to next city to find closest city
	 double currentHighestX - Current X coordinate of city to find distance() to next city to find closest city
	 int indextoRemove      - index of ArrayList of city and city coordinates to remove using remove()
	 int totalsum           - Total distance in miles of complete NN
	 String mapsURL         - Google Maps URL to open in browserAPI()
	 String line            - BufferedReader line reading from allAirports.txt
	 String airportChoice   - String gotten from user input of airport codes
	 String allAirports_txt - String name to refer to allAirports.txt
	 String cities_txt      - String name to refer to cities.txt
	 */

	public static void main(String[] args) throws IOException {
		start();
		long starTime = System.currentTimeMillis();
		readFromFile();
		NearestNeighbor();
		long finTime = System.currentTimeMillis() - starTime;
		System.out.println("\n" + "NN took " + finTime + " milliseconds to solve for " + cityVisited.size()+ " cities for a total flying distance of " + totalsum + " miles");
		System.exit(0);
	}

	public static void start() throws IOException {
		String[] tempPresentDataOptions = { "Yes, overwrite", "No, use current data" };

		BufferedReader brTemp = null;
		brTemp = new BufferedReader(new FileReader(cities_txt));
		// finished initializing variables

		if (brTemp.readLine() != null) {
			JFrame frame = new JFrame();
			String overwriteChoice = (String) JOptionPane.showInputDialog(frame,
					"File seems to have previous cities in the file" + "\n"
							+ "Overwrite current data or use current data?",
					"Travelling Salesman Shortest Route", JOptionPane.QUESTION_MESSAGE, null, tempPresentDataOptions, tempPresentDataOptions[1]);
			brTemp.close();

			if (overwriteChoice.equals("Yes, overwrite")) {
				promptAirport();

			} else if (overwriteChoice.equals("No, use current data")) {
				return;
			}
		}
	}

	public static boolean anotherAirport() {
		Object[] options = {"Yes","No"};
		int choice = JOptionPane.showOptionDialog(
				null,
				"Do you want to input another airport?", 
				"Choose one", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.INFORMATION_MESSAGE,
				null,
				options,
				options[0]);
		
		if(choice==0) {
			next[0]=true;
		}
		else {
			next[0]=false;
		}
		return next[0];
	}
	
	public static void promptAirport() throws IOException {
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(cities_txt, false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		outputStream.println("."); // adding period at the start because idk why
									// the program starts on line 2

		while (next[0]) {
			airportChoice = JOptionPane.showInputDialog("What airport do you want to visit?");

			BufferedReader br = null;
			br = new BufferedReader(new FileReader(allAirports_txt));

			
			while ((line = br.readLine()) != null) {
				try {
					airportChoice = airportChoice.toUpperCase();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

				if (line.contains(airportChoice)) {
					System.out.println(line);
					outputStream.println(line);
				}
			}
			anotherAirport();
			br.close();
		}
		outputStream.close();
	}

	public static void readFromFile() throws IOException {

		String line = "", cvsSplitBy = ",";
		BufferedReader br = null;

		br = new BufferedReader(new FileReader(cities_txt));
		if (br.readLine() == null) {
			System.err.println("System airport file is empty! Solving default airports written to file. " + "\n");
			PrintWriter outputStream = null;
			try {
				outputStream = new PrintWriter(new FileOutputStream(cities_txt, false));
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}

			outputStream.println(
					"." + "\n" + "IAD,38.9475,77.4600" + "\n" + "DFW,32.8969,97.0381" + "\n" + "LAX,33.9425,118.4072");
			outputStream.close();
			br.readLine();
		}
		while ((line = br.readLine()) != null) {
			// use comma as separator
			String[] country = line.split(cvsSplitBy);
			// array split into [city,x,y]

			cities.add(country[0]);
			cityX.add(country[1]);
			cityY.add(country[2]);
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void NearestNeighbor() {
		findFirstCity();

		while (cities.size() != 1) { // stop when there's only one city left
			double compareTo = 10000;
			for (int i = 0; i <= cities.size() - 1; i++) {
				x2 = Double.parseDouble(cityX.get(i));
				y2 = Double.parseDouble(cityY.get(i));
				double DistancefromCurrenttoNext = distance(currentHighestX, x2, currentHighestY, y2, 1, 1);
				System.out.println(cityVisited.get(cityVisited.size() - 1) + " to " + cities.get(i) + ": "
						+ DistancefromCurrenttoNext + " miles");
				if (DistancefromCurrenttoNext < compareTo) {
					compareTo = DistancefromCurrenttoNext;
					indexToRemove = i;
				}
			}

			System.out.println("Next city: " + cities.get(indexToRemove) + " which is " + compareTo + " miles away");
			totalsum += compareTo;
			cityVisited.add(cities.get(indexToRemove)); // adding element to the list of cities visited
			compareTo = 10000; // resetting the compareto value

			//Set the highest X,Y value to the city that was found to be the closest city
			currentHighestX = Double.parseDouble(cityX.get(indexToRemove));
			currentHighestY = Double.parseDouble(cityY.get(indexToRemove));

			printData();
		}
		
		//Final city doesnt need any calculations
		cityVisited.add(cities.get(0));
		totalsum += distance(currentHighestX, Double.parseDouble(cityX.get(0)), currentHighestY,
				Double.parseDouble(cityY.get(0)), 1, 1);
		System.out.println("Final path: " + cityVisited);
		System.out.println(cityVisited.size() + " cities visited");

		if (openBrowserURL) 
			BrowserAPI(makeURL());
	}

	public static void findFirstCity() {
		// finding closest city to the east coast to use as starting city

		cityVisited.add(null); // adding empty first space to List to be filled
								// by to be found city
		for (int i = 0; i <= cities.size() - 1; i++) {
			double tempCityY = Double.parseDouble(cityY.get(i));
			if (tempCityY < currentHighestY) {
				currentHighestY = tempCityY;
				currentHighestX = Double.parseDouble(cityX.get(i));
				cityVisited.set(0, cities.get(i));
				indexToRemove = i;

				// setting base coords for main NN
				x1 = Double.parseDouble(cityX.get(i));
				y1 = Double.parseDouble(cityY.get(i));
			}
		}

		removeFrom(indexToRemove);
		System.out.println(cityVisited.get(0) + " is closest to the East Coast at " + currentHighestY + "W, "
				+ currentHighestX + "N");
		System.out.println(cityVisited.get(0) + " is the starting city");
		System.out.println(cities + " are the cities left in no particular order");
		System.out.println("Current path: " + cityVisited+"\n");

	}

	public static String makeURL() {
		// Making Maps URL
		for (int i = 0; i <= cityVisited.size() - 1; i++) {
			mapsURL += cityVisited.get(i) + "airport/";
		}
		System.out.println("\n" + mapsURL);
		return mapsURL;
	}

	public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
		/*
		 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2 End altitude in meters
		 * returns Distance in Miles
		 */
		DecimalFormat myFormatter = new DecimalFormat("#.00");
		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double distance = R * c * 1000; // convert to meters
		double height = el1 - el2;

		String distance1 = myFormatter.format(.00062137 * (Math.sqrt(Math.pow(distance, 2) + Math.pow(height, 2)))); 
		distance = Double.parseDouble(distance1);

		return distance;

	}

	public static void printData() {
		removeFrom(indexToRemove); // removing nearest city's data from ArrayList
		System.out.println("Current path: " + cityVisited);
		System.out.println(cities + " are the cities left");
		System.out.println(cityVisited.size() + " cities visited" + "\n");
	}
	
	public static void BrowserAPI(String link) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URL(link).toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeFrom(int x) {
		cities.remove(x);
		cityY.remove(x);
		cityX.remove(x);
	}
}


