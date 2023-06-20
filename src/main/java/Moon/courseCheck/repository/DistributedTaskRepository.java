package Moon.courseCheck.repository;

import Moon.courseCheck.domain.DistributedTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistributedTaskRepository extends JpaRepository<DistributedTask, Long> {
    Optional<DistributedTask> findByMemberIdAndOriginTaskId(Long memberId, Long originTaskId);

    List<DistributedTask> findAllByOriginTaskId(Long originTaskId);

    long countByOriginTaskIdAndStatus(Long originTaskId, String status);

    void deleteByMemberIdAndOriginTaskId(Long memberId, Long originTaskId);
}
