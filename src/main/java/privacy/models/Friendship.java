package privacy.models;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Friendship {
    private int friendshipId;
    private int userOneId;
    private int userTwoId;
}
