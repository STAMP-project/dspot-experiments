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
package org.pentaho.di.trans.steps.webservices;


import XsdType.TYPES.length;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.webservices.wsdl.XsdType;


public class WebServiceMetaLoadSaveTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    Class<WebServiceMeta> testMetaClass = WebServiceMeta.class;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class WebServiceFieldLoadSaveValidator implements FieldLoadSaveValidator<WebServiceField> {
        final Random rand = new Random();

        @Override
        public WebServiceField getTestObject() {
            WebServiceField rtn = new WebServiceField();
            rtn.setName(UUID.randomUUID().toString());
            rtn.setWsName(UUID.randomUUID().toString());
            rtn.setXsdType(XsdType.TYPES[rand.nextInt(length)]);
            return rtn;
        }

        @Override
        public boolean validateTestObject(WebServiceField testObject, Object actual) {
            if (!(actual instanceof WebServiceField)) {
                return false;
            }
            WebServiceField another = ((WebServiceField) (actual));
            return new EqualsBuilder().append(testObject.getName(), another.getName()).append(testObject.getWsName(), another.getWsName()).append(testObject.getXsdType(), another.getXsdType()).isEquals();
        }
    }
}

