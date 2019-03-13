package org.apereo.cas.authentication.surrogate;


import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


/**
 * This is {@link BaseSurrogateAuthenticationServiceTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
public abstract class BaseSurrogateAuthenticationServiceTests {
    public static final String CASUSER = "casuser";

    public static final String BANDERSON = "banderson";

    @Mock
    protected ServicesManager servicesManager;

    @Test
    public void verifyList() {
        Assertions.assertFalse(getService().getEligibleAccountsForSurrogateToProxy(BaseSurrogateAuthenticationServiceTests.CASUSER).isEmpty());
    }

    @Test
    public void verifyProxying() {
        Assertions.assertTrue(getService().canAuthenticateAs(BaseSurrogateAuthenticationServiceTests.BANDERSON, CoreAuthenticationTestUtils.getPrincipal(BaseSurrogateAuthenticationServiceTests.CASUSER), CoreAuthenticationTestUtils.getService()));
        Assertions.assertFalse(getService().canAuthenticateAs("XXXX", CoreAuthenticationTestUtils.getPrincipal(BaseSurrogateAuthenticationServiceTests.CASUSER), CoreAuthenticationTestUtils.getService()));
        Assertions.assertFalse(getService().canAuthenticateAs(BaseSurrogateAuthenticationServiceTests.CASUSER, CoreAuthenticationTestUtils.getPrincipal(BaseSurrogateAuthenticationServiceTests.BANDERSON), CoreAuthenticationTestUtils.getService()));
    }
}

