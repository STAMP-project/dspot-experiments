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
package org.pentaho.di.trans.steps.salesforceinput;


import SalesforceConnectionUtils.recordsFilterCode.length;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.IntLoadSaveValidator;
import org.pentaho.di.trans.steps.salesforce.SalesforceMetaTest;
import org.pentaho.di.trans.steps.salesforce.SalesforceStepMeta;


public class SalesforceInputMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testErrorHandling() {
        SalesforceStepMeta meta = new SalesforceInputMeta();
        Assert.assertFalse(meta.supportsErrorHandling());
    }

    @Test
    public void testSalesforceInputMeta() throws KettleException {
        List<String> attributes = new ArrayList<String>();
        attributes.addAll(SalesforceMetaTest.getDefaultAttributes());
        attributes.addAll(Arrays.asList("inputFields", "condition", "query", "specifyQuery", "includeTargetURL", "targetURLField", "includeModule", "moduleField", "includeRowNumber", "includeDeletionDate", "deletionDateField", "rowNumberField", "includeSQL", "sqlField", "includeTimestamp", "timestampField", "readFrom", "readTo", "recordsFilter", "queryAll", "rowLimit"));
        Map<String, String> getterMap = new HashMap<String, String>();
        Map<String, String> setterMap = new HashMap<String, String>();
        getterMap.put("includeTargetURL", "includeTargetURL");
        getterMap.put("includeModule", "includeModule");
        getterMap.put("includeRowNumber", "includeRowNumber");
        getterMap.put("includeDeletionDate", "includeDeletionDate");
        getterMap.put("includeSQL", "includeSQL");
        getterMap.put("sqlField", "getSQLField");
        setterMap.put("sqlField", "setSQLField");
        getterMap.put("includeTimestamp", "includeTimestamp");
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidators = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidators.put("inputFields", new org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator<SalesforceInputField>(new SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator(), 50));
        fieldLoadSaveValidators.put("recordsFilter", new SalesforceInputMetaTest.RecordsFilterLoadSaveValidator());
        LoadSaveTester loadSaveTester = new LoadSaveTester(SalesforceInputMeta.class, attributes, getterMap, setterMap, fieldLoadSaveValidators, new HashMap<String, FieldLoadSaveValidator<?>>());
        loadSaveTester.testRepoRoundTrip();
        loadSaveTester.testXmlRoundTrip();
    }

    @Test
    public void testGetFields() throws KettleStepException {
        SalesforceInputMeta meta = new SalesforceInputMeta();
        meta.setDefault();
        RowMetaInterface r = new RowMeta();
        meta.getFields(r, "thisStep", null, null, new Variables(), null, null);
        Assert.assertEquals(0, r.size());
        meta.setInputFields(new SalesforceInputField[]{ new SalesforceInputField("field1") });
        r.clear();
        meta.getFields(r, "thisStep", null, null, new Variables(), null, null);
        Assert.assertEquals(1, r.size());
        meta.setIncludeDeletionDate(true);
        meta.setDeletionDateField("DeletionDate");
        meta.setIncludeModule(true);
        meta.setModuleField("ModuleName");
        meta.setIncludeRowNumber(true);
        meta.setRowNumberField("RN");
        meta.setIncludeSQL(true);
        meta.setSQLField("sqlField");
        meta.setIncludeTargetURL(true);
        meta.setTargetURLField("Target");
        meta.setIncludeTimestamp(true);
        meta.setTimestampField("TS");
        r.clear();
        meta.getFields(r, "thisStep", null, null, new Variables(), null, null);
        Assert.assertEquals(7, r.size());
        Assert.assertTrue(((r.indexOfValue("field1")) >= 0));
        Assert.assertTrue(((r.indexOfValue("DeletionDate")) >= 0));
        Assert.assertTrue(((r.indexOfValue("ModuleName")) >= 0));
        Assert.assertTrue(((r.indexOfValue("RN")) >= 0));
        Assert.assertTrue(((r.indexOfValue("sqlField")) >= 0));
        Assert.assertTrue(((r.indexOfValue("Target")) >= 0));
        Assert.assertTrue(((r.indexOfValue("TS")) >= 0));
    }

    @Test
    public void testCheck() {
        SalesforceInputMeta meta = new SalesforceInputMeta();
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
        meta.setInputFields(new SalesforceInputField[]{ new SalesforceInputField("test") });
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertFalse(hasError);
        remarks.clear();
        meta.setDefault();
        meta.setUsername("user");
        meta.setIncludeDeletionDate(true);
        meta.setIncludeModule(true);
        meta.setIncludeRowNumber(true);
        meta.setIncludeSQL(true);
        meta.setIncludeTargetURL(true);
        meta.setIncludeTimestamp(true);
        meta.setInputFields(new SalesforceInputField[]{ new SalesforceInputField("test") });
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        int errorCount = 0;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
                errorCount++;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertTrue(hasError);
        Assert.assertEquals(6, errorCount);
        remarks.clear();
        meta.setDefault();
        meta.setUsername("user");
        meta.setIncludeDeletionDate(true);
        meta.setDeletionDateField("delDate");
        meta.setIncludeModule(true);
        meta.setModuleField("mod");
        meta.setIncludeRowNumber(true);
        meta.setRowNumberField("rownum");
        meta.setIncludeSQL(true);
        meta.setSQLField("theSQL");
        meta.setIncludeTargetURL(true);
        meta.setTargetURLField("theURL");
        meta.setIncludeTimestamp(true);
        meta.setTimestampField("ts_Field");
        meta.setInputFields(new SalesforceInputField[]{ new SalesforceInputField("test") });
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
                errorCount++;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertFalse(hasError);
    }

    public static class RecordsFilterLoadSaveValidator extends IntLoadSaveValidator {
        @Override
        public Integer getTestObject() {
            return new Random().nextInt(length);
        }
    }

    public static class SalesforceInputFieldLoadSaveValidator implements FieldLoadSaveValidator<SalesforceInputField> {
        static final Random rnd = new Random();

        @Override
        public SalesforceInputField getTestObject() {
            SalesforceInputField retval = new SalesforceInputField();
            retval.setName(UUID.randomUUID().toString());
            retval.setField(UUID.randomUUID().toString());
            retval.setIdLookup(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextBoolean());
            retval.setType(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextInt(ValueMetaFactory.getAllValueMetaNames().length));
            retval.setFormat(UUID.randomUUID().toString());
            retval.setCurrencySymbol(UUID.randomUUID().toString());
            retval.setDecimalSymbol(UUID.randomUUID().toString());
            retval.setGroupSymbol(UUID.randomUUID().toString());
            retval.setLength(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextInt());
            retval.setPrecision(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextInt());
            retval.setTrimType(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextInt(SalesforceInputField.trimTypeCode.length));
            retval.setRepeated(SalesforceInputMetaTest.SalesforceInputFieldLoadSaveValidator.rnd.nextBoolean());
            return retval;
        }

        @Override
        public boolean validateTestObject(SalesforceInputField testObject, Object actual) {
            if (!(actual instanceof SalesforceInputField)) {
                return false;
            }
            SalesforceInputField sfActual = ((SalesforceInputField) (actual));
            if ((((((((((((!(sfActual.getName().equals(testObject.getName()))) || (!(sfActual.getField().equals(testObject.getField())))) || ((sfActual.isIdLookup()) != (testObject.isIdLookup()))) || ((sfActual.getType()) != (testObject.getType()))) || (!(sfActual.getFormat().equals(testObject.getFormat())))) || (!(sfActual.getCurrencySymbol().equals(testObject.getCurrencySymbol())))) || (!(sfActual.getDecimalSymbol().equals(testObject.getDecimalSymbol())))) || (!(sfActual.getGroupSymbol().equals(testObject.getGroupSymbol())))) || ((sfActual.getLength()) != (testObject.getLength()))) || ((sfActual.getPrecision()) != (testObject.getPrecision()))) || ((sfActual.getTrimType()) != (testObject.getTrimType()))) || ((sfActual.isRepeated()) != (testObject.isRepeated()))) {
                return false;
            }
            return true;
        }
    }
}

