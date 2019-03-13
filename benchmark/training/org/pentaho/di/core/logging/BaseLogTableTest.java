/**
 * ! ******************************************************************************
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


import LogStatus.END;
import LogStatus.START;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ KettleLogStore.class, Utils.class, Const.class })
public class BaseLogTableTest {
    @Test
    public void testRemoveChannelFromBufferCallInGetLogBufferInFirstJobExecution() {
        StringBuffer sb = new StringBuffer("");
        LoggingBuffer lb = Mockito.mock(LoggingBuffer.class);
        Mockito.doReturn(sb).when(lb).getBuffer(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        mockStatic(KettleLogStore.class);
        mockStatic(Utils.class);
        mockStatic(Const.class);
        Mockito.when(KettleLogStore.getAppender()).thenReturn(lb);
        BaseLogTable baseLogTable = Mockito.mock(BaseLogTable.class);
        Mockito.doCallRealMethod().when(baseLogTable).getLogBuffer(ArgumentMatchers.any(VariableSpace.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(LogStatus.class), ArgumentMatchers.anyString());
        VariableSpace vs = Mockito.mock(VariableSpace.class);
        String s1 = baseLogTable.getLogBuffer(vs, "1", START, null);
        String s2 = baseLogTable.getLogBuffer(vs, "1", END, null);
        Assert.assertEquals((((Const.CR) + "START") + (Const.CR)), s1);
        Assert.assertEquals((((Const.CR) + "START") + (Const.CR)), (((s1 + (Const.CR)) + "END") + (Const.CR)), s2);
        Mockito.verify(lb, Mockito.times(1)).removeChannelFromBuffer("1");
    }

    @Test
    public void testRemoveChannelFromBufferCallInGetLogBufferInRecursiveJobExecution() {
        StringBuffer sb = new StringBuffer("Event previously executed for the same Job");
        LoggingBuffer lb = Mockito.mock(LoggingBuffer.class);
        Mockito.doReturn(sb).when(lb).getBuffer(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        mockStatic(KettleLogStore.class);
        mockStatic(Utils.class);
        mockStatic(Const.class);
        Mockito.when(KettleLogStore.getAppender()).thenReturn(lb);
        BaseLogTable baseLogTable = Mockito.mock(BaseLogTable.class);
        Mockito.doCallRealMethod().when(baseLogTable).getLogBuffer(ArgumentMatchers.any(VariableSpace.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(LogStatus.class), ArgumentMatchers.anyString());
        VariableSpace vs = Mockito.mock(VariableSpace.class);
        String s1 = baseLogTable.getLogBuffer(vs, "1", START, null);
        String s2 = baseLogTable.getLogBuffer(vs, "1", END, null);
        // removeChannelFromBuffer function is void - need to simulate the behaviour here
        s1 = s1.replace("Event previously executed for the same Job", "");
        s2 = s2.replace("Event previously executed for the same Job", "");
        Assert.assertEquals((((Const.CR) + "START") + (Const.CR)), s1);
        Assert.assertEquals((((Const.CR) + "START") + (Const.CR)), (((s1 + (Const.CR)) + "END") + (Const.CR)), s2);
        Mockito.verify(lb, Mockito.times(1)).removeChannelFromBuffer("1");
    }
}

