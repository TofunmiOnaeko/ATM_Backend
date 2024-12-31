package application.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class User {

    @Id
    private String userId;
    private Address address;
    private String userName;
    private LocalDate userDOB;
    private String email;

}
