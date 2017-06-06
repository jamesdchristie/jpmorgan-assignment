package com.supersimplestocks;

import com.supersimplestocks.enums.StockData;
import com.supersimplestocks.enums.StockSymbolEnum;
import com.supersimplestocks.enums.TransactionType;
import com.supersimplestocks.exception.SuperSimpleStocksException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Suite of tests to test the calculations in the Super Simple Stocks Application
 *
 * Created by James Christie on 05/06/2017.
 */
public class SuperSimpleStocksTests {

    private StockTradeHistory stockTradeHistory = new StockTradeHistory();

    /**
     * Creates test Trades for various stocks with different simulated
     * trade times quantities and prices
     *
     * @throws Exception if there is an Exception
     */
    @Before
    public void setUp() throws Exception {

        LocalDateTime tenMinsAgo = LocalDateTime.now().minusMinutes(10L);
        LocalDateTime twentyMinsAgo = LocalDateTime.now().minusMinutes((20L));

        //simulate some trades from 10 minutes ago
        StockTrade buyAleTenMinsAgo = new StockTrade(TransactionType.BUY, StockSymbolEnum.ALE, tenMinsAgo, new BigDecimal(6), new BigDecimal(120));
        StockTrade sellAleTenMinsAgo = new StockTrade(TransactionType.SELL, StockSymbolEnum.ALE, tenMinsAgo, new BigDecimal(4), new BigDecimal(140));
        StockTrade sellTeaTenMinsAgo = new StockTrade(TransactionType.SELL, StockSymbolEnum.TEA, tenMinsAgo, new BigDecimal(20), new BigDecimal(30));
        stockTradeHistory.addTrade(buyAleTenMinsAgo);
        stockTradeHistory.addTrade(sellAleTenMinsAgo);
        stockTradeHistory.addTrade(sellTeaTenMinsAgo);

        //simulate some trades from 20 minutes ago
        StockTrade buyAleTwentyMinsAgo = new StockTrade(TransactionType.BUY, StockSymbolEnum.ALE, twentyMinsAgo, new BigDecimal(10), new BigDecimal(120));
        StockTrade buyPopTwentyMinsAgo = new StockTrade(TransactionType.BUY, StockSymbolEnum.POP, twentyMinsAgo, new BigDecimal(15), new BigDecimal(10));
        StockTrade sellGinTwentyMinsAgo = new StockTrade(TransactionType.SELL, StockSymbolEnum.GIN, twentyMinsAgo, new BigDecimal(17), new BigDecimal(230));

        stockTradeHistory.addTrade(buyAleTwentyMinsAgo);
        stockTradeHistory.addTrade(buyPopTwentyMinsAgo);
        stockTradeHistory.addTrade(sellGinTwentyMinsAgo);


    }

    /*
     * -------------------------------------------------------------------------------------------------------------------------------
     * SUCCESS TESTS
     * -------------------------------------------------------------------------------------------------------------------------------
     */


    /**
     * Tests that the Dividend Yield for a COMMON type stock
     * is calculated as expected
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateDividendYieldForCommonTestSuccess() throws Exception {

        //POP COMMON type, last dividend = 8
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.POP);

        int marketPrice = 4;

        //Last Dividend / Market Price = 8/4 = 2
        BigDecimal expectedResult = new BigDecimal("2.00");

        assertEquals(expectedResult, CalculationUtility.calculateDividendYield(stockData, marketPrice));
    }

    /**
     * Tests that the Dividend Yield for a PREFERRED type stock
     * is calculated as expected
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateDividendYieldForPreferredTestSuccess() throws Exception {

        //GIN PREFERRED type, fixed dividend = 2%, par value = 100
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.GIN);

        int marketPrice = 4;

        //(Fixed dividend * par value) / Market Price = (0.02 * 100)/4 = 2/4 = 0.50
        BigDecimal expectedResult = new BigDecimal("0.50");

        assertEquals(expectedResult, CalculationUtility.calculateDividendYield(stockData, marketPrice));
    }


    /**
     * Test that the Price Earnings ratio for a Stock
     * is calculated as expected
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculatePriceEarningsRatioTestSuccess() throws Exception {

        //ALE last dividend = 23
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.ALE);

        int marketPrice = 46;

        //market Price / Dividend = 46/23 = 2
        BigDecimal expectedResult = new BigDecimal("2.00");

        assertEquals(expectedResult, CalculationUtility.calculatePriceEarningsRatio(stockData, marketPrice));
    }

    /**
     * Tests that the Volume Weighted Stock Price is
     * calculated as expected for a given Stock
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateVolumeWeightedStockPriceTestSuccess() throws Exception {

        List<StockTrade> aleTradeHistory = stockTradeHistory.getStockTradeHistoryListForSymbol(StockSymbolEnum.ALE);

        //ALE has 3 trades, 2 at 10 minutes ago and 1 at twenty minutes ago
        //The cut off time is set to 15 minutes ago, so only the first 2 trades should be taken into account
        //Sum of (Quantity * Trade price) / Sum of Quantity =
        //((120 * 6) + (140 * 4))/(6 + 4) = (720 + 560)/10 = 1280/10 = 128

        BigDecimal expectedResult = new BigDecimal("128.00");

        assertEquals(expectedResult, CalculationUtility.calculateVolumeWeightedStockPrice(aleTradeHistory, 15));

    }

    /**
     * Tests that the Volume Weighted Stock Price
     * calculation will return 0 if no trades have occurred
     * for the given Stock over the last X minutes
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateVolumeWeightedStockPriceTestTradesTooOld() throws Exception {

        //POP has had one trade 20 minutes ago, so should not be included
        //in the calculation as it is only considering trades in the last 15 minutes,
        // so should return 0

        List<StockTrade> popTradeHistory = stockTradeHistory.getStockTradeHistoryListForSymbol(StockSymbolEnum.POP);

        //Ensure the List is not empty
        assertTrue(!popTradeHistory.isEmpty());

        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, CalculationUtility.calculateVolumeWeightedStockPrice(popTradeHistory, 15));

    }

    /**
     * Tests that the Volume Weighted Stock Price
     * calculation will return 0 if no trades have occurred
     * at all for the given Stock
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateVolumeWeightedStockPriceTestNoTrades() throws Exception {

        //JOE has had no trades, so should result in an empty List being entered
        //for the calculation, which should result in 0 being returned

        List<StockTrade> joeTradeHistory = stockTradeHistory.getStockTradeHistoryListForSymbol(StockSymbolEnum.JOE);

        //Ensure the List is empty
        assertTrue(joeTradeHistory.isEmpty());

        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, CalculationUtility.calculateVolumeWeightedStockPrice(joeTradeHistory, 15));

    }

    /**
     * Tests that the GBCE All Shares Index is calculated
     * as expected for all trades
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateGBCETestSuccess() throws Exception {

        //GBCE is the nth root of all trade prices multiplied where n is the number of trades
        //6 trades so
        //6th root of (120 * 140 * 30 *120 * 10 * 230)
        // = 6th root of 139,104,000,000 = 71.98189 = 71.98 to 2 dp

        BigDecimal expectedResult = new BigDecimal("71.98");

        assertEquals(expectedResult, CalculationUtility.calculateGBCE(stockTradeHistory.getStockTradeHistoryListForAllStocks()));

    }

    /*
     * -------------------------------------------------------------------------------------------------------------------------------
     * FAILURE TESTS
     * -------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Tests that the Dividend Yield for a COMMON type stock
     * calculation will handle a divide by 0 exception
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateDividendYieldForCommonTestFail() throws Exception {

        //POP COMMON type, last dividend = 8
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.POP);

        int marketPrice = 0;

        //Last Dividend / Market Price = 8/0 = Exception
        String expectedException = ("Price cannot be 0 for the Dividend Yield Calculation");

        try{
            BigDecimal result = CalculationUtility.calculateDividendYield(stockData, marketPrice);
        }
        catch(SuperSimpleStocksException e){
            assertEquals(expectedException, e.getMessage());
        }

    }

    /**
     * Tests that the Dividend Yield for a PREFERRED type stock
     * calculation will handle a divide by 0 exception
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculateDividendYieldForPreferredTestFail() throws Exception {

        //GIN PREFERRED type, fixed dividend = 2%, par value = 100
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.GIN);

        int marketPrice = 0;

        //(Fixed dividend * par value) / Market Price = (0.02 * 100)/0 = Exception
        String expectedException = ("Price cannot be 0 for the Dividend Yield Calculation");

        try{
            BigDecimal result = CalculationUtility.calculateDividendYield(stockData, marketPrice);
        }
        catch(SuperSimpleStocksException e){
            assertEquals(expectedException, e.getMessage());
        }
    }

    /**
     * Test that the Price Earnings ratio for a Stock
     * calculation will handle a divide by 0 exception
     *
     * @throws Exception if there is an Exception
     */
    @Test
    public void calculatePriceEarningsRatioTestFail() throws Exception {

        //TEA last dividend = 0
        StockData stockData = StockData.getStockDataForSymbol(StockSymbolEnum.TEA);

        int marketPrice = 46;

        //market Price / Dividend = 46/0 = Exception
        String expectedException = ("Cannot calculate PE Ratio as last Dividend for TEA is zero and would result in a divide by zero Arithmetic Exception");

        try{
            BigDecimal result = CalculationUtility.calculatePriceEarningsRatio(stockData, marketPrice);
        }
        catch(SuperSimpleStocksException e){
            assertEquals(expectedException, e.getMessage());
        }
    }



}