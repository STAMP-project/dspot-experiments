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
package org.pentaho.di.trans.steps.textfileoutput;


import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class TextFileOutputMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testRoundTrip() throws KettleException {
        LoadSaveTester<TextFileOutputMeta> loadSaveTester = new LoadSaveTester<TextFileOutputMeta>(TextFileOutputMeta.class, TextFileOutputMetaTest.getMetaAttributes(), TextFileOutputMetaTest.getGetterMap(), TextFileOutputMetaTest.getSetterMap(), TextFileOutputMetaTest.getAttributeValidators(), TextFileOutputMetaTest.getTypeValidators());
        loadSaveTester.testSerialization();
    }

    @Test
    public void testVarReplaceSplit() throws Exception {
        TextFileOutputMeta meta = new TextFileOutputMeta();
        meta.setDefault();
        meta.setSplitEveryRows("${splitVar}");
        VariableSpace varSpace = new Variables();
        Assert.assertEquals(0, meta.getSplitEvery(varSpace));
        String fileName = meta.buildFilename("foo", "txt2", varSpace, 0, null, 3, false, meta);
        Assert.assertEquals("foo.txt2", fileName);
        varSpace.setVariable("splitVar", "2");
        Assert.assertEquals(2, meta.getSplitEvery(varSpace));
        fileName = meta.buildFilename("foo", "txt2", varSpace, 0, null, 5, false, meta);
        Assert.assertEquals("foo_5.txt2", fileName);
    }

    public static class TextFileFieldLoadSaveValidator implements FieldLoadSaveValidator<TextFileField> {
        Random rand = new Random();

        @Override
        public TextFileField getTestObject() {
            String name = UUID.randomUUID().toString();
            int type = ValueMetaFactory.getIdForValueMeta(ValueMetaFactory.getValueMetaNames()[rand.nextInt(ValueMetaFactory.getValueMetaNames().length)]);
            String format = UUID.randomUUID().toString();
            int length = Math.abs(rand.nextInt());
            int precision = Math.abs(rand.nextInt());
            String currencySymbol = UUID.randomUUID().toString();
            String decimalSymbol = UUID.randomUUID().toString();
            String groupSymbol = UUID.randomUUID().toString();
            String nullString = UUID.randomUUID().toString();
            return new TextFileField(name, type, format, length, precision, currencySymbol, decimalSymbol, groupSymbol, nullString);
        }

        @Override
        public boolean validateTestObject(TextFileField testObject, Object actual) {
            if ((!(actual instanceof TextFileField)) || ((testObject.compare(actual)) != 0)) {
                return false;
            }
            TextFileField act = ((TextFileField) (actual));
            if (((((((((testObject.getName().equals(act.getName())) && ((testObject.getType()) == (act.getType()))) && (testObject.getFormat().equals(act.getFormat()))) && ((testObject.getLength()) == (act.getLength()))) && ((testObject.getPrecision()) == (act.getPrecision()))) && (testObject.getCurrencySymbol().equals(act.getCurrencySymbol()))) && (testObject.getDecimalSymbol().equals(act.getDecimalSymbol()))) && (testObject.getGroupingSymbol().equals(act.getGroupingSymbol()))) && (testObject.getNullString().equals(act.getNullString()))) {
                return true;
            } else {
                return false;
            }
        }
    }
}

