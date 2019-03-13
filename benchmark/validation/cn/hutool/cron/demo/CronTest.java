package cn.hutool.cron.demo;


import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import org.junit.Test;


/**
 * ??????
 */
public class CronTest {
    // @Ignore
    @Test
    public void addAndRemoveTest() {
        String id = CronUtil.schedule("*/2 * * * * *", new Runnable() {
            @Override
            public void run() {
                Console.log("task running : 2s");
            }
        });
        Console.log(id);
        CronUtil.remove(id);
        // ?????????
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}

