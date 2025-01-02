package application.model;

import application.model.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Transactions")
public class Transaction {
    @Id
    private int transactionId;
    private String userId;
    private int transactionAmount;
    private LocalDateTime timeOfTransaction;
    private String localCurrency;
    @ManyToOne
    private Stock stock;
    private int stockValue;
    private TransactionType transactionType;
    private boolean transactionComplete;
}
