package application.controller;

import application.model.response.Balance;
import application.model.response.Response;
import application.service.BalanceService;
import application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stockATM")
public class WebControllerImpl implements WebController {

    @Autowired
    private BalanceService balanceService;

//    @Override
//    public ResponseEntity<Response> login() {
//
//    }
//
//    @Override
//    public ResponseEntity<Response> stockPrice() {
//
//    }

    @GetMapping("/getBalance/{userId}")
    @Override
    public ResponseEntity<Response> getBalance(@PathVariable String userId) {

        if (balanceService.getBalance(userId).isPresent()) {

            Balance balance = balanceService.getBalance(userId).get();
            return new ResponseEntity<>(new Response(balance.getBalance()), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

}
