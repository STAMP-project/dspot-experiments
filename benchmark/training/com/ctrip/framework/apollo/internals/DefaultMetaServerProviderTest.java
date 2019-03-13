package com.ctrip.framework.apollo.internals;


import ConfigConsts.APOLLO_META_KEY;
import com.ctrip.framework.apollo.core.enums.Env;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMetaServerProviderTest {
    @Test
    public void testWithSystemProperty() throws Exception {
        String someMetaAddress = "someMetaAddress";
        Env someEnv = Env.DEV;
        System.setProperty(APOLLO_META_KEY, ((" " + someMetaAddress) + " "));
        DefaultMetaServerProvider defaultMetaServerProvider = new DefaultMetaServerProvider();
        Assert.assertEquals(someMetaAddress, defaultMetaServerProvider.getMetaServerAddress(someEnv));
    }
}

