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
package org.pentaho.di.ui.spoon.delegates;


import LogLevel.BASIC;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.TransLogTable;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.di.ui.spoon.trans.TransLogDelegate;


public class SpoonTransformationDelegateTest {
    private static final String[] EMPTY_STRING_ARRAY = new String[]{  };

    private static final String TEST_PARAM_KEY = "paramKey";

    private static final String TEST_PARAM_VALUE = "paramValue";

    private static final Map<String, String> MAP_WITH_TEST_PARAM = new HashMap<String, String>() {
        {
            put(SpoonTransformationDelegateTest.TEST_PARAM_KEY, SpoonTransformationDelegateTest.TEST_PARAM_VALUE);
        }
    };

    private static final LogLevel TEST_LOG_LEVEL = LogLevel.BASIC;

    private static final boolean TEST_BOOLEAN_PARAM = true;

    private SpoonTransformationDelegate delegate;

    private Spoon spoon;

    private TransLogTable transLogTable;

    private TransMeta transMeta;

    private List<TransMeta> transformationMap;

    @Test
    public void testIsLogTableDefinedLogTableDefined() {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.doReturn(databaseMeta).when(transLogTable).getDatabaseMeta();
        Mockito.doReturn("test_table").when(transLogTable).getTableName();
        Assert.assertTrue(delegate.isLogTableDefined(transLogTable));
    }

    @Test
    public void testIsLogTableDefinedLogTableNotDefined() {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.doReturn(databaseMeta).when(transLogTable).getDatabaseMeta();
        Assert.assertFalse(delegate.isLogTableDefined(transLogTable));
    }

    @Test
    public void testAddAndCloseTransformation() {
        Mockito.doCallRealMethod().when(delegate).closeTransformation(ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(delegate).addTransformation(ArgumentMatchers.any());
        Assert.assertTrue(delegate.addTransformation(transMeta));
        Assert.assertFalse(delegate.addTransformation(transMeta));
        delegate.closeTransformation(transMeta);
        Assert.assertTrue(delegate.addTransformation(transMeta));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testSetParamsIntoMetaInExecuteTransformation() throws KettleException {
        Mockito.doCallRealMethod().when(delegate).executeTransformation(transMeta, true, false, false, false, false, null, false, BASIC);
        RowMetaInterface rowMeta = Mockito.mock(RowMetaInterface.class);
        TransExecutionConfiguration transExecutionConfiguration = Mockito.mock(TransExecutionConfiguration.class);
        TransGraph activeTransGraph = Mockito.mock(TransGraph.class);
        activeTransGraph.transLogDelegate = Mockito.mock(TransLogDelegate.class);
        Mockito.doReturn(rowMeta).when(spoon.variables).getRowMeta();
        Mockito.doReturn(SpoonTransformationDelegateTest.EMPTY_STRING_ARRAY).when(rowMeta).getFieldNames();
        Mockito.doReturn(transExecutionConfiguration).when(spoon).getTransExecutionConfiguration();
        Mockito.doReturn(SpoonTransformationDelegateTest.MAP_WITH_TEST_PARAM).when(transExecutionConfiguration).getParams();
        Mockito.doReturn(activeTransGraph).when(spoon).getActiveTransGraph();
        Mockito.doReturn(SpoonTransformationDelegateTest.TEST_LOG_LEVEL).when(transExecutionConfiguration).getLogLevel();
        Mockito.doReturn(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM).when(transExecutionConfiguration).isClearingLog();
        Mockito.doReturn(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM).when(transExecutionConfiguration).isSafeModeEnabled();
        Mockito.doReturn(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM).when(transExecutionConfiguration).isGatheringMetrics();
        delegate.executeTransformation(transMeta, true, false, false, false, false, null, false, BASIC);
        Mockito.verify(transMeta).setParameterValue(SpoonTransformationDelegateTest.TEST_PARAM_KEY, SpoonTransformationDelegateTest.TEST_PARAM_VALUE);
        Mockito.verify(transMeta).activateParameters();
        Mockito.verify(transMeta).setLogLevel(SpoonTransformationDelegateTest.TEST_LOG_LEVEL);
        Mockito.verify(transMeta).setClearingLog(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM);
        Mockito.verify(transMeta).setSafeModeEnabled(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM);
        Mockito.verify(transMeta).setGatheringMetrics(SpoonTransformationDelegateTest.TEST_BOOLEAN_PARAM);
    }
}

