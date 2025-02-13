package commerce.emmerce_jpa.config.jwt;

import commerce.emmerce_jpa.config.exception.ErrorCode;
import commerce.emmerce_jpa.config.exception.GlobalException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;    // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    private final Key key;


    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 인증 정보로 토큰 생성
     * @param authentication
     * @return TokenDto
     */
    public Mono<TokenDTO> generateToken(Authentication authentication) {
        return Mono.fromCallable(() -> {
            // 권한 가져오기
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            long now = (new Date()).getTime();

            // accessToken 생성
            String accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim(AUTHORITIES_KEY, authorities)
                    .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // refreshToken 생성
            String refreshToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        });
    }


    /**
     * JWT 토큰으로 인증 정보 추출
     * @param accessToken
     * @return Authentication
     */
    public Mono<Authentication> getAuthentication(String accessToken) {
        return Mono.fromCallable(() -> {
            Claims claims = parseClaims(accessToken);

            if(claims.get(AUTHORITIES_KEY) == null) {
                throw new RuntimeException("권한 정보가 없는 토큰입니다.");
            }

            // 클레임에서 권한 정보 추출
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            // UserDetails 객체로 인증 객체 리턴
            UserDetails principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        });
    }


    /**
     * 토큰 복호화
     * @param accessToken
     * @return
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    /**
     * 토큰 검증
     * @param token
     * @return Mono<Boolean>
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key).build()
                        .parseClaimsJws(token);

                return true;
            } catch (SecurityException | MalformedJwtException e) {
                log.info("유효하지 않은 토큰입니다.", e);
                throw new GlobalException(ErrorCode.ACCESS_TOKEN_NOT_VALIDATE);
            } catch (ExpiredJwtException e){
                log.info("만료된 토큰입니다.", e);
                throw new GlobalException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            } catch (UnsupportedJwtException e){
                log.info("지원하지 않는 토큰입니다.", e);
                throw new GlobalException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
            } catch (IllegalArgumentException e){
                log.info("JWT 클레임 문자열이 비었습니다.", e);
                throw new GlobalException(ErrorCode.ACCESS_TOKEN_CLAIM_EMPTY);
            }
        });
    }


    /**
     * 토큰 만료 시간 반환
     * @param accessToken
     * @return
     */
    public Long getExpiration(String accessToken) {
        Claims claims = parseClaims(accessToken);

        return claims.getExpiration().getTime();
    }

}
