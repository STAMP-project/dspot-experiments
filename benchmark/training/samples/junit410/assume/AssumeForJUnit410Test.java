package samples.junit410.assume;


import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class AssumeForJUnit410Test {
    @Test
    public void assumesWorkWithPowerMockForJUnit410() throws Exception {
        // When
        Assume.assumeTrue(false);
    }
}

