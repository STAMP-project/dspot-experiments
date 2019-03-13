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
package org.pentaho.di.trans.steps.nullif;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for NullIf step
 *
 * @author Ivan Pogodin
 * @see NullIf
 */
public class NullIfTest {
    StepMockHelper<NullIfMeta, NullIfData> smh;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws KettleException {
        KettleEnvironment.init();
        NullIf step = new NullIf(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(smh.initStepMetaInterface, smh.stepDataInterface);
        step.setInputRowMeta(getInputRowMeta());
        step.addRowSetToInputRowSets(mockInputRowSet());
        step.addRowSetToOutputRowSets(new QueueRowSet());
        boolean hasMoreRows;
        do {
            hasMoreRows = step.processRow(mockProcessRowMeta(), smh.processRowsStepDataInterface);
        } while (hasMoreRows );
        RowSet outputRowSet = step.getOutputRowSets().get(0);
        Object[] actualRow = outputRowSet.getRow();
        Object[] expectedRow = new Object[]{ "value1", null, "value3" };
        Assert.assertEquals("Output row is of an unexpected length", expectedRow.length, outputRowSet.getRowMeta().size());
        for (int i = 0; i < (expectedRow.length); i++) {
            Assert.assertEquals(("Unexpected output value at index " + i), expectedRow[i], actualRow[i]);
        }
    }

    @Test
    public void testDateWithFormat() throws KettleException {
        KettleEnvironment.init();
        NullIf step = new NullIf(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.init(smh.initStepMetaInterface, smh.stepDataInterface);
        step.setInputRowMeta(getInputRowMeta2());
        Date d1 = null;
        Date d2 = null;
        Date d3 = null;
        Date d4 = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            d1 = formatter.parse("20150606");
            d3 = formatter.parse("20150607");
            formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            d2 = formatter.parse("2015/06/06 00:00:00.000");
            d4 = formatter.parse("2015/07/06 00:00:00.000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        step.addRowSetToInputRowSets(smh.getMockInputRowSet(new Object[][]{ new Object[]{ d1, d2, d3, d4 } }));
        step.addRowSetToOutputRowSets(new QueueRowSet());
        boolean hasMoreRows;
        do {
            hasMoreRows = step.processRow(mockProcessRowMeta2(), smh.processRowsStepDataInterface);
        } while (hasMoreRows );
        RowSet outputRowSet = step.getOutputRowSets().get(0);
        Object[] actualRow = outputRowSet.getRow();
        Object[] expectedRow = new Object[]{ null, null, d3, d4 };
        Assert.assertEquals("Output row is of an unexpected length", expectedRow.length, outputRowSet.getRowMeta().size());
        for (int i = 0; i < (expectedRow.length); i++) {
            Assert.assertEquals(("Unexpected output value at index " + i), expectedRow[i], actualRow[i]);
        }
    }
}

