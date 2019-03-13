package com.baeldung.metrics.servo;


import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.MonitorConfig;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.math.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Atlas server needs to be up and running for this live test to work.
 */
public class AtlasObserverLiveTest {
    private final String atlasUri = "http://localhost:7101/api/v1";

    @Test
    public void givenAtlasAndCounter_whenRegister_thenPublishedToAtlas() throws Exception {
        Counter counter = new com.netflix.servo.monitor.BasicCounter(MonitorConfig.builder("test").withTag("servo", "counter").build());
        DefaultMonitorRegistry.getInstance().register(counter);
        Assert.assertThat(atlasValuesOfTag("servo"), CoreMatchers.not(CoreMatchers.containsString("counter")));
        for (int i = 0; i < 3; i++) {
            counter.increment(RandomUtils.nextInt(10));
            TimeUnit.SECONDS.sleep(1);
            counter.increment(((-1) * (RandomUtils.nextInt(10))));
            TimeUnit.SECONDS.sleep(1);
        }
        Assert.assertThat(atlasValuesOfTag("servo"), CoreMatchers.containsString("counter"));
    }
}

