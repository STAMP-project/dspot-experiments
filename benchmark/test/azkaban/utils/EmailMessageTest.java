/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.utils;


import RecipientType.TO;
import javax.mail.Address;
import javax.mail.Message;
import org.junit.Test;
import org.mockito.Mockito;


public class EmailMessageTest {
    private final String host = "example.com";

    private final int port = 25;

    private final String sender = "from@example.com";

    private final String user = "user";

    private final String password = "pass";

    private final String toAddr = "to@example.com";

    private EmailMessage em;

    private JavaxMailSender mailSender;

    private Message mimeMessage;

    private Address[] addresses;

    private EmailMessageCreator creator;

    @Test
    public void testSendEmail() throws Exception {
        this.em.setTLS("true");
        this.em.addToAddress(this.toAddr);
        this.em.setFromAddress(this.sender);
        this.em.setSubject("azkaban test email");
        this.em.setBody("azkaban test email");
        this.em.sendEmail();
        Mockito.verify(this.mimeMessage).addRecipient(TO, this.addresses[0]);
        Mockito.verify(this.mailSender).sendMessage(this.mimeMessage, this.addresses);
    }
}

