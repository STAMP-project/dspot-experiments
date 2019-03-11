package org.linlinjava.litemall.core;


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
 * 1. ?????????????notify.properties??????
 * 2. ?????????????
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MailTest {
    @Autowired
    private NotifyService notifyService;

    @Test
    public void testMail() {
        notifyService.notifyMail("????", "??1111111???????");
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

