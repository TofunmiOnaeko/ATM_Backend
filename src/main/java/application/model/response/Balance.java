package application.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Balance {

    @Id
    private String userId;
    private int balance;
    private LocalDateTime lastUpdated;
}
