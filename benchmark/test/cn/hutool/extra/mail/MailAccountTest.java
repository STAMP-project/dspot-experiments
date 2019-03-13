package cn.hutool.extra.mail;


import GlobalMailAccount.INSTANCE;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????
 *
 * @author looly
 */
public class MailAccountTest {
    @Test
    public void parseSettingTest() {
        MailAccount account = INSTANCE.getAccount();
        account.getSmtpProps();
        Assert.assertNotNull(account.getCharset());
        Assert.assertTrue(account.isSslEnable());
    }
}

