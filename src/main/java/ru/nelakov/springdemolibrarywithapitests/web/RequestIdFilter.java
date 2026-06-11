package ru.nelakov.springdemolibrarywithapitests.web;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Puts a correlation id into the MDC for every request so all log lines of one
 * request share a {@code requestId} field. Reuses an inbound X-Request-Id when a
 * caller (gateway, test) supplies one; otherwise generates a UUID.
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    // Inbound ids must be a bounded, safe token. Allowlisting rejects oversized and
    // hostile values (control chars, newlines) that would otherwise poison or inflate
    // the JSON logs, since requestId is promoted into every log event.
    private static final Pattern VALID_REQUEST_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || !VALID_REQUEST_ID.matcher(requestId).matches()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Worker threads are pooled and MDC is thread-local: clear it or the
            // next request on this thread inherits a stale requestId.
            MDC.remove(MDC_KEY);
        }
    }
}
