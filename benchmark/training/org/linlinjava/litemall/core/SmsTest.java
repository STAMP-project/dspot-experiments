package org.linlinjava.litemall.core;


import NotifyType.CAPTCHA;
import NotifyType.PAY_SUCCEED;
import NotifyType.REFUND;
import NotifyType.SHIP;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * ????????
 * <p>
 * ??LitemallNotifyService????????
 * ?????????????????????
 * <p>
 * ????????
 * 1. ???????????????????notify.properties??????
 * 2. ???????????????????
 * 3. ????????????????
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SmsTest {
    @Autowired
    private NotifyService notifyService;

    @Test
    public void testCaptcha() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[]{ "123456" };
        notifyService.notifySmsTemplate(phone, CAPTCHA, params);
    }

    @Test
    public void testPaySucceed() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[]{ "123456" };
        notifyService.notifySmsTemplate(phone, PAY_SUCCEED, params);
    }

    @Test
    public void testShip() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[]{ "123456" };
        notifyService.notifySmsTemplate(phone, SHIP, params);
    }

    @Test
    public void testRefund() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[]{ "123456" };
        notifyService.notifySmsTemplate(phone, REFUND, params);
    }

    @Configuration
    @Import(Application.class)
    static class ContextConfiguration {
        @Bean
        @Primary
        public Executor executor() {
            return new SyncTaskExecutor();
        }
    }
}

