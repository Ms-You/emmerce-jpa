package commerce.emmerce_jpa.config;

import commerce.emmerce_jpa.domain.Member;
import commerce.emmerce_jpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> {
            Optional<Member> optionalMember = memberRepository.findByName(username);
            return optionalMember.map(user -> User.withUsername(user.getName())
                            .password(user.getPassword())
                            .authorities(getAuthorities(user))
                            .accountExpired(false)
                            .accountLocked(false)
                            .credentialsExpired(false)
                            .disabled(false)
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        });
    }

    private Collection<GrantedAuthority> getAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().toString()));
        return authorities;
    }
}