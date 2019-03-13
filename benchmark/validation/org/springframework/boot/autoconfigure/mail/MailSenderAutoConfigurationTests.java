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
package org.springframework.boot.autoconfigure.mail;


import javax.mail.Session;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Tests for {@link MailSenderAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 */
public class MailSenderAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MailSenderAutoConfiguration.class, MailSenderValidatorAutoConfiguration.class));

    private ClassLoader threadContextClassLoader;

    private String initialContextFactory;

    @Test
    public void smtpHostSet() {
        String host = "192.168.1.234";
        this.contextRunner.withPropertyValues(("spring.mail.host:" + host)).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getHost()).isEqualTo(host);
            assertThat(mailSender.getPort()).isEqualTo(JavaMailSenderImpl.DEFAULT_PORT);
            assertThat(mailSender.getProtocol()).isEqualTo(JavaMailSenderImpl.DEFAULT_PROTOCOL);
        });
    }

    @Test
    public void smtpHostWithSettings() {
        String host = "192.168.1.234";
        this.contextRunner.withPropertyValues(("spring.mail.host:" + host), "spring.mail.port:42", "spring.mail.username:john", "spring.mail.password:secret", "spring.mail.default-encoding:US-ASCII", "spring.mail.protocol:smtps").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getHost()).isEqualTo(host);
            assertThat(mailSender.getPort()).isEqualTo(42);
            assertThat(mailSender.getUsername()).isEqualTo("john");
            assertThat(mailSender.getPassword()).isEqualTo("secret");
            assertThat(mailSender.getDefaultEncoding()).isEqualTo("US-ASCII");
            assertThat(mailSender.getProtocol()).isEqualTo("smtps");
        });
    }

    @Test
    public void smtpHostWithJavaMailProperties() {
        this.contextRunner.withPropertyValues("spring.mail.host:localhost", "spring.mail.properties.mail.smtp.auth:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getJavaMailProperties().get("mail.smtp.auth")).isEqualTo("true");
        });
    }

    @Test
    public void smtpHostNotSet() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void mailSenderBackOff() {
        this.contextRunner.withUserConfiguration(MailSenderAutoConfigurationTests.ManualMailConfiguration.class).withPropertyValues("spring.mail.host:smtp.acme.org", "spring.mail.user:user", "spring.mail.password:secret").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getUsername()).isNull();
            assertThat(mailSender.getPassword()).isNull();
        });
    }

    @Test
    public void jndiSessionAvailable() {
        Session session = configureJndiSession("java:comp/env/foo");
        testJndiSessionLookup(session, "java:comp/env/foo");
    }

    @Test
    public void jndiSessionAvailableWithResourceRef() {
        Session session = configureJndiSession("java:comp/env/foo");
        testJndiSessionLookup(session, "foo");
    }

    @Test
    public void jndiSessionIgnoredIfJndiNameNotSet() {
        configureJndiSession("foo");
        this.contextRunner.withPropertyValues("spring.mail.host:smtp.acme.org").run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void jndiSessionNotUsedIfJndiNameNotSet() {
        configureJndiSession("foo");
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void jndiSessionNotAvailableWithJndiName() {
        this.contextRunner.withPropertyValues("spring.mail.jndi-name:foo").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(.class).hasMessageContaining("Unable to find Session in JNDI location foo");
        });
    }

    @Test
    public void jndiSessionTakesPrecedenceOverProperties() {
        Session session = configureJndiSession("foo");
        this.contextRunner.withPropertyValues("spring.mail.jndi-name:foo", "spring.mail.host:localhost").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Session sessionBean = context.getBean(.class);
            assertThat(sessionBean).isEqualTo(session);
            assertThat(context.getBean(.class).getSession()).isEqualTo(sessionBean);
        });
    }

    @Test
    public void defaultEncodingWithProperties() {
        this.contextRunner.withPropertyValues("spring.mail.host:localhost", "spring.mail.default-encoding:UTF-16").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getDefaultEncoding()).isEqualTo("UTF-16");
        });
    }

    @Test
    public void defaultEncodingWithJndi() {
        configureJndiSession("foo");
        this.contextRunner.withPropertyValues("spring.mail.jndi-name:foo", "spring.mail.default-encoding:UTF-16").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            assertThat(mailSender.getDefaultEncoding()).isEqualTo("UTF-16");
        });
    }

    @Test
    public void connectionOnStartup() {
        this.contextRunner.withUserConfiguration(MailSenderAutoConfigurationTests.MockMailConfiguration.class).withPropertyValues("spring.mail.host:10.0.0.23", "spring.mail.test-connection:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            verify(mailSender, times(1)).testConnection();
        });
    }

    @Test
    public void connectionOnStartupNotCalled() {
        this.contextRunner.withUserConfiguration(MailSenderAutoConfigurationTests.MockMailConfiguration.class).withPropertyValues("spring.mail.host:10.0.0.23", "spring.mail.test-connection:false").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JavaMailSenderImpl mailSender = context.getBean(.class);
            verify(mailSender, never()).testConnection();
        });
    }

    @Configuration
    static class ManualMailConfiguration {
        @Bean
        JavaMailSender customMailSender() {
            return new JavaMailSenderImpl();
        }
    }

    @Configuration
    static class MockMailConfiguration {
        @Bean
        JavaMailSenderImpl mockMailSender() {
            return Mockito.mock(JavaMailSenderImpl.class);
        }
    }
}

