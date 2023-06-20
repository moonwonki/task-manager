package Moon.courseCheck.repository;

import Moon.courseCheck.domain.TeamManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamManagerRepository extends JpaRepository<TeamManager, Long> {
    boolean existsTeamManagerByMemberIdAndTeamId(Long memberId, Long teamId);
}
