package brave.sampler;


import Sampler.ALWAYS_SAMPLE;
import SamplingFlags.EMPTY;
import SamplingFlags.NOT_SAMPLED;
import SamplingFlags.SAMPLED;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


public class DeclarativeSamplerTest {
    DeclarativeSampler<DeclarativeSamplerTest.Traced> declarativeSampler = DeclarativeSampler.create(( t) -> t.enabled() ? t.sampleRate() : null);

    @Test
    public void honorsSampleRate() {
        assertThat(declarativeSampler.sample(DeclarativeSamplerTest.traced(1.0F, true))).isEqualTo(SAMPLED);
        assertThat(declarativeSampler.sample(DeclarativeSamplerTest.traced(0.0F, true))).isEqualTo(NOT_SAMPLED);
    }

    @Test
    public void acceptsFallback() {
        assertThat(declarativeSampler.sample(DeclarativeSamplerTest.traced(1.0F, false))).isEqualTo(EMPTY);
    }

    @Test
    public void toSampler() {
        assertThat(declarativeSampler.toSampler(DeclarativeSamplerTest.traced(1.0F, true)).isSampled(0L)).isTrue();
        assertThat(declarativeSampler.toSampler(DeclarativeSamplerTest.traced(0.0F, true)).isSampled(0L)).isFalse();
        // check not enabled is false
        assertThat(declarativeSampler.toSampler(DeclarativeSamplerTest.traced(1.0F, false)).isSampled(0L)).isFalse();
    }

    @Test
    public void toSampler_fallback() {
        Sampler withFallback = declarativeSampler.toSampler(DeclarativeSamplerTest.traced(1.0F, false), ALWAYS_SAMPLE);
        assertThat(withFallback.isSampled(0L)).isTrue();
    }

    @Test
    public void samplerLoadsLazy() {
        assertThat(declarativeSampler.methodsToSamplers).isEmpty();
        declarativeSampler.sample(DeclarativeSamplerTest.traced(1.0F, true));
        assertThat(declarativeSampler.methodsToSamplers).hasSize(1);
        declarativeSampler.sample(DeclarativeSamplerTest.traced(0.0F, true));
        assertThat(declarativeSampler.methodsToSamplers).hasSize(2);
    }

    @Test
    public void cardinalityIsPerAnnotationNotInvocation() {
        DeclarativeSamplerTest.Traced traced = DeclarativeSamplerTest.traced(1.0F, true);
        declarativeSampler.sample(traced);
        declarativeSampler.sample(traced);
        declarativeSampler.sample(traced);
        assertThat(declarativeSampler.methodsToSamplers).hasSize(1);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Traced {
        float sampleRate() default 1.0F;

        boolean enabled() default true;
    }
}

