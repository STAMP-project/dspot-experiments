/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.query.h2.twostep;


import PartitionParameterType.BOOLEAN;
import PartitionParameterType.BYTE;
import PartitionParameterType.DECIMAL;
import PartitionParameterType.DOUBLE;
import PartitionParameterType.FLOAT;
import PartitionParameterType.INT;
import PartitionParameterType.LONG;
import PartitionParameterType.SHORT;
import PartitionParameterType.STRING;
import PartitionParameterType.UUID;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.sql.optimizer.affinity.PartitionDataTypeUtils;
import org.apache.ignite.internal.sql.optimizer.affinity.PartitionParameterType;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;


/**
 * Data conversion tests.
 */
public class SqlDataTypeConversionTest extends GridCommonAbstractTest {
    /**
     * Map to convert <code>PartitionParameterType</code> instances to correspondig java classes.
     */
    private static final Map<PartitionParameterType, Class<?>> PARAMETER_TYPE_TO_JAVA_CLASS;

    /**
     * Map to convert <code>PartitionParameterType</code> instances to correspondig H2 data types.
     */
    private static final Map<PartitionParameterType, Integer> IGNITE_PARAMETER_TYPE_TO_H2_PARAMETER_TYPE;

    /**
     * Ignite H2 Indexing.
     */
    private static IgniteH2Indexing idx;

    static {
        Map<PartitionParameterType, Class<?>> paramTypeToJavaCls = new java.util.EnumMap(PartitionParameterType.class);
        paramTypeToJavaCls.put(BOOLEAN, Boolean.class);
        paramTypeToJavaCls.put(BYTE, Byte.class);
        paramTypeToJavaCls.put(SHORT, Short.class);
        paramTypeToJavaCls.put(INT, Integer.class);
        paramTypeToJavaCls.put(LONG, Long.class);
        paramTypeToJavaCls.put(FLOAT, Float.class);
        paramTypeToJavaCls.put(DOUBLE, Double.class);
        paramTypeToJavaCls.put(STRING, String.class);
        paramTypeToJavaCls.put(DECIMAL, BigDecimal.class);
        paramTypeToJavaCls.put(UUID, java.util.UUID.class);
        PARAMETER_TYPE_TO_JAVA_CLASS = Collections.unmodifiableMap(paramTypeToJavaCls);
        Map<PartitionParameterType, Integer> igniteParamTypeToH2ParamType = new java.util.EnumMap(PartitionParameterType.class);
        igniteParamTypeToH2ParamType.put(BOOLEAN, Value.BOOLEAN);
        igniteParamTypeToH2ParamType.put(BYTE, Value.BYTE);
        igniteParamTypeToH2ParamType.put(SHORT, Value.SHORT);
        igniteParamTypeToH2ParamType.put(INT, Value.INT);
        igniteParamTypeToH2ParamType.put(LONG, Value.LONG);
        igniteParamTypeToH2ParamType.put(FLOAT, Value.FLOAT);
        igniteParamTypeToH2ParamType.put(DOUBLE, Value.DOUBLE);
        igniteParamTypeToH2ParamType.put(STRING, Value.STRING);
        igniteParamTypeToH2ParamType.put(DECIMAL, Value.DECIMAL);
        igniteParamTypeToH2ParamType.put(UUID, Value.UUID);
        IGNITE_PARAMETER_TYPE_TO_H2_PARAMETER_TYPE = Collections.unmodifiableMap(igniteParamTypeToH2ParamType);
    }

    /**
     * Test null value conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertNull() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(null);
    }

    /**
     * Test boolean conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertBoolean() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Boolean.TRUE);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Boolean.FALSE);
    }

    /**
     * Test byte conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertByte() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(((byte) (42)), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(((byte) (0)), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Byte.MIN_VALUE, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Byte.MAX_VALUE, UUID);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert(((byte) (42)), UUID));
    }

    /**
     * Test short conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertShort() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(((short) (42)), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(((short) (0)), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Short.MIN_VALUE, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Short.MAX_VALUE, UUID);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert(((short) (42)), UUID));
    }

    /**
     * Test int conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertInteger() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(42, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Integer.MIN_VALUE, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Integer.MAX_VALUE, UUID);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert(42, UUID));
    }

    /**
     * Test long conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertLong() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(42L, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0L, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Long.MIN_VALUE, UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Long.MAX_VALUE, UUID);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert(42L, UUID));
    }

    /**
     * Test float conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertFloat() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(42.1F);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0.1F);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0.0F);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(1.2345678E7F);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Float.POSITIVE_INFINITY);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Float.NEGATIVE_INFINITY);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Float.NaN);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Float.MIN_VALUE);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Float.MAX_VALUE);
    }

    /**
     * Test double conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertDouble() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(42.2);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0.2);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(0.0);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(1.2345678E7);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Double.POSITIVE_INFINITY);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Double.NEGATIVE_INFINITY);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Double.NaN);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Double.MIN_VALUE);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(Double.MAX_VALUE);
    }

    /**
     * Test string conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertString() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2("42", BOOLEAN);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert("42", BOOLEAN));
        makeSureThatConvertationResultsExactTheSameAsWithinH2("0");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("1");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("42.3", BOOLEAN);
        makeSureThatConvertationResultsExactTheSameAsWithinH2("0.3", BOOLEAN);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert("0.3", BOOLEAN));
        makeSureThatConvertationResultsExactTheSameAsWithinH2("42.4f");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("0.4d");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("12345678901234567890.123456789012345678901d");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("04d17cf3-bc20-4e3d-9ff7-72437cdae227");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("04d17cf3bc204e3d9ff772437cdae227");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("a");
        makeSureThatConvertationResultsExactTheSameAsWithinH2(("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + (("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")));
        makeSureThatConvertationResultsExactTheSameAsWithinH2("aaa", BOOLEAN);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(" aaa ");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("true");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("t");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("yes");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("y");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("false");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("f");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("no");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("n");
        makeSureThatConvertationResultsExactTheSameAsWithinH2(" true ");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("null");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("NULL");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("2000-01-02");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("10:00:00");
        makeSureThatConvertationResultsExactTheSameAsWithinH2("2001-01-01 23:59:59.123456");
    }

    /**
     * Test decimal conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertDecimal() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(42.5), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(0.5), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(0), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(1.2345678E7), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(BigDecimal.valueOf(1.23345353454567E16), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(Double.MIN_VALUE), UUID);
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new BigDecimal(Double.MAX_VALUE), UUID);
        assertEquals(PartitionDataTypeUtils.CONVERTATION_FAILURE, PartitionDataTypeUtils.convert(new BigDecimal(42.5), UUID));
    }

    /**
     * Test uuid conversion.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void convertUUID() throws Exception {
        makeSureThatConvertationResultsExactTheSameAsWithinH2(randomUUID());
        makeSureThatConvertationResultsExactTheSameAsWithinH2(fromString("00000000-0000-0000-0000-00000000000a"));
        makeSureThatConvertationResultsExactTheSameAsWithinH2(new java.util.UUID(0L, 1L));
    }

    /**
     * Test string to uuid conversion with different combinations of upper and lower case letters and with/without
     * hyphens.
     */
    @Test
    public void stringToUUIDConvertation() {
        java.util.UUID expUuid = fromString("273ded0d-86de-432e-b252-54c06ec22927");
        // Lower case and hyphens.
        assertEquals(expUuid, PartitionDataTypeUtils.stringToUUID("273ded0d-86de-432e-b252-54c06ec22927"));
        // Lower case without hyphens.
        assertEquals(expUuid, PartitionDataTypeUtils.stringToUUID("273ded0d86de432eb25254c06ec22927"));
        // Lower case without few hyphens.
        assertEquals(expUuid, PartitionDataTypeUtils.stringToUUID("273ded0d86de432e-b25254c06ec22927"));
        // Upper case and hyphens.
        assertEquals(expUuid, PartitionDataTypeUtils.stringToUUID("273dED0D-86DE-432E-B25254C06EC22927"));
        // Upper case without few hyphens.
        assertEquals(expUuid, PartitionDataTypeUtils.stringToUUID("273dED0D86DE432Eb252-54c06ec22927"));
    }
}

