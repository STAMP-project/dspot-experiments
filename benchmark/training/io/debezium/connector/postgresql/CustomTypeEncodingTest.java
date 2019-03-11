/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;


import io.debezium.data.SpecialValueDecimal;
import io.debezium.data.VariableScaleDecimal;
import java.math.BigDecimal;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;


public class CustomTypeEncodingTest {
    @Test
    public void testVariableScaleDecimal() {
        final BigDecimal testValue = new BigDecimal("138.456");
        final Struct struct = VariableScaleDecimal.fromLogical(VariableScaleDecimal.schema(), new SpecialValueDecimal(testValue));
        final BigDecimal decodedValue = VariableScaleDecimal.toLogical(struct).getDecimalValue().get();
        Assert.assertEquals("Number should be same after serde", testValue, decodedValue);
    }
}

