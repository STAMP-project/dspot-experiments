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
package org.apache.beam.sdk.coders;


import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.coders.Coder.NonDeterministicException;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link StringDelegateCoder}.
 */
@RunWith(JUnit4.class)
public class StringDelegateCoderTest {
    // Test data
    private static final Coder<URI> uriCoder = StringDelegateCoder.of(URI.class);

    private static final List<String> TEST_URI_STRINGS = Arrays.asList("http://www.example.com", "gs://myproject/mybucket/some/gcs/path", "/just/some/path", "file:/path/with/no/authority", "file:///path/with/empty/authority");

    // Tests
    @Test
    public void testDeterministic() throws Exception, NonDeterministicException {
        StringDelegateCoderTest.uriCoder.verifyDeterministic();
        for (String uriString : StringDelegateCoderTest.TEST_URI_STRINGS) {
            CoderProperties.coderDeterministic(StringDelegateCoderTest.uriCoder, new URI(uriString), new URI(uriString));
        }
    }

    @Test
    public void testDecodeEncodeEqual() throws Exception {
        for (String uriString : StringDelegateCoderTest.TEST_URI_STRINGS) {
            CoderProperties.coderDecodeEncodeEqual(StringDelegateCoderTest.uriCoder, new URI(uriString));
        }
    }

    @Test
    public void testSerializable() throws Exception {
        CoderProperties.coderSerializable(StringDelegateCoderTest.uriCoder);
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        Assert.assertThat(StringDelegateCoder.of(URI.class).getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(URI.class)));
    }
}

