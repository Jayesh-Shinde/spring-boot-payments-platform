package com.springboot.configuration;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight ExecutorService wrapper that propagates MDC and SecurityContext
 * from submitting thread into worker threads and clears them afterwards.
 * It extends AbstractExecutorService so we don't need to implement submit/invokeAll/etc.
 */
public class ContextPropagatingExecutorService extends AbstractExecutorService {

    private final ExecutorService delegate;

    public ContextPropagatingExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
    }

    private Runnable wrap(Runnable task) {
        final Map<String, String> mdc = MDC.getCopyOfContextMap();
        final SecurityContext securityContext = SecurityContextHolder.getContext();

        return () -> {
            if (mdc != null) MDC.setContextMap(mdc);
            if (securityContext != null) SecurityContextHolder.setContext(securityContext);
            try {
                task.run();
            } finally {
                MDC.clear();
                SecurityContextHolder.clearContext();
            }
        };
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(wrap(command));
    }

    // lifecycle delegation
    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }
}

