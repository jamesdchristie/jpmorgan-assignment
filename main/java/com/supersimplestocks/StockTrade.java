package com.supersimplestocks;

import com.supersimplestocks.enums.StockSymbolEnum;
import com.supersimplestocks.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Holds details of a single trade of a Stock item
 *
 * Created by James Christie on 05/06/2017.
 */
 class StockTrade {

    private TransactionType transactionType;
    private StockSymbolEnum symbol;
    private LocalDateTime timeStamp;
    private BigDecimal quantityOfShares;
    private BigDecimal tradePrice;

    StockTrade(TransactionType transactionType, StockSymbolEnum symbol, LocalDateTime timeStamp, BigDecimal quantityOfShares, BigDecimal tradePrice){
        this.transactionType = transactionType;
        this.symbol = symbol;
        this.timeStamp = timeStamp;
        this.quantityOfShares = quantityOfShares;
        this.tradePrice = tradePrice;
    }

    //Not used, but kept for completeness
    TransactionType getTransactionType() { return transactionType; }

    StockSymbolEnum getSymbol() {
        return symbol;
    }

    LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    BigDecimal getQuantityOfShares() {
        return quantityOfShares;
    }

    BigDecimal getTradePrice() {
        return tradePrice;
    }

}
