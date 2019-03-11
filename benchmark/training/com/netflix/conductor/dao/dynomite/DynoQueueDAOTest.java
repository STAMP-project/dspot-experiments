/**
 * Copyright 2016 Netflix, Inc.
 *
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
 */
package com.netflix.conductor.dao.dynomite;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.utils.JsonMapperProvider;
import com.netflix.conductor.dao.QueueDAO;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Viren
 */
public class DynoQueueDAOTest {
    private QueueDAO dao;

    private static ObjectMapper om = new JsonMapperProvider().get();

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void test() {
        String queueName = "TestQueue";
        long offsetTimeInSecond = 0;
        for (int i = 0; i < 10; i++) {
            String messageId = "msg" + i;
            dao.push(queueName, messageId, offsetTimeInSecond);
        }
        int size = dao.getSize(queueName);
        Assert.assertEquals(10, size);
        Map<String, Long> details = dao.queuesDetail();
        Assert.assertEquals(1, details.size());
        Assert.assertEquals(10L, details.get(queueName).longValue());
        for (int i = 0; i < 10; i++) {
            String messageId = "msg" + i;
            dao.pushIfNotExists(queueName, messageId, offsetTimeInSecond);
        }
        List<String> popped = dao.pop(queueName, 10, 100);
        Assert.assertNotNull(popped);
        Assert.assertEquals(10, popped.size());
        Map<String, Map<String, Map<String, Long>>> verbose = dao.queuesDetailVerbose();
        Assert.assertEquals(1, verbose.size());
        long shardSize = verbose.get(queueName).get("a").get("size");
        long unackedSize = verbose.get(queueName).get("a").get("uacked");
        Assert.assertEquals(0, shardSize);
        Assert.assertEquals(10, unackedSize);
        popped.forEach(( messageId) -> dao.ack(queueName, messageId));
        verbose = dao.queuesDetailVerbose();
        Assert.assertEquals(1, verbose.size());
        shardSize = verbose.get(queueName).get("a").get("size");
        unackedSize = verbose.get(queueName).get("a").get("uacked");
        Assert.assertEquals(0, shardSize);
        Assert.assertEquals(0, unackedSize);
        popped = dao.pop(queueName, 10, 100);
        Assert.assertNotNull(popped);
        Assert.assertEquals(0, popped.size());
        for (int i = 0; i < 10; i++) {
            String messageId = "msg" + i;
            dao.pushIfNotExists(queueName, messageId, offsetTimeInSecond);
        }
        size = dao.getSize(queueName);
        Assert.assertEquals(10, size);
        for (int i = 0; i < 10; i++) {
            String messageId = "msg" + i;
            dao.remove(queueName, messageId);
        }
        size = dao.getSize(queueName);
        Assert.assertEquals(0, size);
        for (int i = 0; i < 10; i++) {
            String messageId = "msg" + i;
            dao.pushIfNotExists(queueName, messageId, offsetTimeInSecond);
        }
        dao.flush(queueName);
        size = dao.getSize(queueName);
        Assert.assertEquals(0, size);
    }
}

