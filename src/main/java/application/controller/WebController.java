package application.controller;

import application.model.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface WebController {

//    public ResponseEntity<Response> login(@PathVariable String userID);

    public ResponseEntity<Response> createStock(@PathVariable String userId, @RequestParam int value, @RequestParam String stockName);

    public ResponseEntity<Response> deposit(@PathVariable String userID, @RequestParam int amount);

    public ResponseEntity<Response> withdraw(@PathVariable String userID, @RequestParam int amount);

    public ResponseEntity<Response> getStock(@PathVariable String stockName);

    public ResponseEntity<Response> buy(@PathVariable String userID, @PathVariable String stockName, @RequestParam int amount);

    public ResponseEntity<Response> sell(@PathVariable String userID, @PathVariable String stockName, @RequestParam int amount);

    public ResponseEntity<Response> getBalance(@PathVariable String userID);

}
