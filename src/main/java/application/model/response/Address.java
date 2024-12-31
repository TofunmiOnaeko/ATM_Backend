package application.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Address {

    @Id
    private String userId;
    private String addressLine1;
    private String addressLine2;
    private String locality;
    private String town;
    private String postcode;
}
