package application.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Transaction {
    private String userId;
    private int transactionAmount;
    private LocalDateTime timeOfTransaction;
}
