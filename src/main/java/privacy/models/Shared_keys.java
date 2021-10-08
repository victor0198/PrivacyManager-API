package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Shared_keys {
    int cloudKeyId;
    int friendshipId;
}
