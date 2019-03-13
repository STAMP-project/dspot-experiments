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
package com.hazelcast.test.starter.test;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.starter.HazelcastStarter;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class HazelcastStarterTest {
    private HazelcastInstance hz;

    @Test
    public void testMember() {
        hz = HazelcastStarter.newHazelcastInstance("3.7", false);
        HazelcastInstance bouncingInstance = null;
        try {
            for (int i = 1; i < 6; i++) {
                String version = "3.7." + i;
                System.out.println(("Starting member " + version));
                bouncingInstance = HazelcastStarter.newHazelcastInstance(version);
                System.out.println(("Stopping member " + version));
                bouncingInstance.shutdown();
            }
        } finally {
            if ((bouncingInstance != null) && (bouncingInstance.getLifecycleService().isRunning())) {
                TestUtil.terminateInstance(bouncingInstance);
            }
        }
    }

    @Test
    public void testGetOrCreateWorkingDir() {
        String versionSpec = "3.10-EE-test";
        File dir = HazelcastStarter.getOrCreateVersionDirectory(versionSpec);
        Assert.assertTrue("Temporary directory should have been created", dir.exists());
        String path = dir.getAbsolutePath();
        // ensure no exception is thrown when attempting to recreate an existing version directory
        dir = HazelcastStarter.getOrCreateVersionDirectory(versionSpec);
        Assert.assertEquals(path, dir.getAbsolutePath());
    }

    @Test
    public void testHazelcastInstanceCompatibility_withStarterInstance() {
        Config config = new Config();
        hz = HazelcastStarter.newHazelcastInstance("3.10.3", config, false);
        HazelcastStarterTest.testHazelcastInstanceCompatibility(hz, null);
    }

    @Test
    public void testHazelcastInstanceCompatibility_withRealInstance() {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();
        try {
            HazelcastStarterTest.testHazelcastInstanceCompatibility(instance, null);
        } finally {
            TestUtil.terminateInstance(instance);
        }
    }

    @Test
    public void testHazelcastInstanceCompatibility_withFactoryInstance() {
        TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory(1);
        HazelcastInstance instance = factory.newHazelcastInstance();
        try {
            HazelcastStarterTest.testHazelcastInstanceCompatibility(instance, factory);
        } finally {
            factory.terminateAll();
        }
    }
}

