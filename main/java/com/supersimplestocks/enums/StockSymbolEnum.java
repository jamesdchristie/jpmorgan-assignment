package com.supersimplestocks.enums;

import com.supersimplestocks.exception.SuperSimpleStocksException;

/**
 * Simple Enum to hold the symbol representation of Stocks
 *
 * Created by James Christie on 05/06/2017.
 */
public enum StockSymbolEnum {

    TEA("TEA"),
    POP("POP"),
    ALE("ALE"),
    GIN("GIN"),
    JOE("JOE");

    private final String symbol;

    StockSymbolEnum(String symbol){
        this.symbol = symbol;
    }

    /**
     * Returns the relevant Stock Symbol for the given symbol
     * if no data found, throws an Exception
     *
     * @param enteredSymbol to search for
     * @return Stock Symbol Enum for given symbol
     * @throws SuperSimpleStocksException if given symbol has no match
     */
    public static StockSymbolEnum getStockSymbolFor(String enteredSymbol) throws SuperSimpleStocksException{
        for(StockSymbolEnum stockSymbol: StockSymbolEnum.values()){
            if(enteredSymbol.toUpperCase().equals(stockSymbol.symbol)){
                return  stockSymbol;
            }
        }
        throw new SuperSimpleStocksException("No stock exists with Symbol " + enteredSymbol);
    }

    public String getSymbol(){
        return  symbol;
    }
}
