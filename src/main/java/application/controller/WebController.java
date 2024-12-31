package application.controller;

import application.model.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface WebController {

//    public ResponseEntity<Response> login();
//
//    public ResponseEntity<Response> stockPrice();

    public ResponseEntity<Response> getBalance(@PathVariable String userID);

}
