package com.example.jtc.controller;

import com.example.jtc.request.BankTransferRq;
import com.example.jtc.response.ResponseUtils;
import com.example.jtc.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    private final TransactionService transactionService;

    @Autowired
    public RestApiController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public String transferMoney(@RequestBody BankTransferRq request) {
        boolean isSuccessful = transactionService.transfer(request.getSenderId(), request.getRecipientId(), request.getAmount());
        if (isSuccessful) {
            return ResponseUtils.createSuccessfulRs();
        }
        return ResponseUtils.createErrorRs();
    }
}
