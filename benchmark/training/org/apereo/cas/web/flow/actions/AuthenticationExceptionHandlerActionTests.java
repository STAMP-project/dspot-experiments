package org.apereo.cas.web.flow.actions;


import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Optional;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.ContextualAuthenticationPolicy;
import org.apereo.cas.configuration.model.core.web.MessageBundleProperties;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.UnsatisfiedAuthenticationPolicyException;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.binding.message.DefaultMessageResolver;


/**
 *
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@Slf4j
public class AuthenticationExceptionHandlerActionTests {
    @Test
    public void handleAccountNotFoundExceptionByDefault() {
        val handler = new AuthenticationExceptionHandlerAction(CollectionUtils.wrapSet(AccountLockedException.class, AccountNotFoundException.class), MessageBundleProperties.DEFAULT_BUNDLE_PREFIX_AUTHN_FAILURE);
        val req = AuthenticationExceptionHandlerActionTests.getMockRequestContext();
        val map = new HashMap<String, Throwable>();
        map.put("notFound", new AccountNotFoundException());
        val id = handler.handle(new org.apereo.cas.authentication.AuthenticationException(map), req);
        Assertions.assertEquals(AccountNotFoundException.class.getSimpleName(), id);
    }

    @Test
    public void handleUnknownExceptionByDefault() {
        val handler = new AuthenticationExceptionHandlerAction(MessageBundleProperties.DEFAULT_BUNDLE_PREFIX_AUTHN_FAILURE);
        val req = AuthenticationExceptionHandlerActionTests.getMockRequestContext();
        val map = new HashMap<String, Throwable>();
        map.put("unknown", new GeneralSecurityException());
        val id = handler.handle(new org.apereo.cas.authentication.AuthenticationException(map), req);
        Assertions.assertEquals("UNKNOWN", id);
    }

    @Test
    public void handleUnknownTicketExceptionByDefault() {
        val handler = new AuthenticationExceptionHandlerAction();
        val req = AuthenticationExceptionHandlerActionTests.getMockRequestContext();
        val id = handler.handle(new InvalidTicketException("TGT"), req);
        Assertions.assertEquals("UNKNOWN", id);
    }

    @Test
    public void handleUnsatisfiedAuthenticationPolicyExceptionByDefault() {
        val handler = new AuthenticationExceptionHandlerAction(CollectionUtils.wrapSet(UnsatisfiedAuthenticationPolicyException.class, AccountNotFoundException.class), MessageBundleProperties.DEFAULT_BUNDLE_PREFIX_AUTHN_FAILURE);
        val req = AuthenticationExceptionHandlerActionTests.getMockRequestContext();
        val policy = new AuthenticationExceptionHandlerActionTests.TestContextualAuthenticationPolicy();
        val id = handler.handle(new UnsatisfiedAuthenticationPolicyException(policy), req);
        Assertions.assertEquals("UnsatisfiedAuthenticationPolicyException", id);
        val message = ArgumentCaptor.forClass(DefaultMessageResolver.class);
        Mockito.verify(req.getMessageContext(), Mockito.times(1)).addMessage(message.capture());
        Assertions.assertArrayEquals(new String[]{ policy.getCode().get() }, message.getValue().getCodes());
    }

    private static class TestContextualAuthenticationPolicy implements ContextualAuthenticationPolicy<Object> {
        @Override
        public Optional<String> getCode() {
            return Optional.of("CUSTOM_CODE");
        }

        @Override
        public Object getContext() {
            return null;
        }

        @Override
        public boolean isSatisfiedBy(final Authentication authentication) {
            return false;
        }
    }
}

