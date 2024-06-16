package com.project.chatting.auth;


import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.exception.TokenException;
import com.project.chatting.exception.UnAuthorizedException;
import com.project.chatting.user.entity.User;
import com.project.chatting.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    // 토큰 검증이 필요 없는 url
    private static final String[] WHITELIST = {
      "/user/auth/signin", // 로그인
      "/user/auth/signup"     // 회원가입
    };
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;
    
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("path: " + path);
        boolean flag = false;

        if (Arrays.stream(WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = resolveToken(request);
        try {
            // Access Token 유효성 검사
        	// to-be jwt exception 추가 개발 필요 (현재는 500에러 내려옴)

            // accessToken이 없다면, 로그인 되지 않은 상태로 간주
            if(accessToken == null) {
                request.setAttribute("exception", new UnAuthorizedException("로그인이 필요합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION));
            }else{
                if(jwtTokenProvider.validateAccessToken(accessToken)) {
                
                    String blToken = (String)redisTemplate.opsForValue().get(accessToken);
                    // 해당 토큰이 블랙리스트 처리된 토큰인지 확인
                    if (ObjectUtils.isEmpty(blToken)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            // if(jwtTokenProvider.validateAccessToken(accessToken)) {
                
            // 	String blToken = (String)redisTemplate.opsForValue().get(accessToken);
            // 	// 해당 토큰이 블랙리스트 처리된 토큰인지 확인
            // 	if (ObjectUtils.isEmpty(blToken)) {
            // 		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            //     	SecurityContextHolder.getContext().setAuthentication(authentication);
            // 	}
            // }
            // else if(accessToken != null && path.equals("/auth/refresh")){
            //     String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            //     System.out.println("UserId Of Token : " + userId);
            //     ValueOperations<String, String> vop = stringRedisTemplate.opsForValue();
            //     String refreshToken = (String)vop.get("RT:" + userId);
            //     if(refreshToken == null) {
            //         request.setAttribute("exception", new TokenException("토큰이 만료되었습니다.", ErrorCode.TOKEN_EXPIRED_EXCEPTION));
            //     }else{
            //         try{
            //             System.out.println("RefreshToken = " + refreshToken); 
            //             jwtTokenProvider.validateRefreshToken(refreshToken);
    
            //             Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
            //             SecurityContextHolder.getContext().setAuthentication(authentication);
    
            //         }catch(ConflictException e){
            //             throw new ConflictException(e.getMessage());
            //         }catch(ExpiredJwtException e){
            //             throw new TokenException(e.getMessage());
            //         } catch(IllegalArgumentException e){
            //             throw new ConflictException(e.getMessage());
            //         }
            //         System.out.println("refresh Token 유효성 통과");
            //     }
            // }
        } catch (UnAuthorizedException e) {
            request.setAttribute("exception", new UnAuthorizedException("로그인이 필요합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION));
        	//throw new TokenException("토큰이 유효하지 않습니다..", ErrorCode.TOKEN_EXPIRED_EXCEPTION);
        } catch (TokenException e){
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            System.out.println("UserId Of Token : " + userId);
            ValueOperations<String, String> vop = stringRedisTemplate.opsForValue();
            String refreshToken = (String)vop.get("RT:" + userId);
            if(refreshToken == null) {
                flag = false;
                request.setAttribute("exception", new TokenException("토큰이 만료되었습니다. 다시 로그인해주세요.", ErrorCode.TOKEN_EXPIRED_EXCEPTION));
            }else{
                refreshToken = refreshToken.replace("\"","");

                try{
                    System.out.println("RefreshToken = " + refreshToken); 
                    jwtTokenProvider.validateRefreshToken(refreshToken);

                    User userDetails = userRepository.findMemberById(userId);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, userDetails.getPassword());
                    String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
                    RefreshToken token = new RefreshToken(authentication.getName(), newAccessToken, refreshToken);

                    authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setHeader("Authorization", "Bearer " + newAccessToken);
                    httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                    HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest){
                        @Override
                        public String getHeader(String name){
                            if("Authorization".equalsIgnoreCase(name)){
                                return "Bearer " + token.getAccessToken();
                            }
                            return super.getHeader(name);
                        }
                    };
                    flag = true;
                    filterChain.doFilter(requestWrapper, response);
                }catch(ConflictException ex){
                    throw new ConflictException(ex.getMessage());
                }catch(ExpiredJwtException ex){
                    throw new TokenException(ex.getMessage());
                } catch(IllegalArgumentException ex){
                    throw new ConflictException(ex.getMessage());
                } catch(SignatureException ex){
                    throw new ConflictException(ex.getMessage());
                }
                System.out.println("refresh Token 유효성 통과");
            }
        }catch(ConflictException e){
            request.setAttribute("exception", new ConflictException("유효하지 않은 토큰입니다.", ErrorCode.CONFLICT_TOKEN_EXCEPTION));
        }
        
        if(!flag) {
            System.out.println("그냥 실행");
            filterChain.doFilter(request, response);
        }
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }
}