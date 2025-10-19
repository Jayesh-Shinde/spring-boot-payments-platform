package com.springboot.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class CorelationIdFilter implements Filter {

    private final String corelationIdHeader = "X-Correlation-Id";

    // added for future api requests
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String corelationId = httpServletRequest.getHeader(corelationIdHeader);
        if (corelationId == null || corelationId.isEmpty()) {
            corelationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", corelationId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }
}
