/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.operation.aggregation;


import DataTypes.BOOLEAN;
import DataTypes.BYTE;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.IP;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import HyperLogLogDistinctAggregation.HllState;
import HyperLogLogDistinctAggregation.HllStateType.INSTANCE;
import HyperLogLogDistinctAggregation.Murmur3Hash;
import HyperLogLogPlusPlus.DEFAULT_PRECISION;
import com.google.common.collect.ImmutableList;
import io.crate.Streamer;
import io.crate.metadata.FunctionImplementation;
import io.crate.types.DataTypes;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.util.BigArrays;
import org.hamcrest.Matchers;
import org.junit.Test;

import static HyperLogLogDistinctAggregation.NAME;


public class HyperLogLogDistinctAggregationTest extends AggregationTest {
    @Test
    public void testReturnTypeIsAlwaysLong() {
        // Return type is fixed to Long
        FunctionImplementation func = functions.getQualified(new io.crate.metadata.FunctionIdent(NAME, ImmutableList.of(INTEGER)));
        assertEquals(LONG, func.info().returnType());
        func = functions.getQualified(new io.crate.metadata.FunctionIdent(NAME, ImmutableList.of(INTEGER, INTEGER)));
        assertEquals(LONG, func.info().returnType());
    }

    @Test
    public void testCallWithInvalidPrecisionResultsinAnError() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("precision must be >= 4 and <= 18");
        executeAggregationWithPrecision(INTEGER, new Object[][]{ new Object[]{ 4, 1 } });
    }

    @Test
    public void testWithoutPrecision() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, createTestData(10000, null));
        assertThat(result[0][0], Matchers.is(9899L));
    }

    @Test
    public void testWithPrecision() throws Exception {
        Object[][] result = executeAggregationWithPrecision(DOUBLE, createTestData(10000, 18));
        assertThat(result[0][0], Matchers.is(9997L));
    }

    @Test
    public void testMurmu3HashCalculationsForAllTypes() throws Exception {
        // double types
        assertThat(Murmur3Hash.getForType(DOUBLE).hash(1.3), Matchers.is(3706823019612663850L));
        assertThat(Murmur3Hash.getForType(FLOAT).hash(1.3F), Matchers.is(1386670595997310747L));
        // long types
        assertThat(Murmur3Hash.getForType(LONG).hash(1L), Matchers.is((-2508561340476696217L)));
        assertThat(Murmur3Hash.getForType(INTEGER).hash(1), Matchers.is((-2508561340476696217L)));
        assertThat(Murmur3Hash.getForType(SHORT).hash(new Short("1")), Matchers.is((-2508561340476696217L)));
        assertThat(Murmur3Hash.getForType(BYTE).hash(new Byte("1")), Matchers.is((-2508561340476696217L)));
        assertThat(Murmur3Hash.getForType(TIMESTAMP).hash(1512569562000L), Matchers.is((-3066297687939346384L)));
        // bytes types
        assertThat(Murmur3Hash.getForType(STRING).hash("foo"), Matchers.is((-2129773440516405919L)));
        assertThat(Murmur3Hash.getForType(BOOLEAN).hash(true), Matchers.is(7529381342917315814L));
        // ip type
        assertThat(Murmur3Hash.getForType(IP).hash("127.0.0.1"), Matchers.is(5662530066633765140L));
    }

    @Test
    public void testStreaming() throws Exception {
        HyperLogLogDistinctAggregation.HllState hllState1 = new HyperLogLogDistinctAggregation.HllState(BigArrays.NON_RECYCLING_INSTANCE, DataTypes.IP);
        hllState1.init(DEFAULT_PRECISION);
        BytesStreamOutput out = new BytesStreamOutput();
        Streamer streamer = INSTANCE.streamer();
        streamer.writeValueTo(out, hllState1);
        StreamInput in = out.bytes().streamInput();
        HyperLogLogDistinctAggregation.HllState hllState2 = ((HyperLogLogDistinctAggregation.HllState) (streamer.readValueFrom(in)));
        // test that murmur3hash and HLL++ is correctly initialized with streamed dataType and version
        hllState1.add("127.0.0.1");
        hllState2.add("127.0.0.1");
        assertThat(hllState2.value(), Matchers.is(hllState1.value()));
    }
}

