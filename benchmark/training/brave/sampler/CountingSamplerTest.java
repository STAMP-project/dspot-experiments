package brave.sampler;


import org.junit.Test;


public class CountingSamplerTest extends SamplerTest {
    @Test
    public void sampleRateMinimumOnePercent() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        newSampler(1.0E-4F);
    }
}

