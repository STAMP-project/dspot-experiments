/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.scheduler.internal.verify;


import SchedulerHelperPropertiesVerifyProcess.AUDIT_SCHEDULER_JOB_ENABLED;
import SchedulerHelperPropertiesVerifyProcess.LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB;
import StringPool.QUESTION;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration;
import java.util.Collections;
import java.util.Dictionary;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;


/**
 *
 *
 * @author Michael C. Han
 */
@RunWith(MockitoJUnitRunner.class)
public class SchedulerHelperPropertiesVerifyProcessTest {
    @Test
    public void testNoVerify() throws Exception {
        SchedulerHelperPropertiesVerifyProcess schedulerHelperPropertiesVerifyProcess = new SchedulerHelperPropertiesVerifyProcess();
        schedulerHelperPropertiesVerifyProcess.props = PropsTestUtil.setProps(Collections.emptyMap());
        ConfigurationAdmin configurationAdmin = Mockito.mock(ConfigurationAdmin.class);
        schedulerHelperPropertiesVerifyProcess.configurationAdmin = configurationAdmin;
        Mockito.when(configurationAdmin.getConfiguration(SchedulerEngineHelperConfiguration.class.getName())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Assert.fail("No properties should have been verified");
                return null;
            }
        });
        schedulerHelperPropertiesVerifyProcess.doVerify();
    }

    @Test
    public void testVerify() throws Exception {
        SchedulerHelperPropertiesVerifyProcess schedulerHelperPropertiesVerifyProcess = new SchedulerHelperPropertiesVerifyProcess();
        schedulerHelperPropertiesVerifyProcess.props = PropsTestUtil.setProps(LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB, "true");
        ConfigurationAdmin configurationAdmin = Mockito.mock(ConfigurationAdmin.class);
        schedulerHelperPropertiesVerifyProcess.configurationAdmin = configurationAdmin;
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configurationAdmin.getConfiguration(SchedulerEngineHelperConfiguration.class.getName(), QUESTION)).thenReturn(configuration);
        schedulerHelperPropertiesVerifyProcess.doVerify();
        Mockito.verify(configuration).update(_argumentCaptor.capture());
        Dictionary<String, Object> dictionary = _argumentCaptor.getValue();
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(Boolean.TRUE, dictionary.get(AUDIT_SCHEDULER_JOB_ENABLED));
    }

    @Captor
    private ArgumentCaptor<Dictionary<String, Object>> _argumentCaptor;
}

