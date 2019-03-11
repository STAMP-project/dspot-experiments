package com.netflix.discovery.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class DiscoveryBuildInfoTest {
    @Test
    public void testRequestedManifestIsLocatedAndLoaded() throws Exception {
        DiscoveryBuildInfo buildInfo = new DiscoveryBuildInfo(ObjectMapper.class);
        Assert.assertThat(buildInfo.getBuildVersion().contains("version_unknown"), CoreMatchers.is(false));
    }
}

