package Moon.courseCheck.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String inviteCode;
}
