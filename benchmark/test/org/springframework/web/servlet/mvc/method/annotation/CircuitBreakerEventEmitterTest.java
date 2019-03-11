package org.springframework.web.servlet.mvc.method.annotation;


import CircuitBreakerEvent.Type;
import io.github.resilience4j.adapter.ReactorAdapter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.monitoring.endpoint.CircuitBreakerEventDTO;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 *
 *
 * @author bstorozhuk
 */
public class CircuitBreakerEventEmitterTest {
    @Test
    public void testEmitter() throws IOException {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom().ringBufferSizeInClosedState(3).ringBufferSizeInHalfOpenState(2).failureRateThreshold(66).waitDurationInOpenState(Duration.ofSeconds(1)).recordFailure(( e) -> !(e instanceof IllegalArgumentException)).build();
        CircuitBreaker circuitBreaker = io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry.ofDefaults().circuitBreaker("test", config);
        Runnable run = CircuitBreaker.decorateRunnable(circuitBreaker, () -> System.out.println("."));
        Runnable fail = CircuitBreaker.decorateRunnable(circuitBreaker, () -> {
            throw new ConcurrentModificationException();
        });
        Runnable ignore = CircuitBreaker.decorateRunnable(circuitBreaker, () -> {
            throw new IllegalArgumentException();
        });
        SseEmitter sseEmitter = createSseEmitter(ReactorAdapter.toFlux(circuitBreaker.getEventPublisher()));
        CircuitBreakerEventEmitterTest.TestHandler handler = new CircuitBreakerEventEmitterTest.TestHandler();
        sseEmitter.initialize(handler);
        exec(run, 2);
        exec(ignore, 1);
        exec(fail, 3);
        circuitBreaker.reset();
        exec(run, 2);
        circuitBreaker.reset();
        sseEmitter.complete();
        assert handler.isCompleted;
        exec(run, 2);
        List<CircuitBreakerEvent.Type> events = handler.events.stream().map(CircuitBreakerEventDTO::getType).collect(Collectors.toList());
        then(events).containsExactly(SUCCESS, SUCCESS, IGNORED_ERROR, ERROR, ERROR, STATE_TRANSITION, NOT_PERMITTED, STATE_TRANSITION, RESET, SUCCESS, SUCCESS, RESET);
    }

    private static class TestHandler implements ResponseBodyEmitter.Handler {
        public List<CircuitBreakerEventDTO> events = new ArrayList<>();

        public boolean isCompleted = false;

        private Runnable callback;

        @Override
        public void send(Object data, MediaType mediaType) throws IOException {
            if (((APPLICATION_JSON) == mediaType) && (data instanceof CircuitBreakerEventDTO)) {
                events.add(((CircuitBreakerEventDTO) (data)));
            }
        }

        @Override
        public void complete() {
            isCompleted = true;
            callback.run();
        }

        @Override
        public void completeWithError(Throwable failure) {
            System.out.println("E");
        }

        @Override
        public void onTimeout(Runnable callback) {
            System.out.println("T");
        }

        @Override
        public void onCompletion(Runnable callback) {
            this.callback = callback;
        }
    }
}

