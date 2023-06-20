package Moon.courseCheck.service;

import Moon.courseCheck.domain.Member;
import Moon.courseCheck.domain.Role;
import Moon.courseCheck.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserAuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Boolean> register(String username, String password, String nickname){
        Map<String, Boolean> registerValid = new HashMap<>();

        if (memberRepository.existsMemberByUsername(username)) registerValid.put("username", false);
        else registerValid.put("username", true);
        if (memberRepository.existsMemberByNickname(nickname)) registerValid.put("nickname", false);
        else registerValid.put("nickname", true);

        if (registerValid.get("username") && registerValid.get("nickname")){
            Member member = new Member();
            member.setUsername(username);
            member.setPassword(passwordEncoder.encode(password));
            member.setRole(Role.USER);
            member.setNickname(nickname);
            memberRepository.save(member);
        }

        return registerValid;
    }
}
