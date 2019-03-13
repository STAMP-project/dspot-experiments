/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.io.buffer;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Arjen Poutsma
 */
@RunWith(Parameterized.class)
public class PooledDataBufferTests {
    @Parameterized.Parameter
    public DataBufferFactory dataBufferFactory;

    @Test
    public void retainAndRelease() {
        PooledDataBuffer buffer = createDataBuffer(1);
        buffer.write(((byte) ('a')));
        buffer.retain();
        boolean result = buffer.release();
        Assert.assertFalse(result);
        result = buffer.release();
        Assert.assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void tooManyReleases() {
        PooledDataBuffer buffer = createDataBuffer(1);
        buffer.write(((byte) ('a')));
        buffer.release();
        buffer.release();
    }
}

