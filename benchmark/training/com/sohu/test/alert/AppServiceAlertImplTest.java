package com.sohu.test.alert;


import com.sohu.cache.alert.AppAlertService;
import com.sohu.cache.entity.InstanceInfo;
import com.sohu.cache.web.service.AppService;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 * app????
 *
 * @author leifu
 * @unknown 2014?12?16?
 * @unknown ??2:58:47
 */
public class AppServiceAlertImplTest extends BaseTest {
    @Resource(name = "appAlertService")
    private AppAlertService appAlertService;

    @Resource
    private AppService appService;

    @Test
    public void testNotNull() {
        Assert.assertNotNull(appAlertService);
    }

    @Test
    public void getAppInstanceInfo() {
        watch.start("getAppInstanceInfo1");
        List<InstanceInfo> list = appService.getAppInstanceInfo(10129L);
        watch.stop();
        watch.start("getAppInstanceInfo2");
        list = appService.getAppInstanceInfo(10129L);
        watch.stop();
        logger.info(watch.prettyPrint());
        for (InstanceInfo info : list) {
            logger.warn("{}:{} -> {}:{} id={}", info.getIp(), info.getPort(), info.getMasterHost(), info.getMasterPort(), info.getMasterInstanceId());
        }
    }
}

