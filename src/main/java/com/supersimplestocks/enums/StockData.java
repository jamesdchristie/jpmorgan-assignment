package com.supersimplestocks.enums;

import com.supersimplestocks.exception.SuperSimpleStocksException;

import java.math.BigDecimal;

/**
 * Enum that hold all relevant Sample Stock data
 *
 * Enum values populated for this exercise, but in a real world application,
 * these values could probably be populated from a database.
 *
 * It is assumed that as this is just a representation of sample data
 * there is no need to have any checks that each entry is unique
 *
 * Created by James Christie on 05/06/2017.
 */
public enum StockData {

    TEA(StockSymbolEnum.TEA, StockType.COMMON, 0, null, 100),
    POP(StockSymbolEnum.POP, StockType.COMMON, 8, null, 100),
    ALE(StockSymbolEnum.ALE, StockType.COMMON, 23, null, 60),
    GIN(StockSymbolEnum.GIN, StockType.PREFERRED, 8, new BigDecimal(0.02), 100),
    JOE(StockSymbolEnum.JOE, StockType.COMMON, 13, null, 250);

    private final StockSymbolEnum symbol;
    private final StockType type;
    private final int lastDividend; //Pence. integer used as it is assumed fractions of pennies will not be needed
    private final BigDecimal fixedDividend; //Percentage value. E.g. 1.5% = 0.015, 100% = 1.00
    private final int parValue; //Pence. integer used as it is assumed fractions of pennies will not be needed

    StockData(StockSymbolEnum symbol, StockType type, int lastDividend, BigDecimal fixedDividend, int parValue){
        this.symbol = symbol;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;

    }

    public StockSymbolEnum getSymbol(){
        return symbol;
    }

    public StockType getType(){
        return type;
    }

    public int getLastDividend(){
        return lastDividend;
    }

    public BigDecimal getFixedDividend(){
        return fixedDividend;
    }

    public int getParValue(){
        return parValue;
    }

    /**
     * Returns the relevant Stock Data for the given symbol
     * if no data found, throws an Exception
     *
     * @param symbol to search for
     * @return Stock Data enum for given symbol
     * @throws SuperSimpleStocksException if no Stock data Enum found
     */
    public static StockData getStockDataForSymbol(StockSymbolEnum symbol) throws SuperSimpleStocksException{
        for(StockData stockData: StockData.values()){
            if (stockData.getSymbol() == symbol){
                return  stockData;
            }
        }
        throw new SuperSimpleStocksException("No Sample Stock Data found for symbol " + symbol);
    }
}
