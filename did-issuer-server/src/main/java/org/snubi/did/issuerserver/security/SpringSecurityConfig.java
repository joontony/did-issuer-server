package org.snubi.did.issuerserver.security;
/*
config.setAllowedOriginPatterns(List.of("*"));
config.setAllowedMethods(List.of("*"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(false or true);

Access-Contorl-Allow-Origin : CORS 요청을 허용할 사이트 (e.g. https://oddpoet.net)
Access-Contorl-Allow-Method : CORS 요청을 허용할 Http Method들 (e.g. GET,PUT,POST)
Access-Contorl-Allow-Headers : 특정 헤더를 가진 경우에만 CORS 요청을 허용할 경우
Access-Contorl-Allow-Credencial : 자격증명과 함께 요청을 할 수 있는지 여부., 해당 서버에서 Authorization로 사용자 인증도 서비스할 것이라면 true로 응답해야 할 것이다.
Access-Contorl-Max-Age : preflight 요청의 캐시 시간.
 */

import lombok.AllArgsConstructor;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.exception.CustomAccessDeniedHandler;
import org.snubi.did.issuerserver.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfig {	
	
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
      return new CustomAccessDeniedHandler();
    }
	
	@Bean
	public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
	      return new CustomAuthenticationEntryPoint();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(CustomConfig.allowedOrigins);
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}	
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {       
		
		AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
		http.csrf().disable(); // 403 Forbidden 에러 csrf 체크하면 post 가 정상적으로 수행되지 않는다.
		http 
		.cors().configurationSource(corsConfigurationSource()).and()
		.authorizeHttpRequests(authorize -> authorize
//				.antMatchers("/club/notice/**").authenticated()
//				.antMatchers("/club/auth/**").authenticated()
//				.antMatchers("/credential/**").authenticated()
				.anyRequest().permitAll()
		)
		.exceptionHandling()
		.accessDeniedHandler(customAccessDeniedHandler()) 			// 권한 
		.authenticationEntryPoint(customAuthenticationEntryPoint()) // 인증 
		.and()
		.addFilter(new PlatformJWTAuthorizationFilter(authenticationManager))
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
        return http.build();
    }
	
	
	
	
//  클래스 생성해서 @Bean 등록안하고 바로 GlobalExceptionHandler 로 넘기자.
//	private AuthenticationEntryPoint unauthorizedEntryPoint =
//			(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	
//	@Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/FormDesigner/**", "/css/**", "/script/**", "/image/**", "/fonts/**", "/lib/**","/platform/code/**","/platform-websocket/**","/agent/**","/sync/download/**");
//    }

}




