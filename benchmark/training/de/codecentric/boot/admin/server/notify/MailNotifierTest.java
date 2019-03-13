/**
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.server.notify;


import Message.RecipientType.CC;
import Message.RecipientType.TO;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class MailNotifierTest {
    private final Instance instance = Instance.create(InstanceId.of("cafebabe")).register(Registration.create("application-name", "http://localhost:8081/actuator/health").managementUrl("http://localhost:8081/actuator").serviceUrl("http://localhost:8081/").build());

    private JavaMailSender sender;

    private MailNotifier notifier;

    private InstanceRepository repository;

    @Test
    public void should_send_mail_using_default_template() throws IOException, MessagingException {
        Map<String, Object> details = new HashMap<>();
        details.put("Simple Value", 1234);
        details.put("Complex Value", Collections.singletonMap("Nested Simple Value", "99!"));
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofDown(details)))).verifyComplete();
        ArgumentCaptor<MimeMessage> mailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(sender).send(mailCaptor.capture());
        MimeMessage mail = mailCaptor.getValue();
        assertThat(mail.getSubject()).isEqualTo("application-name (cafebabe) is DOWN");
        assertThat(mail.getRecipients(TO)).containsExactly(new InternetAddress("foo@bar.com"));
        assertThat(mail.getRecipients(CC)).containsExactly(new InternetAddress("bar@foo.com"));
        assertThat(mail.getFrom()).containsExactly(new InternetAddress("SBA <no-reply@example.com>"));
        assertThat(mail.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
        String body = extractBody(mail.getDataHandler());
        assertThat(body).isEqualTo(loadExpectedBody("expected-default-mail"));
    }

    @Test
    public void should_send_mail_using_custom_template_with_additional_properties() throws IOException, MessagingException {
        notifier.setTemplate("/de/codecentric/boot/admin/server/notify/custom-mail.html");
        notifier.getAdditionalProperties().put("customProperty", "HELLO WORLD!");
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        ArgumentCaptor<MimeMessage> mailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(sender).send(mailCaptor.capture());
        MimeMessage mail = mailCaptor.getValue();
        String body = extractBody(mail.getDataHandler());
        assertThat(body).isEqualTo(loadExpectedBody("expected-custom-mail"));
    }

    // The following tests are rather for AbstractNotifier
    @Test
    public void should_not_send_mail_when_disabled() {
        notifier.setEnabled(false);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Mockito.verifyNoMoreInteractions(sender);
    }

    @Test
    public void should_not_send_when_unknown_to_up() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Mockito.verifyNoMoreInteractions(sender);
    }

    @Test
    public void should_not_send_on_wildcard_ignore() {
        notifier.setIgnoreChanges(new String[]{ "*:UP" });
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Mockito.verifyNoMoreInteractions(sender);
    }

    @Test
    public void should_not_propagate_error() {
        Notifier notifier = new AbstractStatusChangeNotifier(repository) {
            @Override
            protected Mono<Void> doNotify(InstanceEvent event, Instance application) {
                return Mono.error(new IllegalStateException("test"));
            }
        };
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
    }
}

