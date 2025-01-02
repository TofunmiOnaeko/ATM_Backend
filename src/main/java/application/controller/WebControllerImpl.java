package application.controller;

import application.model.Balance;
import application.model.Stock;
import application.model.enums.TransactionType;
import application.model.response.Response;
import application.service.BalanceService;
import application.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/stockATM")
public class WebControllerImpl implements WebController {

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private StockService stockService;

//    @Override
//    public ResponseEntity<Response> login() {
//
//    }

    @PostMapping("/deposit/{userId}")
    @Override
    public ResponseEntity<Response> deposit(@PathVariable String userID, @RequestParam int amount) {
        if (balanceService.changeBalance(userID, amount, TransactionType.DEPOSIT, lock)) {
            return new ResponseEntity<>(new Response("Deposit was successful"), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/withdraw/{userId}")
    @Override
    public ResponseEntity<Response> withdraw(@PathVariable String userID, @RequestParam int amount) {
        if (balanceService.changeBalance(userID, (amount * -1), TransactionType.WITHDRAWAL, lock)) {
            return new ResponseEntity<>(new Response("Withdrawal was successful"), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/buy/{userId}/{stockName}")
    @Override
    public ResponseEntity<Response> buy(@PathVariable String userID, @PathVariable String stockName, @RequestParam int amount) {
       try {
           stockService.stockTransaction(userID, stockName, amount, TransactionType.BUY, lock);
           balanceService.changeBalance(userID, (amount * -1), TransactionType.BUY, lock);
           return new ResponseEntity<>(new Response("Stock transaction was successful"), HttpStatusCode.valueOf(200));

       } catch (RuntimeException e) {
           return new ResponseEntity<>(HttpStatusCode.valueOf(400));
       }
    }

    @PostMapping("/sell/{userId}/{stockName}")
    @Override
    public ResponseEntity<Response> sell(@PathVariable String userID, @PathVariable String stockName, @RequestParam int amount) {
        try {
            stockService.stockTransaction(userID, stockName, amount, TransactionType.SELL, lock);
            balanceService.changeBalance(userID, amount, TransactionType.SELL, lock);
            return new ResponseEntity<>(new Response("Stock transaction was successful"), HttpStatusCode.valueOf(200));

        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @GetMapping("/getStock/{stockName}")
    @Override
    public ResponseEntity<Response> getStock(@PathVariable String stockName) {
        if (stockService.getStock(stockName, lock).isPresent()) {
            Stock stock = stockService.getStock(stockName, lock).get();
            return new ResponseEntity<>(new Response(stock), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

    @GetMapping("/getBalance/{userId}")
    @Override
    public ResponseEntity<Response> getBalance(@PathVariable String userId) {
        Optional<Balance> optBalance = balanceService.getBalance(userId, lock);

        if (optBalance.isPresent()) {
            Balance balance = optBalance.get();
            return new ResponseEntity<>(new Response(balance), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

}
