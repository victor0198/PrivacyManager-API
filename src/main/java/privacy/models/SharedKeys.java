package privacy.models;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@ToString
@Getter
@Setter
//@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SharedKeys implements Serializable {
    @Id
    private long cloudKeyId;
    @Id
    private long friendshipId;
}
