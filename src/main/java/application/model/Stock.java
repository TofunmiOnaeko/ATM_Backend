package application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "Stocks")
public class Stock {
    @Id
    private Integer stockId;
    private String stockName;
    private int value;
    private LocalDateTime valueLastUpdated;
    private String creatorId;
}
