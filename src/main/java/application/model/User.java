package application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    private String userId;
    private Address address;
    private String userName;
    private LocalDate userDOB;
    private String email;
    private LocalDate latestLogin;
}
