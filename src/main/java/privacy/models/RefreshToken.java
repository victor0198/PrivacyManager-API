package privacy.models;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "refreshtoken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ownerId;

    @OneToOne
    @JoinColumn(name = "ownerId", referencedColumnName = "ownerId")
    private Owner owner;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;


}
