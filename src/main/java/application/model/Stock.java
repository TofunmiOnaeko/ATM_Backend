package application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Stocks")
public class Stock {
    @Id
    @Column(name = "stock_id")
    private Integer stockId;
    @Column(name = "stock_name")
    private String stockName;
    @Column(name = "value")
    private int value;
    @Column(name = "value_last_updated")
    private LocalDateTime valueLastUpdated;
    @Column(name = "creator_id")
    private String creatorId;
}
