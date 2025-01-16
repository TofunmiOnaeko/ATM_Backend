package application.controller;

import application.model.Balance;
import application.model.Stock;
import application.model.enums.TransactionType;
import application.model.response.Response;
import application.service.StockBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Controller
@RequestMapping("/stockATM")
public class WebControllerImpl implements WebController {

    @Autowired
    private StockBalanceService stockBalanceService;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

//    @Override
//    public ResponseEntity<Response> login() {
//
//    }

    @PostMapping("/stock/create/{userId}/{stockName}")
    @Override
    public ResponseEntity<Response> createStock(@PathVariable String userId, @PathVariable String stockName, @RequestParam int value) {
        if (stockBalanceService.createStock(userId, value, stockName, lock)) {
            return new ResponseEntity<>(new Response("Stock created successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response("Stock was not created"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("balance/deposit/{userId}")
    @Override
    public ResponseEntity<Response> deposit(@PathVariable String userID, @RequestParam int amount) {
        if (stockBalanceService.changeBalance(userID, amount, TransactionType.DEPOSIT, lock)) {
            return new ResponseEntity<>(new Response("Deposit was successful"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response("Deposit was unsuccessful"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("balance/withdraw/{userId}")
    @Override
    public ResponseEntity<Response> withdraw(@PathVariable String userID, @RequestParam int amount) {
        if (stockBalanceService.changeBalance(userID, (amount * -1), TransactionType.WITHDRAWAL, lock)) {
            return new ResponseEntity<>(new Response("Withdrawal was successful"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response("Withdrawal was unsuccessful"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stock/buy/{userId}/{stockName}")
    @Override
    public ResponseEntity<Response> buy(@PathVariable String userId, @PathVariable String stockName, @RequestParam int amount) {
        return handleTransaction(userId, stockName, amount, TransactionType.BUY);
    }

    @PostMapping("/stock/sell/{userId}/{stockName}")
    @Override
    public ResponseEntity<Response> sell(@PathVariable String userId, @PathVariable String stockName, @RequestParam int amount) {
        return handleTransaction(userId, stockName, amount, TransactionType.SELL);
    }

    private ResponseEntity<Response> handleTransaction(String userId, String stockName, int amount, TransactionType transactionType) {
        try {
            boolean success = stockBalanceService.stockTransaction(userId, stockName, amount, transactionType, lock);
            if (success) {
                return new ResponseEntity<>(new Response("Stock transaction was successful, transaction type: " + transactionType.name()), HttpStatus.OK);
            } else {
                log.error("An error has occurred for sell transaction for userId: " + userId + "please check logs for error");
                return new ResponseEntity<>(new Response("Stock transaction was unsuccessful for userId: " + userId +  "transaction type: " + transactionType.name()), HttpStatus.BAD_REQUEST);
            }

        } catch (RuntimeException e) {
            return new ResponseEntity<>(new Response("Stock transaction was unsuccessful, transaction type: " + transactionType.name()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/stock/get/{stockName}")
    @Override
    public ResponseEntity<Response> getStock(@PathVariable String stockName) {
        Optional<Stock> optStock = stockBalanceService.getStock(stockName, lock);

        if (optStock.isPresent()) {
            return new ResponseEntity<>(new Response(optStock.get()), HttpStatus.OK);
        } else {
            log.debug("Stock with name: " + stockName + ", not found in database");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/balance/get/{userId}")
    @Override
    public ResponseEntity<Response> getBalance(@PathVariable String userId) {
        Optional<Balance> optBalance = stockBalanceService.getBalance(userId, lock);

        if (optBalance.isPresent()) {
            return new ResponseEntity<>(new Response(optBalance.get()), HttpStatus.OK);
        } else {
            log.debug("Balance for user: " + userId + ", not found in database");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
