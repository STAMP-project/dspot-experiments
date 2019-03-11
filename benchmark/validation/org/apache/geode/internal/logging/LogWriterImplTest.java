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
package org.apache.geode.internal.logging;


import LogWriterImpl.levelNames;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link LogWriterImpl}.
 */
@Category(LoggingTest.class)
public class LogWriterImplTest {
    @Test
    public void testAllowedLogLevels() {
        Assert.assertEquals("all|finest|finer|fine|config|info|warning|error|severe|none", LogWriterImpl.allowedLogLevels());
    }

    @Test
    public void testLevelNames() {
        String[] levelNames = levelNames.toArray(new String[0]);
        Assert.assertEquals("all", levelNames[0]);
        Assert.assertEquals("finest", levelNames[1]);
        Assert.assertEquals("finer", levelNames[2]);
        Assert.assertEquals("fine", levelNames[3]);
        Assert.assertEquals("config", levelNames[4]);
        Assert.assertEquals("info", levelNames[5]);
        Assert.assertEquals("warning", levelNames[6]);
        Assert.assertEquals("error", levelNames[7]);
        Assert.assertEquals("severe", levelNames[8]);
        Assert.assertEquals("none", levelNames[9]);
        Assert.assertEquals(10, levelNames.length);
    }

    @Test
    public void testLevelNameToCode() {
        Assert.assertEquals(Integer.MIN_VALUE, LogWriterImpl.levelNameToCode("all"));
        Assert.assertEquals(300, LogWriterImpl.levelNameToCode("finest"));
        Assert.assertEquals(300, LogWriterImpl.levelNameToCode("trace"));
        Assert.assertEquals(400, LogWriterImpl.levelNameToCode("finer"));
        Assert.assertEquals(500, LogWriterImpl.levelNameToCode("fine"));
        Assert.assertEquals(500, LogWriterImpl.levelNameToCode("debug"));
        Assert.assertEquals(700, LogWriterImpl.levelNameToCode("config"));
        Assert.assertEquals(800, LogWriterImpl.levelNameToCode("info"));
        Assert.assertEquals(900, LogWriterImpl.levelNameToCode("warning"));
        Assert.assertEquals(900, LogWriterImpl.levelNameToCode("warn"));
        Assert.assertEquals(950, LogWriterImpl.levelNameToCode("error"));
        Assert.assertEquals(1000, LogWriterImpl.levelNameToCode("severe"));
        Assert.assertEquals(1000, LogWriterImpl.levelNameToCode("fatal"));
        Assert.assertEquals(Integer.MAX_VALUE, LogWriterImpl.levelNameToCode("none"));
    }

    @Test
    public void testLevelToString() {
        Assert.assertEquals("all", LogWriterImpl.levelToString(Integer.MIN_VALUE));
        Assert.assertEquals("finest", LogWriterImpl.levelToString(300));
        Assert.assertEquals("finer", LogWriterImpl.levelToString(400));
        Assert.assertEquals("fine", LogWriterImpl.levelToString(500));
        Assert.assertEquals("config", LogWriterImpl.levelToString(700));
        Assert.assertEquals("info", LogWriterImpl.levelToString(800));
        Assert.assertEquals("warning", LogWriterImpl.levelToString(900));
        Assert.assertEquals("error", LogWriterImpl.levelToString(950));
        Assert.assertEquals("severe", LogWriterImpl.levelToString(1000));
        Assert.assertEquals("none", LogWriterImpl.levelToString(Integer.MAX_VALUE));
        // everything else...
        Assert.assertEquals("level-600", LogWriterImpl.levelToString(600));
    }
}

