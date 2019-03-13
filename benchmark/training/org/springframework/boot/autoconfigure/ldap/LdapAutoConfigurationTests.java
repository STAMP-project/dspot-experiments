/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.ldap;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;


/**
 * Tests for {@link LdapAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Vedran Pavic
 */
public class LdapAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(LdapAutoConfiguration.class));

    @Test
    public void contextSourceWithDefaultUrl() {
        this.contextRunner.run(( context) -> {
            LdapContextSource contextSource = context.getBean(.class);
            assertThat(contextSource.getUrls()).containsExactly("ldap://localhost:389");
            assertThat(contextSource.isAnonymousReadOnly()).isFalse();
        });
    }

    @Test
    public void contextSourceWithSingleUrl() {
        this.contextRunner.withPropertyValues("spring.ldap.urls:ldap://localhost:123").run(( context) -> {
            LdapContextSource contextSource = context.getBean(.class);
            assertThat(contextSource.getUrls()).containsExactly("ldap://localhost:123");
        });
    }

    @Test
    public void contextSourceWithSeveralUrls() {
        this.contextRunner.withPropertyValues("spring.ldap.urls:ldap://localhost:123,ldap://mycompany:123").run(( context) -> {
            LdapContextSource contextSource = context.getBean(.class);
            LdapProperties ldapProperties = context.getBean(.class);
            assertThat(contextSource.getUrls()).containsExactly("ldap://localhost:123", "ldap://mycompany:123");
            assertThat(ldapProperties.getUrls()).hasSize(2);
        });
    }

    @Test
    public void contextSourceWithExtraCustomization() {
        this.contextRunner.withPropertyValues("spring.ldap.urls:ldap://localhost:123", "spring.ldap.username:root", "spring.ldap.password:secret", "spring.ldap.anonymous-read-only:true", "spring.ldap.base:cn=SpringDevelopers", "spring.ldap.baseEnvironment.java.naming.security.authentication:DIGEST-MD5").run(( context) -> {
            LdapContextSource contextSource = context.getBean(.class);
            assertThat(contextSource.getUserDn()).isEqualTo("root");
            assertThat(contextSource.getPassword()).isEqualTo("secret");
            assertThat(contextSource.isAnonymousReadOnly()).isTrue();
            assertThat(contextSource.getBaseLdapPathAsString()).isEqualTo("cn=SpringDevelopers");
            LdapProperties ldapProperties = context.getBean(.class);
            assertThat(ldapProperties.getBaseEnvironment()).containsEntry("java.naming.security.authentication", "DIGEST-MD5");
        });
    }

    @Test
    public void templateExists() {
        this.contextRunner.withPropertyValues("spring.ldap.urls:ldap://localhost:389").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void contextSourceWithUserProvidedPooledContextSource() {
        this.contextRunner.withUserConfiguration(LdapAutoConfigurationTests.PooledContextSourceConfig.class).run(( context) -> {
            LdapContextSource contextSource = context.getBean(.class);
            assertThat(contextSource.getUrls()).containsExactly("ldap://localhost:389");
            assertThat(contextSource.isAnonymousReadOnly()).isFalse();
        });
    }

    @Configuration
    static class PooledContextSourceConfig {
        @Bean
        @Primary
        public PooledContextSource pooledContextSource(LdapContextSource ldapContextSource) {
            PooledContextSource pooledContextSource = new PooledContextSource(new PoolConfig());
            pooledContextSource.setContextSource(ldapContextSource);
            return pooledContextSource;
        }
    }
}

