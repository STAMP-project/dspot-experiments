/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.ldap;


import Status.DOWN;
import Status.UP;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.LdapTemplate;


/**
 * Tests for {@link LdapHealthIndicator}
 *
 * @author Edd? Mel?ndez
 */
public class LdapHealthIndicatorTests {
    @Test
    @SuppressWarnings("unchecked")
    public void ldapIsUp() {
        LdapTemplate ldapTemplate = Mockito.mock(LdapTemplate.class);
        BDDMockito.given(ldapTemplate.executeReadOnly(((ContextExecutor<String>) (ArgumentMatchers.any())))).willReturn("3");
        LdapHealthIndicator healthIndicator = new LdapHealthIndicator(ldapTemplate);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("version")).isEqualTo("3");
        Mockito.verify(ldapTemplate).executeReadOnly(((ContextExecutor<String>) (ArgumentMatchers.any())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ldapIsDown() {
        LdapTemplate ldapTemplate = Mockito.mock(LdapTemplate.class);
        BDDMockito.given(ldapTemplate.executeReadOnly(((ContextExecutor<String>) (ArgumentMatchers.any())))).willThrow(new CommunicationException(new javax.naming.CommunicationException("Connection failed")));
        LdapHealthIndicator healthIndicator = new LdapHealthIndicator(ldapTemplate);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection failed");
        Mockito.verify(ldapTemplate).executeReadOnly(((ContextExecutor<String>) (ArgumentMatchers.any())));
    }
}

