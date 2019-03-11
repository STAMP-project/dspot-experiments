/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.internal.listener;


import io.elasticjob.lite.api.listener.ElasticJobListener;
import io.elasticjob.lite.internal.config.RescheduleListenerManager;
import io.elasticjob.lite.internal.election.ElectionListenerManager;
import io.elasticjob.lite.internal.failover.FailoverListenerManager;
import io.elasticjob.lite.internal.guarantee.GuaranteeListenerManager;
import io.elasticjob.lite.internal.instance.ShutdownListenerManager;
import io.elasticjob.lite.internal.instance.TriggerListenerManager;
import io.elasticjob.lite.internal.sharding.MonitorExecutionListenerManager;
import io.elasticjob.lite.internal.sharding.ShardingListenerManager;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ListenerManagerTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ElectionListenerManager electionListenerManager;

    @Mock
    private ShardingListenerManager shardingListenerManager;

    @Mock
    private FailoverListenerManager failoverListenerManager;

    @Mock
    private MonitorExecutionListenerManager monitorExecutionListenerManager;

    @Mock
    private ShutdownListenerManager shutdownListenerManager;

    @Mock
    private TriggerListenerManager triggerListenerManager;

    @Mock
    private RescheduleListenerManager rescheduleListenerManager;

    @Mock
    private GuaranteeListenerManager guaranteeListenerManager;

    @Mock
    private RegistryCenterConnectionStateListener regCenterConnectionStateListener;

    private final ListenerManager listenerManager = new ListenerManager(null, "test_job", Collections.<ElasticJobListener>emptyList());

    @Test
    public void assertStartAllListeners() {
        listenerManager.startAllListeners();
        Mockito.verify(electionListenerManager).start();
        Mockito.verify(shardingListenerManager).start();
        Mockito.verify(failoverListenerManager).start();
        Mockito.verify(monitorExecutionListenerManager).start();
        Mockito.verify(shutdownListenerManager).start();
        Mockito.verify(rescheduleListenerManager).start();
        Mockito.verify(guaranteeListenerManager).start();
        Mockito.verify(jobNodeStorage).addConnectionStateListener(regCenterConnectionStateListener);
    }
}

