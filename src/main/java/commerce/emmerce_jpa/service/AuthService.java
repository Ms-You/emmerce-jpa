package commerce.emmerce_jpa.service;

import commerce.emmerce_jpa.config.exception.ErrorCode;
import commerce.emmerce_jpa.config.exception.GlobalException;
import commerce.emmerce_jpa.config.jwt.TokenDTO;
import commerce.emmerce_jpa.config.jwt.TokenProvider;
import commerce.emmerce_jpa.domain.Member;
import commerce.emmerce_jpa.domain.RoleType;
import commerce.emmerce_jpa.dto.MemberDTO;
import commerce.emmerce_jpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${jwt.live.rtk}")
    private long refreshTokenExpiresIn;

    /**
     * 회원가입
     * @param registerReq
     * @return
     */
    @Transactional
    public void register(MemberDTO.RegisterReq registerReq) {
        passwordCorrect(registerReq.getPassword(), registerReq.getPasswordConfirm());

        Member member = Member.createMember()
                .name(registerReq.getName())
                .email(registerReq.getEmail())
                .password(passwordEncoder.encode(registerReq.getPassword()))
                .tel(registerReq.getTel())
                .birth(registerReq.getBirth())
                .point(0)
                .role(RoleType.ROLE_USER)
                .city("도시")
                .street("도로명")
                .zipcode("우편번호")
                .build();

        memberRepository.save(member);
    }

    /**
     * 사용자 이름 중복 체크
     * @param duplicateCheckReq
     * @return
     */
    public void duplicateCheck(MemberDTO.DuplicateCheckReq duplicateCheckReq) {
        if (memberRepository.findByName(duplicateCheckReq.getName()).isPresent()) {
            throw new GlobalException(ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    /**
     * 비밀번호 일치 여부 확인
     * @param password
     * @param passwordConfirm
     * @return
     */
    private void passwordCorrect(String password, String passwordConfirm) {
        if(!password.equals(passwordConfirm))
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCH);
    }

    /**
     * 로그인
     * @param loginReq
     * @return
     */
    public Mono<TokenDTO> login(MemberDTO.LoginReq loginReq) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginReq.getName(), loginReq.getPassword());

        return authenticationManager.authenticate(authentication)
                .flatMap(tokenProvider::generateToken)
                .flatMap(tokenDTO -> reactiveRedisTemplate.opsForValue().set(
                                authentication.getName(),
                                tokenDTO.getRefreshToken(),
                                Duration.ofMillis(refreshTokenExpiresIn)
                        ).thenReturn(tokenDTO)
                );
    }

}
