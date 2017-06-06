package com.supersimplestocks.enums;

import com.supersimplestocks.exception.SuperSimpleStocksException;

/**
 * Simple Enum to designate whether a trade was buying or selling Stock
 *
 * Created by James Christie on 05/06/2017.
 */
public enum TransactionType {
    BUY("BUY"),
    SELL("SELL");

    private final String transactionName;

    TransactionType(String transactionName){
        this.transactionName = transactionName;
    }

    /**
     * returns the matched Transaction Type Enum for the given transaction name
     *
     * @param enteredTransactionName to search for
     * @return matched transaction Type Enum
     * @throws SuperSimpleStocksException if given transaction name has no match
     */
    public static TransactionType getTransactionTypeFor(String enteredTransactionName) throws SuperSimpleStocksException{
        for(TransactionType transactionType: TransactionType.values()){
            if(transactionType.transactionName.equals(enteredTransactionName.toUpperCase())){
                return transactionType;
            }
        }
        throw new SuperSimpleStocksException("Trade can be " + BUY.transactionName + " or " + SELL.transactionName + ". " +
                "Value entered was " + enteredTransactionName);
    }
}
