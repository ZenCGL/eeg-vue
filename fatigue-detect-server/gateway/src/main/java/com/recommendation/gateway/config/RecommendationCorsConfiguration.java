package com.recommendation.gateway.config;

import com.recommendation.gateway.Filter.AuthGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecommendationCorsConfiguration {
    @Bean
    public AuthGlobalFilter tokenFilter(){
        return new AuthGlobalFilter();
    }
//    @Bean
//    public MyFilter doOptionsFilter(){
//        return new MyFilter();
//    }
//    @Bean
//    AuthGlobalFilter authGlobalFilter;
//    @Bean
//    public CorsWebFilter corsWebFilter(){
//        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        //跨域
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addAllowedOriginPattern("*");
////        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.setAllowCredentials(true);
//
//        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
//        return  new CorsWebFilter(urlBasedCorsConfigurationSource);
//    }
//    @Autowired
//    AuthHandlerInterceptor authHandlerInterceptor;
//    @Bean
//    public FilterRegistrationBean corsFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(doOptionsFilter());
//        registrationBean.setUrlPatterns(Lists.newArrayList("/*"));
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 或者您可以指定更具体的路径
//                        .allowedHeaders("*")
//                        .allowedOriginPatterns("*")
////                        .allowedOrigins("*") // 允许所有域的请求
//                        .allowedMethods("*") // 允许的HTTP方法
//                        .allowCredentials(true); // 是否允许发送Cookie信息
//            }
//        };
//    }
}
