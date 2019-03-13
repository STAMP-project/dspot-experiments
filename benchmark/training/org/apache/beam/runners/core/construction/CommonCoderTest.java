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
package org.apache.beam.runners.core.construction;


import StandardCoders.Enum.BYTES;
import StandardCoders.Enum.GLOBAL_WINDOW;
import StandardCoders.Enum.INTERVAL_WINDOW;
import StandardCoders.Enum.ITERABLE;
import StandardCoders.Enum.KV;
import StandardCoders.Enum.TIMER;
import StandardCoders.Enum.VARINT;
import StandardCoders.Enum.WINDOWED_VALUE;
import WindowedValue.FullWindowedValueCoder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.beam.sdk.coders.ByteCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.Coder.Context;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow.IntervalWindowCoder;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests that Java SDK coders standardized by the Fn API meet the common spec.
 */
@RunWith(Parameterized.class)
public class CommonCoderTest {
    private static final String STANDARD_CODERS_YAML_PATH = "/org/apache/beam/model/fnexecution/v1/standard_coders.yaml";

    private static final Map<String, Class<?>> coders = ImmutableMap.<String, Class<?>>builder().put(BeamUrns.getUrn(BYTES), ByteCoder.class).put(BeamUrns.getUrn(KV), KvCoder.class).put(BeamUrns.getUrn(VARINT), VarLongCoder.class).put(BeamUrns.getUrn(INTERVAL_WINDOW), IntervalWindowCoder.class).put(BeamUrns.getUrn(ITERABLE), IterableCoder.class).put(BeamUrns.getUrn(TIMER), Coder.class).put(BeamUrns.getUrn(GLOBAL_WINDOW), Coder.class).put(BeamUrns.getUrn(WINDOWED_VALUE), FullWindowedValueCoder.class).build();

    @AutoValue
    abstract static class CommonCoder {
        abstract String getUrn();

        abstract List<CommonCoderTest.CommonCoder> getComponents();

        abstract Boolean getNonDeterministic();

        @JsonCreator
        static CommonCoderTest.CommonCoder create(@JsonProperty("urn")
        String urn, @JsonProperty("components")
        @Nullable
        List<CommonCoderTest.CommonCoder> components, @JsonProperty("non_deterministic")
        @Nullable
        Boolean nonDeterministic) {
            return new AutoValue_CommonCoderTest_CommonCoder(checkNotNull(urn, "urn"), firstNonNull(components, Collections.emptyList()), firstNonNull(nonDeterministic, Boolean.FALSE));
        }
    }

    @AutoValue
    abstract static class CommonCoderTestSpec {
        abstract CommonCoderTest.CommonCoder getCoder();

        @Nullable
        abstract Boolean getNested();

        abstract Map<String, Object> getExamples();

        @JsonCreator
        static CommonCoderTest.CommonCoderTestSpec create(@JsonProperty("coder")
        CommonCoderTest.CommonCoder coder, @JsonProperty("nested")
        @Nullable
        Boolean nested, @JsonProperty("examples")
        Map<String, Object> examples) {
            return new AutoValue_CommonCoderTest_CommonCoderTestSpec(coder, nested, examples);
        }
    }

    @AutoValue
    abstract static class OneCoderTestSpec {
        abstract CommonCoderTest.CommonCoder getCoder();

        abstract boolean getNested();

        @SuppressWarnings("mutable")
        abstract byte[] getSerialized();

        abstract Object getValue();

        static CommonCoderTest.OneCoderTestSpec create(CommonCoderTest.CommonCoder coder, boolean nested, byte[] serialized, Object value) {
            return new AutoValue_CommonCoderTest_OneCoderTestSpec(coder, nested, serialized, value);
        }
    }

    @Parameterized.Parameter(0)
    public CommonCoderTest.OneCoderTestSpec testSpec;

    @Parameterized.Parameter(1)
    public String ignoredTestName;

    @Test
    public void executeSingleTest() throws IOException {
        CommonCoderTest.assertCoderIsKnown(testSpec.getCoder());
        Coder coder = CommonCoderTest.instantiateCoder(testSpec.getCoder());
        Object testValue = CommonCoderTest.convertValue(testSpec.getValue(), testSpec.getCoder(), coder);
        Context context = (testSpec.getNested()) ? Context.NESTED : Context.OUTER;
        byte[] encoded = CoderUtils.encodeToByteArray(coder, testValue, context);
        Object decodedValue = CoderUtils.decodeFromByteArray(coder, testSpec.getSerialized(), context);
        if (!(testSpec.getCoder().getNonDeterministic())) {
            Assert.assertThat(testSpec.toString(), encoded, Matchers.equalTo(testSpec.getSerialized()));
        }
        verifyDecodedValue(testSpec.getCoder(), decodedValue, testValue);
    }
}

