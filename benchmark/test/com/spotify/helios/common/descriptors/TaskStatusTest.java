/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.common.descriptors;


import State.RUNNING;
import ThrottleState.NO;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.helios.common.Json;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TaskStatusTest {
    private static final Job JOB = Job.newBuilder().setCommand(ImmutableList.of("BOGUS")).setImage("IMAGE").setName("NAME").setVersion("VERSION").build();

    private static final Map<String, String> ENV = ImmutableMap.<String, String>builder().put("VAR", "VALUE").build();

    private static final TaskStatus STATUS = TaskStatus.newBuilder().setContainerId("CONTAINER_ID").setGoal(Goal.START).setJob(TaskStatusTest.JOB).setState(RUNNING).setEnv(TaskStatusTest.ENV).setThrottled(NO).build();

    @Test
    public void testSerializationOfEnvironment() throws Exception {
        final byte[] bytes = Json.asBytes(TaskStatusTest.STATUS);
        final TaskStatus read = Json.read(bytes, TaskStatus.class);
        Assert.assertEquals(1, read.getEnv().size());
        Assert.assertEquals("VALUE", read.getEnv().get("VAR"));
    }

    @Test
    public void testToBuilderAndBackEnvironment() throws Exception {
        final TaskStatus s = TaskStatusTest.STATUS.asBuilder().build();
        Assert.assertEquals(1, s.getEnv().size());
        Assert.assertEquals("VALUE", s.getEnv().get("VAR"));
    }
}

