/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.region.group;


import junit.framework.TestCase;
import org.apache.activemq.command.ConsumerId;


/**
 *
 */
public class MessageGroupMapTest extends TestCase {
    protected MessageGroupMap map;

    private ConsumerId consumer1;

    private ConsumerId consumer2;

    private ConsumerId consumer3;

    private long idCounter;

    public void testSingleConsumerForManyBucks() throws Exception {
        assertGet("1", null);
        map.put("1", consumer1);
        assertGet("1", consumer1);
        map.put("2", consumer1);
        assertGet("2", consumer1);
        map.put("3", consumer1);
        assertGet("3", consumer1);
        MessageGroupSet set = map.removeConsumer(consumer1);
        assertContains(set, "1");
        assertContains(set, "2");
        assertContains(set, "3");
        assertGet("1", null);
        assertGet("2", null);
        assertGet("3", null);
    }

    public void testManyConsumers() throws Exception {
        assertGet("1", null);
        map.put("1", consumer1);
        assertGet("1", consumer1);
        map.put("2", consumer2);
        assertGet("2", consumer2);
        map.put("3", consumer3);
        assertGet("3", consumer3);
        MessageGroupSet set = map.removeConsumer(consumer1);
        assertContains(set, "1");
        assertGet("1", null);
        map.put("1", consumer2);
        assertGet("1", consumer2);
        set = map.removeConsumer(consumer2);
        assertContains(set, "1");
        assertContains(set, "2");
    }
}

