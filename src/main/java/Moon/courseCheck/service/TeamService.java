package Moon.courseCheck.service;

import Moon.courseCheck.domain.*;
import Moon.courseCheck.dto.DistributedTaskDto;
import Moon.courseCheck.dto.TeamSelectionDto;
import Moon.courseCheck.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final OriginTaskRepository originTaskRepository;
    private final DistributedTaskRepository distributedTaskRepository;
    private final TeamManagerRepository teamManagerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamOriginTaskRepository teamOriginTaskRepository;




    public TeamService(TeamRepository teamRepository, MemberRepository memberRepository, OriginTaskRepository originTaskRepository, DistributedTaskRepository distributedTaskRepository, TeamManagerRepository teamManagerRepository, TeamMemberRepository teamMemberRepository, TeamOriginTaskRepository teamOriginTaskRepository) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.originTaskRepository = originTaskRepository;
        this.distributedTaskRepository = distributedTaskRepository;
        this.teamManagerRepository = teamManagerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamOriginTaskRepository = teamOriginTaskRepository;
    }

    public Map<String, Boolean> checkCreateTeamValid(String name, String inviteCode){
        Map<String, Boolean> isValidList = new HashMap<>();
        isValidList.put("name", !teamRepository.existsTeamByName(name));
        isValidList.put("inviteCode", !teamRepository.existsTeamByInviteCode(inviteCode));
        return isValidList;
    }
    //team을 만든다. 만들면서 동시에 만든 인원을 팀에 소속시키고 매니저로 지정한다.
    public Long createTeam(Long memberId, String name, String inviteCode){
        //Team 만들고 멤버 추가
        Team team = new Team();
        team.setName(name);
        team.setInviteCode(inviteCode);
        team = teamRepository.save(team);
        Long teamId = team.getId();

        //Member 정보에 Team 소속 정보 업데이트.
        TeamMember teamMember = new TeamMember();
        teamMember.setMemberId(memberId);
        teamMember.setTeamId(teamId);
        teamMemberRepository.save(teamMember);
        //TeamManager 설정.
        TeamManager teamManager = new TeamManager();
        teamManager.setTeamId(teamId);
        teamManager.setMemberId(memberId);
        teamManagerRepository.save(teamManager);

        return teamId;

    }

    //해당 팀의 이름을 가져온다.
    public String getTeamName(Long teamId){
        return teamRepository.findById(teamId).get().getName();
    }
    //해당 유저가 속한 모든 팀의 정보를 가져온다.
    public List<TeamSelectionDto> getTeamList(Long memberId){
        List<TeamSelectionDto> teamSelectionDtoList= new ArrayList<>();
        for (TeamMember teamMember : teamMemberRepository.findAllByMemberId(memberId)){
            Team team = teamRepository.findById(teamMember.getTeamId()).get();
            Long numberOfMembers = teamMemberRepository.countAllByTeamId(teamMember.getTeamId());
            boolean isManager = teamManagerRepository.existsTeamManagerByMemberIdAndTeamId(memberId, team.getId());
            teamSelectionDtoList.add(TeamSelectionDto.getInstance(team, numberOfMembers, isManager));
        }
        return teamSelectionDtoList;
    }


    //특정 유저에게 할당된, 해당 팀의 모든 태스크를 다 가져온다.
    public List<DistributedTaskDto> getTaskList(Long teamId, Long memberId){
        List<DistributedTaskDto> distributedTaskDtoList = new ArrayList<>();
        List<TeamOriginTask> teamOriginTaskList = teamOriginTaskRepository.findAllByTeamId(teamId);

        for (TeamOriginTask teamOriginTask : teamOriginTaskList){
            Optional<DistributedTask> distTask = distributedTaskRepository.findByMemberIdAndOriginTaskId(memberId, teamOriginTask.getOriginTaskId());
            if (distTask.isPresent()) {
                if (originTaskRepository.findById(teamOriginTask.getOriginTaskId()).isPresent()){
                    distributedTaskDtoList.add(DistributedTaskDto.getInstance(distTask.get(), originTaskRepository.findById(teamOriginTask.getOriginTaskId()).get()));
                }
            }
        }
        return distributedTaskDtoList;
    }

    //특정 task의 상세보기를 가져온다.
    public DistributedTaskDto getTask(Long originTaskId, Long memberId){

        Optional<DistributedTask> distTask = distributedTaskRepository.findByMemberIdAndOriginTaskId(memberId, originTaskId);
        Optional<OriginTask> originTask = originTaskRepository.findById(originTaskId);


        DistributedTaskDto dto = DistributedTaskDto.getInstance(distTask.get(), originTask.get());

        return dto;

    }

    //해당 팀에 task를 더한다.
    public void createTask(Long memberId, Long teamId, String title, String content){
        if (!managerCheck(memberId, teamId)) return;

        OriginTask newOriginTask = new OriginTask();
        newOriginTask.setTitle(title);
        newOriginTask.setContent(content);
        newOriginTask = originTaskRepository.save(newOriginTask);

        TeamOriginTask teamOriginTask = new TeamOriginTask();
        teamOriginTask.setTeamId(teamId);
        teamOriginTask.setOriginTaskId(newOriginTask.getId());
        teamOriginTaskRepository.save(teamOriginTask);



        for (TeamMember member : teamMemberRepository.findAllByTeamId(teamId)){
            DistributedTask distTask = new DistributedTask();
            distTask.setOriginTaskId(newOriginTask.getId());
            distTask.setStatus(Status.IDLE.getLabel());
            distTask.setMemberId(member.getMemberId());
            distributedTaskRepository.save(distTask);
        }
    }

    //해당 팀에 멤버가 추가된다.
    public void joinMemberByUsername(String username, String inviteCode){
        Optional<Member> memberOptional = memberRepository.findMemberByUsername(username);
        Optional<Team> teamOptional = teamRepository.findTeamByInviteCode(inviteCode);
        if (memberOptional.isEmpty()) return;
        if (teamOptional.isEmpty()) return;
        Long memberId = memberOptional.get().getId();
        Long teamId = teamOptional.get().getId();

        joinMember(memberId, teamId);
    }
    public void joinMember(Long memberId, Long teamId){

        if (teamMemberRepository.existsTeamMemberByTeamIdAndMemberId(teamId, memberId)) return;

        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setMemberId(memberId);

        teamMemberRepository.save(teamMember);

        for (TeamOriginTask teamOriginTask : teamOriginTaskRepository.findAllByTeamId(teamId)){
            DistributedTask distTask = new DistributedTask();
            distTask.setMemberId(memberId);
            distTask.setOriginTaskId(teamOriginTask.getOriginTaskId());
            distTask.setStatus("idle");
            distributedTaskRepository.save(distTask);
        }
    }

    //해당 팀에서 멤버가 나간다.
    @Transactional
    public void fireMember(Long memberId, Long teamId){
        teamMemberRepository.deleteTeamMemberByMemberIdAndTeamId(memberId, teamId);
        for (TeamOriginTask teamOriginTask: teamOriginTaskRepository.findAllByTeamId(teamId)){
            distributedTaskRepository.deleteByMemberIdAndOriginTaskId(memberId, teamOriginTask.getId());
        }
    }





    //관리자인지 확인한다. true는 관리자, false는 관리자 아님.
    public boolean managerCheck(Long memberId, Long teamId){
        return teamManagerRepository.existsTeamManagerByMemberIdAndTeamId(memberId, teamId);
    }

    //관리자가 관리자를 추가한다.
    public boolean managerAdd(Long memberId, Long targetMemberId, Long teamId){
        if (managerCheck(memberId, teamId)){
            TeamManager teamManager = new TeamManager();
            teamManager.setMemberId(targetMemberId);
            teamManager.setTeamId(teamId);
            teamManagerRepository.save(teamManager);
            return true;
        }
        else return false;
    }


    //invite 코드에 따른 팀 아이디를 가져온다.
    public Long getTeamIdByInviteCode(String inviteCode){
        Optional<Team> targetTeam = teamRepository.findTeamByInviteCode(inviteCode);
        if (targetTeam.isPresent()){
            return targetTeam.get().getId();
        }
        else return -1l;
    }

    public boolean checkMemberInTeam(Long teamId, Long memberId){
        return teamMemberRepository.existsTeamMemberByTeamIdAndMemberId(teamId, memberId);
    }



}
