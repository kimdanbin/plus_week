package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.filter.AuthFilter;
import com.example.demo.filter.RoleFilter;
import com.example.demo.interceptor.AdminRoleInterceptor;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.interceptor.UserRoleInterceptor;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // TODO: 2. 인가에 대한 이해
    private static final String[] AUTH_REQUIRED_PATH_PATTERNS = {"/users/logout", "/admins/*", "/items/*"};
    private static final String[] USER_ROLE_REQUIRED_PATH_PATTERNS = {"/reservations/*"};
    private static final String[] ADMIN_ROLE_REQUIRED_PATH_PATTERNS = {"/admins/*"}; // 새로 작성한 부분, admin 경로추가

    private final AuthInterceptor authInterceptor;
    private final UserRoleInterceptor userRoleInterceptor;
    private final AdminRoleInterceptor adminRoleInterceptor; // 새로 작성한 부분, admin 권한 확인하는 interceptor 추가

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 여부
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(AUTH_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE);

        // user 권한 여부
        registry.addInterceptor(userRoleInterceptor)
                .addPathPatterns(USER_ROLE_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE + 2);

        // admin 권한 여부, 새로 작성한 부분, registry에 interceptor 추가
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns(ADMIN_ROLE_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE + 3);
    }

    @Bean
    public FilterRegistrationBean authFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new AuthFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.addUrlPatterns(AUTH_REQUIRED_PATH_PATTERNS);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean userRoleFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RoleFilter(Role.USER));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        filterRegistrationBean.addUrlPatterns(USER_ROLE_REQUIRED_PATH_PATTERNS);
        return filterRegistrationBean;
    }
}