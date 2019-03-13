package brave.sampler;


import org.junit.Test;


public class BoundarySamplerTest extends SamplerTest {
    @Test
    public void acceptsOneInTenThousandSampleRate() {
        newSampler(1.0E-4F);
    }
}

