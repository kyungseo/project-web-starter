/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 *
 * Original Code: https://github.com/Baeldung/spring-security-registration
 * @author Baeldung, modified by Kyungseo Park
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================================
 * Author     Date            Description
 * --------   ----------      -------------------------------------------------
 * Kyungseo   2023-03-02      initial version
 * ========================================================================= */

package kyungseo.poc.simple.web.site.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

//import com.maxmind.geoip2.DatabaseReader;
//import com.maxmind.geoip2.exception.GeoIp2Exception;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.geoip.DatabaseReader;
import kyungseo.poc.simple.web.security.components.CustomRememberMeServices;
import kyungseo.poc.simple.web.security.components.google2fa.CustomAuthenticationProvider;
import kyungseo.poc.simple.web.security.components.google2fa.CustomWebAuthenticationDetailsSource;
import kyungseo.poc.simple.web.security.components.location.DifferentLocationChecker;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = { "kyungseo.poc.simple.web.security.components" })
//@ImportResource({ "classpath:webSecurityConfig.xml" })
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    // JWT 사용하지 않음
    //@Autowired
    //private JwtAuthenticationEntryPoint jwtEntryPoint;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authProvider())
            .build();
    }

    // JWT 사용하지 않음
    /*
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    */

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setPostAuthenticationChecks(differentLocationChecker());
        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 해당 경로의 파일들은 spring security가 무시
        return (web) -> web.ignoring()
            .antMatchers("/resources/**")
            .antMatchers("/h2/**") // h2-console 접근 경로
            .antMatchers("/vendor/**");
    }

    // 스프링 시큐리티에서 사용 가능한 SpEL
    //   hasAnyRole(역할 목록): 사용자가 역할 목록 중 하나라도 역할이 있는 경우 참
    //   hasRole(역할): 사용자가 주어진 역할이 있는 경우 참
    //   hasIpAddress(IP 주소): 주어진 IP 주소로부터 요청이 오는 경우 참
    //   isAnonymous(): 사용자가 익명인 경우 참
    //   isAuthenticated(): 사용자가 인증된 경우 참
    //   isFullyAuthenticated(): 사용자가 완전히 인증된 경우 참 (remember-me로는 인증되지 않음)
    //   ...
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
          .and()
            .csrf().disable()
            .exceptionHandling()
            // JWT 사용할 경우
            //.authenticationEntryPoint(jwtEntryPoint)      // 인증에 실패한 경우: 401(UNAUTHORIZED)
            //.and()
            // JWT 사용할 경우: Token(JWT) 기반 인증이기 때문에 Session 사용하지 않음
            //.sessionManagement()
            //.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 또는 SessionCreationPolicy.NEVER

            .and()
            .authorizeRequests() // 요청에 대한 사용권한 체크

            // ============================================================
            // 'Static Contents'의 경우, 익명 허용
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers("/js/**").permitAll()
            .antMatchers("/js/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/img/**", "/image/**", "/images/**").permitAll()
            .antMatchers("/font/**", "/fonts/**").permitAll()
            .antMatchers("/file/**", "/files/**").permitAll()
            .antMatchers("/vendor/**").permitAll()
            .antMatchers("/configuration/**", "/api-docs/**", "/v3/api-docs/**", "/swagger*/**").permitAll() // springdoc, swagger
            .antMatchers("/webjars/**").permitAll()
            // 'Static Contents' - END

            // ============================================================
            // Spring Security 관련 페이지들
            .antMatchers("/view/common/sec/login*", "/view/common/sec/logout*").permitAll()
            .antMatchers("/view/common/sec/signin/**", "/view/common/sec/signup/**").permitAll()
            //.antMatchers("/view/common/auth/**").permitAll() // JWT 사용하지 않음
            .antMatchers("/view/common/sec/registration*").permitAll()
            .antMatchers("/view/common/sec/registrationConfirm*").permitAll()
            .antMatchers("/view/common/sec/forgetPassword*").permitAll()
            .antMatchers("/view/common/sec/changePassword*").permitAll()
            .antMatchers("/view/common/sec/successRegister*").permitAll()
            .antMatchers("/view/common/sec/emailError*").permitAll()
            .antMatchers("/view/common/sec/expiredAccount*").permitAll()
            .antMatchers("/view/common/sec/badUser*").permitAll()
            .antMatchers("/view/common/sec/enableNewLoc*").permitAll()
            .antMatchers("/view/common/sec/qrcode*").permitAll()
            .antMatchers("/view/common/sec/updatePasswordProc").permitAll()

            .antMatchers("/view/common/sec/invalidSession*").anonymous()
            .antMatchers("/view/common/sec/updatePassword").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")

            .antMatchers("/view/common/sec/admin").hasAnyRole("ADMIN")
            .antMatchers("/view/common/sec/users").hasAnyRole("ADMIN", "STAFF", "USER")
            .antMatchers("/view/common/sec/twoFactor").hasAnyRole("ADMIN", "STAFF", "USER")
            .antMatchers("/view/common/sec/roleHierarchy*").hasAnyRole("ADMIN", "STAFF", "USER")
            // 'Spring Security' - END

            // ============================================================
            // View 경로들에 대한 접근권한 설정
            .antMatchers("/", "/index", "/home", "/view/home").permitAll() // 메인 페이지
            .antMatchers("/view/common/error/**").permitAll()
            .antMatchers("/view/sample/**").permitAll() // TODO 삭제할 것

            .antMatchers("/view/admin/**").hasRole("ADMIN")
            .antMatchers("/view/staff/**").hasRole("STAFF")
            .antMatchers("/view/user/**").hasRole("USER")
            // 'View' - END

            // ============================================================
            // API 경로들에 대한 접근권한 설정
            //.antMatchers("/api/v1/user/sec/**").permitAll()
            .antMatchers("/api/v*/user/sec/registration*").permitAll()
            .antMatchers("/api/v*/user/sec/resetPassword").permitAll()
            .antMatchers("/api/v*/user/sec/savePassword*").permitAll()
            .antMatchers("/api/v*/user/sec/resendRegistrationToken*").permitAll()

            .antMatchers("/api/v*/user/sec/notice").hasAnyRole("ADMIN", "STAFF", "USER")
            .antMatchers("/api/v*/user/sec/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
            .antMatchers("/api/v*/user/sec/update/2fa").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")

            //.antMatchers("/api/v*/auth/**").permitAll() // JWT 사용하지 않음
            .antMatchers("/api/v*/common/**").permitAll()
            .antMatchers("/api/v*/sample/**").permitAll() // TODO 삭제할 것

            .antMatchers("/api/v*/admin/**").hasRole("ADMIN")
            .antMatchers("/api/v*/staff/**").hasRole("STAFF")
            .antMatchers("/api/v*/user/**").hasRole("USER")
            //.antMatchers(HttpMethod.POST, "/api/v1/users/**").hasAnyRole("ADMIN")
            //.antMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "USER")
            // 'API' - END

            // ============================================================
            // 이하, 상기 패턴에 해당되지 않는 모든 요청들은 최소 읽기 권한 필요
            .anyRequest().hasAuthority("READ_PRIVILEGE")
            // '이하 모든 요청' - END

            // ============================================================
            // Spring Security > login 관련 설정
            .and()
            .formLogin()
            .loginPage(AppConstants.AUTH_LOGIN_URL)
            .defaultSuccessUrl(AppConstants.MAIN_HOME_URL) // 메인 페이지
            .failureUrl(AppConstants.AUTH_LOGIN_FAIL_URL)
            .successHandler(customAuthenticationSuccessHandler)
            .failureHandler(customAuthenticationFailureHandler)
            .authenticationDetailsSource(authenticationDetailsSource)
            .permitAll()
            // 'Spring Security > login' - END

            // ============================================================
            // Spring Security > session 관련 설정
            .and()
            .sessionManagement()
            .invalidSessionUrl(AppConstants.AUTH_SESSION_INVALID_URL)
            .maximumSessions(1) // 중복로그인 허용하지 않음
            .expiredUrl(AppConstants.AUTH_SESSION_EXPIRED_URL)
            .sessionRegistry(sessionRegistry())

            .and()
            .sessionFixation()
            .none()
            // 'Spring Security > session' - END

            // ============================================================
            // Spring Security > logout 관련 설정
            .and()
            .logout()
            .logoutUrl(AppConstants.AUTH_LOGOUT_API) // 로그아웃 처리 URL
            .logoutSuccessHandler(customLogoutSuccessHandler)
            .invalidateHttpSession(true) // 로그아웃 시 세션정보를 제거할 지 여부를 지정한다. 기본값은 TRUE이고 세션정보를 제거
            .logoutSuccessUrl(AppConstants.AUTH_LOGOUT_SUCCESS_URL) // 로그아웃이 처리된 후 이동될 경로
            .deleteCookies(AppConstants.SESSION_COOKIES_NAME) // 로그아웃 시 제거할 쿠키이름을 지정
            .permitAll()
            // 'Spring Security > logout' - END

            // ============================================================
            // Spring Security > remember-me 관련 설정
            // remember-me 쿠키 사이클
            //   - 로그인(인증) 성공: remember-me 쿠키 발급
            //   - 로그인(인증) 실패: remember-me 쿠키가 있다면 무효화
            //   - 로그아웃: remember-me 쿠키가 있다면 무효화
            //   - 만료 시간 = 만료 시간이 지나면 expried
            .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices())
            //.tokenValiditySeconds(3600) // 토큰 만료시간(초), default: 14일
            //.alwaysRemember(false)
            .key(AppConstants.REMEMBER_ME_KEY);
            // 'Spring Security > remember-me' - END

        // JWT 사용하지 않음
        // JWT를 사용할 경우, jwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 삽입
        //http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(
                AppConstants.REMEMBER_ME_KEY, userDetailsService, new InMemoryTokenRepositoryImpl());
        return rememberMeServices;
    }

    @Bean(name="GeoIPCountry")
    //public DatabaseReader databaseReader() throws IOException, GeoIp2Exception {
    //    final File resource = new File(AppConstants.GEO_IP_COUNTRY_DB_FILEPATH);
    //    return new DatabaseReader.Builder(resource).build();
    //}
    public DatabaseReader databaseReader() {
        return new DatabaseReader();
    }

    // RoleHierarchy를 적용하려면 하단 주석을 제거할 것!
    //
    // 단, 제거하는 경우 sec:authorize="hasRole('USER')"이 애매해진다.
    // 왜냐하면 다음 룰에 따라
    //   "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER"
    // 어드민 사용자는 hasRole('ADMIN'), hasRole('STAFF'), hasRole('USER') 모두 true가 된다.
    //
    // Role에 따른 분기가 어려워지는 것을 피하기 위해 우선 RoleHierachy 적용을 해제함(주석 처리함)
    // 단, 이렇게 되면 반대급부로 ADMIN이 USER 역할만 접근 가능한 리소스에 접근이 불가능해지므로
    // 관리자 사용자에게는 ADMIN 외에 USER 역할을 추가적으로 할당해줘야 한다.
    /*
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }
    */

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public DifferentLocationChecker differentLocationChecker() {
        return new DifferentLocationChecker();
    }

}
