package com.ctrip.framework.apollo.core.internals;


import Env.DEV;
import Env.FAT;
import Env.LOCAL;
import Env.PRO;
import org.junit.Assert;
import org.junit.Test;


public class LegacyMetaServerProviderTest {
    @Test
    public void testFromPropertyFile() {
        LegacyMetaServerProvider legacyMetaServerProvider = new LegacyMetaServerProvider();
        Assert.assertEquals("http://localhost:8080", legacyMetaServerProvider.getMetaServerAddress(LOCAL));
        Assert.assertEquals("http://dev:8080", legacyMetaServerProvider.getMetaServerAddress(DEV));
        Assert.assertEquals(null, legacyMetaServerProvider.getMetaServerAddress(PRO));
    }

    @Test
    public void testWithSystemProperty() throws Exception {
        String someDevMetaAddress = "someMetaAddress";
        String someFatMetaAddress = "someFatMetaAddress";
        System.setProperty("dev_meta", someDevMetaAddress);
        System.setProperty("fat_meta", someFatMetaAddress);
        LegacyMetaServerProvider legacyMetaServerProvider = new LegacyMetaServerProvider();
        Assert.assertEquals(someDevMetaAddress, legacyMetaServerProvider.getMetaServerAddress(DEV));
        Assert.assertEquals(someFatMetaAddress, legacyMetaServerProvider.getMetaServerAddress(FAT));
    }
}

