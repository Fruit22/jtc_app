package com.example.jtc.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankTransferRq {
    private long senderId;
    private long recipientId;
    private double amount;
}
