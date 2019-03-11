/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.redis.state;


import org.apache.storm.redis.common.commands.RedisCommands;
import org.apache.storm.redis.common.container.RedisCommandsInstanceContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link RedisKeyValueState}
 *
 * NOTE: The type of key for mockMap is String, which should be byte[],
 * since but byte[] doesn't implement equals() so taking workaround to make life happier.
 * It shouldn't make issues on Redis side, since raw type of Redis is binary.
 */
public class RedisKeyValueStateTest {
    RedisCommandsInstanceContainer mockContainer;

    RedisCommands mockCommands;

    RedisKeyValueState<String, String> keyValueState;

    @Test
    public void testPutAndGet() throws Exception {
        keyValueState.put("a", "1");
        keyValueState.put("b", "2");
        Assert.assertEquals("1", keyValueState.get("a"));
        Assert.assertEquals("2", keyValueState.get("b"));
        Assert.assertEquals(null, keyValueState.get("c"));
    }

    @Test
    public void testPutAndDelete() throws Exception {
        keyValueState.put("a", "1");
        keyValueState.put("b", "2");
        Assert.assertEquals("1", keyValueState.get("a"));
        Assert.assertEquals("2", keyValueState.get("b"));
        Assert.assertEquals(null, keyValueState.get("c"));
        Assert.assertEquals("1", keyValueState.delete("a"));
        Assert.assertEquals(null, keyValueState.get("a"));
        Assert.assertEquals("2", keyValueState.get("b"));
        Assert.assertEquals(null, keyValueState.get("c"));
    }

    @Test
    public void testPrepareCommitRollback() throws Exception {
        keyValueState.put("a", "1");
        keyValueState.put("b", "2");
        keyValueState.prepareCommit(1);
        keyValueState.put("c", "3");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, getValues());
        keyValueState.rollback();
        Assert.assertArrayEquals(new String[]{ null, null, null }, getValues());
        keyValueState.put("a", "1");
        keyValueState.put("b", "2");
        keyValueState.prepareCommit(1);
        keyValueState.commit(1);
        keyValueState.put("c", "3");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, getValues());
        keyValueState.rollback();
        Assert.assertArrayEquals(new String[]{ "1", "2", null }, getValues());
        keyValueState.put("c", "3");
        Assert.assertEquals("2", keyValueState.delete("b"));
        Assert.assertEquals("3", keyValueState.delete("c"));
        Assert.assertArrayEquals(new String[]{ "1", null, null }, getValues());
        keyValueState.prepareCommit(2);
        Assert.assertArrayEquals(new String[]{ "1", null, null }, getValues());
        keyValueState.commit(2);
        Assert.assertArrayEquals(new String[]{ "1", null, null }, getValues());
        keyValueState.put("b", "2");
        keyValueState.prepareCommit(3);
        keyValueState.put("c", "3");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, getValues());
        keyValueState.rollback();
        Assert.assertArrayEquals(new String[]{ "1", null, null }, getValues());
    }
}

