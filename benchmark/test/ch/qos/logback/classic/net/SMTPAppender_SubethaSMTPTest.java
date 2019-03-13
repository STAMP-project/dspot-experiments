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


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.XHTMLEntityResolver;
import ch.qos.logback.core.CoreConstants;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;


// public class TrivialAuthHandlerFactory implements AuthenticationHandlerFactory {
// public AuthenticationHandler create() {
// PluginAuthenticationHandler ret = new PluginAuthenticationHandler();
// UsernamePasswordValidator validator = new UsernamePasswordValidator() {
// public void login(String username, String password) throws LoginFailedException {
// if (!username.equals(password)) {
// throw new LoginFailedException("username=" + username + ", password=" + password);
// }
// }
// };
// ret.addPlugin(new PlainAuthenticationHandler(validator));
// ret.addPlugin(new LoginAuthenticationHandler(validator));
// return ret;
// }
// }
public class SMTPAppender_SubethaSMTPTest {
    static final String TEST_SUBJECT = "test subject";

    static final String HEADER = "HEADER\n";

    static final String FOOTER = "FOOTER\n";

    static int DIFF = 1024 + (new Random().nextInt(30000));

    static Wiser WISER;

    SMTPAppender smtpAppender;

    LoggerContext loggerContext = new LoggerContext();

    int numberOfOldMessages;

    @Test
    public void smoke() throws Exception {
        smtpAppender.setLayout(buildPatternLayout(loggerContext));
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        System.out.println(("*** " + (((ThreadPoolExecutor) (loggerContext.getExecutorService())).getCompletedTaskCount())));
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(((numberOfOldMessages) + 1), wiserMsgList.size());
        WiserMessage wm = wiserMsgList.get(numberOfOldMessages);
        // http://jira.qos.ch/browse/LBCLASSIC-67
        MimeMessage mm = wm.getMimeMessage();
        Assert.assertEquals(SMTPAppender_SubethaSMTPTest.TEST_SUBJECT, mm.getSubject());
        MimeMultipart mp = ((MimeMultipart) (mm.getContent()));
        String body = SMTPAppender_SubethaSMTPTest.getBody(mp.getBodyPart(0));
        System.out.println(("[" + body));
        Assert.assertTrue(body.startsWith(SMTPAppender_SubethaSMTPTest.HEADER.trim()));
        Assert.assertTrue(body.endsWith(SMTPAppender_SubethaSMTPTest.FOOTER.trim()));
    }

    @Test
    public void html() throws Exception {
        smtpAppender.setLayout(buildHTMLLayout(loggerContext));
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(((numberOfOldMessages) + 1), wiserMsgList.size());
        WiserMessage wm = wiserMsgList.get(numberOfOldMessages);
        MimeMessage mm = wm.getMimeMessage();
        Assert.assertEquals(SMTPAppender_SubethaSMTPTest.TEST_SUBJECT, mm.getSubject());
        MimeMultipart mp = ((MimeMultipart) (mm.getContent()));
        // verify strict adherence to xhtml1-strict.dtd
        SAXReader reader = new SAXReader();
        reader.setValidation(true);
        reader.setEntityResolver(new XHTMLEntityResolver());
        reader.read(mp.getBodyPart(0).getInputStream());
        // System.out.println(GreenMailUtil.getBody(mp.getBodyPart(0)));
    }

    /**
     * Checks that even when many events are processed, the output is still
     * conforms to xhtml-strict.dtd.
     *
     * Note that SMTPAppender only keeps only 500 or so (=buffer size) events. So
     * the generated output will be rather short.
     */
    @Test
    public void htmlLong() throws Exception {
        smtpAppender.setLayout(buildHTMLLayout(loggerContext));
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        for (int i = 0; i < ((CoreConstants.TABLE_ROW_LIMIT) * 3); i++) {
            logger.debug(("hello " + i));
        }
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(((numberOfOldMessages) + 1), wiserMsgList.size());
        WiserMessage wm = wiserMsgList.get(numberOfOldMessages);
        MimeMessage mm = wm.getMimeMessage();
        Assert.assertEquals(SMTPAppender_SubethaSMTPTest.TEST_SUBJECT, mm.getSubject());
        MimeMultipart mp = ((MimeMultipart) (mm.getContent()));
        // verify strict adherence to xhtml1-strict.dtd
        SAXReader reader = new SAXReader();
        reader.setValidation(true);
        reader.setEntityResolver(new XHTMLEntityResolver());
        reader.read(mp.getBodyPart(0).getInputStream());
    }

    static String REQUIRED_USERNAME = "user";

    static String REQUIRED_PASSWORD = "password";

    class RequiredUsernamePasswordValidator implements UsernamePasswordValidator {
        public void login(String username, String password) throws LoginFailedException {
            if ((!(username.equals(SMTPAppender_SubethaSMTPTest.REQUIRED_USERNAME))) || (!(password.equals(SMTPAppender_SubethaSMTPTest.REQUIRED_PASSWORD)))) {
                throw new LoginFailedException();
            }
        }
    }

    @Test
    public void authenticated() throws Exception {
        setAuthenticanHandlerFactory();
        // MessageListenerAdapter mla = (MessageListenerAdapter) WISER.getServer().getMessageHandlerFactory();
        // mla.setAuthenticationHandlerFactory(new TrivialAuthHandlerFactory());
        smtpAppender.setUsername(SMTPAppender_SubethaSMTPTest.REQUIRED_USERNAME);
        smtpAppender.setPassword(SMTPAppender_SubethaSMTPTest.REQUIRED_PASSWORD);
        smtpAppender.setLayout(buildPatternLayout(loggerContext));
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(((numberOfOldMessages) + 1), wiserMsgList.size());
        WiserMessage wm = wiserMsgList.get(numberOfOldMessages);
        // http://jira.qos.ch/browse/LBCLASSIC-67
        MimeMessage mm = wm.getMimeMessage();
        Assert.assertEquals(SMTPAppender_SubethaSMTPTest.TEST_SUBJECT, mm.getSubject());
        MimeMultipart mp = ((MimeMultipart) (mm.getContent()));
        String body = SMTPAppender_SubethaSMTPTest.getBody(mp.getBodyPart(0));
        Assert.assertTrue(body.startsWith(SMTPAppender_SubethaSMTPTest.HEADER.trim()));
        Assert.assertTrue(body.endsWith(SMTPAppender_SubethaSMTPTest.FOOTER.trim()));
    }

    // Unfortunately, there seems to be a problem with SubethaSMTP's implementation
    // of startTLS. The same SMTPAppender code works fine when tested with gmail.
    @Test
    public void authenticatedSSL() throws Exception {
        setAuthenticanHandlerFactory();
        smtpAppender.setSTARTTLS(true);
        smtpAppender.setUsername(SMTPAppender_SubethaSMTPTest.REQUIRED_USERNAME);
        smtpAppender.setPassword(SMTPAppender_SubethaSMTPTest.REQUIRED_PASSWORD);
        smtpAppender.setLayout(buildPatternLayout(loggerContext));
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(1, wiserMsgList.size());
    }

    static String GMAIL_USER_NAME = "xx@gmail.com";

    static String GMAIL_PASSWORD = "xxx";

    @Test
    public void testMultipleTo() throws Exception {
        smtpAppender.setLayout(buildPatternLayout(loggerContext));
        smtpAppender.addTo("Test <test@example.com>, other-test@example.com");
        smtpAppender.start();
        Logger logger = loggerContext.getLogger("test");
        logger.addAppender(smtpAppender);
        logger.debug("hello");
        logger.error("en error", new Exception("an exception"));
        waitUntilEmailIsSent();
        List<WiserMessage> wiserMsgList = SMTPAppender_SubethaSMTPTest.WISER.getMessages();
        Assert.assertNotNull(wiserMsgList);
        Assert.assertEquals(((numberOfOldMessages) + 3), wiserMsgList.size());
    }
}

