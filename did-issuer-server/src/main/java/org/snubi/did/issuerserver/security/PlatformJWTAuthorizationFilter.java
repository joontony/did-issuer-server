package org.snubi.did.issuerserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.lib.date.DateUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class PlatformJWTAuthorizationFilter extends BasicAuthenticationFilter {

    public PlatformJWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(CustomConfig.strResponseAuthHeader);
        log.info("Filter HttpServletRequest URI [{}] ",request.getRequestURI());

        if (header == null || header.startsWith(CustomConfig.strTokenPrefix) == false) {
//        	log.error("인증정보 HEADER 없다. [{},{},{}]",header,request.getMethod(),request.getLocalAddr());
            chain.doFilter(request, response);            
            return;
        }
        try {
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@@@@@@@@@@@@@@@@ : " + getAuthentication(request));
        	UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(Exception e) {     
        	// jwt 토큰 문자열 수정해서 테스트할때 
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        chain.doFilter(request, response);
    }
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {    	
    	
    	String token = request.getHeader(CustomConfig.strResponseAuthHeader);
    	log.debug("Filter token [{}] ",token);
        if(token != null) {        	
    		Claims clsClaims 		= Jwts.parser().setSigningKey(CustomConfig.strSecrete.getBytes()).parseClaimsJws(token.replace(CustomConfig.strTokenPrefix, "")).getBody();
        	String strUser 			= clsClaims.getSubject();
        	log.info("Filter strUser [{}] ",strUser);
        	Date dateExpiredInToken = DateUtil.toDate("yyyyMMddHHmmss",String.valueOf(clsClaims.get(CustomConfig.strResponseAuthClaimIssue)));
        	Date dateCurrent	 	= DateUtil.getThisDate();
        	if (dateExpiredInToken.before(dateCurrent)) {
        		log.info("요청URL [{}]",request.getRequestURI());
        		log.warn("인증토큰이 만료되었습니니다. [{}:{}]",dateExpiredInToken,dateCurrent);
        		return null;
        	}
        	if(strUser != null) {  
               return new UsernamePasswordAuthenticationToken(strUser, null, new ArrayList<>());
        	}        	
            return null;
        }
        return null;
    }
}