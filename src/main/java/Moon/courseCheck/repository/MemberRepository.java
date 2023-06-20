package Moon.courseCheck.repository;

import Moon.courseCheck.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUsername(String username);

    Boolean existsMemberByUsername(String username);
    Boolean existsMemberByNickname(String nickname);
}
