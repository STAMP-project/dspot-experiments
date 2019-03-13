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
package org.pentaho.di.trans;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.core.logging.LoggingObject;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepStatus;
import org.pentaho.di.trans.steps.transexecutor.TransExecutorParameters;


@RunWith(MockitoJUnitRunner.class)
public class SubtransExecutorTest {
    @Mock
    private LogChannelInterfaceFactory logChannelFactory;

    @Mock
    private LogChannel logChannel;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testRunningZeroRowsIsEmptyOptional() throws Exception {
        SubtransExecutor subtransExecutor = new SubtransExecutor("subtransname", null, null, false, null, "");
        Optional<Result> execute = subtransExecutor.execute(Collections.emptyList());
        Assert.assertFalse(execute.isPresent());
    }

    @Test
    public void testRunsATrans() throws Exception {
        TransMeta parentMeta = new TransMeta(this.getClass().getResource("subtrans-executor-parent.ktr").getPath(), new Variables());
        TransMeta subMeta = new TransMeta(this.getClass().getResource("subtrans-executor-sub.ktr").getPath(), new Variables());
        LoggingObjectInterface loggingObject = new LoggingObject("anything");
        Trans parentTrans = Mockito.spy(new Trans(parentMeta, loggingObject));
        SubtransExecutor subtransExecutor = new SubtransExecutor("subtransname", parentTrans, subMeta, true, new TransExecutorParameters(), "Group By");
        RowMetaInterface rowMeta = parentMeta.getStepFields("Data Grid");
        List<RowMetaAndData> rows = Arrays.asList(new RowMetaAndData(rowMeta, "Pentaho", 1L), new RowMetaAndData(rowMeta, "Pentaho", 2L), new RowMetaAndData(rowMeta, "Pentaho", 3L), new RowMetaAndData(rowMeta, "Pentaho", 4L));
        Optional<Result> optionalResult = subtransExecutor.execute(rows);
        Assert.assertEquals(1, optionalResult.orElseThrow(AssertionError::new).getRows().size());
        Mockito.verify(this.logChannel).logBasic((((((((((Const.CR) + "------------> Linenr 1------------------------------") + (Const.CR)) + "name = Pentaho") + (Const.CR)) + "sum = 10") + (Const.CR)) + (Const.CR)) + "===================="));
        Map<String, StepStatus> statuses = subtransExecutor.getStatuses();
        Assert.assertEquals(3, statuses.size());
        List<StepStatus> statusList = new java.util.ArrayList(statuses.values());
        Assert.assertEquals("Get rows from result", statusList.get(0).getStepname());
        Assert.assertEquals("Group by", statusList.get(1).getStepname());
        Assert.assertEquals("Write to log", statusList.get(2).getStepname());
        for (Map.Entry<String, StepStatus> entry : statuses.entrySet()) {
            StepStatus statusSpy = Mockito.spy(entry.getValue());
            statuses.put(entry.getKey(), statusSpy);
        }
        subtransExecutor.execute(rows);
        for (Map.Entry<String, StepStatus> entry : statuses.entrySet()) {
            Mockito.verify(entry.getValue()).updateAll(ArgumentMatchers.any());
        }
        Mockito.verify(parentTrans, Mockito.atLeastOnce()).addActiveSubTransformation(ArgumentMatchers.eq("subtransname"), ArgumentMatchers.any(Trans.class));
    }

    @Test
    public void stopsAll() throws KettleException {
        TransMeta parentMeta = new TransMeta(this.getClass().getResource("subtrans-executor-parent.ktr").getPath(), new Variables());
        TransMeta subMeta = new TransMeta(this.getClass().getResource("subtrans-executor-sub.ktr").getPath(), new Variables());
        LoggingObjectInterface loggingObject = new LoggingObject("anything");
        Trans parentTrans = new Trans(parentMeta, loggingObject);
        SubtransExecutor subtransExecutor = new SubtransExecutor("subtransname", parentTrans, subMeta, true, new TransExecutorParameters(), "");
        subtransExecutor.running = Mockito.spy(subtransExecutor.running);
        RowMetaInterface rowMeta = parentMeta.getStepFields("Data Grid");
        List<RowMetaAndData> rows = Arrays.asList(new RowMetaAndData(rowMeta, "Pentaho", 1L), new RowMetaAndData(rowMeta, "Pentaho", 2L), new RowMetaAndData(rowMeta, "Pentaho", 3L), new RowMetaAndData(rowMeta, "Pentaho", 4L));
        subtransExecutor.execute(rows);
        Mockito.verify(subtransExecutor.running).add(ArgumentMatchers.any());
        subtransExecutor.stop();
        Assert.assertTrue(subtransExecutor.running.isEmpty());
    }

    @Test
    public void doesNotExecuteWhenStopped() throws KettleException {
        TransMeta parentMeta = new TransMeta(this.getClass().getResource("subtrans-executor-parent.ktr").getPath(), new Variables());
        TransMeta subMeta = new TransMeta(this.getClass().getResource("subtrans-executor-sub.ktr").getPath(), new Variables());
        LoggingObjectInterface loggingObject = new LoggingObject("anything");
        Trans parentTrans = new Trans(parentMeta, loggingObject);
        SubtransExecutor subtransExecutor = new SubtransExecutor("subtransname", parentTrans, subMeta, true, new TransExecutorParameters(), "");
        RowMetaInterface rowMeta = parentMeta.getStepFields("Data Grid");
        List<RowMetaAndData> rows = Arrays.asList(new RowMetaAndData(rowMeta, "Pentaho", 1L), new RowMetaAndData(rowMeta, "Pentaho", 2L), new RowMetaAndData(rowMeta, "Pentaho", 3L), new RowMetaAndData(rowMeta, "Pentaho", 4L));
        subtransExecutor.stop();
        subtransExecutor.execute(rows);
        Mockito.verify(this.logChannel, Mockito.never()).logBasic(("\n" + (((("------------> Linenr 1------------------------------\n" + "name = Pentaho\n") + "sum = 10\n") + "\n") + "====================")));
    }
}

