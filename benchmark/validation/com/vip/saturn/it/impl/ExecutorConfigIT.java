package com.vip.saturn.it.impl;


import SystemConfigProperties.EXECUTOR_CONFIGS;
import com.alibaba.fastjson.JSON;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.SaturnConsoleInstance;
import com.vip.saturn.job.console.service.SystemConfigService;
import com.vip.saturn.job.executor.ExecutorConfig;
import java.util.Map;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author hebelala
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecutorConfigIT extends AbstractSaturnIT {
    private static String CONSOLE_HOST_URL;

    @Test
    public void testA() throws Exception {
        ExecutorConfig executorConfig = getExecutorConfig(0);
        assertThat(executorConfig).isNotNull();
        // ?????????executor???????
        addOrUpdateConfig("whoami", "hebelala");
        SystemConfigService systemConfigService = AbstractSaturnIT.saturnConsoleInstanceList.get(0).applicationContext.getBean(SystemConfigService.class);
        String dbData = systemConfigService.getValueDirectly(EXECUTOR_CONFIGS);
        assertThat(dbData).isNotNull();
        Map<String, Object> map = JSON.parseObject(dbData, Map.class);
        assertThat(map).containsEntry("whoami", "hebelala");
        executorConfig = getExecutorConfig(0);
        assertThat(executorConfig).isNotNull();
        // ???????????executor???????
        addOrUpdateConfig("ruok", "ok");
        systemConfigService = AbstractSaturnIT.saturnConsoleInstanceList.get(0).applicationContext.getBean(SystemConfigService.class);
        dbData = systemConfigService.getValueDirectly(EXECUTOR_CONFIGS);
        assertThat(dbData).isNotNull();
        map = JSON.parseObject(dbData, Map.class);
        assertThat(map).containsEntry("whoami", "hebelala").containsEntry("ruok", "ok");
        executorConfig = getExecutorConfig(0);
        assertThat(executorConfig).isNotNull();
    }
}

