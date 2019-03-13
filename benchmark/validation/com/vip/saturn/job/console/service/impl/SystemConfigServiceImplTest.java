package com.vip.saturn.job.console.service.impl;


import com.vip.saturn.job.console.mybatis.entity.SystemConfig;
import com.vip.saturn.job.console.mybatis.service.SystemConfig4SqlService;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SystemConfigServiceImplTest {
    @Mock
    private SystemConfig4SqlService systemConfig4SqlService;

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;

    @Test
    public void testGetValueByPrefix() {
        List<SystemConfig> configs = Lists.newArrayList();
        configs.add(initSystemConfig("test_1", "value_1"));
        configs.add(initSystemConfig("test_2", "value_2"));
        Mockito.when(systemConfig4SqlService.selectByLastly()).thenReturn(configs);
        systemConfigService.reload();
        List<String> result = systemConfigService.getValuesByPrefix("test");
        Assert.assertEquals(2, result.size());
        int count = 0;
        for (String value : result) {
            if ((value.equals("value_1")) || (value.equals("value_2"))) {
                count++;
            } else {
                Assert.fail("should not come here");
            }
        }
        Assert.assertEquals(2, count);
    }
}

