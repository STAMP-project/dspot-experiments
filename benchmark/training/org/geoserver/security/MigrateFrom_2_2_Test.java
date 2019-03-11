/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import GeoServerSecurityFilterChain.DYNAMIC_EXCEPTION_TRANSLATION_FILTER;
import GeoServerSecurityFilterChain.FILTER_SECURITY_INTERCEPTOR;
import GeoServerSecurityFilterChain.FILTER_SECURITY_REST_INTERCEPTOR;
import GeoServerSecurityFilterChain.GUI_EXCEPTION_TRANSLATION_FILTER;
import GeoServerSecurityFilterChain.GWC_CHAIN_NAME;
import GeoServerSecurityFilterChain.REST_CHAIN_NAME;
import GeoServerSecurityFilterChain.ROLE_FILTER;
import GeoServerSecurityFilterChain.SECURITY_CONTEXT_ASC_FILTER;
import GeoServerSecurityFilterChain.SECURITY_CONTEXT_NO_ASC_FILTER;
import GeoServerSecurityFilterChain.SSL_FILTER;
import GeoServerSecurityFilterChain.WEB_CHAIN_NAME;
import GeoServerSecurityFilterChain.WEB_LOGIN_CHAIN_NAME;
import GeoServerSecurityFilterChain.WEB_LOGOUT_CHAIN_NAME;
import java.io.File;
import org.geoserver.security.config.RoleFilterConfig;
import org.geoserver.security.config.SSLFilterConfig;
import org.geoserver.security.config.SecurityManagerConfig;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import static GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER;


/**
 * Tests migration from 2.2.x to 2.3.x
 *
 * @author mcr
 */
public class MigrateFrom_2_2_Test extends GeoServerSystemTestSupport {
    @Test
    public void testMigration() throws Exception {
        File logoutFilterDir = new File(getSecurityManager().get("security/filter").dir(), FORM_LOGOUT_FILTER);
        File oldLogoutFilterConfig = new File(logoutFilterDir, "config.xml.2.2.x");
        Assert.assertTrue(oldLogoutFilterConfig.exists());
        File oldSecManagerConfig = new File(getSecurityManager().get("security").dir(), "config.xml.2.2.x");
        Assert.assertTrue(oldSecManagerConfig.exists());
        RoleFilterConfig rfConfig = ((RoleFilterConfig) (getSecurityManager().loadFilterConfig(ROLE_FILTER)));
        Assert.assertNotNull(rfConfig);
        SSLFilterConfig sslConfig = ((SSLFilterConfig) (getSecurityManager().loadFilterConfig(SSL_FILTER)));
        Assert.assertNotNull(sslConfig);
        Assert.assertNull(getSecurityManager().loadFilterConfig(GUI_EXCEPTION_TRANSLATION_FILTER));
        SecurityManagerConfig config = getSecurityManager().loadSecurityConfig();
        for (RequestFilterChain chain : config.getFilterChain().getRequestChains()) {
            Assert.assertFalse(chain.getFilterNames().contains(DYNAMIC_EXCEPTION_TRANSLATION_FILTER));
            Assert.assertFalse(chain.getFilterNames().remove(FILTER_SECURITY_INTERCEPTOR));
            Assert.assertFalse(chain.getFilterNames().remove(FILTER_SECURITY_REST_INTERCEPTOR));
            Assert.assertFalse(chain.getFilterNames().remove(SECURITY_CONTEXT_ASC_FILTER));
            Assert.assertFalse(chain.getFilterNames().remove(SECURITY_CONTEXT_NO_ASC_FILTER));
            Assert.assertFalse(chain.isDisabled());
            Assert.assertFalse(chain.isRequireSSL());
            Assert.assertFalse(StringUtils.hasLength(chain.getRoleFilterName()));
            if (((WEB_CHAIN_NAME.equals(chain.getName())) || (WEB_LOGIN_CHAIN_NAME.equals(chain.getName()))) || (WEB_LOGOUT_CHAIN_NAME.equals(chain.getName())))
                Assert.assertTrue(chain.isAllowSessionCreation());
            else
                Assert.assertFalse(chain.isAllowSessionCreation());

            if (chain instanceof VariableFilterChain) {
                VariableFilterChain vchain = ((VariableFilterChain) (chain));
                Assert.assertEquals(DYNAMIC_EXCEPTION_TRANSLATION_FILTER, vchain.getExceptionTranslationName());
                if ((REST_CHAIN_NAME.equals(vchain.getName())) || (GWC_CHAIN_NAME.equals(vchain.getName())))
                    Assert.assertEquals(FILTER_SECURITY_REST_INTERCEPTOR, vchain.getInterceptorName());
                else
                    Assert.assertEquals(FILTER_SECURITY_INTERCEPTOR, vchain.getInterceptorName());

            }
        }
    }

    @Test
    public void testWebLoginChainSessionCreation() throws Exception {
        // GEOS-6077
        GeoServerSecurityManager secMgr = getSecurityManager();
        SecurityManagerConfig config = secMgr.loadSecurityConfig();
        RequestFilterChain chain = config.getFilterChain().getRequestChainByName(WEB_LOGIN_CHAIN_NAME);
        Assert.assertTrue(chain.isAllowSessionCreation());
    }
}

