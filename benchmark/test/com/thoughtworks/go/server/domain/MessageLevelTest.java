/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.server.domain;


import MessageLevel.DEBUG;
import MessageLevel.ERROR;
import MessageLevel.INFO;
import MessageLevel.WARN;
import org.junit.Assert;
import org.junit.Test;


public class MessageLevelTest {
    @Test
    public void testShouldMapDisplayNameToType() {
        Assert.assertEquals(DEBUG, MessageLevel.getLevelForPriority("debug"));
        Assert.assertEquals(INFO, MessageLevel.getLevelForPriority("info"));
        Assert.assertEquals(WARN, MessageLevel.getLevelForPriority("warn"));
        Assert.assertEquals(ERROR, MessageLevel.getLevelForPriority("error"));
    }

    @Test
    public void testShouldMapDisplayNameToTypeIgnoringCase() {
        Assert.assertEquals(DEBUG, MessageLevel.getLevelForPriority("Debug"));
        Assert.assertEquals(INFO, MessageLevel.getLevelForPriority("Info"));
        Assert.assertEquals(WARN, MessageLevel.getLevelForPriority("Warn"));
        Assert.assertEquals(ERROR, MessageLevel.getLevelForPriority("Error"));
    }

    @Test
    public void testShouldGetDisplayNameForType() {
        Assert.assertEquals("debug", DEBUG.getDisplayName());
        Assert.assertEquals("info", INFO.getDisplayName());
        Assert.assertEquals("warn", WARN.getDisplayName());
        Assert.assertEquals("error", ERROR.getDisplayName());
    }

    @Test
    public void testShouldShowDisplayNameAsStringRepresentation() {
        Assert.assertEquals("debug", DEBUG.toString());
        Assert.assertEquals("info", INFO.toString());
        Assert.assertEquals("warn", WARN.toString());
        Assert.assertEquals("error", ERROR.toString());
    }
}

