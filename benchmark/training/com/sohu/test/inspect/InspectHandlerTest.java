package com.sohu.test.inspect;


import com.sohu.cache.inspect.InspectHandler;
import com.sohu.test.BaseTest;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * Created by yijunzhang on 15-1-20.
 */
public class InspectHandlerTest extends BaseTest {
    @Resource
    private InspectHandler hostInspectHandler;

    @Resource
    private InspectHandler appInspectHandler;

    @Test
    public void handle() {
        try {
            hostInspectHandler.handle();
            // appInspectHandler.handle();
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

