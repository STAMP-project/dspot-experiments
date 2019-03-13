/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.mail.javamail;


import Message.RecipientType.BCC;
import Message.RecipientType.CC;
import Message.RecipientType.TO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 09.10.2004
 */
public class JavaMailSenderTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void javaMailSenderWithSimpleMessage() throws IOException, MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setPort(30);
        setUsername("username");
        setPassword("password");
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("me@mail.org");
        simpleMessage.setReplyTo("reply@mail.org");
        simpleMessage.setTo("you@mail.org");
        simpleMessage.setCc("he@mail.org", "she@mail.org");
        simpleMessage.setBcc("us@mail.org", "them@mail.org");
        Date sentDate = new GregorianCalendar(2004, 1, 1).getTime();
        simpleMessage.setSentDate(sentDate);
        simpleMessage.setSubject("my subject");
        simpleMessage.setText("my text");
        sender.send(simpleMessage);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals(30, sender.transport.getConnectedPort());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        MimeMessage sentMessage = sender.transport.getSentMessage(0);
        List<Address> froms = Arrays.asList(sentMessage.getFrom());
        Assert.assertEquals(1, froms.size());
        Assert.assertEquals("me@mail.org", getAddress());
        List<Address> replyTos = Arrays.asList(sentMessage.getReplyTo());
        Assert.assertEquals("reply@mail.org", getAddress());
        List<Address> tos = Arrays.asList(sentMessage.getRecipients(TO));
        Assert.assertEquals(1, tos.size());
        Assert.assertEquals("you@mail.org", getAddress());
        List<Address> ccs = Arrays.asList(sentMessage.getRecipients(CC));
        Assert.assertEquals(2, ccs.size());
        Assert.assertEquals("he@mail.org", getAddress());
        Assert.assertEquals("she@mail.org", getAddress());
        List<Address> bccs = Arrays.asList(sentMessage.getRecipients(BCC));
        Assert.assertEquals(2, bccs.size());
        Assert.assertEquals("us@mail.org", getAddress());
        Assert.assertEquals("them@mail.org", getAddress());
        Assert.assertEquals(sentDate.getTime(), sentMessage.getSentDate().getTime());
        Assert.assertEquals("my subject", sentMessage.getSubject());
        Assert.assertEquals("my text", sentMessage.getContent());
    }

    @Test
    public void javaMailSenderWithSimpleMessages() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        SimpleMailMessage simpleMessage1 = new SimpleMailMessage();
        simpleMessage1.setTo("he@mail.org");
        SimpleMailMessage simpleMessage2 = new SimpleMailMessage();
        simpleMessage2.setTo("she@mail.org");
        sender.send(simpleMessage1, simpleMessage2);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(2, sender.transport.getSentMessages().size());
        MimeMessage sentMessage1 = sender.transport.getSentMessage(0);
        List<Address> tos1 = Arrays.asList(sentMessage1.getRecipients(TO));
        Assert.assertEquals(1, tos1.size());
        Assert.assertEquals("he@mail.org", getAddress());
        MimeMessage sentMessage2 = sender.transport.getSentMessage(1);
        List<Address> tos2 = Arrays.asList(sentMessage2.getRecipients(TO));
        Assert.assertEquals(1, tos2.size());
        Assert.assertEquals("she@mail.org", getAddress());
    }

    @Test
    public void javaMailSenderWithMimeMessage() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessage mimeMessage = createMimeMessage();
        mimeMessage.setRecipient(TO, new InternetAddress("you@mail.org"));
        sender.send(mimeMessage);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(mimeMessage, sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailSenderWithMimeMessages() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessage mimeMessage1 = createMimeMessage();
        mimeMessage1.setRecipient(TO, new InternetAddress("he@mail.org"));
        MimeMessage mimeMessage2 = createMimeMessage();
        mimeMessage2.setRecipient(TO, new InternetAddress("she@mail.org"));
        sender.send(mimeMessage1, mimeMessage2);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(2, sender.transport.getSentMessages().size());
        Assert.assertEquals(mimeMessage1, sender.transport.getSentMessage(0));
        Assert.assertEquals(mimeMessage2, sender.transport.getSentMessage(1));
    }

    @Test
    public void javaMailSenderWithMimeMessagePreparator() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        final List<Message> messages = new ArrayList<>();
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                mimeMessage.setRecipient(TO, new InternetAddress("you@mail.org"));
                messages.add(mimeMessage);
            }
        };
        sender.send(preparator);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(messages.get(0), sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailSenderWithMimeMessagePreparators() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        final List<Message> messages = new ArrayList<>();
        MimeMessagePreparator preparator1 = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                mimeMessage.setRecipient(TO, new InternetAddress("he@mail.org"));
                messages.add(mimeMessage);
            }
        };
        MimeMessagePreparator preparator2 = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                mimeMessage.setRecipient(TO, new InternetAddress("she@mail.org"));
                messages.add(mimeMessage);
            }
        };
        sender.send(preparator1, preparator2);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(2, sender.transport.getSentMessages().size());
        Assert.assertEquals(messages.get(0), sender.transport.getSentMessage(0));
        Assert.assertEquals(messages.get(1), sender.transport.getSentMessage(1));
    }

    @Test
    public void javaMailSenderWithMimeMessageHelper() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessageHelper message = new MimeMessageHelper(sender.createMimeMessage());
        Assert.assertNull(message.getEncoding());
        Assert.assertTrue(((message.getFileTypeMap()) instanceof ConfigurableMimeFileTypeMap));
        message.setTo("you@mail.org");
        sender.send(message.getMimeMessage());
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(message.getMimeMessage(), sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailSenderWithMimeMessageHelperAndSpecificEncoding() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessageHelper message = new MimeMessageHelper(sender.createMimeMessage(), "UTF-8");
        Assert.assertEquals("UTF-8", message.getEncoding());
        FileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
        message.setFileTypeMap(fileTypeMap);
        Assert.assertEquals(fileTypeMap, message.getFileTypeMap());
        message.setTo("you@mail.org");
        sender.send(message.getMimeMessage());
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(message.getMimeMessage(), sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailSenderWithMimeMessageHelperAndDefaultEncoding() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        setDefaultEncoding("UTF-8");
        FileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
        setDefaultFileTypeMap(fileTypeMap);
        MimeMessageHelper message = new MimeMessageHelper(sender.createMimeMessage());
        Assert.assertEquals("UTF-8", message.getEncoding());
        Assert.assertEquals(fileTypeMap, message.getFileTypeMap());
        message.setTo("you@mail.org");
        sender.send(message.getMimeMessage());
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(message.getMimeMessage(), sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailSenderWithParseExceptionOnSimpleMessage() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("");
        try {
            sender.send(simpleMessage);
        } catch (MailParseException ex) {
            // expected
            Assert.assertTrue(((ex.getCause()) instanceof AddressException));
        }
    }

    @Test
    public void javaMailSenderWithParseExceptionOnMimeMessagePreparator() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                mimeMessage.setFrom(new InternetAddress(""));
            }
        };
        try {
            sender.send(preparator);
        } catch (MailParseException ex) {
            // expected
            Assert.assertTrue(((ex.getCause()) instanceof AddressException));
        }
    }

    @Test
    public void javaMailSenderWithCustomSession() throws MessagingException {
        final Session session = Session.getInstance(new Properties());
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender() {
            @Override
            protected Transport getTransport(Session sess) throws NoSuchProviderException {
                Assert.assertEquals(session, sess);
                return super.getTransport(sess);
            }
        };
        sender.setSession(session);
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessage mimeMessage = createMimeMessage();
        mimeMessage.setSubject("custom");
        mimeMessage.setRecipient(TO, new InternetAddress("you@mail.org"));
        mimeMessage.setSentDate(new GregorianCalendar(2005, 3, 1).getTime());
        sender.send(mimeMessage);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(mimeMessage, sender.transport.getSentMessage(0));
    }

    @Test
    public void javaMailProperties() throws MessagingException {
        Properties props = new Properties();
        props.setProperty("bogusKey", "bogusValue");
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender() {
            @Override
            protected Transport getTransport(Session sess) throws NoSuchProviderException {
                Assert.assertEquals("bogusValue", sess.getProperty("bogusKey"));
                return super.getTransport(sess);
            }
        };
        setJavaMailProperties(props);
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessage mimeMessage = createMimeMessage();
        mimeMessage.setRecipient(TO, new InternetAddress("you@mail.org"));
        sender.send(mimeMessage);
        Assert.assertEquals("host", sender.transport.getConnectedHost());
        Assert.assertEquals("username", sender.transport.getConnectedUsername());
        Assert.assertEquals("password", sender.transport.getConnectedPassword());
        Assert.assertTrue(sender.transport.isCloseCalled());
        Assert.assertEquals(1, sender.transport.getSentMessages().size());
        Assert.assertEquals(mimeMessage, sender.transport.getSentMessage(0));
    }

    @Test
    public void failedMailServerConnect() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        setHost(null);
        setUsername("username");
        setPassword("password");
        SimpleMailMessage simpleMessage1 = new SimpleMailMessage();
        try {
            sender.send(simpleMessage1);
            Assert.fail("Should have thrown MailSendException");
        } catch (MailSendException ex) {
            // expected
            ex.printStackTrace();
            Assert.assertTrue(((ex.getFailedMessages()) != null));
            Assert.assertEquals(1, ex.getFailedMessages().size());
            Assert.assertSame(simpleMessage1, ex.getFailedMessages().keySet().iterator().next());
            Assert.assertSame(ex.getCause(), ex.getFailedMessages().values().iterator().next());
        }
    }

    @Test
    public void failedMailServerClose() {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("");
        setUsername("username");
        setPassword("password");
        SimpleMailMessage simpleMessage1 = new SimpleMailMessage();
        try {
            sender.send(simpleMessage1);
            Assert.fail("Should have thrown MailSendException");
        } catch (MailSendException ex) {
            // expected
            ex.printStackTrace();
            Assert.assertTrue(((ex.getFailedMessages()) != null));
            Assert.assertEquals(0, ex.getFailedMessages().size());
        }
    }

    @Test
    public void failedSimpleMessage() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        SimpleMailMessage simpleMessage1 = new SimpleMailMessage();
        simpleMessage1.setTo("he@mail.org");
        simpleMessage1.setSubject("fail");
        SimpleMailMessage simpleMessage2 = new SimpleMailMessage();
        simpleMessage2.setTo("she@mail.org");
        try {
            sender.send(simpleMessage1, simpleMessage2);
        } catch (MailSendException ex) {
            ex.printStackTrace();
            Assert.assertEquals("host", sender.transport.getConnectedHost());
            Assert.assertEquals("username", sender.transport.getConnectedUsername());
            Assert.assertEquals("password", sender.transport.getConnectedPassword());
            Assert.assertTrue(sender.transport.isCloseCalled());
            Assert.assertEquals(1, sender.transport.getSentMessages().size());
            Assert.assertEquals(new InternetAddress("she@mail.org"), sender.transport.getSentMessage(0).getAllRecipients()[0]);
            Assert.assertEquals(1, ex.getFailedMessages().size());
            Assert.assertEquals(simpleMessage1, ex.getFailedMessages().keySet().iterator().next());
            Object subEx = ex.getFailedMessages().values().iterator().next();
            Assert.assertTrue((subEx instanceof MessagingException));
            Assert.assertEquals("failed", getMessage());
        }
    }

    @Test
    public void failedMimeMessage() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        setUsername("username");
        setPassword("password");
        MimeMessage mimeMessage1 = createMimeMessage();
        mimeMessage1.setRecipient(TO, new InternetAddress("he@mail.org"));
        mimeMessage1.setSubject("fail");
        MimeMessage mimeMessage2 = createMimeMessage();
        mimeMessage2.setRecipient(TO, new InternetAddress("she@mail.org"));
        try {
            sender.send(mimeMessage1, mimeMessage2);
        } catch (MailSendException ex) {
            ex.printStackTrace();
            Assert.assertEquals("host", sender.transport.getConnectedHost());
            Assert.assertEquals("username", sender.transport.getConnectedUsername());
            Assert.assertEquals("password", sender.transport.getConnectedPassword());
            Assert.assertTrue(sender.transport.isCloseCalled());
            Assert.assertEquals(1, sender.transport.getSentMessages().size());
            Assert.assertEquals(mimeMessage2, sender.transport.getSentMessage(0));
            Assert.assertEquals(1, ex.getFailedMessages().size());
            Assert.assertEquals(mimeMessage1, ex.getFailedMessages().keySet().iterator().next());
            Object subEx = ex.getFailedMessages().values().iterator().next();
            Assert.assertTrue((subEx instanceof MessagingException));
            Assert.assertEquals("failed", getMessage());
        }
    }

    @Test
    public void testConnection() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        sender.setHost("host");
        sender.testConnection();
    }

    @Test
    public void testConnectionWithFailure() throws MessagingException {
        JavaMailSenderTests.MockJavaMailSender sender = new JavaMailSenderTests.MockJavaMailSender();
        setHost(null);
        thrown.expect(MessagingException.class);
        sender.testConnection();
    }

    private static class MockJavaMailSender extends JavaMailSenderImpl {
        private JavaMailSenderTests.MockTransport transport;

        @Override
        protected Transport getTransport(Session session) throws NoSuchProviderException {
            this.transport = new JavaMailSenderTests.MockTransport(session, null);
            return transport;
        }
    }

    private static class MockTransport extends Transport {
        private String connectedHost = null;

        private int connectedPort = -2;

        private String connectedUsername = null;

        private String connectedPassword = null;

        private boolean closeCalled = false;

        private List<Message> sentMessages = new ArrayList<>();

        private MockTransport(Session session, URLName urlName) {
            super(session, urlName);
        }

        public String getConnectedHost() {
            return connectedHost;
        }

        public int getConnectedPort() {
            return connectedPort;
        }

        public String getConnectedUsername() {
            return connectedUsername;
        }

        public String getConnectedPassword() {
            return connectedPassword;
        }

        public boolean isCloseCalled() {
            return closeCalled;
        }

        public List<Message> getSentMessages() {
            return sentMessages;
        }

        public MimeMessage getSentMessage(int index) {
            return ((MimeMessage) (this.sentMessages.get(index)));
        }

        @Override
        public void connect(String host, int port, String username, String password) throws MessagingException {
            if (host == null) {
                throw new MessagingException("no host");
            }
            this.connectedHost = host;
            this.connectedPort = port;
            this.connectedUsername = username;
            this.connectedPassword = password;
            setConnected(true);
        }

        @Override
        public synchronized void close() throws MessagingException {
            if ("".equals(connectedHost)) {
                throw new MessagingException("close failure");
            }
            this.closeCalled = true;
        }

        @Override
        public void sendMessage(Message message, Address[] addresses) throws MessagingException {
            if ("fail".equals(message.getSubject())) {
                throw new MessagingException("failed");
            }
            if ((addresses == null) || ((message.getAllRecipients()) == null ? (addresses.length) > 0 : !(ObjectUtils.nullSafeEquals(addresses, message.getAllRecipients())))) {
                throw new MessagingException("addresses not correct");
            }
            if ((message.getSentDate()) == null) {
                throw new MessagingException("No sentDate specified");
            }
            if (((message.getSubject()) != null) && (message.getSubject().contains("custom"))) {
                Assert.assertEquals(new GregorianCalendar(2005, 3, 1).getTime(), message.getSentDate());
            }
            this.sentMessages.add(message);
        }
    }
}

