package Moon.courseCheck.controller;

import Moon.courseCheck.dto.DistributedTaskDto;
import Moon.courseCheck.dto.TeamSelectionDto;
import Moon.courseCheck.service.JwtService;
import Moon.courseCheck.service.MemberService;
import Moon.courseCheck.service.TaskService;
import Moon.courseCheck.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


//팀의 멤버인지 구분하는 과정이 필요함.
//팀의 매니저인지 구분하는 과정이 필요함.
@Controller
public class HomeController {
    private final TeamService teamService;
    private final MemberService memberService;
    private final TaskService taskService;

    public HomeController(TeamService teamService, JwtService jwtService, MemberService memberService, TaskService taskService) {
        this.teamService = teamService;

        this.memberService = memberService;
        this.taskService = taskService;
    }

    //Home
    @GetMapping("/home")
    public String home(Authentication authentication, Model model){

        if (authentication != null && authentication.isAuthenticated()){
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        else model.addAttribute("username", "Guest");

        return "html/home";
    }


    @GetMapping("/join")
    public String getJoinTeam(Model model){
        return "html/TeamJoin";
    }

    //팀 참가하기
    @PostMapping("/join")
    public String joinTeam(Model model, @RequestParam("inviteCode") String inviteCode, Authentication authentication){
        String username = authentication.getName();
        teamService.joinMemberByUsername(username, inviteCode);
        return "redirect:/home";
    }

    //팀 선택 메뉴 가져오기
    @GetMapping("/teamSelect")
    public String teamSelectMenu (Model model, Authentication authentication){
        List<TeamSelectionDto> teamList = teamService.getTeamList(memberService.getMemberIdByUsername(authentication.getName()));
        model.addAttribute("teamList", teamList);
        return "html/TeamSelect";
    }

    //특정 팀 메뉴 가져오기
    @GetMapping("/team/{teamId}")
    public String getTeamMenu (Model model, @PathVariable("teamId") Long teamId, Authentication authentication){
        List<DistributedTaskDto> distributedTaskDto;
        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);

        if (!teamService.checkMemberInTeam(teamId, memberId)){
            return "redirect:/home";
        }
        model.addAttribute("teamName", teamService.getTeamName(teamId));
        model.addAttribute("teamId", teamId);

        distributedTaskDto = teamService.getTaskList(teamId, memberId);
        model.addAttribute("taskList", distributedTaskDto);
        if (teamService.managerCheck(memberId, teamId)){
            //manager인 상황
            model.addAttribute("isManager", true);
        }
        else {
            //manager가 아닌 상황.
            model.addAttribute("isManager", false);
        }

        return "html/Team";
    }



    //팀 생성 폼 가져오기
    @GetMapping("/createTeam")
    public String getTeamForm (Model model, Authentication authentication){

        model.addAttribute("isValidList", null);
        return "html/TeamForm";
    }
    //팀 생성하기
    @PostMapping("/createTeam")
    public String createTeam (Model model, Authentication authentication, @RequestParam("name") String teamName, @RequestParam("inviteCode") String inviteCode){
        Long memberId = memberService.getMemberIdByUsername(authentication.getName());
        Map<String, Boolean> isValidList = teamService.checkCreateTeamValid(teamName, inviteCode);
        if (!isValidList.get("name") || !isValidList.get("inviteCode")){
            model.addAttribute("isValidList", isValidList);
            return "redirect:/createTeam";
        }
        Long teamId = teamService.createTeam(memberId, teamName, inviteCode);
        return "redirect:/team/" + teamId;
    }

    //태스크 생성 폼 가져오기
    @GetMapping("/{teamId}/createTask")
    public String getTaskForm (Model model, Authentication authentication, @PathVariable("teamId") Long teamId){
        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);
        if (!teamService.managerCheck(memberId, teamId)){
            return "redirect:/home";
        }
        model.addAttribute("teamId", teamId);
        return "html/TaskForm";
    }
    //태스크 생성하기
    @PostMapping("/{teamId}/createTask")
    public String createTask (Model model, Authentication authentication, @PathVariable("teamId") Long teamId, @RequestParam("title") String title, @RequestParam("content") String content) {
        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);
        if (!teamService.managerCheck(memberId, teamId)){
            return "redirect:/home";
        }
        teamService.createTask(memberService.getMemberIdByUsername(authentication.getName()), teamId,title, content);
        return "redirect:/team/" + teamId;
    }

    //태스크 확인하기
    @GetMapping("/task/{teamId}/{originTaskId}")
    public String getOriginTask(Model model, Authentication authentication, @PathVariable("teamId") Long teamId, @PathVariable("originTaskId") Long originTaskId){

        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);
        if (!teamService.checkMemberInTeam(teamId, memberId)){
            return "redirect:/home";
        }

        model.addAttribute("teamId", teamId);

        DistributedTaskDto dto = teamService.getTask(originTaskId, memberId);
        model.addAttribute("dto", dto);

        boolean isManager = teamService.managerCheck(memberId, teamId);
        model.addAttribute("isManager", isManager);

        if (isManager){
            model.addAttribute("count", taskService.countAllRate(originTaskId));
        }

        return "html/Task";
    }

    //태스크 상태 바꾸기
    @PostMapping("/task/{teamId}/{taskId}/{status}")
    public String updateTaskStatus(Model model, Authentication authentication, @PathVariable("teamId") Long teamId, @PathVariable("taskId") Long originTaskId, @PathVariable("status") String status){
        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);
        taskService.updateMemberStatusOnTask(memberId, originTaskId, status);
        return "redirect:/team/" + teamId;
    }
    //th:href="@{/task/{teamId}/{taskId}/error (teamId = ${teamId}, taskId = ${originTaskId})}"

    @PostMapping("task/{teamId}/{taskId}/delete")
    public String deleteTask(Authentication authentication, @PathVariable("teamId") Long teamId, @PathVariable("taskId") Long originTaskId){
        String username = authentication.getName();
        Long memberId = memberService.getMemberIdByUsername(username);
        if (!teamService.managerCheck(memberId, teamId)) return "redirect:/home";

        taskService.deleteTask(memberId, teamId, originTaskId);
        return "redirect:/team/" + teamId;
    }

}
