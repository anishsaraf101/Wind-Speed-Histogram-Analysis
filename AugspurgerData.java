import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
/** Represents data collection and the making of a histogram for wind speeds collected from a csv file.
 * This class contains several places for user input, but is mostly in place for data collection.
 * @author Anish Saraf
 * @version 1.0
 * @since 2023-12-13
 */
public class AugspurgerData {

    Scanner scnr = new Scanner(System.in); // new scanner object
    Bin[] histogram = new Bin[200]; // initializes new array of 200 empty bin objects
    double k; // member variable k used in various methods; to do with cumulative probability


    /** Asks the user for what they'd like to set interval length as this shapes the rest of the histogram building.
     * Also, sets up a histogram data value prepared with populated Bin objects.
     */
    public void binInitialization() throws FileNotFoundException {
        float userDefinedInterval = 0; // new variable created to be updated by user for the amount they'd like each interval in histogram
        try {
            System.out.println("What is the width of each interval you would like the program to use to compute the" +
                    " cumilative probability density (50 - 100):");
            userDefinedInterval = scnr.nextFloat();
        } catch (InputMismatchException nonFloatError) {
            System.out.println("Error: non number entered... Please try again");
            binInitialization();
        }
        while (userDefinedInterval < 50 || userDefinedInterval > 100) { // prompts the user to keep going if interval not in range
            System.out.println("Error: a number less than 50 or greater than 100 was entered... Please try again");
            userDefinedInterval = scnr.nextFloat();
        }
        PrintWriter writer = new PrintWriter(new File("userDefinedIntervalValue.txt"));
        writer.println(userDefinedInterval); // write the user defined interval to a new file for use via JavaFX later
        writer.close();

        int histogramCurrIndex = 0; // used to keep track of where to add elements in histogram array
        for (float i = 1; i <= 200; i++) { // adds new bin objects to histogram array
            histogram[histogramCurrIndex] = new Bin((i * userDefinedInterval), 0, 0);
            histogramCurrIndex++;
        }

    }

    /** This method is responsible for most of the work done for the data being read in. First, the method reads in
     * the data from the user inputted CSV file. Then, the method updates each and every bin value in the histogram
     * with respect to count and cumulative probability. Lastly, the method collects data for ordinary least squares
     * regression analysis.
     */
    public void augspurgerDataRead() throws FileNotFoundException {
        int index = 0; // used to keep track of where to add elements in windValues array
        float[] windValues = new float[9000]; // new array of floats full of wind speed values from csv file
        System.out.println("Please enter the csv file you'd like to gather data from");
        String userFile = scnr.next();
        Scanner s = null;
        try {
            File file = new File(userFile);
            s = new Scanner(file); // creates relationship between scanner and user inputted csv file
        } catch (FileNotFoundException ex) {
            System.out.println("Error: file not found... Please try again");
            augspurgerDataRead();
        }
        for (int i = 0; i < 7; ++i) { // skips the first 7 lines as these don't contain information in the file
            String nextLine = s.nextLine();
//            System.out.println(nextLine);
        }
        while (s.hasNextLine()) { // only keep reading file if there is another line
            String nextLine = s.nextLine();
            String[] splittedLine = nextLine.split(","); // use commas to split up each line in csv formatted file
            float windSpeedValue = Float.parseFloat(splittedLine[5]);
            if (windSpeedValue > -99999 && windSpeedValue < 99999) { // checks if the windSpeedValue variable is a number
                windValues[index] = windSpeedValue; // add data points from file to array named windValues
                index++; // increment index value for each value added
            } else {
                System.out.println("Error in adding data: '" + windSpeedValue + "' is not a number");
            }
        }
        System.out.println("\nSuccessfully added meaningful data");

        float numWindValues = index; // total number of wind speed values in the array
        int windValueSquaredIndex = 0; // holds an index value for windValuesSquared array to keep track fo where to add new elements
        float[] windValuesSquared = new float[9000]; // creates a new array of wind values squared
        for (float windSpeedValue : windValues) { // for loop updates windValuesSquared array based on each element in windValues array
            windValuesSquared[windValueSquaredIndex] = windSpeedValue * windSpeedValue;
//            System.out.println(windValuesSquared[windValueSquaredIndex]);
            windValueSquaredIndex++;
        }
        for (int i = 0; i < numWindValues; i++) { // Update each bin object in the histogram to have the correct count of wind speed squared values
            for (int j = 0; j < 200; j++) {
                if (windValuesSquared[i] <= histogram[0].getInterval()) {
                    histogram[0].setCount(histogram[0].getCount() + 1);
                    break;
                }
                if ((windValuesSquared[i] >= histogram[j].getInterval()) && (windValuesSquared[i] < histogram[j + 1].getInterval())) { // add a count to a bin if and only if the wind speed squared value is in its bound
                    histogram[j].setCount(histogram[j].getCount() + 1);
                    break;
                }
            }
        }
        Float cumalativeProb = new Float(1.0); // starts off the cumaltive probability at 1
        for (int j = 0; j < 200; j++) { // cycles through each bin in the histogram array
            cumalativeProb = (cumalativeProb - (histogram[j].getCount() / numWindValues)); // 1 - everything before - relative proportion of values stored in the j'th bin
            if (cumalativeProb < 0.0) { // cumulative probability can't go below 0
                break;
            }
            histogram[j].setCumProbability(cumalativeProb); // saves the cumulative probability to the respective histogram bin
        }
        PrintWriter writer = new PrintWriter(new File("cumProbability.txt")); // creates a relationship to be able to write out a new file named cumProbability
        for (int i = 0; i < 200; i++) {
            writer.print(i + ", " + histogram[i].getCumProbability()); // writes out the number of each bin followed by its cumulative probability
            writer.println(); // writing this out to file is done for use via JavaFX later on
        }
        writer.close();


        double num = 0;
        double den = 0;
        for (int j = 0; j < 200; j++) { // uses every impactful histogram's cumulative probability value to calculate equations for num and den
            if (histogram[j].getCumProbability() <= 0.01) { // makes sure the lowest 1% of cumulative probability is not included in calculations
                histogram[j].setCumProbability(0); // if the cumulative probability of a bin is <= 0.01, it gets set to 0
                continue;
            }
            num = (num - Math.log((histogram[j].getCumProbability())));
            den = den + histogram[j + 1].getInterval();
        }
        k = num / den;
        PrintWriter writer1 = new PrintWriter(new File("kValue.txt")); // creates a relationship to be able to write out a new file named kValue
        writer1.println(k); // writes simply the k value in the file
        writer1.close();  // again, this is for use via JavaFX later
        s.close(); // close scanner
    }

    /** Gives the user an option to calculate data for the percentage of wind speeds greater than/ equal to or below
     * a wind speed value of their choosing. Data comes from the CSV file previously inputted.
     */
    public void userChoice() {
        System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
        String userOption = scnr.next();
        while (!(userOption.equals("less") || (userOption.equals("greaterEq") || (userOption.equals("q"))))) { // user has to enter an input until it matches one of the three options given
            System.out.println("\nError: invalid choice entered. Please try again."); // shows error if user does not enter correct input
            System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
            userOption = scnr.next();
        }
        while (!(userOption.equals("q"))) { // while user doesn't enter q
            System.out.println("Enter wind speed: ");
            double inputWindSpeed = scnr.nextFloat(); // user enters a wind speed to get greater than/ equal to or less than the windspeed data
            switch (userOption) { // computer makes a choice based on the userOption choice
                case "less" :
                    double lessProbability = (1.0 - Math.exp(-1.0 * k * (inputWindSpeed * inputWindSpeed))); // computer uses this equation to find the prob. of the windspeed being less than inputted number
                    System.out.println("Probability wind speed < " + inputWindSpeed + " is " + lessProbability);
                        break;
                case "greaterEq":
                    double greaterEqProbability = Math.exp(-1.0 * k * (inputWindSpeed * inputWindSpeed));  // computer uses this equation to find the prob. of the windspeed being greater/equal to than inputted number
                    System.out.println("Probability wind speed >= " + inputWindSpeed + " is " + greaterEqProbability);
                        break;
            }
            System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
            userOption = scnr.next();
            while (!(userOption.equals("less") || (userOption.equals("greaterEq") || (userOption.equals("q"))))) { // user has to enter an input until it matches one of the three options given
                System.out.println("\nError: invalid choice entered. Please try again."); // shows error if user does not enter correct input
                System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
                userOption = scnr.next();
            }

        }
        scnr.close();
    }

    /** Main method to call each of the methods in the class systematically.
     * @param args A string array containing the command line arguments.
     */
    public static void main(String[] args) throws FileNotFoundException {
        AugspurgerData testingTest = new AugspurgerData();
        testingTest.binInitialization();
        testingTest.augspurgerDataRead();
        testingTest.userChoice();
    }
}
