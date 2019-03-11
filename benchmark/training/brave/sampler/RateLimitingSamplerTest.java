package brave.sampler;


import Sampler.NEVER_SAMPLE;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest(RateLimitingSampler.class)
public class RateLimitingSamplerTest {
    @Test
    public void samplesOnlySpecifiedNumber() {
        mockStatic(System.class);
        when(System.nanoTime()).thenReturn(RateLimitingSampler.NANOS_PER_SECOND);
        Sampler sampler = RateLimitingSampler.create(2);
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + 1));
        assertThat(sampler.isSampled(0L)).isTrue();
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + 2));
        assertThat(sampler.isSampled(0L)).isTrue();
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + 2));
        assertThat(sampler.isSampled(0L)).isFalse();
    }

    @Test
    public void resetsAfterASecond() {
        mockStatic(System.class);
        when(System.nanoTime()).thenReturn(RateLimitingSampler.NANOS_PER_SECOND);
        Sampler sampler = RateLimitingSampler.create(10);
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isFalse();
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + (RateLimitingSampler.NANOS_PER_DECISECOND)));
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isFalse();
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + ((RateLimitingSampler.NANOS_PER_DECISECOND) * 9)));
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isTrue();
        assertThat(sampler.isSampled(0L)).isFalse();
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + (RateLimitingSampler.NANOS_PER_SECOND)));
        assertThat(sampler.isSampled(0L)).isTrue();
    }

    @Test
    public void allowsOddRates() {
        mockStatic(System.class);
        when(System.nanoTime()).thenReturn(RateLimitingSampler.NANOS_PER_SECOND);
        Sampler sampler = RateLimitingSampler.create(11);
        when(System.nanoTime()).thenReturn(((RateLimitingSampler.NANOS_PER_SECOND) + ((RateLimitingSampler.NANOS_PER_DECISECOND) * 9)));
        for (int i = 0; i < 11; i++) {
            assertThat(sampler.isSampled(0L)).withFailMessage(("failed after " + (i + 1))).isTrue();
        }
        assertThat(sampler.isSampled(0L)).isFalse();
    }

    @Test
    public void worksOnRollover() {
        mockStatic(System.class);
        when(System.nanoTime()).thenReturn((-(RateLimitingSampler.NANOS_PER_SECOND)));
        Sampler sampler = RateLimitingSampler.create(2);
        assertThat(sampler.isSampled(0L)).isTrue();
        when(System.nanoTime()).thenReturn(((-(RateLimitingSampler.NANOS_PER_SECOND)) / 2));
        assertThat(sampler.isSampled(0L)).isTrue();// second request

        when(System.nanoTime()).thenReturn(((-(RateLimitingSampler.NANOS_PER_SECOND)) / 4));
        assertThat(sampler.isSampled(0L)).isFalse();
        when(System.nanoTime()).thenReturn(0L);// reset

        assertThat(sampler.isSampled(0L)).isTrue();
    }

    @DataPoints
    public static final int[] SAMPLE_RESERVOIRS = new int[]{ 1, 10, 100 };

    @Test
    public void zeroMeansDropAllTraces() {
        assertThat(RateLimitingSampler.create(0)).isSameAs(NEVER_SAMPLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tracesPerSecond_cantBeNegative() {
        RateLimitingSampler.create((-1));
    }
}

