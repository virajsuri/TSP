import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class FindAirport {
	public static void main(String[] args) throws IOException {

		
		String line = "", cvsSplitBy = ",", airportChoice = "temp", csvFile = "allairports.txt";
		String [] country;
		String url = "https://www.google.com/maps/dir/";
		
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream("cities.txt", false));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		
		// finished initializing variables

		while (!airportChoice.equals(null)) {
			airportChoice = JOptionPane.showInputDialog("What airport do you wanna go to");
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(csvFile));

			while ((line = br.readLine()) != null) {
				try {
					airportChoice=airportChoice.toUpperCase();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				// use comma as separator
			//	country = line.split(cvsSplitBy);

				// array split into city,x,y
				
				if (line.contains(airportChoice)) {
					System.out.println(line);
					outputStream.println(line);
					url+=airportChoice+"airport/";
					System.out.println(url);
				}
			}
			br.close();
		}
		if (airportChoice.equals(null)) {
			return;
		}
		outputStream.close();
	}
}
