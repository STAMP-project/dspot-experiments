/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.alerting;


import AlertLevel.WARNING;
import java.util.Date;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.admin.remote.AlertListenerMessage;
import org.apache.geode.test.junit.categories.AlertingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AlertMessaging}.
 */
@Category(AlertingTest.class)
public class AlertMessagingTest {
    private InternalDistributedSystem system;

    private InternalDistributedMember localMember;

    private DistributionConfig config;

    private DistributionManager dm;

    private AlertListenerMessageFactory alertListenerMessageFactory;

    private AlertListenerMessage alertListenerMessage;

    private AlertMessaging alertMessaging;

    @Test
    public void sendAlertProcessesMessageIfMemberIsLocal() {
        alertMessaging.sendAlert(localMember, WARNING, new Date(), "threadName", "formattedMessage", "stackTrace");
        Mockito.verify(alertMessaging).processAlertListenerMessage(ArgumentMatchers.eq(alertListenerMessage));
    }

    @Test
    public void sendAlertSendsMessageIfMemberIsRemote() {
        DistributedMember remoteMember = Mockito.mock(DistributedMember.class);
        alertMessaging.sendAlert(remoteMember, WARNING, new Date(), "threadName", "formattedMessage", "stackTrace");
        Mockito.verify(dm).putOutgoing(ArgumentMatchers.eq(alertListenerMessage));
    }

    @Test
    public void processAlertListenerMessage_requires_ClusterDistributionManager() {
        dm = Mockito.mock(DistributionManager.class);
        alertMessaging = new AlertMessaging(system, dm, alertListenerMessageFactory);
        Throwable thrown = catchThrowable(() -> alertMessaging.processAlertListenerMessage(alertListenerMessage));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}

