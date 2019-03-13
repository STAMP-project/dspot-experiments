/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon.delegates;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.logging.JobLogTable;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.ClassLoadingPluginInterface;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.ui.job.dialog.JobExecutionConfigurationDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.job.JobLogDelegate;


public class SpoonJobDelegateTest {
    private static final String[] EMPTY_STRING_ARRAY = new String[]{  };

    private static final String TEST_VARIABLE_KEY = "variableKey";

    private static final String TEST_VARIABLE_VALUE = "variableValue";

    private static final Map<String, String> MAP_WITH_TEST_VARIABLE = new HashMap<String, String>() {
        {
            put(SpoonJobDelegateTest.TEST_VARIABLE_KEY, SpoonJobDelegateTest.TEST_VARIABLE_VALUE);
        }
    };

    private static final String TEST_PARAM_KEY = "paramKey";

    private static final String TEST_PARAM_VALUE = "paramValue";

    private static final Map<String, String> MAP_WITH_TEST_PARAM = new HashMap<String, String>() {
        {
            put(SpoonJobDelegateTest.TEST_PARAM_KEY, SpoonJobDelegateTest.TEST_PARAM_VALUE);
        }
    };

    private static final LogLevel TEST_LOG_LEVEL = LogLevel.BASIC;

    private static final String TEST_START_COPY_NAME = "startCopyName";

    private static final boolean TEST_BOOLEAN_PARAM = true;

    private SpoonJobDelegate delegate;

    private Spoon spoon;

    private JobLogTable jobLogTable;

    private JobMeta jobMeta;

    private ArrayList<JobMeta> jobMap;

    @Test
    public void testAddAndCloseTransformation() {
        Mockito.doCallRealMethod().when(delegate).closeJob(ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(delegate).addJob(ArgumentMatchers.any());
        Assert.assertTrue(delegate.addJob(jobMeta));
        Assert.assertFalse(delegate.addJob(jobMeta));
        delegate.closeJob(jobMeta);
        Assert.assertTrue(delegate.addJob(jobMeta));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testSetParamsIntoMetaInExecuteJob() throws KettleException {
        Mockito.doCallRealMethod().when(delegate).executeJob(jobMeta, true, false, null, false, null, 0);
        JobExecutionConfiguration jobExecutionConfiguration = Mockito.mock(JobExecutionConfiguration.class);
        RowMetaInterface rowMeta = Mockito.mock(RowMetaInterface.class);
        Shell shell = Mockito.mock(Shell.class);
        JobExecutionConfigurationDialog jobExecutionConfigurationDialog = Mockito.mock(JobExecutionConfigurationDialog.class);
        JobGraph activeJobGraph = Mockito.mock(JobGraph.class);
        activeJobGraph.jobLogDelegate = Mockito.mock(JobLogDelegate.class);
        Mockito.doReturn(jobExecutionConfiguration).when(spoon).getJobExecutionConfiguration();
        Mockito.doReturn(rowMeta).when(spoon.variables).getRowMeta();
        Mockito.doReturn(SpoonJobDelegateTest.EMPTY_STRING_ARRAY).when(rowMeta).getFieldNames();
        Mockito.doReturn(shell).when(spoon).getShell();
        Mockito.doReturn(jobExecutionConfigurationDialog).when(delegate).newJobExecutionConfigurationDialog(jobExecutionConfiguration, jobMeta);
        Mockito.doReturn(activeJobGraph).when(spoon).getActiveJobGraph();
        Mockito.doReturn(SpoonJobDelegateTest.MAP_WITH_TEST_VARIABLE).when(jobExecutionConfiguration).getVariables();
        Mockito.doReturn(SpoonJobDelegateTest.MAP_WITH_TEST_PARAM).when(jobExecutionConfiguration).getParams();
        Mockito.doReturn(SpoonJobDelegateTest.TEST_LOG_LEVEL).when(jobExecutionConfiguration).getLogLevel();
        Mockito.doReturn(SpoonJobDelegateTest.TEST_START_COPY_NAME).when(jobExecutionConfiguration).getStartCopyName();
        Mockito.doReturn(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM).when(jobExecutionConfiguration).isClearingLog();
        Mockito.doReturn(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM).when(jobExecutionConfiguration).isSafeModeEnabled();
        Mockito.doReturn(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM).when(jobExecutionConfiguration).isExpandingRemoteJob();
        delegate.executeJob(jobMeta, true, false, null, false, null, 0);
        Mockito.verify(jobMeta).setVariable(SpoonJobDelegateTest.TEST_VARIABLE_KEY, SpoonJobDelegateTest.TEST_VARIABLE_VALUE);
        Mockito.verify(jobMeta).setParameterValue(SpoonJobDelegateTest.TEST_PARAM_KEY, SpoonJobDelegateTest.TEST_PARAM_VALUE);
        Mockito.verify(jobMeta).activateParameters();
        Mockito.verify(jobMeta).setLogLevel(SpoonJobDelegateTest.TEST_LOG_LEVEL);
        Mockito.verify(jobMeta).setStartCopyName(SpoonJobDelegateTest.TEST_START_COPY_NAME);
        Mockito.verify(jobMeta).setClearingLog(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM);
        Mockito.verify(jobMeta).setSafeModeEnabled(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM);
        Mockito.verify(jobMeta).setExpandingRemoteJob(SpoonJobDelegateTest.TEST_BOOLEAN_PARAM);
    }

    @Test
    public void testGetJobEntryDialogClass() throws KettlePluginException {
        PluginRegistry registry = PluginRegistry.getInstance();
        SpoonJobDelegateTest.PluginMockInterface plugin = Mockito.mock(SpoonJobDelegateTest.PluginMockInterface.class);
        Mockito.when(getIds()).thenReturn(new String[]{ "mockJobPlugin" });
        Mockito.when(matches("mockJobPlugin")).thenReturn(true);
        Mockito.when(getName()).thenReturn("mockJobPlugin");
        JobEntryInterface jobEntryInterface = Mockito.mock(JobEntryInterface.class);
        Mockito.when(jobEntryInterface.getDialogClassName()).thenReturn(String.class.getName());
        Mockito.when(getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(JobEntryInterface.class, getName());
                put(JobEntryDialogInterface.class, JobEntryDialogInterface.class.getName());
            }
        });
        registry.registerPlugin(JobEntryPluginType.class, plugin);
        SpoonJobDelegate delegate = Mockito.mock(SpoonJobDelegate.class);
        Spoon spoon = Mockito.mock(Spoon.class);
        delegate.spoon = spoon;
        delegate.log = Mockito.mock(LogChannelInterface.class);
        Mockito.when(spoon.getShell()).thenReturn(Mockito.mock(Shell.class));
        Mockito.doCallRealMethod().when(delegate).getJobEntryDialog(ArgumentMatchers.any(JobEntryInterface.class), ArgumentMatchers.any(JobMeta.class));
        JobMeta meta = Mockito.mock(JobMeta.class);
        // verify that dialog class is requested from plugin
        try {
            delegate.getJobEntryDialog(jobEntryInterface, meta);// exception is expected here

        } catch (Throwable ignore) {
            Mockito.verify(jobEntryInterface, Mockito.never()).getDialogClassName();
        }
        // verify that the deprecated way is still valid
        Mockito.when(getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(JobEntryInterface.class, getName());
            }
        });
        try {
            delegate.getJobEntryDialog(jobEntryInterface, meta);// exception is expected here

        } catch (Throwable ignore) {
            Mockito.verify(jobEntryInterface, Mockito.times(1)).getDialogClassName();
        }
        // cleanup
        registry.removePlugin(JobEntryPluginType.class, plugin);
    }

    public interface PluginMockInterface extends ClassLoadingPluginInterface , PluginInterface {}
}

