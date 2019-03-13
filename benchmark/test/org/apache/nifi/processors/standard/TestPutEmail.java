/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import PutEmail.ATTACH_FILE;
import PutEmail.ATTRIBUTE_NAME_REGEX;
import PutEmail.CONTENT_AS_MESSAGE;
import PutEmail.CONTENT_TYPE;
import PutEmail.FROM;
import PutEmail.HEADER_XMAILER;
import PutEmail.MESSAGE;
import PutEmail.REL_FAILURE;
import PutEmail.REL_SUCCESS;
import PutEmail.SMTP_HOSTNAME;
import PutEmail.TO;
import RecipientType.BCC;
import RecipientType.CC;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.util.LogMessage;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestPutEmail {
    /**
     * Extension to PutEmail that stubs out the calls to
     * Transport.sendMessage().
     *
     * <p>
     * All sent messages are records in a list available via the
     * {@link #getMessages()} method.</p>
     * <p> Calling
     * {@link #setException(MessagingException)} will cause the supplied exception to be
     * thrown when sendMessage is invoked.
     * </p>
     */
    private static final class PutEmailExtension extends PutEmail {
        private MessagingException e;

        private ArrayList<Message> messages = new ArrayList<>();

        @Override
        protected void send(Message msg) throws MessagingException {
            messages.add(msg);
            if ((this.e) != null) {
                throw e;
            }
        }

        void setException(final MessagingException e) {
            this.e = e;
        }

        List<Message> getMessages() {
            return messages;
        }
    }

    TestPutEmail.PutEmailExtension processor;

    TestRunner runner;

    @Test
    public void testExceptionWhenSending() {
        // verifies that files are routed to failure when Transport.send() throws a MessagingException
        runner.setProperty(SMTP_HOSTNAME, "host-doesnt-exist123");
        runner.setProperty(FROM, "test@apache.org");
        runner.setProperty(TO, "test@apache.org");
        runner.setProperty(MESSAGE, "Message Body");
        processor.setException(new MessagingException("Forced failure from send()"));
        final Map<String, String> attributes = new HashMap<>();
        runner.enqueue("Some Text".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertEquals("Expected an attempt to send a single message", 1, processor.getMessages().size());
    }

    @Test
    public void testOutgoingMessage() throws Exception {
        // verifies that are set on the outgoing Message correctly
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingNiFi");
        runner.setProperty(FROM, "test@apache.org");
        runner.setProperty(MESSAGE, "Message Body");
        runner.setProperty(TO, "recipient@apache.org");
        runner.enqueue("Some Text".getBytes());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // Verify that the Message was populated correctly
        Assert.assertEquals("Expected a single message to be sent", 1, processor.getMessages().size());
        Message message = processor.getMessages().get(0);
        Assert.assertEquals("test@apache.org", message.getFrom()[0].toString());
        Assert.assertEquals("X-Mailer Header", "TestingNiFi", message.getHeader("X-Mailer")[0]);
        Assert.assertEquals("Message Body", message.getContent());
        Assert.assertEquals("recipient@apache.org", message.getRecipients(RecipientType.TO)[0].toString());
        Assert.assertNull(message.getRecipients(BCC));
        Assert.assertNull(message.getRecipients(CC));
    }

    @Test
    public void testOutgoingMessageWithOptionalProperties() throws Exception {
        // verifies that optional attributes are set on the outgoing Message correctly
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingN?FiNonASCII");
        runner.setProperty(FROM, "${from}");
        runner.setProperty(MESSAGE, "${message}");
        runner.setProperty(TO, "${to}");
        runner.setProperty(PutEmail.BCC, "${bcc}");
        runner.setProperty(PutEmail.CC, "${cc}");
        runner.setProperty(ATTRIBUTE_NAME_REGEX, "Precedence.*");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("from", "test@apache.org <NiFi>");
        attributes.put("message", "the message body");
        attributes.put("to", "to@apache.org");
        attributes.put("bcc", "bcc@apache.org");
        attributes.put("cc", "cc@apache.org");
        attributes.put("Precedence", "bulk");
        attributes.put("PrecedenceEncodeDecodeTest", "b?lk");
        runner.enqueue("Some Text".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // Verify that the Message was populated correctly
        Assert.assertEquals("Expected a single message to be sent", 1, processor.getMessages().size());
        Message message = processor.getMessages().get(0);
        Assert.assertEquals("\"test@apache.org\" <NiFi>", message.getFrom()[0].toString());
        Assert.assertEquals("X-Mailer Header", "TestingN?FiNonASCII", MimeUtility.decodeText(message.getHeader("X-Mailer")[0]));
        Assert.assertEquals("the message body", message.getContent());
        Assert.assertEquals(1, message.getRecipients(RecipientType.TO).length);
        Assert.assertEquals("to@apache.org", message.getRecipients(RecipientType.TO)[0].toString());
        Assert.assertEquals(1, message.getRecipients(BCC).length);
        Assert.assertEquals("bcc@apache.org", message.getRecipients(BCC)[0].toString());
        Assert.assertEquals(1, message.getRecipients(CC).length);
        Assert.assertEquals("cc@apache.org", message.getRecipients(CC)[0].toString());
        Assert.assertEquals("bulk", MimeUtility.decodeText(message.getHeader("Precedence")[0]));
        Assert.assertEquals("b?lk", MimeUtility.decodeText(message.getHeader("PrecedenceEncodeDecodeTest")[0]));
    }

    @Test
    public void testInvalidAddress() throws Exception {
        // verifies that unparsable addresses lead to the flow file being routed to failure
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingNiFi");
        runner.setProperty(FROM, "test@apache.org <invalid");
        runner.setProperty(MESSAGE, "Message Body");
        runner.setProperty(TO, "recipient@apache.org");
        runner.enqueue("Some Text".getBytes());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertEquals("Expected no messages to be sent", 0, processor.getMessages().size());
    }

    @Test
    public void testEmptyFrom() throws Exception {
        // verifies that if the FROM property evaluates to an empty string at
        // runtime the flow file is transferred to failure.
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingNiFi");
        runner.setProperty(FROM, "${MISSING_PROPERTY}");
        runner.setProperty(MESSAGE, "Message Body");
        runner.setProperty(TO, "recipient@apache.org");
        runner.enqueue("Some Text".getBytes());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertEquals("Expected no messages to be sent", 0, processor.getMessages().size());
        final LogMessage logMessage = runner.getLogger().getErrorMessages().get(0);
        Assert.assertTrue(((String) (logMessage.getArgs()[2])).contains("Required property 'From' evaluates to an empty string"));
    }

    @Test
    public void testOutgoingMessageAttachment() throws Exception {
        // verifies that are set on the outgoing Message correctly
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingNiFi");
        runner.setProperty(FROM, "test@apache.org");
        runner.setProperty(MESSAGE, "Message Body");
        runner.setProperty(ATTACH_FILE, "true");
        runner.setProperty(CONTENT_TYPE, "text/html");
        runner.setProperty(TO, "recipient@apache.org");
        runner.enqueue("Some text".getBytes());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // Verify that the Message was populated correctly
        Assert.assertEquals("Expected a single message to be sent", 1, processor.getMessages().size());
        Message message = processor.getMessages().get(0);
        Assert.assertEquals("test@apache.org", message.getFrom()[0].toString());
        Assert.assertEquals("X-Mailer Header", "TestingNiFi", message.getHeader("X-Mailer")[0]);
        Assert.assertEquals("recipient@apache.org", message.getRecipients(RecipientType.TO)[0].toString());
        Assert.assertTrue(((message.getContent()) instanceof MimeMultipart));
        final MimeMultipart multipart = ((MimeMultipart) (message.getContent()));
        final BodyPart part = multipart.getBodyPart(0);
        final InputStream is = part.getDataHandler().getInputStream();
        final String decodedText = StringUtils.newStringUtf8(Base64.decodeBase64(IOUtils.toString(is, "UTF-8")));
        Assert.assertEquals("Message Body", decodedText);
        final BodyPart attachPart = multipart.getBodyPart(1);
        final InputStream attachIs = attachPart.getDataHandler().getInputStream();
        final String text = IOUtils.toString(attachIs, "UTF-8");
        Assert.assertEquals("Some text", text);
        Assert.assertNull(message.getRecipients(BCC));
        Assert.assertNull(message.getRecipients(CC));
    }

    @Test
    public void testOutgoingMessageWithFlowfileContent() throws Exception {
        // verifies that are set on the outgoing Message correctly
        runner.setProperty(SMTP_HOSTNAME, "smtp-host");
        runner.setProperty(HEADER_XMAILER, "TestingNiFi");
        runner.setProperty(FROM, "test@apache.org,from@apache.org");
        runner.setProperty(MESSAGE, "${body}");
        runner.setProperty(TO, "recipient@apache.org,another@apache.org");
        runner.setProperty(PutEmail.CC, "recipientcc@apache.org,anothercc@apache.org");
        runner.setProperty(PutEmail.BCC, "recipientbcc@apache.org,anotherbcc@apache.org");
        runner.setProperty(CONTENT_AS_MESSAGE, "${sendContent}");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("sendContent", "true");
        attributes.put("body", "Message Body");
        runner.enqueue("Some Text".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // Verify that the Message was populated correctly
        Assert.assertEquals("Expected a single message to be sent", 1, processor.getMessages().size());
        Message message = processor.getMessages().get(0);
        Assert.assertEquals("test@apache.org", message.getFrom()[0].toString());
        Assert.assertEquals("from@apache.org", message.getFrom()[1].toString());
        Assert.assertEquals("X-Mailer Header", "TestingNiFi", message.getHeader("X-Mailer")[0]);
        Assert.assertEquals("Some Text", message.getContent());
        Assert.assertEquals("recipient@apache.org", message.getRecipients(RecipientType.TO)[0].toString());
        Assert.assertEquals("another@apache.org", message.getRecipients(RecipientType.TO)[1].toString());
        Assert.assertEquals("recipientcc@apache.org", message.getRecipients(CC)[0].toString());
        Assert.assertEquals("anothercc@apache.org", message.getRecipients(CC)[1].toString());
        Assert.assertEquals("recipientbcc@apache.org", message.getRecipients(BCC)[0].toString());
        Assert.assertEquals("anotherbcc@apache.org", message.getRecipients(BCC)[1].toString());
    }
}

