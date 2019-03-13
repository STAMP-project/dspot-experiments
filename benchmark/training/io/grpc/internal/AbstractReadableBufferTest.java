/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Tests for {@link AbstractReadableBuffer}.
 */
@RunWith(JUnit4.class)
public class AbstractReadableBufferTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private AbstractReadableBuffer buffer;

    @Test
    public void readPositiveIntShouldSucceed() {
        mockBytes(127, 238, 221, 204);
        Assert.assertEquals(2146360780, buffer.readInt());
    }

    @Test
    public void readNegativeIntShouldSucceed() {
        mockBytes(255, 238, 221, 204);
        Assert.assertEquals(-1122868, buffer.readInt());
    }
}

