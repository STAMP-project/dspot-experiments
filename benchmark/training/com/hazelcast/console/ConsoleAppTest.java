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
package com.hazelcast.console;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests for demo console application.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class ConsoleAppTest extends HazelcastTestSupport {
    private static final PrintStream systemOutOrig = System.out;

    private static ByteArrayOutputStream baos;

    @Test
    public void executeOnKey() {
        ConsoleApp consoleApp = new ConsoleApp(createHazelcastInstance());
        for (int i = 0; i < 100; i++) {
            consoleApp.handleCommand(String.format("executeOnKey message%d key%d", i, i));
            assertTextInSystemOut(("message" + i));
        }
    }

    /**
     * Tests m.put operation.
     */
    @Test
    public void mapPut() {
        HazelcastInstance hz = createHazelcastInstance();
        IMap<String, String> map = hz.getMap("default");
        ConsoleApp consoleApp = new ConsoleApp(hz);
        Assert.assertEquals("Unexpected map size", 0, map.size());
        consoleApp.handleCommand("m.put putTestKey testValue");
        assertTextInSystemOut("null");// original value for the key

        Assert.assertEquals("Unexpected map size", 1, map.size());
        Assert.assertThat(map.get("putTestKey"), CoreMatchers.containsString("testValue"));
        consoleApp.handleCommand("m.put putTestKey testXValue");
        assertTextInSystemOut("testValue");// original value for the key

        Assert.assertThat(map.get("putTestKey"), CoreMatchers.containsString("testXValue"));
        consoleApp.handleCommand("m.put putTestKey2 testValue");
        Assert.assertEquals("Unexpected map size", 2, map.size());
    }

    /**
     * Tests m.remove operation.
     */
    @Test
    public void mapRemove() {
        HazelcastInstance hz = createHazelcastInstance();
        ConsoleApp consoleApp = new ConsoleApp(hz);
        IMap<String, String> map = hz.getMap("default");
        map.put("a", "valueOfA");
        map.put("b", "valueOfB");
        ConsoleAppTest.resetSystemOut();
        consoleApp.handleCommand("m.remove b");
        assertTextInSystemOut("valueOfB");// original value for the key

        Assert.assertEquals("Unexpected map size", 1, map.size());
        Assert.assertFalse("Unexpected entry in the map", map.containsKey("b"));
    }

    /**
     * Tests m.delete operation.
     */
    @Test
    public void mapDelete() {
        HazelcastInstance hz = createHazelcastInstance();
        ConsoleApp consoleApp = new ConsoleApp(hz);
        IMap<String, String> map = hz.getMap("default");
        map.put("a", "valueOfA");
        map.put("b", "valueOfB");
        ConsoleAppTest.resetSystemOut();
        consoleApp.handleCommand("m.delete b");
        assertTextInSystemOut("true");// result of successful operation

        Assert.assertEquals("Unexpected map size", 1, map.size());
        Assert.assertFalse("Unexpected entry in the map", map.containsKey("b"));
    }

    /**
     * Tests m.get operation.
     */
    @Test
    public void mapGet() {
        HazelcastInstance hz = createHazelcastInstance();
        ConsoleApp consoleApp = new ConsoleApp(hz);
        hz.<String, String>getMap("default").put("testGetKey", "testGetValue");
        consoleApp.handleCommand("m.get testGetKey");
        assertTextInSystemOut("testGetValue");
    }

    /**
     * Tests m.putmany operation.
     */
    @Test
    public void mapPutMany() {
        HazelcastInstance hz = createHazelcastInstance();
        ConsoleApp consoleApp = new ConsoleApp(hz);
        IMap<String, ?> map = hz.getMap("default");
        consoleApp.handleCommand("m.putmany 100 8 1000");
        Assert.assertEquals("Unexpected map size", 100, map.size());
        Assert.assertFalse(map.containsKey("key999"));
        Assert.assertTrue(map.containsKey("key1000"));
        Assert.assertTrue(map.containsKey("key1099"));
        Assert.assertFalse(map.containsKey("key1100"));
        Assert.assertEquals(8, ((byte[]) (map.get("key1050"))).length);
    }
}

