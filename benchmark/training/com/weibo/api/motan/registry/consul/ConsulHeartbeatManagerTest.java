package com.weibo.api.motan.registry.consul;


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @unknown ConsulHeartbeatManagerTest
 * @author zhanglei28
 * @unknown 2016?3?22?
 */
public class ConsulHeartbeatManagerTest {
    private ConsulHeartbeatManager heartbeatManager;

    private MockConsulClient client;

    @Test
    public void testStart() throws InterruptedException {
        heartbeatManager.start();
        Map<String, Long> mockServices = new HashMap<String, Long>();
        int serviceNum = 5;
        for (int i = 0; i < serviceNum; i++) {
            String serviceid = "service" + i;
            mockServices.put(serviceid, 0L);
            heartbeatManager.addHeartbeatServcieId(serviceid);
        }
        // ????
        setHeartbeatSwitcher(true);
        checkHeartbeat(mockServices, true, serviceNum);
        // ????
        setHeartbeatSwitcher(false);
        Thread.sleep(100);
        checkHeartbeat(mockServices, false, serviceNum);
    }
}

