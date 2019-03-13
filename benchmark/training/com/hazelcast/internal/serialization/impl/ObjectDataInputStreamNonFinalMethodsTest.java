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
package com.hazelcast.internal.serialization.impl;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteOrder;
import java.util.Random;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ObjectDataInputStreamNonFinalMethodsTest {
    static final byte[] INIT_DATA = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private InternalSerializationService mockSerializationService;

    private ObjectDataInputStream in;

    private DataInputStream dataInputSpy;

    private ByteArrayInputStream inputStream;

    private ByteOrder byteOrder;

    @Test
    public void testSkip() throws Exception {
        long someInput = new Random().nextLong();
        in.skip(someInput);
        Mockito.verify(dataInputSpy).skip(someInput);
    }

    @Test
    public void testAvailable() throws Exception {
        in.available();
        Mockito.verify(dataInputSpy).available();
    }

    @Test
    public void testClose() throws Exception {
        in.close();
        Mockito.verify(dataInputSpy).close();
    }

    @Test
    public void testMark() throws Exception {
        int someInput = new Random().nextInt();
        in.mark(someInput);
        Mockito.verify(dataInputSpy).mark(someInput);
    }

    @Test
    public void testReset() throws Exception {
        in.reset();
        Mockito.verify(dataInputSpy).reset();
    }

    @Test
    public void testMarkSupported() throws Exception {
        in.markSupported();
        Mockito.verify(dataInputSpy).markSupported();
    }
}

