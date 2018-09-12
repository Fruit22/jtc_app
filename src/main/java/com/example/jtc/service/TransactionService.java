package com.example.jtc.service;

import com.example.jtc.model.Client;
import com.example.jtc.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    private final ClientRepository clientRepository;

    @Autowired
    public TransactionService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * @param senderID    Identifier of the sender in the database
     * @param recipientID Identifier of the recipient in the database
     * @param amount      Transfer amount
     * @return If the transaction was successful - true, otherwise - false
     */
    @Transactional
    public boolean transfer(@NotNull Long senderID, @NotNull Long recipientID, double amount) {
        if (amount <= 0) {
            return false;
        }

        Optional<Client> sender = clientRepository.findById(senderID);
        Optional<Client> recipient = clientRepository.findById(recipientID);
        BigDecimal senderAccount = sender.map(Client::getAccount).orElse(null);
        BigDecimal recipientAccount = recipient.map(Client::getAccount).orElse(null);

        if (senderAccount == null || recipientAccount == null) {
            return false;
        }

        BigDecimal amountBigDecimal = new BigDecimal(amount);
        if (checkSenderBalance(senderAccount, amountBigDecimal)) {
            sender.get().setAccount(senderAccount.subtract(amountBigDecimal));
            recipient.get().setAccount(recipientAccount.add(amountBigDecimal));
            return true;
        } else {
            return false;
        }
    }

    private boolean checkSenderBalance(BigDecimal account, BigDecimal amount) {
        return account.compareTo(amount) >= 0;
    }
}
