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
package org.pentaho.di.trans.steps.salesforce;


import ValueMetaInterface.TYPE_INTEGER;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class SalesforceStepTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMockHelper<SalesforceStepMeta, SalesforceStepData> smh;

    @Test
    public void testErrorHandling() {
        SalesforceStepMeta meta = Mockito.mock(SalesforceStepMeta.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertFalse(meta.supportsErrorHandling());
    }

    @Test
    public void testInitDispose() {
        SalesforceStepMeta meta = Mockito.mock(SalesforceStepMeta.class, Mockito.CALLS_REAL_METHODS);
        SalesforceStep step = Mockito.spy(new SalesforceStepTest.MockSalesforceStep(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans));
        /* Salesforce Step should fail if username and password are not set
        We should not set a default account for all users
         */
        meta.setDefault();
        Assert.assertFalse(step.init(meta, smh.stepDataInterface));
        meta.setDefault();
        meta.setTargetURL(null);
        Assert.assertFalse(step.init(meta, smh.stepDataInterface));
        meta.setDefault();
        meta.setUsername("anonymous");
        Assert.assertFalse(step.init(meta, smh.stepDataInterface));
        meta.setDefault();
        meta.setUsername("anonymous");
        meta.setPassword("myPwd");
        meta.setModule(null);
        Assert.assertFalse(step.init(meta, smh.stepDataInterface));
        /* After setting username and password, we should have enough defaults to properly init */
        meta.setDefault();
        meta.setUsername("anonymous");
        meta.setPassword("myPwd");
        Assert.assertTrue(step.init(meta, smh.stepDataInterface));
        // Dispose check
        Assert.assertNotNull(smh.stepDataInterface.connection);
        step.dispose(meta, smh.stepDataInterface);
        Assert.assertNull(smh.stepDataInterface.connection);
    }

    class MockSalesforceStep extends SalesforceStep {
        public MockSalesforceStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }
    }

    @Test
    public void createIntObjectTest() throws KettleValueException {
        SalesforceStep step = Mockito.spy(new SalesforceStepTest.MockSalesforceStep(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans));
        ValueMetaInterface valueMeta = Mockito.mock(ValueMetaInterface.class);
        Mockito.when(valueMeta.getType()).thenReturn(TYPE_INTEGER);
        Object value = step.normalizeValue(valueMeta, 100L);
        Assert.assertTrue((value instanceof Integer));
    }

    @Test
    public void createDateObjectTest() throws ParseException, KettleValueException {
        SalesforceStep step = Mockito.spy(new SalesforceStepTest.MockSalesforceStep(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans));
        ValueMetaInterface valueMeta = Mockito.mock(ValueMetaInterface.class);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = dateFormat.parse("12-10-2017 15:10:25");
        Mockito.when(valueMeta.isDate()).thenReturn(true);
        Mockito.when(valueMeta.getDateFormatTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        Mockito.when(valueMeta.getDate(Mockito.eq(date))).thenReturn(date);
        Object value = step.normalizeValue(valueMeta, date);
        Assert.assertTrue((value instanceof Calendar));
        DateFormat minutesDateFormat = new SimpleDateFormat("mm:ss");
        // check not missing minutes and seconds
        Assert.assertEquals(minutesDateFormat.format(date), minutesDateFormat.format(((Calendar) (value)).getTime()));
    }
}

