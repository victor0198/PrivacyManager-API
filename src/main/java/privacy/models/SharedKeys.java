package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SharedKeys {
    private int cloudKeyId;
    private int friendshipId;
}
