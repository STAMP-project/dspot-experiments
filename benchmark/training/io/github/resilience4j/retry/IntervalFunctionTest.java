package io.github.resilience4j.retry;


import io.vavr.collection.List;
import io.vavr.control.Try;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class IntervalFunctionTest {
    @Test
    public void shouldRejectNonPositiveDuration() {
        // Given
        final Duration negativeDuration = Duration.ofMillis(0);
        final Duration zeroDuration = Duration.ofMillis(0);
        final Duration smallDuration = Duration.ofMillis(9);
        final long negativeInterval = -1;
        final long zeroInterval = 0;
        final long smallInterval = 9;
        // When
        List<Try> tries = List.of(Try.of(() -> IntervalFunction.of(negativeDuration)), Try.of(() -> IntervalFunction.of(zeroDuration)), Try.of(() -> IntervalFunction.of(smallDuration)), Try.of(() -> IntervalFunction.of(negativeInterval)), Try.of(() -> IntervalFunction.of(zeroInterval)), Try.of(() -> IntervalFunction.of(smallInterval)));
        // Then
        Assertions.assertThat(tries.forAll(Try::isFailure)).isTrue();
        Assertions.assertThat(tries.map(Try::getCause).forAll(( t) -> t instanceof IllegalArgumentException)).isTrue();
    }

    @Test
    public void shouldPassPositiveDuration() {
        // Given
        final List<Long> positiveIntervals = List.of(10L, 99L, 981L);
        final List<Duration> positiveDurations = positiveIntervals.map(Duration::ofMillis);
        // When
        positiveDurations.forEach(IntervalFunction::of);
        positiveIntervals.forEach(IntervalFunction::of);
        positiveDurations.forEach(IntervalFunction::ofRandomized);
        positiveIntervals.forEach(IntervalFunction::ofRandomized);
        positiveDurations.forEach(IntervalFunction::ofExponentialBackoff);
        positiveIntervals.forEach(IntervalFunction::ofExponentialBackoff);
        positiveDurations.forEach(IntervalFunction::ofExponentialRandomBackoff);
        positiveIntervals.forEach(IntervalFunction::ofExponentialRandomBackoff);
        // Then should pass
    }

    @Test
    public void shouldRejectAttemptLessThenOne() {
        // Given
        final List<IntervalFunction> fns = List.of(IntervalFunction.ofDefaults(), IntervalFunction.ofRandomized(), IntervalFunction.ofExponentialBackoff(), IntervalFunction.ofExponentialRandomBackoff());
        // When
        final List<Try> tries = fns.map(( fn) -> Try.of(() -> fn.apply(0)));
        // Then
        Assertions.assertThat(tries.forAll(Try::isFailure)).isTrue();
        Assertions.assertThat(tries.map(Try::getCause).forAll(( t) -> t instanceof IllegalArgumentException)).isTrue();
    }

    @Test
    public void shouldPassAttemptGreaterThenZero() {
        // Given
        final List<IntervalFunction> fns = List.of(IntervalFunction.ofDefaults(), IntervalFunction.ofRandomized(), IntervalFunction.ofExponentialBackoff(), IntervalFunction.ofExponentialRandomBackoff());
        // When
        final List<Try> tries1 = fns.map(( fn) -> Try.of(() -> fn.apply(1)));
        final List<Try> tries2 = fns.map(( fn) -> Try.of(() -> fn.apply(2)));
        // Then
        Assertions.assertThat(tries1.forAll(Try::isFailure)).isFalse();
        Assertions.assertThat(tries2.forAll(Try::isFailure)).isFalse();
    }

    @Test
    public void shouldRejectOutOfBoundsRandomizationFactor() {
        // Given
        final Duration duration = Duration.ofMillis(100);
        final float negativeFactor = -1.0E-4F;
        final float greaterThanOneFactor = 1.0001F;
        // When
        final List<Try> tries = List.of(Try.of(() -> IntervalFunction.ofRandomized(duration, negativeFactor)), Try.of(() -> IntervalFunction.ofRandomized(duration, greaterThanOneFactor)));
        // Then
        Assertions.assertThat(tries.forAll(Try::isFailure)).isTrue();
        Assertions.assertThat(tries.map(Try::getCause).forAll(( t) -> t instanceof IllegalArgumentException)).isTrue();
    }

    @Test
    public void shouldPassPositiveRandomizationFactor() {
        // Given
        final Duration duration = Duration.ofMillis(100);
        final float multiplier = 1.5F;
        final List<Float> correctFactors = List.of(0.0F, 0.25F, 0.5F, 0.75F, 0.1F);
        // When
        correctFactors.forEach(( v) -> IntervalFunction.ofRandomized(duration, v));
        correctFactors.forEach(( v) -> IntervalFunction.ofExponentialRandomBackoff(duration, multiplier, v));
    }

    @Test
    public void shouldRejectOutOfBoundsMultiplier() {
        // Given
        final Duration duration = Duration.ofMillis(100);
        final float lessThenOneMultiplier = 0.9999F;
        // When
        final List<Try> tries = List.of(Try.of(() -> IntervalFunction.ofExponentialBackoff(duration, lessThenOneMultiplier)), Try.of(() -> IntervalFunction.ofExponentialRandomBackoff(duration, lessThenOneMultiplier)));
        // Then
        Assertions.assertThat(tries.forAll(Try::isFailure)).isTrue();
        Assertions.assertThat(tries.map(Try::getCause).forAll(( t) -> t instanceof IllegalArgumentException)).isTrue();
    }

    @Test
    public void shouldPassPositiveMultiplier() {
        // Given
        final Duration duration = Duration.ofMillis(100);
        final float greaterThanOneMultiplier = 1.0001F;
        // When
        IntervalFunction.ofExponentialBackoff(duration, greaterThanOneMultiplier);
        IntervalFunction.ofExponentialRandomBackoff(duration, greaterThanOneMultiplier);
    }

    @Test
    public void generatesRandomizedIntervals() {
        final IntervalFunction f = IntervalFunction.ofRandomized(100, 0.5);
        for (int i = 1; i < 50; i++) {
            // When
            final long delay = f.apply(i);
            // Then
            Assertions.assertThat(delay).isGreaterThanOrEqualTo(50).isLessThanOrEqualTo(150);
        }
    }

    @Test
    public void generatesExponentialIntervals() {
        final IntervalFunction f = IntervalFunction.ofExponentialBackoff(100, 1.5);
        long prevV = f.apply(1);
        for (int i = 2; i < 50; i++) {
            // When
            final long v = f.apply(i);
            // Then
            Assertions.assertThat(v).isGreaterThan(prevV);
            prevV = v;
        }
    }

    @Test
    public void generatesExponentialRandomIntervals() {
        final IntervalFunction f = IntervalFunction.ofExponentialRandomBackoff(100, 1.5, 0.5);
        long expectedV = 100;
        for (int i = 1; i < 50; i++) {
            // When
            final long v = f.apply(i);
            // Then
            Assertions.assertThat(v).isGreaterThanOrEqualTo((((long) (expectedV * 0.5)) - 1)).isLessThanOrEqualTo((((long) (expectedV * 1.5)) + 1));
            expectedV = ((long) (expectedV * 1.5));
        }
    }
}

