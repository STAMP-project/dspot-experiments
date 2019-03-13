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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
@SuppressWarnings("WeakerAccess")
public class SystemLogPluginTest extends AbstractDiagnosticsPluginTest {
    protected Config config;

    protected TestHazelcastInstanceFactory hzFactory;

    protected HazelcastInstance hz;

    protected SystemLogPlugin plugin;

    @Test
    public void testGetPeriodSeconds() {
        Assert.assertEquals(1000, plugin.getPeriodMillis());
    }

    @Test
    public void testGetPeriodSeconds_whenPluginIsDisabled_thenReturnDisabled() {
        config.setProperty(SystemLogPlugin.ENABLED.getName(), "false");
        HazelcastInstance instance = hzFactory.newHazelcastInstance(config);
        plugin = new SystemLogPlugin(HazelcastTestSupport.getNodeEngineImpl(instance));
        plugin.onStart();
        Assert.assertEquals(DiagnosticsPlugin.DISABLED, plugin.getPeriodMillis());
    }

    @Test
    public void testLifecycle() {
        hz.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                plugin.run(logWriter);
                assertContains((("Lifecycle[" + (StringUtil.LINE_SEPARATOR)) + "                          SHUTTING_DOWN]"));
            }
        });
    }

    @Test
    public void testMembership() {
        HazelcastInstance instance = hzFactory.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                plugin.run(logWriter);
                assertContains("MemberAdded[");
            }
        });
        instance.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                plugin.run(logWriter);
                assertContains("MemberRemoved[");
            }
        });
    }

    @Test
    public void testMigration() {
        HazelcastTestSupport.warmUpPartitions(hz);
        HazelcastInstance instance = hzFactory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(instance);
        HazelcastTestSupport.waitAllForSafeState(hz, instance);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                plugin.run(logWriter);
                assertContains("MigrationStarted");
                assertContains("MigrationCompleted");
            }
        });
    }
}

