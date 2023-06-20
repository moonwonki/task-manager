package Moon.courseCheck.repository;

import Moon.courseCheck.domain.TeamOriginTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamOriginTaskRepository extends JpaRepository<TeamOriginTask, Long> {
    List<TeamOriginTask> findAllByTeamId(Long teamId);
}
