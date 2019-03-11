/**
 * Copyright 2017 LinkedIn Corp.
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


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractMailerTest {
    List<String> senderList = new ArrayList<>();

    private EmailMessage message;

    private EmailMessageCreator messageCreator;

    private Props props;

    @Test
    public void testCreateEmailMessage() {
        final AbstractMailer mailer = new AbstractMailer(this.props, this.messageCreator);
        final EmailMessage em = mailer.createEmailMessage("subject", "text/html", this.senderList);
        Mockito.verify(this.messageCreator).createMessage();
        assertThat(this.message).isEqualTo(em);
        Mockito.verify(this.message).setSubject("subject");
    }
}

