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
package org.pentaho.di.trans.steps.sasinput;


import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class SasInputMetaTest {
    LoadSaveTester loadSaveTester;

    Class<SasInputMeta> testMetaClass = SasInputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class SasInputFieldLoadSaveValidator implements FieldLoadSaveValidator<SasInputField> {
        final Random rand = new Random();

        @Override
        public SasInputField getTestObject() {
            SasInputField rtn = new SasInputField();
            rtn.setRename(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setConversionMask(UUID.randomUUID().toString());
            rtn.setGroupingSymbol(UUID.randomUUID().toString());
            rtn.setName(UUID.randomUUID().toString());
            rtn.setTrimType(rand.nextInt(4));
            rtn.setPrecision(rand.nextInt(9));
            rtn.setType(rand.nextInt(7));
            rtn.setLength(rand.nextInt(50));
            return rtn;
        }

        @Override
        public boolean validateTestObject(SasInputField testObject, Object actual) {
            if (!(actual instanceof SasInputField)) {
                return false;
            }
            SasInputField another = ((SasInputField) (actual));
            return new EqualsBuilder().append(testObject.getName(), another.getName()).append(testObject.getTrimType(), another.getTrimType()).append(testObject.getType(), another.getType()).append(testObject.getPrecision(), another.getPrecision()).append(testObject.getRename(), another.getRename()).append(testObject.getDecimalSymbol(), another.getDecimalSymbol()).append(testObject.getConversionMask(), another.getConversionMask()).append(testObject.getGroupingSymbol(), another.getGroupingSymbol()).append(testObject.getLength(), another.getLength()).isEquals();
        }
    }
}

