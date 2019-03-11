/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.reader;


import TimelineUIDConverter.APPLICATION_UID;
import TimelineUIDConverter.FLOWRUN_UID;
import TimelineUIDConverter.FLOW_UID;
import TimelineUIDConverter.GENERIC_ENTITY_UID;
import TimelineUIDConverter.SUB_APPLICATION_ENTITY_UID;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineUIDConverter {
    @Test
    public void testUIDEncodingDecoding() throws Exception {
        TimelineReaderContext context = new TimelineReaderContext("!cluster", "!b*o*!xer", "oozie*", null, null, null, null);
        String uid = FLOW_UID.encodeUID(context);
        Assert.assertEquals("*!cluster!*!b**o***!xer!oozie**", uid);
        Assert.assertEquals(context, FLOW_UID.decodeUID(uid));
        context = new TimelineReaderContext("!cluster*", "!b*o!!x!*er", "*oozie!", 123L, null, null, null);
        uid = FLOWRUN_UID.encodeUID(context);
        Assert.assertEquals("*!cluster**!*!b**o*!*!x*!**er!**oozie*!!123", uid);
        Assert.assertEquals(context, FLOWRUN_UID.decodeUID(uid));
        context = new TimelineReaderContext("yarn_cluster", "root", "hive_join", 1234L, "application_1111111111_1111", null, null);
        uid = APPLICATION_UID.encodeUID(context);
        Assert.assertEquals("yarn_cluster!root!hive_join!1234!application_1111111111_1111", uid);
        Assert.assertEquals(context, APPLICATION_UID.decodeUID(uid));
        context = new TimelineReaderContext("yarn_cluster", null, null, null, "application_1111111111_1111", null, null);
        uid = APPLICATION_UID.encodeUID(context);
        Assert.assertEquals("yarn_cluster!application_1111111111_1111", uid);
        Assert.assertEquals(context, APPLICATION_UID.decodeUID(uid));
        context = new TimelineReaderContext("yarn_cluster", "root", "hive_join", 1234L, "application_1111111111_1111", "YARN_CONTAINER", 12345L, "container_1111111111_1111_01_000001");
        uid = GENERIC_ENTITY_UID.encodeUID(context);
        Assert.assertEquals(("yarn_cluster!root!hive_join!1234!application_1111111111_1111!" + "YARN_CONTAINER!12345!container_1111111111_1111_01_000001"), uid);
        Assert.assertEquals(context, GENERIC_ENTITY_UID.decodeUID(uid));
        context = new TimelineReaderContext("yarn_cluster", null, null, null, "application_1111111111_1111", "YARN_CONTAINER", 54321L, "container_1111111111_1111_01_000001");
        uid = GENERIC_ENTITY_UID.encodeUID(context);
        Assert.assertEquals(("yarn_cluster!application_1111111111_1111!YARN_CONTAINER!" + "54321!container_1111111111_1111_01_000001"), uid);
        Assert.assertEquals(context, GENERIC_ENTITY_UID.decodeUID(uid));
        context = new TimelineReaderContext("yarn_cluster", null, null, null, null, "YARN_CONTAINER", 54321L, "container_1111111111_1111_01_000001", "user1");
        uid = SUB_APPLICATION_ENTITY_UID.encodeUID(context);
        Assert.assertEquals(("yarn_cluster!user1!YARN_CONTAINER!" + "54321!container_1111111111_1111_01_000001"), uid);
        Assert.assertEquals(context, SUB_APPLICATION_ENTITY_UID.decodeUID(uid));
    }

    @Test
    public void testUIDNotProperlyEscaped() throws Exception {
        try {
            FLOW_UID.decodeUID("*!cluster!*!b*o***!xer!oozie**");
            Assert.fail("UID not properly escaped. Exception should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
        try {
            FLOW_UID.decodeUID("*!cluster!*!b**o***!xer!oozie*");
            Assert.fail("UID not properly escaped. Exception should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
        try {
            FLOW_UID.decodeUID("*!cluster!*!b**o***xer!oozie*");
            Assert.fail("UID not properly escaped. Exception should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
        Assert.assertNull(FLOW_UID.decodeUID("!cluster!*!b**o***!xer!oozie**"));
        Assert.assertNull(FLOW_UID.decodeUID("*!cluster!*!b**o**!xer!oozie**"));
    }
}

