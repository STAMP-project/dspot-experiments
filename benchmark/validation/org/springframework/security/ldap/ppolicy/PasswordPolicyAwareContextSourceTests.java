/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.ldap.ppolicy;


import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.ldap.UncategorizedLdapException;


/**
 *
 *
 * @author Luke Taylor
 */
public class PasswordPolicyAwareContextSourceTests {
    private PasswordPolicyAwareContextSource ctxSource;

    private final LdapContext ctx = Mockito.mock(LdapContext.class);

    @Test
    public void contextIsReturnedWhenNoControlsAreSetAndReconnectIsSuccessful() throws Exception {
        assertThat(ctxSource.getContext("user", "ignored")).isNotNull();
    }

    @Test(expected = UncategorizedLdapException.class)
    public void standardExceptionIsPropagatedWhenExceptionRaisedAndNoControlsAreSet() throws Exception {
        Mockito.doThrow(new NamingException("some LDAP exception")).when(ctx).reconnect(ArgumentMatchers.any(Control[].class));
        ctxSource.getContext("user", "ignored");
    }

    @Test(expected = PasswordPolicyException.class)
    public void lockedPasswordPolicyControlRaisesPasswordPolicyException() throws Exception {
        Mockito.when(ctx.getResponseControls()).thenReturn(new Control[]{ new PasswordPolicyResponseControl(PasswordPolicyResponseControlTests.OPENLDAP_LOCKED_CTRL) });
        Mockito.doThrow(new NamingException("locked message")).when(ctx).reconnect(ArgumentMatchers.any(Control[].class));
        ctxSource.getContext("user", "ignored");
    }
}

