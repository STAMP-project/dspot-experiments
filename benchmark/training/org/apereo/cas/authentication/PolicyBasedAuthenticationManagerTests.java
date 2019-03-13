package org.apereo.cas.authentication;


import DirtiesContext.MethodMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import lombok.val;
import org.apereo.cas.authentication.policy.AllCredentialsValidatedAuthenticationPolicy;
import org.apereo.cas.authentication.policy.AtLeastOneCredentialValidatedAuthenticationPolicy;
import org.apereo.cas.authentication.policy.RequiredHandlerAuthenticationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;


/**
 * Unit test for {@link PolicyBasedAuthenticationManager}.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
public class PolicyBasedAuthenticationManagerTests {
    private static final String HANDLER_A = "HandlerA";

    private static final String HANDLER_B = "HandlerB";

    private final AuthenticationTransaction transaction = DefaultAuthenticationTransaction.of(CoreAuthenticationTestUtils.getService(), Mockito.mock(Credential.class), Mockito.mock(Credential.class));

    @Test
    public void verifyAuthenticateAnySuccess() {
        val map = new HashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new AtLeastOneCredentialValidatedAuthenticationPolicy());
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        val auth = manager.authenticate(transaction);
        Assertions.assertEquals(1, auth.getSuccesses().size());
        Assertions.assertEquals(2, auth.getCredentials().size());
    }

    @Test
    public void verifyAuthenticateAnyButTryAllSuccess() {
        val map = new HashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new AtLeastOneCredentialValidatedAuthenticationPolicy(true));
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        val auth = manager.authenticate(transaction);
        Assertions.assertEquals(1, auth.getSuccesses().size());
        Assertions.assertEquals(1, auth.getFailures().size());
        Assertions.assertEquals(2, auth.getCredentials().size());
    }

    @Test
    public void verifyAuthenticateAnyFailure() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new AtLeastOneCredentialValidatedAuthenticationPolicy());
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        Assertions.assertThrows(AuthenticationException.class, () -> manager.authenticate(transaction));
    }

    @Test
    public void verifyAuthenticateAllSuccess() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(true), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new AllCredentialsValidatedAuthenticationPolicy());
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        val auth = manager.authenticate(transaction);
        Assertions.assertEquals(2, auth.getSuccesses().size());
        Assertions.assertEquals(0, auth.getFailures().size());
        Assertions.assertEquals(2, auth.getCredentials().size());
    }

    @Test
    public void verifyAuthenticateAllFailure() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new AllCredentialsValidatedAuthenticationPolicy());
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        Assertions.assertThrows(AuthenticationException.class, () -> manager.authenticate(transaction));
    }

    @Test
    public void verifyAuthenticateRequiredHandlerSuccess() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_A, true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_B, false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new RequiredHandlerAuthenticationPolicy(PolicyBasedAuthenticationManagerTests.HANDLER_A));
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        val auth = manager.authenticate(transaction);
        Assertions.assertEquals(1, auth.getSuccesses().size());
        Assertions.assertEquals(2, auth.getCredentials().size());
    }

    @Test
    public void verifyAuthenticateRequiredHandlerFailure() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_A, true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_B, false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new RequiredHandlerAuthenticationPolicy(PolicyBasedAuthenticationManagerTests.HANDLER_B));
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        Assertions.assertThrows(AuthenticationException.class, () -> manager.authenticate(transaction));
    }

    @Test
    public void verifyAuthenticateRequiredHandlerTryAllSuccess() {
        val map = new LinkedHashMap<AuthenticationHandler, org.apereo.cas.authentication.principal.PrincipalResolver>();
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_A, true), null);
        map.put(PolicyBasedAuthenticationManagerTests.newMockHandler(PolicyBasedAuthenticationManagerTests.HANDLER_B, false), null);
        val authenticationExecutionPlan = PolicyBasedAuthenticationManagerTests.getAuthenticationExecutionPlan(map);
        authenticationExecutionPlan.registerAuthenticationPolicy(new RequiredHandlerAuthenticationPolicy(PolicyBasedAuthenticationManagerTests.HANDLER_A, true));
        val manager = new PolicyBasedAuthenticationManager(authenticationExecutionPlan, false, Mockito.mock(ApplicationEventPublisher.class));
        val auth = manager.authenticate(transaction);
        Assertions.assertEquals(1, auth.getSuccesses().size());
        Assertions.assertEquals(1, auth.getFailures().size());
        Assertions.assertEquals(2, auth.getCredentials().size());
    }
}

