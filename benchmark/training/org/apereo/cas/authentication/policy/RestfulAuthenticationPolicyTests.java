package org.apereo.cas.authentication.policy;


import HttpStatus.FORBIDDEN;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.LOCKED;
import HttpStatus.METHOD_NOT_ALLOWED;
import HttpStatus.NOT_FOUND;
import HttpStatus.PRECONDITION_FAILED;
import HttpStatus.PRECONDITION_REQUIRED;
import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON_UTF8;
import java.util.LinkedHashSet;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;


/**
 * This is {@link RestfulAuthenticationPolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreWebConfiguration.class, CasCoreUtilConfiguration.class, CasCoreHttpConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreTicketCatalogConfiguration.class })
@DirtiesContext
@Tag("RestfulApi")
public class RestfulAuthenticationPolicyTests {
    private static final String URI = "http://rest.endpoint.com";

    @Test
    @SneakyThrows
    public void verifyPolicyGood() {
        val restTemplate = new RestTemplate();
        val mockServer = RestfulAuthenticationPolicyTests.newServer(restTemplate);
        val policy = RestfulAuthenticationPolicyTests.newPolicy(restTemplate);
        mockServer.expect(requestTo(RestfulAuthenticationPolicyTests.URI)).andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess());
        Assertions.assertTrue(policy.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication("casuser"), new LinkedHashSet()));
        mockServer.verify();
    }

    @Test
    public void verifyPolicyFailsWithStatusCodes() {
        Assertions.assertAll(() -> {
            RestfulAuthenticationPolicyTests.assertPolicyFails(FailedLoginException.class, UNAUTHORIZED);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountLockedException.class, LOCKED);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountDisabledException.class, METHOD_NOT_ALLOWED);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountDisabledException.class, FORBIDDEN);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountNotFoundException.class, NOT_FOUND);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountExpiredException.class, PRECONDITION_FAILED);
            RestfulAuthenticationPolicyTests.assertPolicyFails(AccountPasswordMustChangeException.class, PRECONDITION_REQUIRED);
            RestfulAuthenticationPolicyTests.assertPolicyFails(FailedLoginException.class, INTERNAL_SERVER_ERROR);
        });
    }
}

