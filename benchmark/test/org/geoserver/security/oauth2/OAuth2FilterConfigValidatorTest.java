/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.oauth2;


import java.util.logging.Logger;
import org.geoserver.test.GeoServerMockTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Test;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions S.A.S.
<p>Validates {@link OAuth2FilterConfig} objects.
 */
public class OAuth2FilterConfigValidatorTest extends GeoServerMockTestSupport {
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    OAuth2FilterConfigValidator validator;

    @Test
    public void testOAuth2FilterConfigValidation() throws Exception {
        GeoNodeOAuth2FilterConfig config = new GeoNodeOAuth2FilterConfig();
        config.setClassName(GeoServerOAuthAuthenticationFilter.class.getName());
        config.setName("testOAuth2");
        check(config);
        validator.validateOAuth2FilterConfig(config);
    }
}

