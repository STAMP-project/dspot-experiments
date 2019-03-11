package org.apereo.cas;


import AuthenticationHandler.SUCCESSFUL_AUTHENTICATION_HANDLERS;
import lombok.val;
import org.apereo.cas.authentication.AcceptUsersAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.config.CasMultifactorTestAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.ticket.UnsatisfiedAuthenticationPolicyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


/**
 * High-level MFA functionality tests that leverage registered service metadata
 * ala {@link RequiredHandlerAuthenticationPolicyFactory} to drive
 * authentication policy.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@TestPropertySource(locations = { "classpath:/core.properties" }, properties = "cas.authn.policy.requiredHandlerAuthenticationPolicyEnabled=true")
@Import(CasMultifactorTestAuthenticationEventExecutionPlanConfiguration.class)
public class MultifactorAuthenticationTests extends BaseCasWebflowMultifactorAuthenticationTests {
    private static final Service NORMAL_SERVICE = MultifactorAuthenticationTests.newService("https://example.com/normal/");

    private static final Service HIGH_SERVICE = MultifactorAuthenticationTests.newService("https://example.com/high/");

    private static final String ALICE = "alice";

    private static final String PASSWORD_31415 = "31415";

    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService cas;

    @Test
    public void verifyAllowsAccessToNormalSecurityServiceWithPassword() {
        val ctx = processAuthenticationAttempt(MultifactorAuthenticationTests.NORMAL_SERVICE, MultifactorAuthenticationTests.newUserPassCredentials(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.ALICE));
        val tgt = cas.createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(tgt);
        val st = cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.NORMAL_SERVICE, ctx);
        Assertions.assertNotNull(st);
    }

    @Test
    public void verifyAllowsAccessToNormalSecurityServiceWithOTP() {
        val ctx = processAuthenticationAttempt(MultifactorAuthenticationTests.NORMAL_SERVICE, new OneTimePasswordCredential(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.PASSWORD_31415));
        val tgt = cas.createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(tgt);
        val st = cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.NORMAL_SERVICE, ctx);
        Assertions.assertNotNull(st);
    }

    @Test
    public void verifyDeniesAccessToHighSecurityServiceWithPassword() {
        val ctx = processAuthenticationAttempt(MultifactorAuthenticationTests.HIGH_SERVICE, MultifactorAuthenticationTests.newUserPassCredentials(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.ALICE));
        val tgt = cas.createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(tgt);
        Assertions.assertThrows(UnsatisfiedAuthenticationPolicyException.class, () -> cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.HIGH_SERVICE, ctx));
    }

    @Test
    public void verifyDeniesAccessToHighSecurityServiceWithOTP() {
        val ctx = processAuthenticationAttempt(MultifactorAuthenticationTests.HIGH_SERVICE, new OneTimePasswordCredential(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.PASSWORD_31415));
        val tgt = cas.createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(tgt);
        Assertions.assertThrows(UnsatisfiedAuthenticationPolicyException.class, () -> cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.HIGH_SERVICE, ctx));
    }

    @Test
    public void verifyAllowsAccessToHighSecurityServiceWithPasswordAndOTP() {
        val ctx = processAuthenticationAttempt(MultifactorAuthenticationTests.HIGH_SERVICE, MultifactorAuthenticationTests.newUserPassCredentials(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.ALICE), new OneTimePasswordCredential(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.PASSWORD_31415));
        val tgt = cas.createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(tgt);
        val st = cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.HIGH_SERVICE, ctx);
        Assertions.assertNotNull(st);
    }

    @Test
    public void verifyAllowsAccessToHighSecurityServiceWithPasswordAndOTPViaRenew() {
        val ctx2 = processAuthenticationAttempt(MultifactorAuthenticationTests.HIGH_SERVICE, MultifactorAuthenticationTests.newUserPassCredentials(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.ALICE), new OneTimePasswordCredential(MultifactorAuthenticationTests.ALICE, MultifactorAuthenticationTests.PASSWORD_31415));
        val tgt = cas.createTicketGrantingTicket(ctx2);
        Assertions.assertNotNull(tgt);
        val st = cas.grantServiceTicket(tgt.getId(), MultifactorAuthenticationTests.HIGH_SERVICE, ctx2);
        Assertions.assertNotNull(st);
        // Confirm the authentication in the assertion is the one that satisfies security policy
        val assertion = cas.validateServiceTicket(st.getId(), MultifactorAuthenticationTests.HIGH_SERVICE);
        Assertions.assertEquals(2, assertion.getPrimaryAuthentication().getSuccesses().size());
        Assertions.assertTrue(assertion.getPrimaryAuthentication().getSuccesses().containsKey(AcceptUsersAuthenticationHandler.class.getSimpleName()));
        Assertions.assertTrue(assertion.getPrimaryAuthentication().getSuccesses().containsKey(TestOneTimePasswordAuthenticationHandler.class.getSimpleName()));
        Assertions.assertTrue(assertion.getPrimaryAuthentication().getAttributes().containsKey(SUCCESSFUL_AUTHENTICATION_HANDLERS));
    }
}

