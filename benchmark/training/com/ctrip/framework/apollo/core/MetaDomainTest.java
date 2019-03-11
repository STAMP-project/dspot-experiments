package com.ctrip.framework.apollo.core;


import Env.DEV;
import Env.FAT;
import Env.LOCAL;
import Env.LPT;
import Env.PRO;
import Env.UAT;
import HttpServletResponse.SC_OK;
import MetaDomainConsts.DEFAULT_META_URL;
import com.ctrip.framework.apollo.BaseIntegrationTest;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.core.internals.LegacyMetaServerProvider;
import com.ctrip.framework.apollo.core.spi.MetaServerProvider;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MetaDomainTest extends BaseIntegrationTest {
    @Test
    public void testGetMetaDomain() {
        Assert.assertEquals("http://localhost:8080", MetaDomainConsts.getDomain(LOCAL));
        Assert.assertEquals("http://dev:8080", MetaDomainConsts.getDomain(DEV));
        Assert.assertEquals(DEFAULT_META_URL, MetaDomainConsts.getDomain(PRO));
    }

    @Test
    public void testGetValidAddress() throws Exception {
        String someResponse = "some response";
        startServerWithHandlers(mockServerHandler(SC_OK, someResponse));
        String validServer = (" http://localhost:" + (BaseIntegrationTest.PORT)) + " ";
        String invalidServer = "http://localhost:" + (BaseIntegrationTest.findFreePort());
        MetaDomainTest.MockMetaServerProvider.mock(FAT, ((validServer + ",") + invalidServer));
        MetaDomainTest.MockMetaServerProvider.mock(UAT, ((invalidServer + ",") + validServer));
        Assert.assertEquals(validServer.trim(), MetaDomainConsts.getDomain(FAT));
        Assert.assertEquals(validServer.trim(), MetaDomainConsts.getDomain(UAT));
    }

    @Test
    public void testInvalidAddress() throws Exception {
        String invalidServer = ("http://localhost:" + (BaseIntegrationTest.findFreePort())) + " ";
        String anotherInvalidServer = ("http://localhost:" + (BaseIntegrationTest.findFreePort())) + " ";
        MetaDomainTest.MockMetaServerProvider.mock(LPT, ((invalidServer + ",") + anotherInvalidServer));
        String metaServer = MetaDomainConsts.getDomain(LPT);
        Assert.assertTrue(((metaServer.equals(invalidServer.trim())) || (metaServer.equals(anotherInvalidServer.trim()))));
    }

    public static class MockMetaServerProvider implements MetaServerProvider {
        private static Map<Env, String> mockMetaServerAddress = Maps.newHashMap();

        private static void mock(Env env, String metaServerAddress) {
            MetaDomainTest.MockMetaServerProvider.mockMetaServerAddress.put(env, metaServerAddress);
        }

        private static void clear() {
            MetaDomainTest.MockMetaServerProvider.mockMetaServerAddress.clear();
        }

        @Override
        public String getMetaServerAddress(Env targetEnv) {
            return MetaDomainTest.MockMetaServerProvider.mockMetaServerAddress.get(targetEnv);
        }

        @Override
        public int getOrder() {
            return (LegacyMetaServerProvider.ORDER) - 1;// just in front of LegacyMetaServerProvider

        }
    }
}

