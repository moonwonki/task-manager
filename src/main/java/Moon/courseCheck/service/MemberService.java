package Moon.courseCheck.service;

import Moon.courseCheck.repository.MemberRepository;
import org.springframework.stereotype.Service;


@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long getMemberIdByUsername(String username){
        return memberRepository.findMemberByUsername(username).get().getId();
    }
}
