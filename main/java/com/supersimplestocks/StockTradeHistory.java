package com.supersimplestocks;

import com.supersimplestocks.enums.StockSymbolEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a history of Stock Trades
 *
 * Held in memory for the purposes of the exercise,
 * but in the real world would probably be stored in a database
 *
 * Created by James Christie on 05/06/2017.
 */
class StockTradeHistory {

    private List<StockTrade> stockTradeHistoryList;

    StockTradeHistory(){
        stockTradeHistoryList = new ArrayList<>();
    }

    void addTrade(StockTrade stockTrade){
        stockTradeHistoryList.add(stockTrade);
    }

    List<StockTrade> getStockTradeHistoryListForAllStocks(){
        return stockTradeHistoryList;
    }

    List<StockTrade> getStockTradeHistoryListForSymbol(StockSymbolEnum symbol){

        List<StockTrade> stockTradeListForSymbol = new ArrayList<>();

        for(StockTrade stockTrade : stockTradeHistoryList){

            if(stockTrade.getSymbol() == symbol){
                stockTradeListForSymbol.add(stockTrade);
            }
        }

        return stockTradeListForSymbol;
    }
}
