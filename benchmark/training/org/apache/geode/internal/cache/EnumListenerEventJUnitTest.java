/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import EnumListenerEvent.AFTER_CREATE;
import EnumListenerEvent.AFTER_DESTROY;
import EnumListenerEvent.AFTER_INVALIDATE;
import EnumListenerEvent.AFTER_REGION_CLEAR;
import EnumListenerEvent.AFTER_REGION_CREATE;
import EnumListenerEvent.AFTER_REGION_DESTROY;
import EnumListenerEvent.AFTER_REGION_INVALIDATE;
import EnumListenerEvent.AFTER_REGION_LIVE;
import EnumListenerEvent.AFTER_REGISTER_DATASERIALIZER;
import EnumListenerEvent.AFTER_REGISTER_INSTANTIATOR;
import EnumListenerEvent.AFTER_REMOTE_REGION_CRASH;
import EnumListenerEvent.AFTER_REMOTE_REGION_CREATE;
import EnumListenerEvent.AFTER_REMOTE_REGION_DEPARTURE;
import EnumListenerEvent.AFTER_ROLE_GAIN;
import EnumListenerEvent.AFTER_ROLE_LOSS;
import EnumListenerEvent.AFTER_TOMBSTONE_EXPIRATION;
import EnumListenerEvent.AFTER_UPDATE;
import EnumListenerEvent.TIMESTAMP_UPDATE;
import org.junit.Test;


public class EnumListenerEventJUnitTest {
    /**
     * tests whether EnumListenerEvent.getEnumListenerEvent(int cCode) returns the right result
     */
    @Test
    public void testGetEnumListEvent() {
        checkAndAssert(0, null);
        checkAndAssert(1, AFTER_CREATE);
        checkAndAssert(2, AFTER_UPDATE);
        checkAndAssert(3, AFTER_INVALIDATE);
        checkAndAssert(4, AFTER_DESTROY);
        checkAndAssert(5, AFTER_REGION_CREATE);
        checkAndAssert(6, AFTER_REGION_INVALIDATE);
        checkAndAssert(7, AFTER_REGION_CLEAR);
        checkAndAssert(8, AFTER_REGION_DESTROY);
        checkAndAssert(9, AFTER_REMOTE_REGION_CREATE);
        checkAndAssert(10, AFTER_REMOTE_REGION_DEPARTURE);
        checkAndAssert(11, AFTER_REMOTE_REGION_CRASH);
        checkAndAssert(12, AFTER_ROLE_GAIN);
        checkAndAssert(13, AFTER_ROLE_LOSS);
        checkAndAssert(14, AFTER_REGION_LIVE);
        checkAndAssert(15, AFTER_REGISTER_INSTANTIATOR);
        checkAndAssert(16, AFTER_REGISTER_DATASERIALIZER);
        checkAndAssert(17, AFTER_TOMBSTONE_EXPIRATION);
        // extra non-existent code checks as a markers so that this test will
        // fail if further events are added (0th or +1 codes) without updating this test
        checkAndAssert(18, TIMESTAMP_UPDATE);
        checkAndAssert(19, null);
    }
}

