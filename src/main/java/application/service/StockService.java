package application.service;

import application.model.Stock;
import application.model.Transaction;
import application.model.enums.TransactionType;
import application.repository.StockRepository;
import application.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
@Service
@Transactional
public class StockService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private HashMap<String, Integer> nameToIdMap;

    public StockService() {
        this.nameToIdMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        loadMappings();
    }

    //TODO: Need to add functionality to add new stocks
    public void loadMappings() {
        Iterable<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            nameToIdMap.put(stock.getStockName(), stock.getStockId());
        }
        log.debug("Stock database built");
    }

    //When you buy/sell the value is increased or decreased randomly from a value between -10 and + 10, therefore it needs a lock, can only be bought by one person at a time
    public boolean stockTransaction(String userId, String stockName, int amount, TransactionType transactionType, ReadWriteLock lock) {
        Optional<Stock> optStock = getStock(stockName, lock);
        if(optStock.isPresent()) {
            try {
                lock.writeLock().lock();
                Stock stock = optStock.get();

                Transaction transaction = Transaction.builder()
                        .userId(userId)
                        .transactionAmount(amount)
                        .timeOfTransaction(LocalDateTime.now())
                        .localCurrency("GBP")
                        .stock(stock)
                        .stockValue(stock.getValue())
                        .transactionType(transactionType)
                        .build();
                transactionRepository.save(transaction);
                stockRepository.save(calculateNewStockPrice(stock, amount));
                return true;

            } finally {
                lock.writeLock().unlock();
            }
        } else {
            log.error("Stock transaction failed, stock is not present in the database");
            return false;
        }
    }

    private Stock calculateNewStockPrice(Stock stock, int amount) {
        Random random = new Random();
        int stockChange = random.nextInt(11);

        if (amount > 0) {
            stock.setValue(stock.getValue() + stockChange);
            return stock;

        } else {
            stock.setValue(stock.getValue() - stockChange);
            return stock;
        }
    }

    public Optional<Stock> getStock(String stockName, ReadWriteLock lock) {
        try {
            lock.readLock().lock();
            return stockRepository.findById(nameToIdMap.get(stockName));
        } finally {
            lock.readLock().unlock();
        }
    }
}
