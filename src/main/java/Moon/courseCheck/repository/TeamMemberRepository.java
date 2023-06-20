package Moon.courseCheck.repository;

import Moon.courseCheck.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeamId(Long teamId);
    List<TeamMember> findAllByMemberId(Long memberId);
    Boolean existsTeamMemberByTeamIdAndMemberId(Long teamId, Long memberId);

    Long countAllByTeamId(Long teamId);
}
