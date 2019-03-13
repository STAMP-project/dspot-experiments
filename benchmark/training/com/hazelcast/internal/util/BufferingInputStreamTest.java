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
package com.hazelcast.internal.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BufferingInputStreamTest {
    private final byte[] mockInput = new byte[]{ 1, 2, 3, 4 };

    private BufferingInputStream in;

    @Test
    public void readByteByByte() throws Exception {
        for (byte b : mockInput) {
            Assert.assertEquals(b, ((byte) (in.read())));
        }
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void readBufByBuf() throws Exception {
        // given
        byte[] buf = new byte[(mockInput.length) / 2];
        int streamPos = 0;
        // when - then
        for (int count; (count = in.read(buf)) != (-1);) {
            for (int i = 0; i < count; i++) {
                Assert.assertEquals(mockInput[(streamPos++)], buf[i]);
            }
        }
        Assert.assertEquals(mockInput.length, streamPos);
    }
}

