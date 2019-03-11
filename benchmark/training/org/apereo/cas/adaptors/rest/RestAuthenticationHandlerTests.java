package org.apereo.cas.adaptors.rest;


import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CasRestAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;


/**
 * This is {@link RestAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@SpringBootTest(classes = { CasRestAuthenticationConfiguration.class, CasCoreAuthenticationConfiguration.class, AopAutoConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreHttpConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreServicesConfiguration.class, RefreshAutoConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreUtilConfiguration.class })
@TestPropertySource(properties = "cas.authn.rest.uri=http://localhost:8081/authn")
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Tag("RestfulApi")
public class RestAuthenticationHandlerTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("restAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    @Qualifier("restAuthenticationTemplate")
    private RestTemplate restAuthenticationTemplate;

    private ResponseActions server;

    @Test
    public void verifySuccess() throws Exception {
        val principalWritten = new DefaultPrincipalFactory().createPrincipal("casuser");
        val writer = new StringWriter();
        RestAuthenticationHandlerTests.MAPPER.writeValue(writer, principalWritten);
        server.andRespond(withSuccess(writer.toString(), APPLICATION_JSON));
        val res = authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        Assertions.assertEquals("casuser", res.getPrincipal().getId());
    }

    @Test
    public void verifyDisabledAccount() {
        server.andRespond(withStatus(HttpStatus.FORBIDDEN).contentType(APPLICATION_JSON));
        Assertions.assertThrows(AccountDisabledException.class, () -> authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyUnauthorized() {
        server.andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        Assertions.assertThrows(FailedLoginException.class, () -> authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyNotFound() {
        server.andRespond(withStatus(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(AccountNotFoundException.class, () -> authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }
}

