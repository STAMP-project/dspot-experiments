package com.ctrip.framework.apollo.biz.service;


import com.ctrip.framework.apollo.biz.AbstractUnitTest;
import com.ctrip.framework.apollo.biz.repository.ServerConfigRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class BizDBPropertySourceTest extends AbstractUnitTest {
    @Mock
    private ServerConfigRepository serverConfigRepository;

    private BizDBPropertySource propertySource;

    private String clusterConfigKey = "clusterKey";

    private String clusterConfigValue = "clusterValue";

    private String dcConfigKey = "dcKey";

    private String dcConfigValue = "dcValue";

    private String defaultKey = "defaultKey";

    private String defaultValue = "defaultValue";

    @Test
    public void testGetClusterConfig() {
        propertySource.refresh();
        Assert.assertEquals(propertySource.getProperty(clusterConfigKey), clusterConfigValue);
    }

    @Test
    public void testGetDcConfig() {
        propertySource.refresh();
        Assert.assertEquals(propertySource.getProperty(dcConfigKey), dcConfigValue);
    }

    @Test
    public void testGetDefaultConfig() {
        propertySource.refresh();
        Assert.assertEquals(propertySource.getProperty(defaultKey), defaultValue);
    }

    @Test
    public void testGetNull() {
        propertySource.refresh();
        Assert.assertNull(propertySource.getProperty("noKey"));
    }
}

