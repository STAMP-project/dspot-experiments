/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.ifnull;


import Const.KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests for IfNull step
 *
 * @author Ivan Pogodin
 * @see IfNull
 */
@RunWith(PowerMockRunner.class)
public class IfNullTest {
    StepMockHelper<IfNullMeta, IfNullData> smh;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testString_emptyIsNull() throws KettleException {
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "N");
        IfNull step = new IfNull(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(smh.initStepMetaInterface, smh.stepDataInterface);
        final RowMeta inputRowMeta = // 
        // 
        // 
        // 
        // 
        // 
        buildInputRowMeta(new ValueMetaString("some-field"), new ValueMetaString("null-field"), new ValueMetaString("empty-field"), new ValueMetaString("space-field"), new ValueMetaString("another-field"));
        step.setInputRowMeta(inputRowMeta);
        final Object[] inputRow = new Object[]{ "value1", null, "", "    ", "value5" };
        final Object[] expectedRow = new Object[]{ "value1", "replace-value", "replace-value", "    ", "value5" };
        step.addRowSetToInputRowSets(buildInputRowSet(inputRow));
        step.addRowSetToOutputRowSets(new QueueRowSet());
        boolean hasMoreRows;
        do {
            hasMoreRows = step.processRow(mockProcessRowMeta(), smh.processRowsStepDataInterface);
        } while (hasMoreRows );
        RowSet outputRowSet = step.getOutputRowSets().get(0);
        assertRowSetMatches("", expectedRow, outputRowSet);
    }

    @Test
    public void testString_emptyIsNotNull() throws KettleException {
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "Y");
        IfNull step = new IfNull(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(smh.initStepMetaInterface, smh.stepDataInterface);
        final RowMeta inputRowMeta = // 
        // 
        // 
        // 
        // 
        // 
        buildInputRowMeta(new ValueMetaString("some-field"), new ValueMetaString("null-field"), new ValueMetaString("empty-field"), new ValueMetaString("space-field"), new ValueMetaString("another-field"));
        step.setInputRowMeta(inputRowMeta);
        final Object[] inputRow = new Object[]{ "value1", null, "", "    ", "value5" };
        final Object[] expectedRow = new Object[]{ "value1", "replace-value", "", "    ", "value5" };
        step.addRowSetToInputRowSets(buildInputRowSet(inputRow));
        step.addRowSetToOutputRowSets(new QueueRowSet());
        boolean hasMoreRows;
        do {
            hasMoreRows = step.processRow(mockProcessRowMeta(), smh.processRowsStepDataInterface);
        } while (hasMoreRows );
        RowSet outputRowSet = step.getOutputRowSets().get(0);
        assertRowSetMatches("", expectedRow, outputRowSet);
    }
}

