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
package org.apache.beam.runners.fnexecution.wire;


import Components.Builder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.runners.core.construction.RehydratedComponents;
import org.apache.beam.runners.core.construction.SdkComponents;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.CustomCoder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link LengthPrefixUnknownCoders}.
 */
@RunWith(Parameterized.class)
public class LengthPrefixUnknownCodersTest {
    private static class UnknownCoder extends CustomCoder<String> {
        private static final Coder<?> INSTANCE = new LengthPrefixUnknownCodersTest.UnknownCoder();

        @Override
        public void encode(String value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public String decode(InputStream inStream) throws IOException, CoderException {
            return "";
        }

        @Override
        public int hashCode() {
            return 1278890232;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LengthPrefixUnknownCodersTest.UnknownCoder;
        }
    }

    @Parameterized.Parameter
    public Coder<?> original;

    @Parameterized.Parameter(1)
    public Coder<?> expected;

    @Parameterized.Parameter(2)
    public boolean replaceWithByteArray;

    @Test
    public void test() throws IOException {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        String coderId = sdkComponents.registerCoder(original);
        Components.Builder components = sdkComponents.toComponents().toBuilder();
        String updatedCoderId = LengthPrefixUnknownCoders.addLengthPrefixedCoder(coderId, components, replaceWithByteArray);
        Assert.assertEquals(expected, RehydratedComponents.forComponents(components.build()).getCoder(updatedCoderId));
    }
}

