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
package org.pentaho.di.trans.steps.calculator;


import CalculatorMetaFunction.CALC_ADD;
import CalculatorMetaFunction.CALC_ADD_DAYS;
import CalculatorMetaFunction.CALC_CONSTANT;
import CalculatorMetaFunction.calcLongDesc.length;
import ValueMetaInterface.TYPE_NONE;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import org.junit.Assert;
import org.junit.Test;


public class CalculatorMetaFunctionTest {
    @Test
    public void testEquals() {
        CalculatorMetaFunction meta1 = new CalculatorMetaFunction();
        CalculatorMetaFunction meta2 = ((CalculatorMetaFunction) (meta1.clone()));
        Assert.assertNotSame(meta1, meta2);
        Assert.assertFalse(meta1.equals(null));
        Assert.assertFalse(meta1.equals(new Object()));
        Assert.assertTrue(meta1.equals(meta2));
        meta2.setCalcType(CALC_ADD_DAYS);
        Assert.assertFalse(meta1.equals(meta2));
    }

    @Test
    public void testGetCalcFunctionLongDesc() {
        Assert.assertNull(CalculatorMetaFunction.getCalcFunctionLongDesc(Integer.MIN_VALUE));
        Assert.assertNull(CalculatorMetaFunction.getCalcFunctionLongDesc(Integer.MAX_VALUE));
        Assert.assertNull(CalculatorMetaFunction.getCalcFunctionLongDesc(length));
    }

    @Test
    public void testGetCalcFunctionDefaultResultType() {
        Assert.assertEquals(TYPE_NONE, CalculatorMetaFunction.getCalcFunctionDefaultResultType(Integer.MIN_VALUE));
        Assert.assertEquals(TYPE_NONE, CalculatorMetaFunction.getCalcFunctionDefaultResultType(Integer.MAX_VALUE));
        Assert.assertEquals(TYPE_NONE, CalculatorMetaFunction.getCalcFunctionDefaultResultType((-1)));
        Assert.assertEquals(TYPE_STRING, CalculatorMetaFunction.getCalcFunctionDefaultResultType(CALC_CONSTANT));
        Assert.assertEquals(TYPE_NUMBER, CalculatorMetaFunction.getCalcFunctionDefaultResultType(CALC_ADD));
    }
}

