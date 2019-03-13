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
package org.pentaho.di.trans.steps.salesforceupsert;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.BooleanLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;
import org.pentaho.di.trans.steps.salesforce.SalesforceMetaTest;
import org.pentaho.di.trans.steps.salesforce.SalesforceStepMeta;


public class SalesforceUpsertMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testErrorHandling() {
        SalesforceStepMeta meta = new SalesforceUpsertMeta();
        Assert.assertTrue(meta.supportsErrorHandling());
    }

    @Test
    public void testBatchSize() {
        SalesforceUpsertMeta meta = new SalesforceUpsertMeta();
        meta.setBatchSize("20");
        Assert.assertEquals("20", meta.getBatchSize());
        Assert.assertEquals(20, meta.getBatchSizeInt());
        // Pass invalid batch size, should get default value of 10
        meta.setBatchSize("unknown");
        Assert.assertEquals("unknown", meta.getBatchSize());
        Assert.assertEquals(10, meta.getBatchSizeInt());
    }

    @Test
    public void testGetFields() throws KettleStepException {
        SalesforceUpsertMeta meta = new SalesforceUpsertMeta();
        meta.setDefault();
        RowMetaInterface r = new RowMeta();
        meta.getFields(r, "thisStep", null, null, new Variables(), null, null);
        Assert.assertEquals(1, r.size());
        Assert.assertEquals("Id", r.getFieldNames()[0]);
        meta.setSalesforceIDFieldName("id_field");
        r.clear();
        meta.getFields(r, "thisStep", null, null, new Variables(), null, null);
        Assert.assertEquals(1, r.size());
        Assert.assertEquals("id_field", r.getFieldNames()[0]);
    }

    @Test
    public void testCheck() {
        SalesforceUpsertMeta meta = new SalesforceUpsertMeta();
        meta.setDefault();
        List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        boolean hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertTrue(hasError);
        remarks.clear();
        meta.setDefault();
        meta.setUsername("user");
        meta.setUpdateLookup(new String[]{ "SalesforceField" });
        meta.setUpdateStream(new String[]{ "StreamField" });
        meta.setUseExternalId(new Boolean[]{ false });
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertFalse(hasError);
    }

    @Test
    public void testSalesforceUpsertMeta() throws KettleException {
        List<String> attributes = new ArrayList<String>();
        attributes.addAll(SalesforceMetaTest.getDefaultAttributes());
        attributes.addAll(Arrays.asList("upsertField", "batchSize", "salesforceIDFieldName", "updateLookup", "updateStream", "useExternalId", "rollbackAllChangesOnError"));
        Map<String, String> getterMap = new HashMap<String, String>();
        Map<String, String> setterMap = new HashMap<String, String>();
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidators = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidators.put("updateLookup", new org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 50));
        fieldLoadSaveValidators.put("updateStream", new org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 50));
        fieldLoadSaveValidators.put("useExternalId", new org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator<Boolean>(new BooleanLoadSaveValidator(), 50));
        LoadSaveTester loadSaveTester = new LoadSaveTester(SalesforceUpsertMeta.class, attributes, getterMap, setterMap, fieldLoadSaveValidators, new HashMap<String, FieldLoadSaveValidator<?>>());
        loadSaveTester.testRepoRoundTrip();
        loadSaveTester.testXmlRoundTrip();
    }
}

