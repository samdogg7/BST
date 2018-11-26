/**StockDriver will execute the main methods.
Author: Sam Doggett
Assignment: Stock
Class: CS 2 ("Data Structures")
*/
import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class StockDriver {
    private ArrayList<Stock> stock_list;
    private ArrayList<Stock> price_update_list;
    private TreeSet<Stock> stock_tree;
    private Scanner scan;
    private boolean price_has_changed = false;
    private boolean arraylist_done = false;
    private long startTime;
    private long endTime;
    private String output_file = "";
    //total Execution time for each type
    private long arraylist_time = 0;
    private long bst_time = 0;

    //this whole constructor scans the files to stock_list and price_update_list (both are <Stock>)
    public StockDriver() {
        //create the arraylists to store the objects
        stock_list = new ArrayList<Stock>();
        price_update_list = new ArrayList<Stock>();

        //take in user input to have them input the output_file
        scan = new Scanner(System.in);
        System.out.print("Please enter the output file name (do not include file extension): ");
        output_file = scan.nextLine();
        output_file = output_file.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        System.out.println();

        scan.close();

        startTime = System.currentTimeMillis();
        try {
            File shuffled_stocks = new File("shuffled_stocks.csv");
            scan = new Scanner(shuffled_stocks);

            //Scan the shuffled_stocks.csv files and create new stocks
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                //remove all white space
                line = line.replace("\"", "");
                //split by comma
                String[] split_line = line.split(",");
                String curr_ticker = split_line[0];
                float curr_price = Float.parseFloat(split_line[1]);
                Stock new_stock = new Stock(curr_ticker, curr_price, 0.0f);
                stock_list.add(new_stock);
            }

            scan.close();

            File price_updates = new File("price_updates.txt");
            scan = new Scanner(price_updates);
            //Scan the price_updates.txt file and create new stocks
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                //remove all white space
                line = line.replace("\"", "");
                //split by comma
                String[] split_line = line.split("\\s+");
                String curr_ticker = split_line[0];
                float updated_price = Float.parseFloat(split_line[1]);
                Stock new_stock_change = new Stock(curr_ticker, -999, updated_price);
                price_update_list.add(new_stock_change);
            }

            scan.close();
            endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Scan Execution Time: " + totalTime + " millis\n");

            //start the arraylist process
            arraylist_process();
            System.out.println("\nSwitching data structure from ArrayList to BST...\n");
            //start the BST process
            bst_process();

            System.out.println("\nSee output files for full lists.");
            System.out.println("\nArray List Execution Time: " + arraylist_time + " millis");
            System.out.println("\nBST Execution Time: " + bst_time + " millis");
            float speed_difference = arraylist_time/bst_time;
            System.out.println("\nBST is " + speed_difference + " times faster than the ArrayList method!");
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
    //this will update the prices with an arraylist type method
    public void arraylist_process() {
        //print the unsorted arraylist
        printStock("ArrayList");

        //new start time for ArrayList exec
        startTime = System.currentTimeMillis();
        //for each stock, get the ticker, and get each price update ticker and compare, if equal, then update price
        for(Stock curr_stock : stock_list) {
            String current_ticker = curr_stock.getTicker();
            for(Stock curr_update : price_update_list) {
                if(curr_update.getTicker().equals(current_ticker)) {
                    curr_stock.updatePrice(curr_update.getPriceChange());
                }
            }
        }

        endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        arraylist_time = totalTime;

        //print the updated arraylist
        printStock("ArrayList");

        //write out the arraylist to file
        writeOut("ArrayList");
        arraylist_done = true;
    }
    public void bst_process() {
        //do a quick reset of all the prices
        for(Stock curr_stock : stock_list) {
            curr_stock.resetPrice();
        }

        stock_tree = new TreeSet<Stock>(new TickerComp());

        //add all of the noraml starting stocks to the tree
        for(Stock curr_stock : stock_list) {
            stock_tree.add(curr_stock);
        }
        //print the non-updated BST
        printStock("BST");

        //new start time for BinarySearchTree price update
        startTime = System.currentTimeMillis();
        //add the price changes to the list, however this should never happen because in the ticker comp, it will deny the addition
        for(Stock curr_update : price_update_list) {
            //the price updates will not actually be added! in my TickerComp,
            //I made it so that it will always return 0 (only when adding something with a price_change)
            stock_tree.add(curr_update);
        }
        endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        bst_time = totalTime;

        //print the updated BST
        printStock("BST");

        //write out the arraylist to file
        writeOut("BST");
    }
    /**@param data_type string val for simple print stuff
    */
    public void printStock(String data_type) {
        if(price_has_changed == false) {
            System.out.println("----------------------------------------");
            System.out.println("Non-Updated Stocks ("+data_type+"):\n");
            price_has_changed = true;
        } else {
            System.out.println("Updated Stocks ("+data_type+"):\n");
            price_has_changed = false;
        }
        //if the arraylist stocks need to be printed
        if(arraylist_done == false) {
            for(int i=0; i<10; i++) {
                System.out.println("Stock: " + stock_list.get(i).getTicker() + " Price: " + stock_list.get(i).getPrice());
            }
        //arraylist_done will equal true in the arraylist_process() method
        //if the stock_tree needs to be printed
        } else {
            int count = 1;
            for (Stock curr_stock : stock_tree) {
                System.out.println("Stock: " + curr_stock.getTicker() + " Price: " + curr_stock.getPrice());
                count++;
                //only print the first ten
                if(count == 10) {
                    break;
                }
            }
        }
        System.out.println("----------------------------------------");
    }
    /**@param data_type is another simple print string
    */
    public void writeOut(String data_type) {
        try{
            if(arraylist_done == false) {
                //create a file w the output_file + the data type
                BufferedWriter file_writer = new BufferedWriter(new FileWriter(output_file+"_"+data_type+".csv", true));
                //for each stock in the arraylist, write data
                for(Stock curr_stock : stock_list) {
                    String temp = Float.toString(curr_stock.getPrice());
                    file_writer.write("\"");
                    file_writer.write(curr_stock.getTicker());
                    file_writer.write("\",");
                    file_writer.write("\"");
                    file_writer.write(temp);
                    file_writer.write("\"");
                    file_writer.newLine();
                }
            } else {
                //create a file w the output_file + the data type
                BufferedWriter file_writer = new BufferedWriter(new FileWriter(output_file+"_"+data_type+".csv", true));
                //for each stock in the arraylist, write data
                for(Stock curr_stock : stock_tree) {
                    String temp = Float.toString(curr_stock.getPrice());
                    file_writer.write("\"");
                    file_writer.write(curr_stock.getTicker());
                    file_writer.write("\",");
                    file_writer.write("\"");
                    file_writer.write(temp);
                    file_writer.write("\"");
                    file_writer.newLine();
                }
                file_writer.close();
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    /**@param args nothin special
    */
    public static void main(String[] args) {
        System.out.println("\n#####################");
        System.out.println("# NASDAQ Change Sim #");
        System.out.println("#####################\n");
        StockDriver driver = new StockDriver();
    }
}
