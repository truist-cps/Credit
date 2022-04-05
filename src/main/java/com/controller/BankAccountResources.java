package com.controller;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.server.ResponseStatusException;

import com.domain.BankAccount;
import com.domain.Credit;
import com.domain.Customer;
import com.domain.model.AccountModel;
import com.domain.model.CustomerModel;
import com.domain.model.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.AccountService;
import com.service.CustomerService;
import com.service.DepositService;
import com.utils.NoSuchAccountException;
import com.utils.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/")
public class BankAccountResources {

  
    private final DepositService depositService;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountResources.class);

    @Autowired
    ObjectMapper objectMapper;
    
    @KafkaListener(topics = {"truist-account"})
    public ResponseEntity<ResponseModel> deposit(ConsumerRecord<Integer,String> consumerRecord) throws JsonMappingException, JsonProcessingException  {
    	
    	Model model=objectMapper.readValue(consumerRecord.value(),Model.class);
    	logger.info(String.format("#### -> Message Consumed SuccessFully-> %s", model.toString()));
    	ResponseModel responseModel=new ResponseModel();
    	Credit credit;
		try {
			credit = depositService.doDeposit(model);
			responseModel.setData(credit);
	    	responseModel.setMessage("Success");
	    	
	    	return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.OK);
		}
		catch (NoSuchAccountException e) {
			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Account not found",e);
		} 
		catch (Exception e) {
			throw new ResponseStatusException(
			           HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!",e);
		} 
		
		      
    }
    
    @GetMapping(value = "test")
    public String testMethod() {
    	return "welcome";
       
    }
    
    private final AccountService accountService;
    private final CustomerService customerService;
    public BankAccountResources(DepositService depositService,AccountService accountService,CustomerService customerService) {
        
        this.depositService = depositService;
        this.accountService=accountService;
        this.customerService=customerService;
    }
    @PostMapping(value = "customer/create")
    public ResponseEntity<ResponseModel> createCustomer(@RequestBody CustomerModel cm) {
    	
    	ResponseModel responseModel=new ResponseModel();
    	Customer customer=customerService.createCustomer(cm);
    	
    	responseModel.setData(customer);
    	responseModel.setMessage("Success");
    	return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.OK);
       
    }
    
    @PostMapping(value = "accounts/create")
    public ResponseEntity<ResponseModel> createAccount(@RequestBody AccountModel am) {
    	
    	ResponseModel responseModel=new ResponseModel();
    	try {
    	BankAccount account=accountService.createAccount(am);
    	
    	responseModel.setData(account);
    	responseModel.setMessage("Success");
    	return new ResponseEntity<ResponseModel>(responseModel, HttpStatus.OK);
    }
    	 
		catch (Exception e) {
			throw new ResponseStatusException(
			           HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!",e);
		}
    }
    
}



