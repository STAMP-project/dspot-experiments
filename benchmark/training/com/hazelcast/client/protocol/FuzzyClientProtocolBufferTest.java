/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.protocol;


import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Random;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class FuzzyClientProtocolBufferTest extends HazelcastTestSupport {
    private static ClientProtocolBuffer safeBuffer;

    private static ClientProtocolBuffer unsafeBuffer;

    private static final Random random = new Random();

    private static int capacity;

    @Test
    public void testBasics_withSafeBuffer() {
        testBasics(FuzzyClientProtocolBufferTest.safeBuffer);
    }

    @Test
    public void testBasics_withUnSafeBuffer() {
        testBasics(FuzzyClientProtocolBufferTest.unsafeBuffer);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundException_withSafeBuffer() {
        testIndexOutOfBoundException(FuzzyClientProtocolBufferTest.safeBuffer);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundException_withUnsafeBuffer() {
        testIndexOutOfBoundException(FuzzyClientProtocolBufferTest.unsafeBuffer);
    }
}

