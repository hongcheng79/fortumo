package chc.fortumo;

import chc.fortumo.interceptor.CounterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("chc.fortumo")
public class MainApplicationConfig implements WebMvcConfigurer {
    @Autowired
    private CounterInterceptor counterInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(counterInterceptor);
    }
}
