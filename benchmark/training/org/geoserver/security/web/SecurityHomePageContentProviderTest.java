/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web;


import org.junit.Test;


public class SecurityHomePageContentProviderTest extends AbstractSecurityWicketTestSupport {
    @Test
    public void testMasterPasswordMessageWithLoginDisabled() throws Exception {
        checkMasterPasswordMessage(false);
    }

    @Test
    public void testMasterPasswordMessageWithLoginEnabled() throws Exception {
        checkMasterPasswordMessage(true);
    }
}

