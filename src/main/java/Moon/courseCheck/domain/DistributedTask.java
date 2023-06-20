package Moon.courseCheck.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


//Task에 대한 정보만 담는다.
//어떤 team에 속해있고, 어떤 멤버에게 할당되는지는 MemberTask에 적혀있다.
@Entity
@Getter
@Setter
public class DistributedTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Long id;
    private Long originTaskId;
    private String status;


    private Long memberId;


}
