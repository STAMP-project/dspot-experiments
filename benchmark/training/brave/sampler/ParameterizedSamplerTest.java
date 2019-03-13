package brave.sampler;


import SamplingFlags.EMPTY;
import SamplingFlags.NOT_SAMPLED;
import SamplingFlags.SAMPLED;
import java.util.Arrays;
import org.junit.Test;


public class ParameterizedSamplerTest {
    @Test
    public void matchesParameters() {
        ParameterizedSampler<Boolean> sampler = ParameterizedSampler.create(Arrays.asList(rule(1.0F, Boolean::booleanValue)));
        assertThat(sampler.sample(true)).isEqualTo(SAMPLED);
    }

    @Test
    public void emptyOnNoMatch() {
        ParameterizedSampler<Boolean> sampler = ParameterizedSampler.create(Arrays.asList(rule(1.0F, Boolean::booleanValue)));
        assertThat(sampler.sample(false)).isEqualTo(EMPTY);
    }

    @Test
    public void emptyOnNull() {
        ParameterizedSampler<Void> sampler = ParameterizedSampler.create(Arrays.asList(rule(1.0F, ( v) -> true)));
        assertThat(sampler.sample(null)).isEqualTo(EMPTY);
    }

    @Test
    public void multipleRules() {
        ParameterizedSampler<Boolean> sampler = ParameterizedSampler.create(// doesn't match
        // match
        Arrays.asList(rule(1.0F, ( v) -> false), rule(0.0F, ( v) -> true)));
        assertThat(sampler.sample(true)).isEqualTo(NOT_SAMPLED);
    }
}

