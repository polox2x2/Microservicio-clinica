package com.clinica.citas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class FeignJwtInterceptor implements RequestInterceptor {

    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && !authHeader.isBlank()) {
                template.header(HttpHeaders.AUTHORIZATION, authHeader);
                return;
            }
        }
        template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generateInternalServiceToken());
    }
}
