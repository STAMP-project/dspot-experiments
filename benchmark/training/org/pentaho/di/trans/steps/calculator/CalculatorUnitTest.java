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
package org.pentaho.di.trans.steps.calculator;


import Const.ROUND_HALF_CEILING;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static CalculatorMetaFunction.CALC_ABS;
import static CalculatorMetaFunction.CALC_ADD_SECONDS;
import static CalculatorMetaFunction.CALC_GET_ONLY_DIGITS;
import static CalculatorMetaFunction.CALC_MD5;


/**
 * Unit tests for calculator step
 *
 * @author Pavel Sakun
 * @see Calculator
 */
public class CalculatorUnitTest {
    private static Class<?> PKG = CalculatorUnitTest.class;// for i18n purposes, needed by Translator2!!


    private StepMockHelper<CalculatorMeta, CalculatorData> smh;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testMissingFile() throws KettleException {
        RowMeta inputRowMeta = new RowMeta();
        ValueMetaString pathMeta = new ValueMetaString("Path");
        inputRowMeta.addValueMeta(pathMeta);
        String filepath = "missingFile";
        Object[] rows = new Object[]{ filepath };
        RowSet inputRowSet = smh.getMockInputRowSet(rows);
        inputRowSet.setRowMeta(inputRowMeta);
        Calculator calculator = Mockito.spy(new Calculator(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans));
        calculator.addRowSetToInputRowSets(inputRowSet);
        calculator.setInputRowMeta(inputRowMeta);
        calculator.init(smh.initStepMetaInterface, smh.initStepDataInterface);
        CalculatorMeta meta = new CalculatorMeta();
        CalculatorMetaFunction[] calculations = new CalculatorMetaFunction[]{ new CalculatorMetaFunction("result", CALC_MD5, "Path", null, null, ValueMetaInterface.TYPE_STRING, 0, 0, false, "", "", "", "") };
        meta.setCalculation(calculations);
        meta.setFailIfNoFile(true);
        boolean processed = calculator.processRow(meta, new CalculatorData());
        Mockito.verify(calculator, Mockito.times(1)).logError(ArgumentMatchers.argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return ((String) (o)).contains(BaseMessages.getString(CalculatorUnitTest.PKG, "Calculator.Log.NoFile"));
            }
        }));
        Assert.assertFalse(processed);
    }

    @Test
    public void testAddSeconds() throws KettleException {
        RowMeta inputRowMeta = new RowMeta();
        ValueMetaDate dayMeta = new ValueMetaDate("Day");
        inputRowMeta.addValueMeta(dayMeta);
        ValueMetaInteger secondsMeta = new ValueMetaInteger("Seconds");
        inputRowMeta.addValueMeta(secondsMeta);
        RowSet inputRowSet = null;
        try {
            inputRowSet = smh.getMockInputRowSet(new Object[][]{ new Object[]{ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 00:00:00"), new Long(10) }, new Object[]{ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-10-31 23:59:50"), new Long(30) } });
        } catch (ParseException pe) {
            pe.printStackTrace();
            Assert.fail();
        }
        inputRowSet.setRowMeta(inputRowMeta);
        Calculator calculator = new Calculator(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        calculator.addRowSetToInputRowSets(inputRowSet);
        calculator.setInputRowMeta(inputRowMeta);
        calculator.init(smh.initStepMetaInterface, smh.initStepDataInterface);
        CalculatorMeta meta = new CalculatorMeta();
        meta.setCalculation(new CalculatorMetaFunction[]{ new CalculatorMetaFunction("new_day", CALC_ADD_SECONDS, "Day", "Seconds", null, ValueMetaInterface.TYPE_DATE, 0, 0, false, "", "", "", "") });
        // Verify output
        try {
            calculator.addRowListener(new RowAdapter() {
                @Override
                public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                    try {
                        CalculatorUnitTest.assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 00:00:10"), row[2]);
                    } catch (ParseException pe) {
                        throw new KettleStepException(pe);
                    }
                }
            });
            calculator.processRow(meta, new CalculatorData());
        } catch (KettleException ke) {
            ke.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testReturnDigitsOnly() throws KettleException {
        RowMeta inputRowMeta = new RowMeta();
        ValueMetaString nameMeta = new ValueMetaString("Name");
        inputRowMeta.addValueMeta(nameMeta);
        ValueMetaString valueMeta = new ValueMetaString("Value");
        inputRowMeta.addValueMeta(valueMeta);
        RowSet inputRowSet = smh.getMockInputRowSet(new Object[][]{ new Object[]{ "name1", "qwe123asd456zxc" }, new Object[]{ "name2", null } });
        inputRowSet.setRowMeta(inputRowMeta);
        Calculator calculator = new Calculator(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        calculator.addRowSetToInputRowSets(inputRowSet);
        calculator.setInputRowMeta(inputRowMeta);
        calculator.init(smh.initStepMetaInterface, smh.initStepDataInterface);
        CalculatorMeta meta = new CalculatorMeta();
        meta.setCalculation(new CalculatorMetaFunction[]{ new CalculatorMetaFunction("digits", CALC_GET_ONLY_DIGITS, "Value", null, null, ValueMetaInterface.TYPE_STRING, 0, 0, false, "", "", "", "") });
        // Verify output
        try {
            calculator.addRowListener(new RowAdapter() {
                @Override
                public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                    CalculatorUnitTest.assertEquals("123456", row[2]);
                }
            });
            calculator.processRow(meta, new CalculatorData());
        } catch (KettleException ke) {
            ke.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void calculatorShouldClearDataInstance() throws Exception {
        RowMeta inputRowMeta = new RowMeta();
        ValueMetaInteger valueMeta = new ValueMetaInteger("Value");
        inputRowMeta.addValueMeta(valueMeta);
        RowSet inputRowSet = smh.getMockInputRowSet(new Object[]{ -1L });
        inputRowSet.setRowMeta(inputRowMeta);
        Calculator calculator = new Calculator(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        calculator.addRowSetToInputRowSets(inputRowSet);
        calculator.setInputRowMeta(inputRowMeta);
        calculator.init(smh.initStepMetaInterface, smh.initStepDataInterface);
        CalculatorMeta meta = new CalculatorMeta();
        meta.setCalculation(new CalculatorMetaFunction[]{ new CalculatorMetaFunction("test", CALC_ABS, "Value", null, null, ValueMetaInterface.TYPE_STRING, 0, 0, false, "", "", "", "") });
        CalculatorData data = new CalculatorData();
        data = Mockito.spy(data);
        calculator.processRow(meta, data);
        Mockito.verify(data).getValueMetaFor(ArgumentMatchers.eq(valueMeta.getType()), ArgumentMatchers.anyString());
        calculator.processRow(meta, data);
        Mockito.verify(data).clearValuesMetaMapping();
    }

    @Test
    public void testRound1() throws KettleException {
        assertRound1(1.0, 1.2);
        assertRound1(2.0, 1.5);
        assertRound1(2.0, 1.7);
        assertRound1(2.0, 2.2);
        assertRound1(3.0, 2.5);
        assertRound1(3.0, 2.7);
        assertRound1((-1.0), (-1.2));
        assertRound1((-1.0), (-1.5));
        assertRound1((-2.0), (-1.7));
        assertRound1((-2.0), (-2.2));
        assertRound1((-2.0), (-2.5));
        assertRound1((-3.0), (-2.7));
        assertRound1(1.0, 1.0);
        assertRound1(2.0, 2.0);
        assertRound1((-3.0), (-3.0));
    }

    @Test
    public void testRound2() throws KettleException {
        assertRound2(1.0, 1.2, 0);
        assertRound2(2.0, 1.5, 0);
        assertRound2(2.0, 1.7, 0);
        assertRound2(2.0, 2.2, 0);
        assertRound2(3.0, 2.5, 0);
        assertRound2(3.0, 2.7, 0);
        assertRound2((-1.0), (-1.2), 0);
        assertRound2((-1.0), (-1.5), 0);
        assertRound2((-2.0), (-1.7), 0);
        assertRound2((-2.0), (-2.2), 0);
        assertRound2((-2.0), (-2.5), 0);
        assertRound2((-3.0), (-2.7), 0);
        assertRound2(1.0, 1.0, 0);
        assertRound2(2.0, 2.0, 0);
        assertRound2((-3.0), (-3.0), 0);
        assertRound2(0.01, 0.012, 2);
        assertRound2(0.02, 0.015, 2);
        assertRound2(0.02, 0.017, 2);
        assertRound2(0.02, 0.022, 2);
        assertRound2(0.03, 0.025, 2);
        assertRound2(0.03, 0.027, 2);
        assertRound2((-0.01), (-0.012), 2);
        assertRound2((-0.01), (-0.015), 2);
        assertRound2((-0.02), (-0.017), 2);
        assertRound2((-0.02), (-0.022), 2);
        assertRound2((-0.02), (-0.025), 2);
        assertRound2((-0.03), (-0.027), 2);
        assertRound2(0.01, 0.01, 2);
        assertRound2(0.02, 0.02, 2);
        assertRound2((-0.03), (-0.03), 2);
        assertRound2(100, 120, (-2));
        assertRound2(200, 150, (-2));
        assertRound2(200, 170, (-2));
        assertRound2(200, 220, (-2));
        assertRound2(300, 250, (-2));
        assertRound2(300, 270, (-2));
        assertRound2((-100), (-120), (-2));
        assertRound2((-100), (-150), (-2));
        assertRound2((-200), (-170), (-2));
        assertRound2((-200), (-220), (-2));
        assertRound2((-200), (-250), (-2));
        assertRound2((-300), (-270), (-2));
        assertRound2(100, 100, (-2));
        assertRound2(200, 200, (-2));
        assertRound2((-300), (-300), (-2));
    }

    @Test
    public void testRoundStd1() throws KettleException {
        assertRoundStd1(1.0, 1.2);
        assertRoundStd1(2.0, 1.5);
        assertRoundStd1(2.0, 1.7);
        assertRoundStd1(2.0, 2.2);
        assertRoundStd1(3.0, 2.5);
        assertRoundStd1(3.0, 2.7);
        assertRoundStd1((-1.0), (-1.2));
        assertRoundStd1((-2.0), (-1.5));
        assertRoundStd1((-2.0), (-1.7));
        assertRoundStd1((-2.0), (-2.2));
        assertRoundStd1((-3.0), (-2.5));
        assertRoundStd1((-3.0), (-2.7));
        assertRoundStd1(1.0, 1.0);
        assertRoundStd1(2.0, 2.0);
        assertRoundStd1((-3.0), (-3.0));
    }

    @Test
    public void testRoundStd2() throws KettleException {
        assertRoundStd2(1.0, 1.2, 0);
        assertRoundStd2(2.0, 1.5, 0);
        assertRoundStd2(2.0, 1.7, 0);
        assertRoundStd2(2.0, 2.2, 0);
        assertRoundStd2(3.0, 2.5, 0);
        assertRoundStd2(3.0, 2.7, 0);
        assertRoundStd2((-1.0), (-1.2), 0);
        assertRoundStd2((-2.0), (-1.5), 0);
        assertRoundStd2((-2.0), (-1.7), 0);
        assertRoundStd2((-2.0), (-2.2), 0);
        assertRoundStd2((-3.0), (-2.5), 0);
        assertRoundStd2((-3.0), (-2.7), 0);
    }

    @Test
    public void testRoundCustom1() throws KettleException {
        assertRoundCustom1(2.0, 1.2, BigDecimal.ROUND_UP);
        assertRoundCustom1(1.0, 1.2, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(2.0, 1.2, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(1.0, 1.2, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(1.0, 1.2, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(1.0, 1.2, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(1.0, 1.2, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(1.0, 1.2, ROUND_HALF_CEILING);
        assertRoundCustom1(2.0, 1.5, BigDecimal.ROUND_UP);
        assertRoundCustom1(1.0, 1.5, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(2.0, 1.5, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(1.0, 1.5, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(2.0, 1.5, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(1.0, 1.5, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(2.0, 1.5, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(2.0, 1.5, ROUND_HALF_CEILING);
        assertRoundCustom1(2.0, 1.7, BigDecimal.ROUND_UP);
        assertRoundCustom1(1.0, 1.7, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(2.0, 1.7, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(1.0, 1.7, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(2.0, 1.7, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(2.0, 1.7, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(2.0, 1.7, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(2.0, 1.7, ROUND_HALF_CEILING);
        assertRoundCustom1(3.0, 2.2, BigDecimal.ROUND_UP);
        assertRoundCustom1(2.0, 2.2, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(3.0, 2.2, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(2.0, 2.2, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(2.0, 2.2, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(2.0, 2.2, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(2.0, 2.2, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(2.0, 2.2, ROUND_HALF_CEILING);
        assertRoundCustom1(3.0, 2.5, BigDecimal.ROUND_UP);
        assertRoundCustom1(2.0, 2.5, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(3.0, 2.5, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(2.0, 2.5, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(3.0, 2.5, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(2.0, 2.5, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(2.0, 2.5, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(3.0, 2.5, ROUND_HALF_CEILING);
        assertRoundCustom1(3.0, 2.7, BigDecimal.ROUND_UP);
        assertRoundCustom1(2.0, 2.7, BigDecimal.ROUND_DOWN);
        assertRoundCustom1(3.0, 2.7, BigDecimal.ROUND_CEILING);
        assertRoundCustom1(2.0, 2.7, BigDecimal.ROUND_FLOOR);
        assertRoundCustom1(3.0, 2.7, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1(3.0, 2.7, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1(3.0, 2.7, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1(3.0, 2.7, ROUND_HALF_CEILING);
        assertRoundCustom1((-2.0), (-1.2), BigDecimal.ROUND_UP);
        assertRoundCustom1((-1.0), (-1.2), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-1.0), (-1.2), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-2.0), (-1.2), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-1.0), (-1.2), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-1.0), (-1.2), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-1.0), (-1.2), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-1.0), (-1.2), ROUND_HALF_CEILING);
        assertRoundCustom1((-2.0), (-1.5), BigDecimal.ROUND_UP);
        assertRoundCustom1((-1.0), (-1.5), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-1.0), (-1.5), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-2.0), (-1.5), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-2.0), (-1.5), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-1.0), (-1.5), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-2.0), (-1.5), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-1.0), (-1.5), ROUND_HALF_CEILING);
        assertRoundCustom1((-2.0), (-1.7), BigDecimal.ROUND_UP);
        assertRoundCustom1((-1.0), (-1.7), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-1.0), (-1.7), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-2.0), (-1.7), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-2.0), (-1.7), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-2.0), (-1.7), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-2.0), (-1.7), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-2.0), (-1.7), ROUND_HALF_CEILING);
        assertRoundCustom1((-3.0), (-2.2), BigDecimal.ROUND_UP);
        assertRoundCustom1((-2.0), (-2.2), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-2.0), (-2.2), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-3.0), (-2.2), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-2.0), (-2.2), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-2.0), (-2.2), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-2.0), (-2.2), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-2.0), (-2.2), ROUND_HALF_CEILING);
        assertRoundCustom1((-3.0), (-2.5), BigDecimal.ROUND_UP);
        assertRoundCustom1((-2.0), (-2.5), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-2.0), (-2.5), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-3.0), (-2.5), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-3.0), (-2.5), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-2.0), (-2.5), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-2.0), (-2.5), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-2.0), (-2.5), ROUND_HALF_CEILING);
        assertRoundCustom1((-3.0), (-2.7), BigDecimal.ROUND_UP);
        assertRoundCustom1((-2.0), (-2.7), BigDecimal.ROUND_DOWN);
        assertRoundCustom1((-2.0), (-2.7), BigDecimal.ROUND_CEILING);
        assertRoundCustom1((-3.0), (-2.7), BigDecimal.ROUND_FLOOR);
        assertRoundCustom1((-3.0), (-2.7), BigDecimal.ROUND_HALF_UP);
        assertRoundCustom1((-3.0), (-2.7), BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom1((-3.0), (-2.7), BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom1((-3.0), (-2.7), ROUND_HALF_CEILING);
    }

    @Test
    public void testRoundCustom2() throws KettleException {
        assertRoundCustom2(2.0, 1.2, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(1.0, 1.2, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(2.0, 1.2, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(1.0, 1.2, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(1.0, 1.2, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(1.0, 1.2, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(1.0, 1.2, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(1.0, 1.2, 0, ROUND_HALF_CEILING);
        assertRoundCustom2(2.0, 1.5, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(1.0, 1.5, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(2.0, 1.5, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(1.0, 1.5, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(2.0, 1.5, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(1.0, 1.5, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(2.0, 1.5, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(2.0, 1.5, 0, ROUND_HALF_CEILING);
        assertRoundCustom2(2.0, 1.7, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(1.0, 1.7, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(2.0, 1.7, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(1.0, 1.7, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(2.0, 1.7, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(2.0, 1.7, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(2.0, 1.7, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(2.0, 1.7, 0, ROUND_HALF_CEILING);
        assertRoundCustom2(3.0, 2.2, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(2.0, 2.2, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(3.0, 2.2, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(2.0, 2.2, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(2.0, 2.2, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(2.0, 2.2, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(2.0, 2.2, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(2.0, 2.2, 0, ROUND_HALF_CEILING);
        assertRoundCustom2(3.0, 2.5, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(2.0, 2.5, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(3.0, 2.5, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(2.0, 2.5, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(3.0, 2.5, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(2.0, 2.5, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(2.0, 2.5, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(3.0, 2.5, 0, ROUND_HALF_CEILING);
        assertRoundCustom2(3.0, 2.7, 0, BigDecimal.ROUND_UP);
        assertRoundCustom2(2.0, 2.7, 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2(3.0, 2.7, 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2(2.0, 2.7, 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2(3.0, 2.7, 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2(3.0, 2.7, 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2(3.0, 2.7, 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2(3.0, 2.7, 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-2.0), (-1.2), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-1.0), (-1.2), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-1.0), (-1.2), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-2.0), (-1.2), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-1.0), (-1.2), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-1.0), (-1.2), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-1.0), (-1.2), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-1.0), (-1.2), 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-2.0), (-1.5), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-1.0), (-1.5), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-1.0), (-1.5), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-2.0), (-1.5), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-2.0), (-1.5), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-1.0), (-1.5), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-2.0), (-1.5), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-1.0), (-1.5), 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-2.0), (-1.7), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-1.0), (-1.7), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-1.0), (-1.7), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-2.0), (-1.7), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-2.0), (-1.7), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-2.0), (-1.7), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-2.0), (-1.7), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-2.0), (-1.7), 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-3.0), (-2.2), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-2.0), (-2.2), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-2.0), (-2.2), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-3.0), (-2.2), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-2.0), (-2.2), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-2.0), (-2.2), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-2.0), (-2.2), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-2.0), (-2.2), 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-3.0), (-2.5), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-2.0), (-2.5), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-2.0), (-2.5), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-3.0), (-2.5), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-3.0), (-2.5), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-2.0), (-2.5), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-2.0), (-2.5), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-2.0), (-2.5), 0, ROUND_HALF_CEILING);
        assertRoundCustom2((-3.0), (-2.7), 0, BigDecimal.ROUND_UP);
        assertRoundCustom2((-2.0), (-2.7), 0, BigDecimal.ROUND_DOWN);
        assertRoundCustom2((-2.0), (-2.7), 0, BigDecimal.ROUND_CEILING);
        assertRoundCustom2((-3.0), (-2.7), 0, BigDecimal.ROUND_FLOOR);
        assertRoundCustom2((-3.0), (-2.7), 0, BigDecimal.ROUND_HALF_UP);
        assertRoundCustom2((-3.0), (-2.7), 0, BigDecimal.ROUND_HALF_DOWN);
        assertRoundCustom2((-3.0), (-2.7), 0, BigDecimal.ROUND_HALF_EVEN);
        assertRoundCustom2((-3.0), (-2.7), 0, ROUND_HALF_CEILING);
    }

    @Test
    public void calculatorReminder() throws Exception {
        assertCalculatorReminder(new Double("0.10000000000000053"), new Object[]{ new Long("10"), new Double("3.3") }, new int[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_NUMBER });
        assertCalculatorReminder(new Double("1.0"), new Object[]{ new Long("10"), new Double("4.5") }, new int[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_NUMBER });
        assertCalculatorReminder(new Double("4.0"), new Object[]{ new Double("12.5"), new Double("4.25") }, new int[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_NUMBER });
        assertCalculatorReminder(new Double("2.6000000000000005"), new Object[]{ new Double("12.5"), new Double("3.3") }, new int[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_NUMBER });
    }
}

