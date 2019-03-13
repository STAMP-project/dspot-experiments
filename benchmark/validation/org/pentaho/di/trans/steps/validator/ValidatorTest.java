/**
 * ! ******************************************************************************
 *  *
 *  * Pentaho Data Integration
 *  *
 *  * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *  *
 *  *******************************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  *****************************************************************************
 */
package org.pentaho.di.trans.steps.validator;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class ValidatorTest {
    private Validator validator;

    private StepMockHelper<ValidatorMeta, ValidatorData> mockHelper;

    @Test
    public void testPatternExpectedCompile() throws KettlePluginException {
        ValidatorData data = new ValidatorData();
        ValidatorMeta meta = new ValidatorMeta();
        data.regularExpression = new String[1];
        data.regularExpressionNotAllowed = new String[1];
        data.patternExpected = new Pattern[1];
        data.patternDisallowed = new Pattern[1];
        Validation v = new Validation();
        v.setFieldName("field");
        v.setDataType(1);
        v.setRegularExpression("${param}");
        v.setRegularExpressionNotAllowed("${param}");
        meta.setValidations(Collections.singletonList(v));
        validator.setVariable("param", ("^(((0[1-9]|[12]\\d|3[01])\\/(0[13578]|1[02])\\/((1[6-9]|[2-9]\\d)\\d{2}))|(" + (("(0[1-9]|[12]\\d|30)\\/(0[13456789]|1[012])\\/((1[6-9]|[2-9]\\d)\\d{2}))|((0[1-9]|1\\d|2[0-8])\\/02\\/(" + "(1[6-9]|[2-9]\\d)\\d{2}))|(29\\/02\\/((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|(") + "(16|[2468][048]|[3579][26])00))))$")));
        Mockito.doReturn(new ValueMetaString("field")).when(validator).createValueMeta(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        Mockito.doReturn(new ValueMetaString("field")).when(validator).cloneValueMeta(((ValueMetaInterface) (ArgumentMatchers.anyObject())), ArgumentMatchers.anyInt());
        validator.init(meta, data);
    }

    @Test
    public void assertNumeric_Integer() throws Exception {
        assertNumericForNumberMeta(new ValueMetaInteger("int"), 1L);
    }

    @Test
    public void assertNumeric_Number() throws Exception {
        assertNumericForNumberMeta(new ValueMetaNumber("number"), 1.0);
    }

    @Test
    public void assertNumeric_BigNumber() throws Exception {
        assertNumericForNumberMeta(new ValueMetaBigNumber("big-number"), BigDecimal.ONE);
    }

    @Test
    public void assertNumeric_StringWithDigits() throws Exception {
        ValueMetaString metaString = new ValueMetaString("string-with-digits");
        Assert.assertNull("Strings with digits are allowed", validator.assertNumeric(metaString, "123", new Validation()));
    }

    @Test
    public void assertNumeric_String() throws Exception {
        ValueMetaString metaString = new ValueMetaString("string");
        Assert.assertNotNull("General strings are not allowed", validator.assertNumeric(metaString, "qwerty", new Validation()));
    }

    @Test
    public void readSourceValuesFromInfoStepsTest() throws Exception {
        String name = "Valid list";
        String field = "sourcing field 1";
        String values = "A";
        mockHelper.stepMeta.setName(name);
        ValidatorMeta meta = new ValidatorMeta();
        List<Validation> validations = new ArrayList<>();
        Validation validation1 = new Validation("validation1");
        validation1.setSourcingValues(true);
        validation1.setSourcingField(field);
        validations.add(validation1);
        Validation validation2 = new Validation("validation2");
        validation2.setSourcingValues(true);
        validation2.setSourcingField("sourcing field 2");
        validations.add(validation2);
        meta.setValidations(validations);
        StepMeta stepMeta = new StepMeta();
        stepMeta.setName(name);
        RowSet rowSet = Mockito.mock(RowSet.class);
        Mockito.when(rowSet.getOriginStepName()).thenReturn(name);
        Mockito.when(rowSet.getDestinationStepName()).thenReturn("Validator");
        Mockito.when(rowSet.getOriginStepCopy()).thenReturn(0);
        Mockito.when(rowSet.getDestinationStepCopy()).thenReturn(0);
        Mockito.when(rowSet.getRow()).thenReturn(new String[]{ values }).thenReturn(null);
        Mockito.when(rowSet.isDone()).thenReturn(true);
        RowMetaInterface allowedRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(rowSet.getRowMeta()).thenReturn(allowedRowMeta);
        Mockito.when(rowSet.getRowMeta()).thenReturn(Mockito.mock(RowMetaInterface.class));
        Mockito.when(allowedRowMeta.indexOfValue(field)).thenReturn(0);
        Mockito.when(allowedRowMeta.getValueMeta(0)).thenReturn(Mockito.mock(ValueMetaInterface.class));
        List<RowSet> rowSets = new ArrayList<>();
        rowSets.add(rowSet);
        validator.setInputRowSets(rowSets);
        mockHelper.transMeta.setStep(0, stepMeta);
        Mockito.when(mockHelper.transMeta.findStep(Mockito.eq(name))).thenReturn(stepMeta);
        StepMeta stepMetaValidList = new StepMeta();
        stepMetaValidList.setName(name);
        meta.getStepIOMeta().getInfoStreams().get(0).setStepMeta(stepMetaValidList);
        meta.getStepIOMeta().getInfoStreams().get(1).setStepMeta(stepMetaValidList);
        Class<?> validatorClass = Validator.class;
        Field metaField = validatorClass.getDeclaredField("meta");
        metaField.setAccessible(true);
        metaField.set(validator, meta);
        ValidatorData data = new ValidatorData();
        data.constantsMeta = new ValueMetaInterface[2];
        Field dataField = validatorClass.getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(validator, data);
        data.listValues = new Object[2][2];
        validator.readSourceValuesFromInfoSteps();
        Assert.assertEquals(values, data.listValues[0][0]);
        Assert.assertEquals(values, data.listValues[1][0]);
    }
}

