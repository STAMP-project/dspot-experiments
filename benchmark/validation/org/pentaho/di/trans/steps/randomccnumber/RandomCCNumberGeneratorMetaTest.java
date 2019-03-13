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
package org.pentaho.di.trans.steps.randomccnumber;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;


public class RandomCCNumberGeneratorMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testStepMeta() throws KettleException {
        List<String> attributes = Arrays.asList("cardNumberFieldName", "cardLengthFieldName", "cardTypeFieldName", "cctype", "cclen", "ccsize");
        Map<String, String> getterMap = new HashMap<String, String>();
        getterMap.put("cardNumberFieldName", "getCardNumberFieldName");
        getterMap.put("cardLengthFieldName", "getCardLengthFieldName");
        getterMap.put("cardTypeFieldName", "getCardTypeFieldName");
        getterMap.put("cctype", "getFieldCCType");
        getterMap.put("cclen", "getFieldCCLength");
        getterMap.put("ccsize", "getFieldCCSize");
        Map<String, String> setterMap = new HashMap<String, String>();
        setterMap.put("cardNumberFieldName", "setCardNumberFieldName");
        setterMap.put("cardLengthFieldName", "setCardLengthFieldName");
        setterMap.put("cardTypeFieldName", "setCardTypeFieldName");
        setterMap.put("cctype", "setFieldCCType");
        setterMap.put("cclen", "setFieldCCLength");
        setterMap.put("ccsize", "setFieldCCSize");
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorAttributeMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidatorAttributeMap.put("cctype", new ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 25));
        fieldLoadSaveValidatorAttributeMap.put("cclen", new ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 25));
        fieldLoadSaveValidatorAttributeMap.put("ccsize", new ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 25));
        LoadSaveTester loadSaveTester = new LoadSaveTester(RandomCCNumberGeneratorMeta.class, attributes, getterMap, setterMap, fieldLoadSaveValidatorAttributeMap, new HashMap<String, FieldLoadSaveValidator<?>>());
        loadSaveTester.testSerialization();
    }
}

