package io.searchbox.client.config;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Min Cha
 */
public class ClientConfigTest {
    @Test
    public void testTimeoutSettings() {
        ClientConfig config = new ClientConfig.Builder("someUri").connTimeout(1500).readTimeout(2000).build();
        Assert.assertEquals(1500, config.getConnTimeout());
        Assert.assertEquals(2000, config.getReadTimeout());
    }

    @Test
    public void testTimeoutSettingsAsDefault() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true).build();
        Assert.assertTrue(((config.getConnTimeout()) > 0));
        Assert.assertTrue(((config.getReadTimeout()) > 0));
    }

    @Test
    public void testDefaultMaxIdleConnectionTime() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true).build();
        Assert.assertEquals((-1L), config.getMaxConnectionIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, config.getMaxConnectionIdleTimeDurationTimeUnit());
    }

    @Test
    public void testCustomMaxIdleConnectionTime() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true).maxConnectionIdleTime(30L, TimeUnit.MINUTES).build();
        Assert.assertEquals(30L, config.getMaxConnectionIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, config.getMaxConnectionIdleTimeDurationTimeUnit());
    }
}

