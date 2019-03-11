/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.auth;


import GeoServerUser.ROOT_USERNAME;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.password.MasterPasswordProviderConfig;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Category(SystemTest.class)
public class GeoServerRootAuthenticationProviderTest extends GeoServerSecurityTestSupport {
    @Test
    public void testRootProvider() throws Exception {
        // Check if the root provider is the first
        AuthenticationProvider first = getSecurityManager().getProviders().get(0);
        Assert.assertEquals(GeoServerRootAuthenticationProvider.class, first.getClass());
        GeoServerRootAuthenticationProvider provider = new GeoServerRootAuthenticationProvider();
        provider.setSecurityManager(getSecurityManager());
        provider.initializeFromConfig(null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("abc", null);
        Assert.assertTrue(provider.supports(token.getClass()));
        Assert.assertFalse(provider.supports(RememberMeAuthenticationToken.class));
        Assert.assertNull(provider.authenticate(token));
        token = new UsernamePasswordAuthenticationToken(GeoServerUser.ROOT_USERNAME, null);
        Assert.assertNull(provider.authenticate(token));
        token = new UsernamePasswordAuthenticationToken(GeoServerUser.ROOT_USERNAME, "abc");
        Assert.assertNull(provider.authenticate(token));
        String masterPassword = getMasterPassword();
        // We need to enable Master Root login first
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        token = new UsernamePasswordAuthenticationToken(GeoServerUser.ROOT_USERNAME, masterPassword);
        token.setDetails("hallo");
        UsernamePasswordAuthenticationToken result = ((UsernamePasswordAuthenticationToken) (provider.authenticate(token)));
        Assert.assertNotNull(result);
        Assert.assertNull(result.getCredentials());
        Assert.assertEquals(ROOT_USERNAME, result.getPrincipal());
        Assert.assertEquals("hallo", result.getDetails());
    }
}

