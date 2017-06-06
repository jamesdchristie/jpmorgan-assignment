package com.supersimplestocks;

import com.supersimplestocks.enums.Operations;
import com.supersimplestocks.enums.StockData;
import com.supersimplestocks.enums.StockSymbolEnum;
import com.supersimplestocks.enums.TransactionType;
import com.supersimplestocks.exception.SuperSimpleStocksException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Super Simple Stocks Application
 *
 * Allows a user to create trades, and perform various calculations on Stocks
 *
 * developed as a command line application for the purposes of this exercise
 * but in the real world the interface would be a GUI or a web service
 *
 * As it is a command line application, there is no logging facility
 * as all output goes to the command line
 *
 * Created by James Christie on 05/06/2017.
 */
public class SuperSimpleStocksApp {

    private static Scanner scanner;
    private final static int minutes = 15;

    /**
     * Main processing loop that receives input from user
     * and creates trades or performs calculations
     *
     * @param args none
     */
    public static void main(String[] args) {

        System.out.println("Super Simple Stock Application");
        System.out.println("Please choose from the following Operations:");
        printMenu();

        scanner = new Scanner(System.in);
        String selection;
        StockTradeHistory stockTradeHistory = new StockTradeHistory();
        StockSymbolEnum stockSymbol;
        int price;

        while(true){

            selection = scanner.nextLine();

            try {

                Operations selectedOperation = Operations.getOperationFor(selection);

                switch(selectedOperation){
                    case DY:
                        stockSymbol = getStockSymbolFromUser();
                        price = getMarketPriceFromUser();
                        calculateDividendYield(stockSymbol, price);
                        break;
                    case PE:
                        stockSymbol = getStockSymbolFromUser();
                        price = getMarketPriceFromUser();
                        calculatePriceEarnngsRatio(stockSymbol, price);
                        break;
                    case T:
                        TransactionType transactionType = getTransactionTypeFromUser();
                        stockSymbol = getStockSymbolFromUser();
                        BigDecimal quantity = getStockQuantityFromUser();
                        price = getTradePriceFromUser();
                        createTradeForStock(stockSymbol, quantity, transactionType, new BigDecimal(price), stockTradeHistory);
                        break;
                    case VWSP:
                        stockSymbol = getStockSymbolFromUser();
                        calculateVolumeWeightedStockPriceForSingleStock(stockTradeHistory, stockSymbol);
                        break;
                    case GBCE:
                        calculateGBCEAllShareIndex(stockTradeHistory);
                        break;
                    case Q:
                        System.out.println("Quitting");
                        scanner.close();
                        System.exit(0);

                }
            } catch (SuperSimpleStocksException e) {

                System.out.println("An Exception occurred....");
                System.out.println(e.getMessage());
                System.out.println("");
                printMenu();
            }
        }

    }

    /**
     * Displays to the user what Operations they can perform
     */
    private static void printMenu() {

        System.out.println("DY: Calculate Dividend Yield");
        System.out.println("PE: Calculate P/E Ratio");
        System.out.println("T: Record a Trade");
        System.out.println("VWSP: Calculate Volume Weighted Stock Price");
        System.out.println("GBCE: Calculate GBCE All Share Index");
        System.out.println("Q: Quit");
    }

    /**
     * Asks the user for a Stock Symbol and returns
     * the stock Symbol that the user has entered
     *
     * @return Stock Symbol
     * @throws SuperSimpleStocksException if Stock Symbol is not recognised
     */
    private static StockSymbolEnum getStockSymbolFromUser() throws SuperSimpleStocksException {

        System.out.println("Please input stock symbol");

        String enteredStockSymbol = scanner.nextLine();

        return StockSymbolEnum.getStockSymbolFor(enteredStockSymbol);
    }

    /**
     * Asks the user for a Market Price in pence and returns
     * the price that the user has entered
     *
     * @return Market Price
     * @throws SuperSimpleStocksException if price entered is not a positive Integer
     */
    private static int getMarketPriceFromUser() throws SuperSimpleStocksException {

        System.out.println("Please input Market Price in Pence");

        return getPriceFromUser();
    }

    /**
     * Asks the user for a Trade Price in pence and returns
     * the price that the user has entered
     *
     * @return Trade Price
     * @throws SuperSimpleStocksException if price entered is not a positive Integer
     */
    private static int getTradePriceFromUser() throws SuperSimpleStocksException {

        System.out.println("Please input Trade Price in Pence");

        return getPriceFromUser();
    }

    /**
     * Gets the price entered by the user and checks to see if the value
     * entered is a positive Integer
     *
     * @return price
     * @throws SuperSimpleStocksException if price entered is not a positive Integer
     */
    private static int getPriceFromUser() throws SuperSimpleStocksException {

        String enteredMarketPrice = scanner.nextLine();
        int price;

        try {

            price = Integer.parseInt(enteredMarketPrice);

        }catch (NumberFormatException e){

            throw new SuperSimpleStocksException("Price must be an Integer, value entered was "+enteredMarketPrice);
        }

        if(price < 0){

            throw new SuperSimpleStocksException("Price must be a positive Integer, value entered was "+enteredMarketPrice);
        }
        else{

            return price;
        }
    }

    /**
     * Asks the user for a quantity value to 2 decimal places
     * and returns the value entered
     *
     * @return quantity
     * @throws SuperSimpleStocksException if value entered is not a number
     */
    private static BigDecimal getStockQuantityFromUser() throws SuperSimpleStocksException {

        System.out.println("Please input quantity of shares to maximum 2 decimal places");

        String quantity = scanner.nextLine();
        BigDecimal quantityAsBigDecimal;

        try{

            quantityAsBigDecimal = new BigDecimal(quantity);

        }catch (NumberFormatException e){

            throw new SuperSimpleStocksException("Quantity must be a decimal number to maximum 2 decimal places. Value entered was "+quantity);
        }

        if(quantityAsBigDecimal.scale() > 2){

            //If the value entered has more than 2 decimal places, truncate the value to 2 decimal places
            quantityAsBigDecimal = quantityAsBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

            System.out.println("Maximum allowed scale is 2 decimal places, quantity to be used for trade has been set to "+quantityAsBigDecimal);
        }

        return quantityAsBigDecimal;
    }

    /**
     * Asks the user to enter a transaction type
     * then returns the transaction type entered
     *
     * @return transaction type
     * @throws SuperSimpleStocksException if value entered is not BUY or SELL
     */
    private static TransactionType getTransactionTypeFromUser() throws SuperSimpleStocksException{

        System.out.println("is this trade BUY or SELL?");

        String enteredTransactionName = scanner.nextLine();

        return TransactionType.getTransactionTypeFor(enteredTransactionName);

    }

    /**
     * Makes a call to the CalculationUtility Class to calculate
     * the Dividend Yield and displays the result
     *
     * @param stockSymbol Stock Symbol
     * @param marketPrice Market Price
     * @throws SuperSimpleStocksException if exception occurred during the calculation
     */
    private static void calculateDividendYield(StockSymbolEnum stockSymbol, int marketPrice) throws SuperSimpleStocksException{

        System.out.println("Calculating Dividend Yield for Stock "+ stockSymbol.getSymbol() + " and Market Price "+marketPrice);

        StockData stockData = StockData.getStockDataForSymbol(stockSymbol);

        System.out.println("Result = " + CalculationUtility.calculateDividendYield(stockData, marketPrice));

        printContinuation();
    }

    /**
     * Makes a call to the CalculationUtility Class to calculate
     * the Price Earnings Ratio and displays the result
     *
     * @param stockSymbol Stock Symbol
     * @param marketPrice Market Price
     * @throws SuperSimpleStocksException if exception occurred during the calculation
     */
    private static void calculatePriceEarnngsRatio(StockSymbolEnum stockSymbol, int marketPrice) throws SuperSimpleStocksException{

        System.out.println("Calculating Price Earnings Ratio for Stock "+ stockSymbol.getSymbol() + " and Market Price "+marketPrice);

        StockData stockData = StockData.getStockDataForSymbol(stockSymbol);

        System.out.println("Result = " + CalculationUtility.calculatePriceEarningsRatio(stockData, marketPrice));

        printContinuation();
    }

    /**
     * Creates and stores a new trade for a given Stock
     *
     * @param stockSymbol StockSymbol to be traded
     * @param quantity how many shares of the stock to be traded
     * @param transactionType BUY or SELL
     * @param price per share in pence
     * @param stockTradeHistory current list of all trades for all Stocks
     */
    private static void createTradeForStock(StockSymbolEnum stockSymbol, BigDecimal quantity, TransactionType transactionType, BigDecimal price, StockTradeHistory stockTradeHistory){

        //Timestamp of trade
        LocalDateTime now = LocalDateTime.now();

        //Create the new Trade
        StockTrade stockTrade = new StockTrade(transactionType, stockSymbol, now, quantity, price);

        //Add the new Trade to the current list of all trades for all Stocks
        stockTradeHistory.addTrade(stockTrade);

        System.out.println("New Trade added:");
        System.out.println("Transaction Type: " + transactionType + " Stock Symbol: "+ stockSymbol + " quantity: " + quantity + " TimeStamp: "+ now +" Price: " + price);

        printContinuation();

    }

    /**
     * calculates and displays the Volume Weighted Stock Price for a given Stock
     * that has been traded in the last number of minutes given
     * (currently defaulted to 15 minutes)
     *
     * @param stockTradeHistory List of all trades for all Stocks
     * @param stockSymbol to perform the calculation on
     * @throws SuperSimpleStocksException if exception occurred during the calculation
     */
    private static void calculateVolumeWeightedStockPriceForSingleStock(StockTradeHistory stockTradeHistory, StockSymbolEnum stockSymbol) throws SuperSimpleStocksException{

        System.out.println("Calculating Volume Weighted Stock Price for all trades for " + stockSymbol + " in the last " + minutes + " minutes");
        System.out.println("Result = " + CalculationUtility.calculateVolumeWeightedStockPrice(stockTradeHistory.getStockTradeHistoryListForSymbol(stockSymbol), minutes));

        printContinuation();
    }

    /**
     * Calculates and displays the GBCE All Shares Index
     *
     * @param stockTradeHistory List of all trades for all Stocks
     * @throws SuperSimpleStocksException if exception occurred during the calculation
     */
    private static void calculateGBCEAllShareIndex(StockTradeHistory stockTradeHistory) throws SuperSimpleStocksException{

        System.out.println("Calculating GBCE All Share Index");
        System.out.println("Result = " + CalculationUtility.calculateGBCE(stockTradeHistory.getStockTradeHistoryListForAllStocks()));

        printContinuation();
    }

    /**
     * Helper method to inform the user that they can perform another operation
     */
    private static void printContinuation(){

        System.out.println("");
        System.out.println("Please select another Operation");

        printMenu();
    }



}
