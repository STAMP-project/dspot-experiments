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


import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IncompatibleWindowException;
import org.apache.beam.sdk.transforms.windowing.NonMergingWindowFn;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.transforms.windowing.WindowMappingFn;
import org.apache.beam.sdk.util.VarInt;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollection.IsBounded;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link PCollectionTranslation}.
 */
@RunWith(Parameterized.class)
public class PCollectionTranslationTest {
    @Parameterized.Parameter(0)
    public PCollection<?> testCollection;

    @Test
    public void testEncodeDecodeCycle() throws Exception {
        // Encode
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.PCollection protoCollection = PCollectionTranslation.toProto(testCollection, sdkComponents);
        RehydratedComponents protoComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        // Decode
        Pipeline pipeline = Pipeline.create();
        PCollection<?> decodedCollection = PCollectionTranslation.fromProto(protoCollection, pipeline, protoComponents);
        // Verify
        Assert.assertThat(decodedCollection.getCoder(), Matchers.equalTo(testCollection.getCoder()));
        Assert.assertThat(decodedCollection.getWindowingStrategy(), Matchers.equalTo(testCollection.getWindowingStrategy().fixDefaults()));
        Assert.assertThat(decodedCollection.isBounded(), Matchers.equalTo(testCollection.isBounded()));
    }

    @Test
    public void testEncodeDecodeFields() throws Exception {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.PCollection protoCollection = PCollectionTranslation.toProto(testCollection, sdkComponents);
        RehydratedComponents protoComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        Coder<?> decodedCoder = protoComponents.getCoder(protoCollection.getCoderId());
        WindowingStrategy<?, ?> decodedStrategy = protoComponents.getWindowingStrategy(protoCollection.getWindowingStrategyId());
        IsBounded decodedIsBounded = PCollectionTranslation.isBounded(protoCollection);
        Assert.assertThat(decodedCoder, Matchers.equalTo(testCollection.getCoder()));
        Assert.assertThat(decodedStrategy, Matchers.equalTo(testCollection.getWindowingStrategy().fixDefaults()));
        Assert.assertThat(decodedIsBounded, Matchers.equalTo(testCollection.isBounded()));
    }

    @AutoValue
    abstract static class CustomIntCoder extends CustomCoder<Integer> {
        @Override
        public Integer decode(InputStream inStream) throws IOException {
            return VarInt.decodeInt(inStream);
        }

        @Override
        public void encode(Integer value, OutputStream outStream) throws IOException {
            VarInt.encode(value, outStream);
        }
    }

    private static class CustomWindows extends NonMergingWindowFn<Integer, BoundedWindow> {
        @Override
        public Collection<BoundedWindow> assignWindows(final AssignContext c) throws Exception {
            return Collections.singleton(new BoundedWindow() {
                @Override
                public Instant maxTimestamp() {
                    return new Instant(c.element().longValue());
                }
            });
        }

        @Override
        public boolean isCompatible(WindowFn<?, ?> other) {
            return (other != null) && (this.getClass().equals(other.getClass()));
        }

        @Override
        public void verifyCompatibility(WindowFn<?, ?> other) throws IncompatibleWindowException {
            if (!(this.isCompatible(other))) {
                throw new IncompatibleWindowException(other, String.format("%s is only compatible with %s.", PCollectionTranslationTest.CustomWindows.class.getSimpleName(), PCollectionTranslationTest.CustomWindows.class.getSimpleName()));
            }
        }

        @Override
        public Coder<BoundedWindow> windowCoder() {
            return new org.apache.beam.sdk.coders.AtomicCoder<BoundedWindow>() {
                @Override
                public void verifyDeterministic() {
                }

                @Override
                public void encode(BoundedWindow value, OutputStream outStream) throws IOException {
                    VarInt.encode(value.maxTimestamp().getMillis(), outStream);
                }

                @Override
                public BoundedWindow decode(InputStream inStream) throws IOException {
                    final Instant ts = new Instant(VarInt.decodeLong(inStream));
                    return new BoundedWindow() {
                        @Override
                        public Instant maxTimestamp() {
                            return ts;
                        }
                    };
                }
            };
        }

        @Override
        public WindowMappingFn<BoundedWindow> getDefaultWindowMappingFn() {
            throw new UnsupportedOperationException();
        }
    }
}

