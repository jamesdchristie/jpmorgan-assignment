package com.supersimplestocks.enums;

import com.supersimplestocks.exception.SuperSimpleStocksException;

/**
 * Simple Enum to represent the Operations a user can perform
 *
 * Created by James Christie on 05/06/2017.
 */
public enum Operations {

    DY("DY"),
    PE("PE"),
    T("T"),
    VWSP("VWSP"),
    GBCE("GBCE"),
    Q("Q");

    private final String operationCode;

    Operations(String operationCode){
        this.operationCode = operationCode;
    }

    /**
     * Returns the Operations Enum that matches the given operation code
     *
     * @param enteredOperationCode to find a match for
     * @return matched Operations Enum
     * @throws SuperSimpleStocksException if entered Operation Code has no match
     */
    public static Operations getOperationFor(String enteredOperationCode) throws SuperSimpleStocksException{

        for(Operations operation: Operations.values()){

            if(enteredOperationCode.toUpperCase().equals(operation.operationCode)){
                return operation;
            }
        }
        throw new SuperSimpleStocksException("Entry "+enteredOperationCode+" is not a recognised Operation");
    }
}
