package com.baeldung.reactive;


import com.baeldung.reactive.model.Foo;
import java.time.Duration;
import java.util.Random;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


public class FluxUnitTest {
    public static final Random RANDOM = new Random();

    @Test
    public void whenFluxIsConstructed_thenCorrect() {
        final Flux<Foo> flux = Flux.<Foo>create(( fluxSink) -> {
            for (int i = 0; i < 100; i++) {
                fluxSink.next(new Foo(RANDOM.nextLong(), randomAlphabetic(6)));
            }
        }).sample(Duration.ofSeconds(1)).log();
        flux.subscribe();
        Assert.assertNotNull(flux);
    }
}

