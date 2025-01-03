package application.service;

import application.model.Balance;
import application.model.Stock;
import application.model.Transaction;
import application.model.enums.TransactionType;
import application.repository.BalanceRepository;
import application.repository.StockRepository;
import application.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
@Service
@Transactional
public class StockBalanceService {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private ConcurrentHashMap<String, Integer> nameToIdMap;
    private AtomicInteger stockIdCounter;
    private final int managementFee = 2;

    public StockBalanceService() {
        this.nameToIdMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        loadMappings();
        stockIdCounter = new AtomicInteger(nameToIdMap.size() + 1);
    }

    public void loadMappings() {
        Iterable<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            nameToIdMap.put(stock.getStockName(), stock.getStockId());
        }
        log.debug("Stock database built");
    }

    public boolean createStock(String userId, int value, String stockName, ReadWriteLock lock) {
        int id = stockIdCounter.getAndIncrement();
        Stock newStock = Stock.builder()
                .stockId(id)
                .creatorId(userId)
                .value(value)
                .stockName(stockName)
                .valueLastUpdated(LocalDateTime.now())
                .build();

        try {
            lock.writeLock().lock();
            stockRepository.save(newStock);
            nameToIdMap.put(stockName, id);
            return true;
        } catch (RuntimeException e) {
            log.error("Stock transaction failed for userId: " + userId + ", unable to save new stock to the database");
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Stock> getStock(String stockName, ReadWriteLock lock) {
        try {
            lock.readLock().lock();
            Integer stockId = nameToIdMap.get(stockName);
            if (stockId == null) {
                log.error("Stock with name: " + stockName + ", not found in ID map");
                return Optional.empty();
            }
            return stockRepository.findById(stockId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean stockTransaction(String userId, String stockName, int amount, TransactionType transactionType, ReadWriteLock lock) {
        Optional<Stock> optStock = getStock(stockName, lock);

        if(optStock.isPresent()) {
                Stock stock = optStock.get();
                Transaction transaction = buildTransaction(userId, stock, amount,transactionType);

            try {
                lock.writeLock().lock();
                transactionRepository.save(transaction);
                stockRepository.save(calculateNewStockPrice(stock, amount));
                return updateBalance(userId, amount, transactionType, lock);

            } finally {
                lock.writeLock().unlock();
            }

        } else {
            log.error("Stock transaction failed for userId: " + userId + ", stock is not present in the database");
            return false;
        }
    }

    private Transaction buildTransaction(String userId, Stock stock, int amount, TransactionType transactionType) {
        return Transaction.builder()
                .userId(userId)
                .transactionAmount(amount)
                .timeOfTransaction(LocalDateTime.now())
                .localCurrency("GBP")
                .stock(stock)
                .stockValue(stock.getValue())
                .transactionType(transactionType)
                .build();
    }

    private Stock calculateNewStockPrice(Stock stock, int amount) {
        Random random = new Random();
        int stockChange = random.nextInt(11);

        if (amount > 0) {
            stock.setValue(stock.getValue() + stockChange);
            return stock;

        } else {
            stock.setValue(Math.max(1, stock.getValue() - stockChange));
            return stock;
        }
    }

    private boolean updateBalance(String userId, int amount, TransactionType transactionType, ReadWriteLock lock) {
        if (transactionType.equals(TransactionType.BUY)) {
            return changeBalance(userId, (amount * -1), transactionType, lock);
        } else if (transactionType.equals(TransactionType.SELL)) {
            return changeBalance(userId, amount, transactionType, lock);
        } else {
            return false;
        }
    }

    public boolean changeBalance(String userId, int amount, TransactionType transactionType, ReadWriteLock lock) {
        Optional<Balance> optBalance = getBalance(userId, lock);
        if (!optBalance.isPresent()) {
            log.error("Balance transaction failed for userId: " + userId + ", balance is not present in the database");
            return false;
        }

        Balance balance = optBalance.get();
        int totalNeeded = calculateTotalNeeded(balance, amount, transactionType);

        if (balance.getBalance() > totalNeeded) {
            try {
                lock.writeLock().lock();
                balance.setBalance((balance.getBalance() + amount) - managementFee);
                balanceRepository.save(balance);
            } finally {
                lock.writeLock().unlock();
            }
            return balanceTransaction(userId, amount, transactionType, balance, lock);

        } else {
            log.error("Balance transaction failed for userId: " + userId + ", balance is not high enough to complete transaction");
            return false;
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

            Transaction transaction = Transaction.builder()
                    .userId(userId)
                    .transactionAmount(amount)
                    .timeOfTransaction(LocalDateTime.now())
                    .localCurrency("GBP")
                    .transactionType(transactionType)
                    .build();

            try {
                lock.writeLock().lock();
                transactionRepository.save(transaction);
            } finally {
                lock.writeLock().unlock();
            }
            return true;

        } else {
            try {
                lock.writeLock().lock();
                balanceRepository.save(balance);
            } finally {
                lock.writeLock().unlock();
            }

            log.error("Balance transaction failed for userId: " + userId + ", balance is not high enough to complete transaction");
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
