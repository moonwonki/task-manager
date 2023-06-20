package Moon.courseCheck.repository;

import Moon.courseCheck.domain.OriginTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OriginTaskRepository extends JpaRepository<OriginTask, Long> {
}
