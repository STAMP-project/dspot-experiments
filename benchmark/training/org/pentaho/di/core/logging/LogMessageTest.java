/**
 * *****************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.logging;


import org.junit.Assert;
import org.junit.Test;

import static LogLevel.BASIC;
import static LogLevel.DEBUG;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class LogMessageTest {
    private LogMessage logMessage;

    private static final String LOG_MESSAGE = "Test Message";

    private static final LogLevel LOG_LEVEL = BASIC;

    private static String treeLogChannelId;

    private static String simpleLogChannelId;

    @Test
    public void testWhenLogMarkMappingTurnOn_DetailedSubjectUsed() throws Exception {
        turnOnLogMarkMapping();
        logMessage = new LogMessage(LogMessageTest.LOG_MESSAGE, LogMessageTest.treeLogChannelId, LogMessageTest.LOG_LEVEL);
        Assert.assertTrue(LogMessageTest.LOG_MESSAGE.equals(logMessage.getMessage()));
        Assert.assertTrue(LogMessageTest.LOG_LEVEL.equals(logMessage.getLevel()));
        Assert.assertTrue(LogMessageTest.treeLogChannelId.equals(logMessage.getLogChannelId()));
        Assert.assertTrue("[TRANS_SUBJECT].[STEP_SUBJECT].TRANS_CHILD_SUBJECT".equals(logMessage.getSubject()));
    }

    @Test
    public void testWhenLogMarkMappingTurnOff_SimpleSubjectUsed() throws Exception {
        turnOffLogMarkMapping();
        logMessage = new LogMessage(LogMessageTest.LOG_MESSAGE, LogMessageTest.treeLogChannelId, LogMessageTest.LOG_LEVEL);
        Assert.assertTrue(LogMessageTest.LOG_MESSAGE.equals(logMessage.getMessage()));
        Assert.assertTrue(LogMessageTest.LOG_LEVEL.equals(logMessage.getLevel()));
        Assert.assertTrue(LogMessageTest.treeLogChannelId.equals(logMessage.getLogChannelId()));
        Assert.assertTrue("TRANS_CHILD_SUBJECT".equals(logMessage.getSubject()));
    }

    @Test
    public void testWhenLogMarkMappingTurnOnAndNoSubMappingUsed_DetailedSubjectContainsOnlySimpleSubject() throws Exception {
        turnOnLogMarkMapping();
        LogMessageTest.simpleLogChannelId = LoggingRegistry.getInstance().registerLoggingSource(LogMessageTest.getLoggingObjectWithOneParent());
        logMessage = new LogMessage(LogMessageTest.LOG_MESSAGE, LogMessageTest.simpleLogChannelId, LogMessageTest.LOG_LEVEL);
        Assert.assertTrue(LogMessageTest.LOG_MESSAGE.equals(logMessage.getMessage()));
        Assert.assertTrue(LogMessageTest.LOG_LEVEL.equals(logMessage.getLevel()));
        Assert.assertTrue(LogMessageTest.simpleLogChannelId.equals(logMessage.getLogChannelId()));
        Assert.assertTrue("TRANS_SUBJECT".equals(logMessage.getSubject()));
        LoggingRegistry.getInstance().removeIncludingChildren(LogMessageTest.simpleLogChannelId);
    }

    @Test
    public void testToString() throws Exception {
        LogMessage msg = new LogMessage("Log message", "Channel 01", DEBUG);
        msg.setSubject("Simple");
        Assert.assertEquals("Simple - Log message", msg.toString());
    }

    @Test
    public void testToString_withOneArgument() throws Exception {
        LogMessage msg = new LogMessage("Log message for {0}", "Channel 01", new String[]{ "Test" }, DEBUG);
        msg.setSubject("Subject");
        Assert.assertEquals("Subject - Log message for Test", msg.toString());
    }

    @Test
    public void testGetMessage() {
        LogMessage msg = new LogMessage("m {0}, {1}, {2}, {3}, {4,number,#.00}, {5} {foe}", "Channel 01", new Object[]{ "Foo", "{abc}", "", null, 123 }, DEBUG);
        Assert.assertEquals("m Foo, {abc}, , null, 123.00, {5} {foe}", msg.getMessage());
    }
}

