package com.w.t.config;

import com.w.t.Intercptor.DeafultIntercptor;
import com.w.t.Intercptor.Intercptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Packagename com.w.t.config
 * @Classname WebInterceptorConfig
 * @Description
 * @Authors Mr.Wu
 * @Date 2020/10/12 09:27
 * @Version 1.0
 */
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DeafultIntercptor());
        registry.addInterceptor(new Intercptor());
    }
}
