/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon;


import junit.framework.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.ChannelLogTable;
import org.pentaho.di.core.logging.JobEntryLogTable;
import org.pentaho.di.core.logging.JobLogTable;
import org.pentaho.di.core.logging.MetricsLogTable;
import org.pentaho.di.core.logging.PerformanceLogTable;
import org.pentaho.di.core.logging.StepLogTable;
import org.pentaho.di.core.logging.TransLogTable;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.HasDatabasesInterface;
import org.pentaho.di.trans.TransMeta;


public class SpoonExportXmlTest {
    private VariableSpace mockedVariableSpace;

    private HasDatabasesInterface mockedHasDbInterface;

    private static String PARAM_START_SYMBOL = "${";

    private static String PARAM_END_SYMBOL = "}";

    private static String GLOBAL_PARAM = ((SpoonExportXmlTest.PARAM_START_SYMBOL) + (Const.KETTLE_STEP_LOG_SCHEMA)) + (SpoonExportXmlTest.PARAM_END_SYMBOL);

    private static String USER_PARAM = ((SpoonExportXmlTest.PARAM_START_SYMBOL) + "param-content") + (SpoonExportXmlTest.PARAM_END_SYMBOL);

    private static String HARDCODED_VALUE = "hardcoded";

    private final Spoon spoon = Mockito.mock(Spoon.class);

    @Test
    public void savingTransToXmlNotChangesLogTables() {
        TransMeta transMeta = new TransMeta();
        initTables(transMeta);
        TransLogTable originTransLogTable = transMeta.getTransLogTable();
        StepLogTable originStepLogTable = transMeta.getStepLogTable();
        PerformanceLogTable originPerformanceLogTable = transMeta.getPerformanceLogTable();
        ChannelLogTable originChannelLogTable = transMeta.getChannelLogTable();
        MetricsLogTable originMetricsLogTable = transMeta.getMetricsLogTable();
        Mockito.when(spoon.getActiveTransformation()).thenReturn(transMeta);
        Mockito.when(spoon.saveXMLFile(ArgumentMatchers.any(TransMeta.class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(spoon.saveXMLFile(ArgumentMatchers.anyBoolean())).thenCallRealMethod();
        spoon.saveXMLFile(true);
        tablesCommonValuesEqual(originTransLogTable, transMeta.getTransLogTable());
        Assert.assertEquals(originTransLogTable.getLogInterval(), transMeta.getTransLogTable().getLogInterval());
        Assert.assertEquals(originTransLogTable.getLogSizeLimit(), transMeta.getTransLogTable().getLogSizeLimit());
        tablesCommonValuesEqual(originStepLogTable, transMeta.getStepLogTable());
        tablesCommonValuesEqual(originPerformanceLogTable, transMeta.getPerformanceLogTable());
        Assert.assertEquals(originPerformanceLogTable.getLogInterval(), transMeta.getPerformanceLogTable().getLogInterval());
        tablesCommonValuesEqual(originChannelLogTable, transMeta.getChannelLogTable());
        tablesCommonValuesEqual(originMetricsLogTable, transMeta.getMetricsLogTable());
    }

    @Test
    public void savingJobToXmlNotChangesLogTables() {
        JobMeta jobMeta = new JobMeta();
        initTables(jobMeta);
        JobLogTable originJobLogTable = jobMeta.getJobLogTable();
        JobEntryLogTable originJobEntryLogTable = jobMeta.getJobEntryLogTable();
        ChannelLogTable originChannelLogTable = jobMeta.getChannelLogTable();
        Mockito.when(spoon.getActiveTransformation()).thenReturn(null);
        Mockito.when(spoon.getActiveJob()).thenReturn(jobMeta);
        Mockito.when(spoon.saveXMLFile(ArgumentMatchers.any(JobMeta.class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(spoon.saveXMLFile(ArgumentMatchers.anyBoolean())).thenCallRealMethod();
        spoon.saveXMLFile(true);
        tablesCommonValuesEqual(originJobLogTable, jobMeta.getJobLogTable());
        Assert.assertEquals(originJobLogTable.getLogInterval(), jobMeta.getJobLogTable().getLogInterval());
        Assert.assertEquals(originJobLogTable.getLogSizeLimit(), jobMeta.getJobLogTable().getLogSizeLimit());
        tablesCommonValuesEqual(originJobEntryLogTable, jobMeta.getJobEntryLogTable());
        tablesCommonValuesEqual(originChannelLogTable, jobMeta.getChannelLogTable());
    }
}

