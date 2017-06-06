package com.supersimplestocks;

import com.supersimplestocks.enums.StockData;
import com.supersimplestocks.exception.SuperSimpleStocksException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides static methods for performing various calculations for the
 * requirements of the Super Simple Stocks application
 *
 * Created by James Christie on 05/06/2017.
 */
class CalculationUtility {

    /**
     * Calculates and returns the P/E Ratio
     *
     * Formula used is: market Price/Dividend  (assumed Dividend is the Last Dividend)
     *
     * @param stockData Data relating to the Stock
     * @param price market price
     * @return Calculated P/E ratio to 2 Decimal Places
     * @throws SuperSimpleStocksException if there would be a divide by zero Arithmetic Exception
     */
    static BigDecimal calculatePriceEarningsRatio(StockData stockData, int price) throws SuperSimpleStocksException{

        //Convert Last Dividend value to BigDecimal so easier to perform mathematical operations on
        BigDecimal dividendAsBigDecimal = new BigDecimal(stockData.getLastDividend());

        if (dividendAsBigDecimal.compareTo(BigDecimal.ZERO) == 0){
            throw new SuperSimpleStocksException("Cannot calculate PE Ratio as last Dividend for "+ stockData.getSymbol() +" is zero" +
                    " and would result in a divide by zero Arithmetic Exception");
        }

        BigDecimal priceAsBigDecimal = new BigDecimal(price);

        return priceAsBigDecimal.divide(dividendAsBigDecimal, 2, BigDecimal.ROUND_HALF_UP); //Assume 2 dp is sufficient precision
    }

    /**
     * Calculates and returns the Volume Weighted Stock Price for the given Stock Transaction List
     * on trades that have occurred in the last number of minutes given
     *
     * Formula used is: Sum of (Quantity * Trade price) / Sum of Quantity
     *
     * @param stockTradeList List of transactions
     * @param minutes Only trades that have occurred in the last number of minutes set will be used in the calculation
     * @return Calculated Volume weighted Stock Price to 2 Decimal Places,
     *         or 0 if no trades have happened in the last number of minutes given
     */
     static BigDecimal calculateVolumeWeightedStockPrice(List<StockTrade> stockTradeList, long minutes) {

        //Set time to start including trades for calculation
        LocalDateTime calculationTime = LocalDateTime.now().minusMinutes(minutes);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal sumOfTradePriceMultipliedByQuantity = BigDecimal.ZERO;


        // Only consider trades that have happened in the last X minutes
        // Build up a sum of Quantity * Trade Price, and a sum of Quantity
        for(StockTrade stockTrade : stockTradeList){

            if(stockTrade.getTimeStamp().isAfter(calculationTime)){

                sumOfTradePriceMultipliedByQuantity = sumOfTradePriceMultipliedByQuantity
                        .add(stockTrade.getQuantityOfShares()
                                .multiply(stockTrade.getTradePrice()));

                totalQuantity = totalQuantity.add(stockTrade.getQuantityOfShares());
            }
        }

        //If totalQuantity is 0, then no trades have happened in the last X minutes so return 0
        if(totalQuantity.compareTo(BigDecimal.ZERO) == 0){
          return BigDecimal.ZERO;
        }else {
            //Calculate and return value as Sum of (Quantity * Trade price) / Sum of Quantity
            return sumOfTradePriceMultipliedByQuantity.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP); //Assume 2 dp is sufficient precision
        }
    }

    /**
     * Calculates and returns the GBCE All share index
     *
     * Multiplies all stock trade prices then returns the nth root of that number
     * where n is the total number of trades
     *
     * @param stockTradeList List of transactions
     * @return Calculated GBCE to 2 Decimal Places
     */
     static BigDecimal calculateGBCE(List<StockTrade> stockTradeList) throws SuperSimpleStocksException{

        if(stockTradeList.isEmpty()){
            throw new SuperSimpleStocksException("GBCE can not be calculated as there have been no trades");
        }

        double nThRoot = stockTradeList.size();

        BigDecimal allTradePricesMultiplied = new BigDecimal("1");  //Initially 1 so the first trade price is handled correctly

        for(StockTrade stockTrade : stockTradeList){

            allTradePricesMultiplied = allTradePricesMultiplied.multiply(stockTrade.getTradePrice());
        }

        //Have to convert values to double to get the nth root, then convert back to BigDecimal
        BigDecimal result = BigDecimal.valueOf(Math.pow(allTradePricesMultiplied.doubleValue(), 1 / nThRoot));

        return result.setScale(2, BigDecimal.ROUND_HALF_UP); //Assume 2 dp is sufficient precision

    }

    /**
     * Determines which calculation to use to calculate the Dividend Yield
     * depending on Stock Type, then returns the calculated value.
     *
     * @param stockData Data relating to the Stock
     * @param price market price in Pence
     * @return Calculated dividend yield to 2 Decimal Places
     */
     static BigDecimal calculateDividendYield(StockData stockData, int price) throws SuperSimpleStocksException {

        //Convert price to BigDecimal so its easier to perform mathematical operations on
        BigDecimal priceAsBigDecimal = new BigDecimal(price);

        //Check for Market Price of 0, which will cause a divide by 0 Arithmetic Exception
         if(priceAsBigDecimal.compareTo(BigDecimal.ZERO) == 0){
             throw new SuperSimpleStocksException("Price cannot be 0 for the Dividend Yield Calculation");
         }

        switch(stockData.getType()){
            case COMMON:
                return calculateCommonDividendYield(stockData, priceAsBigDecimal);
            case PREFERRED:
                return calculatePreferredDividendYield(stockData, priceAsBigDecimal);
            default:
                throw new SuperSimpleStocksException("Stock Type "+ stockData.getType() + "not recognised, unable to calculate dividend yield");
        }

    }

    /**
     * Calculates the Dividend Yield for the COMMON StockType
     *
     * Formula used is: Last Dividend / Market Price
     *
     * Assumes Market Price will never be zero
     *
     * @param stockData Data relating to the Stock
     * @param priceAsBigDecimal market price in Pence represented as BigDecimal
     * @return Calculated Dividend Yield to 2 Decimal Places
     */
    private static BigDecimal calculateCommonDividendYield(StockData stockData, BigDecimal priceAsBigDecimal){

        //Convert Last Dividend value to BigDecimal so easier to perform mathematical operations on
        BigDecimal lastDividendAsBigDecimal = new BigDecimal(stockData.getLastDividend());

        //Last Dividend / Market Price
        return lastDividendAsBigDecimal.divide(priceAsBigDecimal, 2, BigDecimal.ROUND_HALF_UP); //Assume 2 dp is sufficient precision
    }

    /**
     * Calculates the Dividend Yield for the PREFERRED StockType
     *
     * Formula used is: Fixed Dividend * Par Value / Market Price
     *
     * Assumes Market Price will never be zero
     *
     * @param stockData Data relating to the Stock
     * @param priceAsBigDecimal market price in Pence represented as BigDecimal
     * @return Calculated Dividend Yield to 2 Decimal Places
     */
    private static BigDecimal calculatePreferredDividendYield(StockData stockData, BigDecimal priceAsBigDecimal){

        //Convert Par Value to BigDecimal so easier to perform mathematical operations on
        BigDecimal parValueAsBigDecimal = new BigDecimal(stockData.getParValue());

        //Fixed Dividend * Par Value / Market Price
        return (stockData.getFixedDividend().multiply(parValueAsBigDecimal))
                .divide(priceAsBigDecimal, 2, BigDecimal.ROUND_HALF_UP); //Assume 2 dp is sufficient precision
    }

}
