/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.typeutils;


import BasicTypeInfo.DOUBLE_TYPE_INFO;
import BasicTypeInfo.FLOAT_TYPE_INFO;
import BasicTypeInfo.INT_TYPE_INFO;
import BasicTypeInfo.SHORT_TYPE_INFO;
import BasicTypeInfo.STRING_TYPE_INFO;
import java.io.IOException;
import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.io.GenericInputFormat;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.InputSplit;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class TypeExtractorInputFormatsTest {
    @Test
    public void testExtractInputFormatType() {
        try {
            InputFormat<?, ?> format = new TypeExtractorInputFormatsTest.DummyFloatInputFormat();
            TypeInformation<?> typeInfo = TypeExtractor.getInputFormatTypes(format);
            Assert.assertEquals(FLOAT_TYPE_INFO, typeInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExtractDerivedInputFormatType() {
        try {
            // simple type
            {
                InputFormat<?, ?> format = new TypeExtractorInputFormatsTest.DerivedInputFormat();
                TypeInformation<?> typeInfo = TypeExtractor.getInputFormatTypes(format);
                Assert.assertEquals(SHORT_TYPE_INFO, typeInfo);
            }
            // composite type
            {
                InputFormat<?, ?> format = new TypeExtractorInputFormatsTest.DerivedTupleInputFormat();
                TypeInformation<?> typeInfo = TypeExtractor.getInputFormatTypes(format);
                Assert.assertTrue(typeInfo.isTupleType());
                Assert.assertTrue((typeInfo instanceof TupleTypeInfo));
                @SuppressWarnings("unchecked")
                TupleTypeInfo<Tuple3<String, Short, Double>> tupleInfo = ((TupleTypeInfo<Tuple3<String, Short, Double>>) (typeInfo));
                Assert.assertEquals(3, tupleInfo.getArity());
                Assert.assertEquals(STRING_TYPE_INFO, tupleInfo.getTypeAt(0));
                Assert.assertEquals(SHORT_TYPE_INFO, tupleInfo.getTypeAt(1));
                Assert.assertEquals(DOUBLE_TYPE_INFO, tupleInfo.getTypeAt(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultiLevelDerivedInputFormatType() {
        try {
            // composite type
            {
                InputFormat<?, ?> format = new TypeExtractorInputFormatsTest.FinalRelativeInputFormat();
                TypeInformation<?> typeInfo = TypeExtractor.getInputFormatTypes(format);
                Assert.assertTrue(typeInfo.isTupleType());
                Assert.assertTrue((typeInfo instanceof TupleTypeInfo));
                @SuppressWarnings("unchecked")
                TupleTypeInfo<Tuple3<String, Integer, Double>> tupleInfo = ((TupleTypeInfo<Tuple3<String, Integer, Double>>) (typeInfo));
                Assert.assertEquals(3, tupleInfo.getArity());
                Assert.assertEquals(STRING_TYPE_INFO, tupleInfo.getTypeAt(0));
                Assert.assertEquals(INT_TYPE_INFO, tupleInfo.getTypeAt(1));
                Assert.assertEquals(DOUBLE_TYPE_INFO, tupleInfo.getTypeAt(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testQueryableFormatType() {
        try {
            InputFormat<?, ?> format = new TypeExtractorInputFormatsTest.QueryableInputFormat();
            TypeInformation<?> typeInfo = TypeExtractor.getInputFormatTypes(format);
            Assert.assertEquals(DOUBLE_TYPE_INFO, typeInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    // Test formats
    // --------------------------------------------------------------------------------------------
    public static final class DummyFloatInputFormat implements InputFormat<Float, InputSplit> {
        @Override
        public void configure(Configuration parameters) {
        }

        @Override
        public BaseStatistics getStatistics(BaseStatistics cachedStatistics) {
            return null;
        }

        @Override
        public InputSplit[] createInputSplits(int minNumSplits) {
            return null;
        }

        @Override
        public DefaultInputSplitAssigner getInputSplitAssigner(InputSplit[] splits) {
            return null;
        }

        @Override
        public void open(InputSplit split) {
        }

        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Float nextRecord(Float reuse) throws IOException {
            return null;
        }

        @Override
        public void close() {
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class DerivedInputFormat extends GenericInputFormat<Short> {
        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Short nextRecord(Short reuse) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class DerivedTupleInputFormat extends GenericInputFormat<Tuple3<String, Short, Double>> {
        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Tuple3<String, Short, Double> nextRecord(Tuple3<String, Short, Double> reuse) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------
    public static class RelativeInputFormat<T> extends GenericInputFormat<Tuple3<String, T, Double>> {
        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Tuple3<String, T, Double> nextRecord(Tuple3<String, T, Double> reuse) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class FinalRelativeInputFormat extends TypeExtractorInputFormatsTest.RelativeInputFormat<Integer> {
        @Override
        public Tuple3<String, Integer, Double> nextRecord(Tuple3<String, Integer, Double> reuse) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class QueryableInputFormat implements InputFormat<Float, InputSplit> , ResultTypeQueryable<Double> {
        @Override
        public void configure(Configuration parameters) {
        }

        @Override
        public BaseStatistics getStatistics(BaseStatistics cachedStatistics) {
            return null;
        }

        @Override
        public InputSplit[] createInputSplits(int minNumSplits) {
            return null;
        }

        @Override
        public DefaultInputSplitAssigner getInputSplitAssigner(InputSplit[] splits) {
            return null;
        }

        @Override
        public void open(InputSplit split) {
        }

        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Float nextRecord(Float reuse) throws IOException {
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public TypeInformation<Double> getProducedType() {
            return BasicTypeInfo.DOUBLE_TYPE_INFO;
        }
    }
}

