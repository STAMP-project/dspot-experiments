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
package org.springframework.boot.actuate.mail;


import Status.DOWN;
import Status.UP;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Tests for {@link MailHealthIndicator}.
 *
 * @author Johannes Edmeier
 * @author Stephane Nicoll
 */
public class MailHealthIndicatorTests {
    private JavaMailSenderImpl mailSender;

    private MailHealthIndicator indicator;

    @Test
    public void smtpIsUp() {
        BDDMockito.given(this.mailSender.getProtocol()).willReturn("success");
        Health health = this.indicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("location")).isEqualTo("smtp.acme.org:25");
    }

    @Test
    public void smtpIsDown() throws MessagingException {
        BDDMockito.willThrow(new MessagingException("A test exception")).given(this.mailSender).testConnection();
        Health health = this.indicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("location")).isEqualTo("smtp.acme.org:25");
        Object errorMessage = health.getDetails().get("error");
        assertThat(errorMessage).isNotNull();
        assertThat(errorMessage.toString().contains("A test exception")).isTrue();
    }

    public static class SuccessTransport extends Transport {
        public SuccessTransport(Session session, URLName urlName) {
            super(session, urlName);
        }

        @Override
        public void connect(String host, int port, String user, String password) {
        }

        @Override
        public void sendMessage(Message msg, Address[] addresses) {
        }
    }
}

