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
package org.pentaho.di.trans.steps.creditcardvalidator;


import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_STRING;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class CreditCardValidatorMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testLoadSave() throws KettleException {
        List<String> attributes = Arrays.asList("DynamicField", "ResultFieldName", "CardType", "OnlyDigits", "NotValidMsg");
        LoadSaveTester<CreditCardValidatorMeta> loadSaveTester = new LoadSaveTester<CreditCardValidatorMeta>(CreditCardValidatorMeta.class, attributes);
        loadSaveTester.testSerialization();
    }

    @Test
    public void testSupportsErrorHandling() {
        Assert.assertTrue(new CreditCardValidatorMeta().supportsErrorHandling());
    }

    @Test
    public void testDefaults() {
        CreditCardValidatorMeta meta = new CreditCardValidatorMeta();
        meta.setDefault();
        Assert.assertEquals("result", meta.getResultFieldName());
        Assert.assertFalse(meta.isOnlyDigits());
        Assert.assertEquals("card type", meta.getCardType());
        Assert.assertEquals("not valid message", meta.getNotValidMsg());
    }

    @Test
    public void testGetFields() throws KettleStepException {
        CreditCardValidatorMeta meta = new CreditCardValidatorMeta();
        meta.setDefault();
        meta.setResultFieldName("The Result Field");
        meta.setCardType("The Card Type Field");
        meta.setNotValidMsg("Is Card Valid");
        RowMeta rowMeta = new RowMeta();
        meta.getFields(rowMeta, "this step", null, null, new Variables(), null, null);
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals("The Result Field", rowMeta.getValueMeta(0).getName());
        Assert.assertEquals(TYPE_BOOLEAN, rowMeta.getValueMeta(0).getType());
        Assert.assertEquals("this step", rowMeta.getValueMeta(0).getOrigin());
        Assert.assertEquals("The Card Type Field", rowMeta.getValueMeta(1).getName());
        Assert.assertEquals(TYPE_STRING, rowMeta.getValueMeta(1).getType());
        Assert.assertEquals("this step", rowMeta.getValueMeta(1).getOrigin());
        Assert.assertEquals("Is Card Valid", rowMeta.getValueMeta(2).getName());
        Assert.assertEquals(TYPE_STRING, rowMeta.getValueMeta(2).getType());
        Assert.assertEquals("this step", rowMeta.getValueMeta(2).getOrigin());
    }
}

