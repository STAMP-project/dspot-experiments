package org.apereo.cas.authentication.handler.support;


import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class JaasAuthenticationHandlerTests {
    private File fileName;

    @Test
    @SneakyThrows
    public void verifyWithValidCredentials() {
        val handler = new org.apereo.cas.authentication.handler.support.jaas.JaasAuthenticationHandler("JAAS", Mockito.mock(ServicesManager.class), PrincipalFactoryUtils.newPrincipalFactory(), 0);
        handler.setLoginConfigType("JavaLoginConfig");
        handler.setLoginConfigurationFile(this.fileName);
        handler.setRealm("CAS");
        Assertions.assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    @SneakyThrows
    public void verifyWithValidCredentialsPreDefined() {
        val handler = new org.apereo.cas.authentication.handler.support.jaas.JaasAuthenticationHandler("JAAS", Mockito.mock(ServicesManager.class), PrincipalFactoryUtils.newPrincipalFactory(), 0);
        handler.setLoginConfigType("JavaLoginConfig");
        handler.setLoginConfigurationFile(this.fileName);
        handler.setRealm("ACCTS");
        Assertions.assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon")));
    }
}

