package com.github.ltsopensource.alarm.email;


import org.junit.Test;


/**
 *
 *
 * @author Robert HG (254963746@qq.com) on 2/17/16.
 */
public class SMTPMailManagerImplTest {
    @Test
    public void testSend() throws Exception {
        String host = "smtp.qq.com";
        // ???????? http://service.mail.qq.com/cgi-bin/help?subtype=1&&id=28&&no=1001256
        // lts12345
        MailManager mailManager = new SMTPMailManagerImpl(host, "2179816070@qq.com", "??????", "LTS????(notice@lts.com)", true);
        mailManager.send("254963746@qq.com", "??", "fdsafhakdsjfladslfj???");
    }
}

