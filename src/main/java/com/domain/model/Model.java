package com.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@RequiredArgsConstructor
public class Model {
	
    public Model(Model transactionRequest) {
    	super();
		this.destinationAccountNumber = transactionRequest.getDestinationAccountNumber();
		this.amount = transactionRequest.getAmount();
		this.sourceAccountNumber = transactionRequest.getSourceAccountNumber();
	}
    
    private long destinationAccountNumber;
	private long amount ;
    private long sourceAccountNumber;
}
