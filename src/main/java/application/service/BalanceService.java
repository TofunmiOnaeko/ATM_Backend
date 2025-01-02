package application.service;

import application.model.Balance;
import application.model.Transaction;
import application.model.enums.TransactionType;
import application.repository.BalanceRepository;
import application.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
@Service
@Transactional
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public boolean changeBalance(String userId, int amount, TransactionType transactionType, ReadWriteLock lock) {
        Optional<Balance> optBalance = getBalance(userId, lock);
        Balance balance = optBalance.get();
        int totalNeeded = calculateTotalNeeded(balance, amount, transactionType);

        try {
            lock.writeLock().lock();
            if (optBalance.isPresent() && (balance.getBalance() > totalNeeded)) {
                balance.setBalance((balance.getBalance() + amount) - 2); //-2 for fees
                balanceRepository.save(balance);
                return balanceTransaction(userId, amount, transactionType, balance, lock);
            } else {
                log.error("Balance transaction failed, balance is not present in the database or balance is not high enough to complete transaction");
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private int calculateTotalNeeded(Balance balance, int amount, TransactionType transactionType) {
        if (transactionType.equals(TransactionType.BUY) || transactionType.equals(TransactionType.WITHDRAWAL)) {
            return balance.getBalance() + (amount * -1) + 5;

        } else {
            return 5;
        }
    }

    private boolean balanceTransaction(String userId, int amount, TransactionType transactionType, Balance balance, ReadWriteLock lock) {
        if(balance.getBalance() > 5) {
            try {
                lock.writeLock().lock();
                Transaction transaction = Transaction.builder()
                        .userId(userId)
                        .transactionAmount(amount)
                        .timeOfTransaction(LocalDateTime.now())
                        .localCurrency("GBP")
                        .transactionType(transactionType)
                        .build();
                transactionRepository.save(transaction);
                return true;

            } finally {
                lock.writeLock().unlock();
            }
        } else {
            balanceRepository.save(balance);
            log.error("Balance transaction failed, balance is not high enough to complete transaction");
            return false;
        }
    }

    public Optional<Balance> getBalance(String userId, ReadWriteLock lock) {
        try {
            lock.readLock().lock();
            return balanceRepository.findById(userId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
