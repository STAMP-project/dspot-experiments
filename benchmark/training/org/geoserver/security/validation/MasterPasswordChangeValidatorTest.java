/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.validation;


import URLMasterPasswordProviderException.URL_LOCATION_NOT_READABLE;
import URLMasterPasswordProviderException.URL_REQUIRED;
import java.net.URL;
import org.geoserver.security.password.URLMasterPasswordProvider;
import org.geoserver.security.password.URLMasterPasswordProvider.URLMasterPasswordProviderValidator;
import org.geoserver.test.GeoServerMockTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class MasterPasswordChangeValidatorTest extends GeoServerMockTestSupport {
    MasterPasswordChangeValidator validator;

    @Test
    public void testUrlConfig() throws Exception {
        URLMasterPasswordProviderValidator validator = new URLMasterPasswordProviderValidator(getSecurityManager());
        URLMasterPasswordProviderConfig config = new URLMasterPasswordProviderConfig();
        config.setName("foo");
        config.setClassName(URLMasterPasswordProvider.class.getCanonicalName());
        try {
            validator.validateAddMasterPasswordProvider(config);
            // getSecurityManager().saveMasterPasswordProviderConfig(config);
            Assert.fail();
        } catch (URLMasterPasswordProviderException e) {
            assertSecurityException(e, URL_REQUIRED);
        }
        config.setURL(new URL("file:ABC"));
        config.setReadOnly(true);
        try {
            validator.validateAddMasterPasswordProvider(config);
            // getSecurityManager().saveMasterPasswordProviderConfig(config);
            Assert.fail();
        } catch (URLMasterPasswordProviderException e) {
            assertSecurityException(e, URL_LOCATION_NOT_READABLE, new URL("file:ABC"));
        }
    }

    @Test
    public void testValidator() throws Exception {
        // test spring
        MasterPasswordChangeRequest r = new MasterPasswordChangeRequest();
        checkCurrentPassword(r);
        r.setCurrentPassword("geoserver".toCharArray());
        // r.setCurrentPassword(getMasterPassword().toCharArray());
        checkConfirmationPassword(r);
        r.setConfirmPassword("abc".toCharArray());
        checkNewPassword(r);
        r.setNewPassword("def".toCharArray());
        checkConfirmationEqualsNewPassword(r);
        r.setNewPassword("abc".toCharArray());
        validateAgainstPolicy(r);
        r.setConfirmPassword(r.getCurrentPassword());
        r.setNewPassword(r.getCurrentPassword());
        checkCurrentEqualsNewPassword(r);
        r.setConfirmPassword(((new String(r.getCurrentPassword())) + "1").toCharArray());
        r.setNewPassword(((new String(r.getCurrentPassword())) + "1").toCharArray());
        validator.validateChangeRequest(r);
    }
}

