package Moon.courseCheck.dto;


import Moon.courseCheck.domain.Team;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TeamSelectionDto {
    private Long id;
    private String name;
    private Long MemberNum;
    private Boolean isManager;

    public static TeamSelectionDto getInstance(Team team, Long memberNum, Boolean isManager){
        TeamSelectionDto dto = new TeamSelectionDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setMemberNum(memberNum);
        dto.setIsManager(isManager);
        return dto;
    }
}
