/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.common;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class BitSourceTestCase extends Assert {
    @Test
    public void testSource() {
        byte[] bytes = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        BitSource source = new BitSource(bytes);
        Assert.assertEquals(40, source.available());
        Assert.assertEquals(0, source.readBits(1));
        Assert.assertEquals(39, source.available());
        Assert.assertEquals(0, source.readBits(6));
        Assert.assertEquals(33, source.available());
        Assert.assertEquals(1, source.readBits(1));
        Assert.assertEquals(32, source.available());
        Assert.assertEquals(2, source.readBits(8));
        Assert.assertEquals(24, source.available());
        Assert.assertEquals(12, source.readBits(10));
        Assert.assertEquals(14, source.available());
        Assert.assertEquals(16, source.readBits(8));
        Assert.assertEquals(6, source.available());
        Assert.assertEquals(5, source.readBits(6));
        Assert.assertEquals(0, source.available());
    }
}

