package com.example.jtc.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Client {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private BigDecimal account;

    public Client(String name, double account) {
        this.account = new BigDecimal(account).setScale(2, RoundingMode.HALF_UP);
        this.name = name;
    }
}
