package com.project.chatting.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.exception.TokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
	
	@Value("${jwt.access-token-validity-in-seconds}")
    private int ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private int REFRESH_TOKEN_EXPIRE_TIME ;
    private static final String KEY_ROLE = "ROLE_USER";
    @Value("${jwt.secret}")
	private String key;
    
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
   
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME, key);
    }
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME, key);
    }
    private String createToken(Authentication authentication, long expireTime, String key2) {

    	Key secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    	 String authorities = authentication.getAuthorities().stream()
                 .map(auth -> auth.getAuthority())
                 .collect(Collectors.joining(","));

         long now = new Date().getTime();
         Date validity = new Date(now + expireTime);

         return Jwts.builder()
                 .setSubject(authentication.getName()) // 페이로드 주제 정보
                 .claim(KEY_ROLE, authorities)
                 .signWith(secretKey,SignatureAlgorithm.HS256)
                 .setExpiration(validity) 
                 .compact();
    }

    public String refreshAccessToken(String accessToken) {
    	UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + user.getUsername());
        validateRefreshToken(refreshToken);
        Authentication authentication = getAuthentication(refreshToken);
        return createAccessToken(authentication);
    }
  
	public Authentication getAuthentication(String token) {
    	Key secretKey = Keys.hmacShaKeyFor(key.getBytes());
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(secretKey)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (ExpiredJwtException e) {
        	// to-be jwt exception처리 추가
            throw new TokenException(String.format("토큰이 만료되었습니다.", ErrorCode.CONFLICT_MEMBER_EXCEPTION));
        }
    }
  
	public boolean validateAccessToken(String accessToken) {
		
		Key secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
		System.out.println("accessToken::::"+accessToken);
        try {
        	Jwts.parserBuilder()
        	        .setSigningKey(secretKey)  // your secret key
        	        .build()
        	        .parseClaimsJws(accessToken);
        	
        	//to-be jwt exception처리 추가
        	
        	return true;
        } catch (SecurityException | MalformedJwtException e ) {
        	log.error(e.getMessage());
            throw new ConflictException("Invalid JWT token: {}", ErrorCode.CONFLICT_MEMBER_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new TokenException("토큰이 만료되었습니다.", ErrorCode.TOKEN_EXPIRED_EXCEPTION);
        } catch (SignatureException | IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new ConflictException(String.format(e.getMessage(), ErrorCode.CONFLICT_MEMBER_EXCEPTION));
        }
    }
 
	public void validateRefreshToken(String refreshToken) {
    	Key secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        System.out.println("RefreshToken:::::" + refreshToken);
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(refreshToken);
        } catch (SecurityException | MalformedJwtException e) {
            throw new  ConflictException("Invalid JWT token: {}", ErrorCode.CONFLICT_MEMBER_EXCEPTION);
        } catch (ExpiredJwtException e) {
            throw new TokenException("토큰이 만료되었습니다.", ErrorCode.TOKEN_EXPIRED_EXCEPTION);
        } catch (IllegalArgumentException e) {
            throw new ConflictException(String.format(e.getMessage(), ErrorCode.CONFLICT_MEMBER_EXCEPTION));
        } catch (SignatureException e){
            throw new ConflictException(String.format(e.getMessage(), ErrorCode.CONFLICT_MEMBER_EXCEPTION));
        }
    }
	
	public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }
	
	public Long getExpiration(String accessToken) {
		Key secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
		Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody().getExpiration();
		
		return expiration.getTime() - new Date().getTime();
	}

    private Claims getAllClaims(String token){
        Key secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String getUserIdFromToken(String token){
        try{
            Claims claims = getAllClaims(token);
            String userId = String.valueOf(claims.getSubject());
            return userId;
        }catch(ExpiredJwtException e){
            Claims exClaims = e.getClaims();
            String exUserId = String.valueOf(exClaims.getSubject());
            return exUserId;
        }
    }
    
    public void deleteAccessToken(HttpServletRequest request) {
    	String accessToken = resolveToken(request);
    	
    	if (validateAccessToken(accessToken)) {
    		//레디스에서 해당 id-토큰 삭제
			redisTemplate.delete("RT:"+getUserIdFromToken(accessToken));
			
			//엑세스 토큰 남은 유효시간
	        Long expiration = getExpiration(accessToken);
	        System.out.println("expiration Time: "+expiration);
	        
	        //로그아웃 후 유효한 토큰으로 접근가능하기 때문에 만료전 로그아웃된 accesstoken은 블랙리스트로 관리
	        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    	}
    	
    }

}
