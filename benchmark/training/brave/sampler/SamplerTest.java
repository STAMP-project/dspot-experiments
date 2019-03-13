package brave.sampler;


import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public abstract class SamplerTest {
    /**
     * Zipkin trace ids are random 64bit numbers. This creates a relatively large input to avoid
     * flaking out due to PRNG nuance.
     */
    static final int INPUT_SIZE = 100000;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DataPoints
    public static final float[] SAMPLE_RATES = new float[]{ 0.01F, 0.5F, 0.9F };

    @Test
    public void zeroMeansDropAllTraces() {
        final Sampler sampler = newSampler(0.0F);
        assertThat(new Random().longs(SamplerTest.INPUT_SIZE).filter(sampler::isSampled).findAny()).isEmpty();
    }

    @Test
    public void oneMeansKeepAllTraces() {
        final Sampler sampler = newSampler(1.0F);
        assertThat(new Random().longs(SamplerTest.INPUT_SIZE).filter(sampler::isSampled).count()).isEqualTo(SamplerTest.INPUT_SIZE);
    }

    @Test
    public void rateCantBeNegative() {
        thrown.expect(IllegalArgumentException.class);
        newSampler((-1.0F));
    }

    @Test
    public void rateCantBeOverOne() {
        thrown.expect(IllegalArgumentException.class);
        newSampler(1.1F);
    }
}

