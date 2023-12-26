package us.usserver.global;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import us.usserver.global.jwt.JwtAuthenticationFilter;
import us.usserver.global.oauth.CustomOAuth2UserService;
import us.usserver.global.oauth.handler.Oauth2LoginFailureHandler;
import us.usserver.global.oauth.handler.Oauth2LoginSuccessHandler;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final Oauth2LoginFailureHandler oauth2LoginFailureHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults());

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(
                        getCustomizer(introspector,
                                //TODO: 아래 patterns는 1차 개발 이후 정리 예정
                                "/", "/login**", "/oauth**", "/member/join",
                                "/resources/**", "/favicon.ico", //resource
                                "/swagger-ui/**", "/api-docs/**" //swagger
                        )
                );

        http
                .formLogin(
                        login -> {
                            login.loginPage("/login"); //미인증자일경우 해당 uri를 호출
                            login.loginProcessingUrl("/login"); //login 주소가 호출되면 시큐리티가 낚아 채서(post로 오는것) 대신 로그인 진행 -> 컨트롤러를 안만들어도 된다.
                            login.defaultSuccessUrl("/novel/main");
                        }
                );

        http
                .oauth2Login(
                        oauth -> {
                            oauth.loginPage("/loginForm");
                            oauth.defaultSuccessUrl("/");
                            oauth.userInfoEndpoint(userService -> userService.userService(customOAuth2UserService));
                            oauth.successHandler(oauth2LoginSuccessHandler);
                            oauth.failureHandler(oauth2LoginFailureHandler);
                        }
                );

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getCustomizer(HandlerMappingIntrospector introspector, String... patterns) {
        return (request) -> {
            Arrays.stream(patterns).forEach(pattern -> {
                    request.requestMatchers(new MvcRequestMatcher(introspector, pattern)).permitAll();
            });
            request.anyRequest().authenticated();
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
