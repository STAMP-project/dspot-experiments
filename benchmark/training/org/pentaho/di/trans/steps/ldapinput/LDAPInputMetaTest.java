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
package org.pentaho.di.trans.steps.ldapinput;


import LDAPInputField.FetchAttributeAsCode.length;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class LDAPInputMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<LDAPInputMeta> testMetaClass = LDAPInputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class LDAPInputFieldLoadSaveValidator implements FieldLoadSaveValidator<LDAPInputField> {
        final Random rand = new Random();

        @Override
        public LDAPInputField getTestObject() {
            LDAPInputField rtn = new LDAPInputField();
            rtn.setCurrencySymbol(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setFormat(UUID.randomUUID().toString());
            rtn.setGroupSymbol(UUID.randomUUID().toString());
            rtn.setName(UUID.randomUUID().toString());
            rtn.setTrimType(rand.nextInt(4));
            rtn.setPrecision(rand.nextInt(9));
            rtn.setRepeated(rand.nextBoolean());
            rtn.setLength(rand.nextInt(50));
            rtn.setType(rand.nextInt(7));
            rtn.setSortedKey(rand.nextBoolean());
            rtn.setFetchAttributeAs(rand.nextInt(length));
            rtn.setAttribute(UUID.randomUUID().toString());
            return rtn;
        }

        @Override
        public boolean validateTestObject(LDAPInputField testObject, Object actual) {
            if (!(actual instanceof LDAPInputField)) {
                return false;
            }
            LDAPInputField another = ((LDAPInputField) (actual));
            return new EqualsBuilder().append(testObject.getName(), another.getName()).append(testObject.getAttribute(), another.getAttribute()).append(testObject.getReturnType(), another.getReturnType()).append(testObject.getType(), another.getType()).append(testObject.getLength(), another.getLength()).append(testObject.getFormat(), another.getFormat()).append(testObject.getTrimType(), another.getTrimType()).append(testObject.getPrecision(), another.getPrecision()).append(testObject.getCurrencySymbol(), another.getCurrencySymbol()).append(testObject.getDecimalSymbol(), another.getDecimalSymbol()).append(testObject.getGroupSymbol(), another.getGroupSymbol()).append(testObject.isRepeated(), another.isRepeated()).append(testObject.isSortedKey(), another.isSortedKey()).isEquals();
        }
    }
}

