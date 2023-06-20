package Moon.courseCheck.controller;

import Moon.courseCheck.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {
    private final UserAuthService userAuthService;

    @Autowired
    public LoginController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "html/login";
    }

    //Register Form
    @GetMapping("/register")
    public String getRegisterForm(){
        return "html/register";
    }

    //Register
    @PostMapping("/register")
    public String register(Model model, @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("nickname") String nickname){
        System.out.println("레지스터 요청");
        Map<String, Boolean> registerValid = userAuthService.register(username, password, nickname);
        if (registerValid.get("username") && registerValid.get("nickname")) return "redirect:/login";
        else {
            model.addAttribute("registerValid", registerValid);
            return "html/register";
        }
    }
}
