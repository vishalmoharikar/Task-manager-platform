package com.taskplatform.task.config;

import com.ctc.wstx.util.StringUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.nonNull(attributes)){
                    HttpServletRequest request =  attributes.getRequest();
                    String authHeader  =  request.getHeader("Authorization");
                    if (StringUtils.isNotEmpty(authHeader)){
                        template.header("Authorization", authHeader);
                    }
                }

            }
        };
    }
}
