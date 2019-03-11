package org.apereo.cas.authentication.support;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import lombok.val;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.auth.AccountState;
import org.ldaptive.auth.AuthenticationResponse;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This is {@link OptionalWarningLdapAccountStateHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OptionalWarningLdapAccountStateHandlerTests {
    @Test
    public void verifyWarningOnMatch() {
        val h = new OptionalWarningLdapAccountStateHandler();
        h.setWarnAttributeName("attribute");
        h.setWarningAttributeValue("value");
        h.setDisplayWarningOnMatch(true);
        val response = Mockito.mock(AuthenticationResponse.class);
        val entry = Mockito.mock(LdapEntry.class);
        Mockito.when(response.getLdapEntry()).thenReturn(entry);
        Mockito.when(entry.getAttribute(ArgumentMatchers.anyString())).thenReturn(new LdapAttribute("attribute", "value"));
        val messages = new ArrayList<org.apereo.cas.authentication.MessageDescriptor>();
        val config = new PasswordPolicyConfiguration();
        config.setPasswordWarningNumberOfDays(5);
        h.handleWarning(new AccountState.DefaultWarning(ZonedDateTime.now(), 1), response, config, messages);
        Assertions.assertEquals(2, messages.size());
    }

    @Test
    public void verifyAlwaysWarningOnMatch() {
        val h = new OptionalWarningLdapAccountStateHandler();
        h.setWarnAttributeName("attribute");
        h.setWarningAttributeValue("value");
        h.setDisplayWarningOnMatch(true);
        val response = Mockito.mock(AuthenticationResponse.class);
        val entry = Mockito.mock(LdapEntry.class);
        Mockito.when(response.getLdapEntry()).thenReturn(entry);
        Mockito.when(entry.getAttribute(ArgumentMatchers.anyString())).thenReturn(new LdapAttribute("attribute", "value"));
        val messages = new ArrayList<org.apereo.cas.authentication.MessageDescriptor>();
        val config = new PasswordPolicyConfiguration();
        config.setAlwaysDisplayPasswordExpirationWarning(true);
        h.handleWarning(new AccountState.DefaultWarning(ZonedDateTime.now(), 1), response, config, messages);
        Assertions.assertEquals(2, messages.size());
    }

    @Test
    public void verifyNoWarningOnMatch() {
        val h = new OptionalWarningLdapAccountStateHandler();
        h.setWarnAttributeName("attribute");
        h.setWarningAttributeValue("value");
        h.setDisplayWarningOnMatch(false);
        val response = Mockito.mock(AuthenticationResponse.class);
        val entry = Mockito.mock(LdapEntry.class);
        Mockito.when(response.getLdapEntry()).thenReturn(entry);
        Mockito.when(entry.getAttribute(ArgumentMatchers.anyString())).thenReturn(new LdapAttribute("attribute", "value"));
        val messages = new ArrayList<org.apereo.cas.authentication.MessageDescriptor>();
        val config = new PasswordPolicyConfiguration();
        config.setPasswordWarningNumberOfDays(5);
        h.handleWarning(new AccountState.DefaultWarning(ZonedDateTime.now(), 1), response, config, messages);
        Assertions.assertEquals(0, messages.size());
    }
}

