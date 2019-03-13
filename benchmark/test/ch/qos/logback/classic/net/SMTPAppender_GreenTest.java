/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.net;


import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.XHTMLEntityResolver;
import ch.qos.logback.core.testUtil.RandomUtil;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import java.io.ByteArrayInputStream;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;


public class SMTPAppender_GreenTest {
    static final String HEADER = "HEADER\n";

    static final String FOOTER = "FOOTER\n";

    static final String DEFAULT_PATTERN = "%-4relative %mdc [%thread] %-5level %class - %msg%n";

    static final boolean SYNCHRONOUS = false;

    static final boolean ASYNCHRONOUS = true;

    int port = RandomUtil.getRandomServerPort();

    // GreenMail cannot be static. As a shared server induces race conditions
    GreenMail greenMailServer;

    SMTPAppender smtpAppender;

    LoggerContext loggerContext = new LoggerContext();

    Logger logger = loggerContext.getLogger(this.getClass());

    @Test
    public void synchronousSmoke() throws Exception {
        String subject = "synchronousSmoke";
        buildSMTPAppender(subject, SMTPAppender_GreenTest.SYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        MimeMultipart mp = verifyAndExtractMimeMultipart(subject);
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertTrue(body.startsWith(SMTPAppender_GreenTest.HEADER.trim()));
        Assert.assertTrue(body.endsWith(SMTPAppender_GreenTest.FOOTER.trim()));
    }

    @Test
    public void asynchronousSmoke() throws Exception {
        String subject = "asynchronousSmoke";
        buildSMTPAppender(subject, SMTPAppender_GreenTest.ASYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        MimeMultipart mp = verifyAndExtractMimeMultipart(subject);
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertTrue(body.startsWith(SMTPAppender_GreenTest.HEADER.trim()));
        Assert.assertTrue(body.endsWith(SMTPAppender_GreenTest.FOOTER.trim()));
    }

    // See also http://jira.qos.ch/browse/LOGBACK-734
    @Test
    public void callerDataShouldBeCorrectlySetWithAsynchronousSending() throws Exception {
        String subject = "LOGBACK-734";
        buildSMTPAppender("LOGBACK-734", SMTPAppender_GreenTest.ASYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.setIncludeCallerData(true);
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("LOGBACK-734");
        logger.error("callerData", new Exception("ShouldBeCorrectlySetWithAsynchronousSending"));
        waitUntilEmailIsSent();
        MimeMultipart mp = verifyAndExtractMimeMultipart(subject);
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertTrue((("actual [" + body) + "]"), body.contains((("DEBUG " + (this.getClass().getName())) + " - LOGBACK-734")));
    }

    // lost MDC
    @Test
    public void LBCLASSIC_104() throws Exception {
        String subject = "LBCLASSIC_104";
        buildSMTPAppender(subject, SMTPAppender_GreenTest.SYNCHRONOUS);
        smtpAppender.setAsynchronousSending(false);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        MDC.put("key", "val");
        logger.debug("LBCLASSIC_104");
        MDC.clear();
        logger.error("en error", new Exception("test"));
        MimeMultipart mp = verifyAndExtractMimeMultipart(subject);
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertTrue(body.startsWith(SMTPAppender_GreenTest.HEADER.trim()));
        System.out.println(body);
        Assert.assertTrue(body.contains("key=val"));
        Assert.assertTrue(body.endsWith(SMTPAppender_GreenTest.FOOTER.trim()));
    }

    @Test
    public void html() throws Exception {
        String subject = "html";
        buildSMTPAppender(subject, SMTPAppender_GreenTest.SYNCHRONOUS);
        smtpAppender.setAsynchronousSending(false);
        smtpAppender.setLayout(buildHTMLLayout());
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("html");
        logger.error("en error", new Exception("an exception"));
        MimeMultipart mp = verifyAndExtractMimeMultipart(subject);
        // verifyAndExtractMimeMultipart strict adherence to xhtml1-strict.dtd
        SAXReader reader = new SAXReader();
        reader.setValidation(true);
        reader.setEntityResolver(new XHTMLEntityResolver());
        byte[] messageBytes = getAsByteArray(mp.getBodyPart(0).getInputStream());
        ByteArrayInputStream bais = new ByteArrayInputStream(messageBytes);
        try {
            reader.read(bais);
        } catch (DocumentException de) {
            System.out.println("incoming message:");
            System.out.println(new String(messageBytes));
            throw de;
        }
    }

    @Test
    public void testCustomEvaluator() throws Exception {
        configure(((ClassicTestConstants.JORAN_INPUT_PREFIX) + "smtp/customEvaluator.xml"));
        logger.debug("test");
        String msg2 = "CustomEvaluator";
        logger.debug(msg2);
        logger.debug("invisible");
        waitUntilEmailIsSent();
        MimeMultipart mp = verifyAndExtractMimeMultipart(((("testCustomEvaluator " + (this.getClass().getName())) + " - ") + msg2));
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertEquals("testCustomEvaluator", body);
    }

    @Test
    public void testCustomBufferSize() throws Exception {
        configure(((ClassicTestConstants.JORAN_INPUT_PREFIX) + "smtp/customBufferSize.xml"));
        logger.debug("invisible1");
        logger.debug("invisible2");
        String msg = "hello";
        logger.error(msg);
        waitUntilEmailIsSent();
        MimeMultipart mp = verifyAndExtractMimeMultipart(((("testCustomBufferSize " + (this.getClass().getName())) + " - ") + msg));
        String body = GreenMailUtil.getBody(mp.getBodyPart(0));
        Assert.assertEquals(msg, body);
    }

    // this test fails intermittently on Jenkins.
    @Test
    public void testMultipleTo() throws Exception {
        buildSMTPAppender("testMultipleTo", SMTPAppender_GreenTest.SYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        // buildSMTPAppender() already added one destination address
        smtpAppender.addTo("Test <test@example.com>, other-test@example.com");
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("testMultipleTo hello");
        logger.error("testMultipleTo en error", new Exception("an exception"));
        Thread.yield();
        int expectedEmailCount = 3;
        waitForServerToReceiveEmails(expectedEmailCount);
        MimeMessage[] mma = greenMailServer.getReceivedMessages();
        Assert.assertNotNull(mma);
        Assert.assertEquals(expectedEmailCount, mma.length);
    }

    // http://jira.qos.ch/browse/LBCLASSIC-221
    @Test
    public void bufferShouldBeResetBetweenMessages() throws Exception {
        buildSMTPAppender("bufferShouldBeResetBetweenMessages", SMTPAppender_GreenTest.SYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        String msg0 = "hello zero";
        logger.debug(msg0);
        logger.error("error zero");
        String msg1 = "hello one";
        logger.debug(msg1);
        logger.error("error one");
        Thread.yield();
        int oldCount = 0;
        int expectedEmailCount = oldCount + 2;
        waitForServerToReceiveEmails(expectedEmailCount);
        MimeMessage[] mma = greenMailServer.getReceivedMessages();
        Assert.assertNotNull(mma);
        Assert.assertEquals(expectedEmailCount, mma.length);
        MimeMessage mm0 = mma[oldCount];
        MimeMultipart content0 = ((MimeMultipart) (mm0.getContent()));
        @SuppressWarnings("unused")
        String body0 = GreenMailUtil.getBody(content0.getBodyPart(0));
        MimeMessage mm1 = mma[(oldCount + 1)];
        MimeMultipart content1 = ((MimeMultipart) (mm1.getContent()));
        String body1 = GreenMailUtil.getBody(content1.getBodyPart(0));
        // second body should not contain content from first message
        Assert.assertFalse(body1.contains(msg0));
    }

    @Test
    public void multiLineSubjectTruncatedAtFirstNewLine() throws Exception {
        String line1 = "line 1 of subject";
        String subject = line1 + "\nline 2 of subject\n";
        buildSMTPAppender(subject, SMTPAppender_GreenTest.ASYNCHRONOUS);
        smtpAppender.setLayout(buildPatternLayout(SMTPAppender_GreenTest.DEFAULT_PATTERN));
        smtpAppender.start();
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        Thread.yield();
        waitUntilEmailIsSent();
        waitForServerToReceiveEmails(1);
        MimeMessage[] mma = greenMailServer.getReceivedMessages();
        Assert.assertEquals(1, mma.length);
        Assert.assertEquals(line1, mma[0].getSubject());
    }
}

