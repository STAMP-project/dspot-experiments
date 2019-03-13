package samples.junit4.assume;


import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class AssumeTest {
    @Test
    public void assumesWorkWithPowerMockForJUnit44() throws Exception {
        // When
        Assume.assumeTrue(false);
    }
}

