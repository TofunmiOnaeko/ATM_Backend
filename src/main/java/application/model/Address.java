package application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Address")
public class Address {
    @Id
    private String userId;
    @OneToOne
    private User user;
    private String addressLine1;
    private String addressLine2;
    private String locality;
    private String town;
    private String postcode;
}
