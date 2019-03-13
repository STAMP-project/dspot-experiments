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
package org.pentaho.di.trans.steps.rssinput;


import ValueMetaBase.trimTypeCode.length;
import java.util.Random;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class RssInputMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<RssInputMeta> testMetaClass = RssInputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    // RssInputField
    public class RssInputFieldLoadSaveValidator implements FieldLoadSaveValidator<RssInputField> {
        final Random rand = new Random();

        @Override
        public RssInputField getTestObject() {
            RssInputField rtn = new RssInputField();
            rtn.setCurrencySymbol(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setFormat(UUID.randomUUID().toString());
            rtn.setGroupSymbol(UUID.randomUUID().toString());
            rtn.setName(UUID.randomUUID().toString());
            rtn.setTrimType(rand.nextInt(length));
            rtn.setPrecision(rand.nextInt(9));
            rtn.setRepeated(rand.nextBoolean());
            rtn.setLength(rand.nextInt(50));
            rtn.setType(rand.nextInt(ValueMetaBase.typeCodes.length));
            rtn.setColumn(rand.nextInt(RssInputField.ColumnCode.length));
            return rtn;
        }

        @Override
        public boolean validateTestObject(RssInputField testObject, Object actual) {
            if (!(actual instanceof RssInputField)) {
                return false;
            }
            RssInputField actualInput = ((RssInputField) (actual));
            boolean tst1 = testObject.getXML().equals(actualInput.getXML());
            RssInputField aClone = ((RssInputField) (((RssInputField) (actual)).clone()));
            boolean tst2 = testObject.getXML().equals(aClone.getXML());
            return tst1 && tst2;
        }
    }
}

