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
    int friendshipId;
    int userOneId;
    int userTwoId;
}
