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
package org.pentaho.di.trans.steps.ldifinput;


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


public class LDIFInputMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class LDIFInputFieldLoadSaveValidator implements FieldLoadSaveValidator<LDIFInputField> {
        final Random rand = new Random();

        @Override
        public LDIFInputField getTestObject() {
            LDIFInputField rtn = new LDIFInputField();
            rtn.setAttribut(UUID.randomUUID().toString());
            rtn.setCurrencySymbol(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setFormat(UUID.randomUUID().toString());
            rtn.setGroupSymbol(UUID.randomUUID().toString());
            rtn.setLength(rand.nextInt(50));
            rtn.setName(UUID.randomUUID().toString());
            rtn.setPrecision(rand.nextInt(9));
            rtn.setRepeated(rand.nextBoolean());
            rtn.setSamples(new String[]{ UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString() });
            rtn.setTrimType(rand.nextInt(4));
            rtn.setType(rand.nextInt(5));
            return rtn;
        }

        @Override
        public boolean validateTestObject(LDIFInputField testObject, Object actual) {
            if (!(actual instanceof LDIFInputField)) {
                return false;
            }
            LDIFInputField actualInput = ((LDIFInputField) (actual));
            return testObject.getXML().equals(actualInput.getXML());
        }
    }
}

