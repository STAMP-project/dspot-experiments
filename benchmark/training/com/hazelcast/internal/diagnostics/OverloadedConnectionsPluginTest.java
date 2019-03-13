/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.diagnostics;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.operation.GetOperation;
import com.hazelcast.spi.impl.operationservice.impl.DummyOperation;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This test can't run with mocked Hazelcast instances, since we rely on a real TcpIpConnectionManager.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class OverloadedConnectionsPluginTest extends AbstractDiagnosticsPluginTest {
    private HazelcastInstance local;

    private InternalSerializationService serializationService;

    private OverloadedConnectionsPlugin plugin;

    private String remoteKey;

    private volatile boolean stop;

    @Test
    public void test() {
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                IMap<String, String> map = local.getMap("foo");
                while (!(stop)) {
                    map.getAsync(remoteKey);
                } 
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                plugin.run(logWriter);
                assertContains(((GetOperation.class.getSimpleName()) + " sampleCount="));
            }
        });
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void toKey() {
        assertToKey(DummyOperation.class.getName(), new DummyOperation());
        assertToKey(Integer.class.getName(), Integer.valueOf(10));
        assertToKey((("Backup(" + (DummyOperation.class.getName())) + ")"), new com.hazelcast.spi.impl.operationservice.impl.operations.Backup(new DummyOperation(), HazelcastTestSupport.getAddress(local), new long[0], true));
    }
}

