/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.utils.time;


import org.junit.Assert;
import org.junit.Test;


/**
 * Logical timestamp test.
 */
public class LogicalTimestampTest {
    @Test
    public void testLogicalTimestamp() throws Exception {
        LogicalTimestamp timestamp = LogicalTimestamp.of(1);
        Assert.assertEquals(1, timestamp.value());
        Assert.assertTrue(timestamp.isNewerThan(LogicalTimestamp.of(0)));
        Assert.assertFalse(timestamp.isNewerThan(LogicalTimestamp.of(2)));
        Assert.assertTrue(timestamp.isOlderThan(LogicalTimestamp.of(2)));
        Assert.assertFalse(timestamp.isOlderThan(LogicalTimestamp.of(0)));
    }
}

