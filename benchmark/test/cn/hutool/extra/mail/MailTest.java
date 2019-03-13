package cn.hutool.extra.mail;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author looly
 */
public class MailTest {
    @Test
    public void mailAccountTest() {
        MailAccount account = new MailAccount();
        account.setFrom("hutool@yeah.net");
        account.setDebug(true);
        account.defaultIfEmpty();
        Properties props = account.getSmtpProps();
        Assert.assertEquals("true", props.getProperty("mail.debug"));
    }
}

