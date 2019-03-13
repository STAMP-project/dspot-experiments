package reactor.core.publisher;


import org.junit.Test;
import reactor.test.StepVerifier;


public class MonoSingleMonoTest {
    @Test
    public void callableEmpty() {
        StepVerifier.create(Mono.empty().single()).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("Source was a (constant) empty"));
    }

    @Test
    public void callableValued() {
        StepVerifier.create(Mono.just("foo").single()).expectNext("foo").verifyComplete();
    }

    @Test
    public void normalEmpty() {
        StepVerifier.create(Mono.empty().hide().single()).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("Source was empty"));
    }

    @Test
    public void normalValued() {
        StepVerifier.create(Mono.just("foo").hide().single()).expectNext("foo").verifyComplete();
    }
}

