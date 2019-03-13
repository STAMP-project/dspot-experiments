/**
 * *****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package org.apache.storm.eventhubs.spout;


import org.junit.Assert;
import org.junit.Test;


public class TestEventData {
    @Test
    public void testEventDataComparision() {
        MessageId messageId1 = MessageId.create(null, "3", 1);
        EventDataWrap eventData1 = EventDataWrap.create(null, messageId1);
        MessageId messageId2 = MessageId.create(null, "13", 2);
        EventDataWrap eventData2 = EventDataWrap.create(null, messageId2);
        Assert.assertTrue(((eventData2.compareTo(eventData1)) > 0));
    }
}

