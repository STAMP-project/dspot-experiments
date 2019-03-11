package com.ctrip.framework.apollo.configservice.util;


import InstanceConfigAuditUtil.InstanceConfigAuditModel;
import com.ctrip.framework.apollo.biz.entity.Instance;
import com.ctrip.framework.apollo.biz.entity.InstanceConfig;
import com.ctrip.framework.apollo.biz.service.InstanceService;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceConfigAuditUtilTest {
    private InstanceConfigAuditUtil instanceConfigAuditUtil;

    @Mock
    private InstanceService instanceService;

    private BlockingQueue<InstanceConfigAuditUtil.InstanceConfigAuditModel> audits;

    private String someAppId;

    private String someConfigClusterName;

    private String someClusterName;

    private String someDataCenter;

    private String someIp;

    private String someConfigAppId;

    private String someConfigNamespace;

    private String someReleaseKey;

    private InstanceConfigAuditModel someAuditModel;

    @Test
    public void testAudit() throws Exception {
        boolean result = instanceConfigAuditUtil.audit(someAppId, someClusterName, someDataCenter, someIp, someConfigAppId, someConfigClusterName, someConfigNamespace, someReleaseKey);
        InstanceConfigAuditUtil.InstanceConfigAuditModel audit = audits.poll();
        Assert.assertTrue(result);
        Assert.assertTrue(Objects.equals(someAuditModel, audit));
    }

    @Test
    public void testDoAudit() throws Exception {
        long someInstanceId = 1;
        Instance someInstance = Mockito.mock(Instance.class);
        Mockito.when(someInstance.getId()).thenReturn(someInstanceId);
        Mockito.when(instanceService.createInstance(ArgumentMatchers.any(Instance.class))).thenReturn(someInstance);
        instanceConfigAuditUtil.doAudit(someAuditModel);
        Mockito.verify(instanceService, Mockito.times(1)).findInstance(someAppId, someClusterName, someDataCenter, someIp);
        Mockito.verify(instanceService, Mockito.times(1)).createInstance(ArgumentMatchers.any(Instance.class));
        Mockito.verify(instanceService, Mockito.times(1)).findInstanceConfig(someInstanceId, someConfigAppId, someConfigNamespace);
        Mockito.verify(instanceService, Mockito.times(1)).createInstanceConfig(ArgumentMatchers.any(InstanceConfig.class));
    }
}

