package brave.propagation;


import SamplingFlags.Builder;
import SamplingFlags.DEBUG;
import SamplingFlags.DEBUG_SAMPLED_LOCAL;
import SamplingFlags.EMPTY;
import SamplingFlags.EMPTY.flags;
import SamplingFlags.EMPTY_SAMPLED_LOCAL;
import SamplingFlags.NOT_SAMPLED;
import SamplingFlags.NOT_SAMPLED_SAMPLED_LOCAL;
import SamplingFlags.SAMPLED;
import SamplingFlags.SAMPLED_SAMPLED_LOCAL;
import brave.internal.InternalPropagation;
import org.junit.Test;


public class SamplingFlagsTest {
    @Test
    public void builder_defaultIsEmpty() {
        SamplingFlags flags = new SamplingFlags.Builder().build();
        assertThat(flags).isSameAs(EMPTY);
        assertThat(flags.sampled()).isNull();
        assertThat(flags.debug()).isFalse();
    }

    @Test
    public void builder_debugImpliesSampled() {
        SamplingFlags flags = new SamplingFlags.Builder().debug(true).build();
        assertThat(flags).isSameAs(DEBUG);
        assertThat(flags.sampled()).isTrue();
        assertThat(flags.debug()).isTrue();
    }

    @Test
    public void builder_sampled() {
        SamplingFlags flags = new SamplingFlags.Builder().sampled(true).build();
        assertThat(flags).isSameAs(SAMPLED);
        assertThat(flags.sampled()).isTrue();
        assertThat(flags.debug()).isFalse();
    }

    @Test
    public void builder_notSampled() {
        SamplingFlags flags = new SamplingFlags.Builder().sampled(false).build();
        assertThat(flags).isSameAs(NOT_SAMPLED);
        assertThat(flags.sampled()).isFalse();
        assertThat(flags.debug()).isFalse();
    }

    @Test
    public void builder_nullSampled() {
        SamplingFlags flags = new SamplingFlags.Builder().sampled(true).sampled(null).build();
        assertThat(flags).isSameAs(EMPTY);
        assertThat(flags.sampled()).isNull();
        assertThat(flags.debug()).isFalse();
    }

    @Test
    public void debug_set_true() {
        assertThat(SamplingFlags.debug(true, flags)).isEqualTo(SamplingFlags.DEBUG.flags).isEqualTo((((InternalPropagation.FLAG_SAMPLED_SET) | (InternalPropagation.FLAG_SAMPLED)) | (InternalPropagation.FLAG_DEBUG)));
    }

    @Test
    public void sampled_flags() {
        assertThat(SamplingFlags.debug(false, SamplingFlags.DEBUG.flags)).isEqualTo(SamplingFlags.SAMPLED.flags).isEqualTo(((InternalPropagation.FLAG_SAMPLED_SET) | (InternalPropagation.FLAG_SAMPLED)));
    }

    /**
     * Ensures constants are used
     */
    @Test
    public void toSamplingFlags_returnsConstants() {
        assertThat(SamplingFlags.toSamplingFlags(flags)).isSameAs(EMPTY);
        assertThat(SamplingFlags.toSamplingFlags(SamplingFlags.NOT_SAMPLED.flags)).isSameAs(NOT_SAMPLED);
        assertThat(SamplingFlags.toSamplingFlags(SamplingFlags.SAMPLED.flags)).isSameAs(SAMPLED);
        assertThat(SamplingFlags.toSamplingFlags(SamplingFlags.DEBUG.flags)).isSameAs(DEBUG);
        assertThat(SamplingFlags.toSamplingFlags(((EMPTY.flags) | (InternalPropagation.FLAG_SAMPLED_LOCAL)))).isSameAs(EMPTY_SAMPLED_LOCAL);
        assertThat(SamplingFlags.toSamplingFlags(((NOT_SAMPLED.flags) | (InternalPropagation.FLAG_SAMPLED_LOCAL)))).isSameAs(NOT_SAMPLED_SAMPLED_LOCAL);
        assertThat(SamplingFlags.toSamplingFlags(((SAMPLED.flags) | (InternalPropagation.FLAG_SAMPLED_LOCAL)))).isSameAs(SAMPLED_SAMPLED_LOCAL);
        assertThat(SamplingFlags.toSamplingFlags(((DEBUG.flags) | (InternalPropagation.FLAG_SAMPLED_LOCAL)))).isSameAs(DEBUG_SAMPLED_LOCAL);
    }

    @Test
    public void sampledLocal() {
        SamplingFlags.Builder flagsBuilder = new SamplingFlags.Builder();
        flagsBuilder.flags |= InternalPropagation.FLAG_SAMPLED_LOCAL;
        SamplingFlags flags = flagsBuilder.build();
        assertThat(flags).isSameAs(EMPTY_SAMPLED_LOCAL);
        assertThat(flags.sampledLocal()).isTrue();
        assertThat(flags.sampled()).isNull();
    }
}

