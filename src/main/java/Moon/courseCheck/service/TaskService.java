package Moon.courseCheck.service;

import Moon.courseCheck.domain.DistributedTask;
import Moon.courseCheck.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamManagerRepository teamManagerRepository;

    private final OriginTaskRepository originTaskRepository;
    private final DistributedTaskRepository distributedTaskRepository;

    public TaskService(TeamRepository teamRepository, MemberRepository memberRepository, TeamManagerRepository teamManagerRepository, OriginTaskRepository originTaskRepository, DistributedTaskRepository distributedTaskRepository) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.teamManagerRepository = teamManagerRepository;
        this.originTaskRepository = originTaskRepository;
        this.distributedTaskRepository = distributedTaskRepository;
    }


    public Map<String, Long> countAllRate(Long originTaskId){
        Map<String, Long> count = new HashMap<>();
        count.put("successCount", countSuccessRate(originTaskId));
        count.put("errorCount", countErrorRate(originTaskId));
        count.put("idleCount", countIdleRate(originTaskId));
        return count;
    }

    public Long countIdleRate(Long originTaskId) {
        return distributedTaskRepository.countByOriginTaskIdAndStatus(originTaskId, "Idle");
    }

    public Long countErrorRate(Long originTaskId) {
        return distributedTaskRepository.countByOriginTaskIdAndStatus(originTaskId, "Error");
    }

    public Long countSuccessRate(Long originTaskId){
        return distributedTaskRepository.countByOriginTaskIdAndStatus(originTaskId, "Success");
    }

    //특정 유저의 task 처리 상태를 처리한다.
    public void updateMemberStatusOnTask(Long memberId, Long originTaskId, String status){
        Optional<DistributedTask> distTask = distributedTaskRepository.findByMemberIdAndOriginTaskId(memberId, originTaskId);
        if (distTask.isPresent()){
            distTask.get().setStatus(status);
            distributedTaskRepository.save(distTask.get());
        }

    }

    //관리자가 특정 task를 지운다.
    public void deleteTask(Long memberId, Long teamId, Long originTaskId){
        originTaskRepository.deleteById(originTaskId);
        for (DistributedTask distTask : distributedTaskRepository.findAllByOriginTaskId(originTaskId)){
            distributedTaskRepository.deleteById(distTask.getId());
        }
    }
}
