package org.apereo.cas.syncope.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.syncope.common.lib.to.UserTO;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.SyncopeAuthenticationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link SyncopeAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SuppressWarnings("unused")
@SpringBootTest(classes = { RefreshAutoConfiguration.class, SyncopeAuthenticationConfiguration.class, CasCoreServicesConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class, CasPersonDirectoryTestConfiguration.class })
@TestPropertySource(properties = "cas.authn.syncope.url=http://localhost:8095")
@Slf4j
@ResourceLock("Syncope")
public class SyncopeAuthenticationHandlerTests {
    private static final ObjectMapper MAPPER = new IgnoringJaxbModuleJacksonObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("syncopeAuthenticationHandler")
    private AuthenticationHandler syncopeAuthenticationHandler;

    @Test
    public void verifyHandlerPasses() {
        val user = new UserTO();
        user.setUsername("casuser");
        @Cleanup("stop")
        val webserver = SyncopeAuthenticationHandlerTests.startMockSever(user);
        Assertions.assertDoesNotThrow(() -> syncopeAuthenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser")));
    }

    @Test
    public void verifyHandlerMustChangePassword() {
        val user = new UserTO();
        user.setUsername("casuser");
        user.setMustChangePassword(true);
        @Cleanup("stop")
        val webserver = SyncopeAuthenticationHandlerTests.startMockSever(user);
        Assertions.assertThrows(AccountPasswordMustChangeException.class, () -> syncopeAuthenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser")));
    }

    @Test
    public void verifyHandlerSuspended() {
        val user = new UserTO();
        user.setUsername("casuser");
        user.setSuspended(true);
        @Cleanup("stop")
        val webserver = SyncopeAuthenticationHandlerTests.startMockSever(user);
        Assertions.assertThrows(AccountDisabledException.class, () -> syncopeAuthenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser")));
    }
}

