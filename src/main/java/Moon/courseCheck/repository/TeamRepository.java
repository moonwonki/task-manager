package Moon.courseCheck.repository;

import Moon.courseCheck.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findTeamByInviteCode(String inviteCode);

    boolean existsTeamByName(String name);

    boolean existsTeamByInviteCode(String inviteCode);

}
