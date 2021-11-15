package privacy.models;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedKeys implements Serializable {
    @Id
    private Long cloudKeyId;

    private Long friendshipId;
}
