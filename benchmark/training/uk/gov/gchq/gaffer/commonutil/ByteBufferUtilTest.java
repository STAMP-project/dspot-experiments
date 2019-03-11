/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test class is copied from org.apache.accumulo.core.util.ByteByfferUtilTest.
 */
public class ByteBufferUtilTest {
    @Test
    public void testNonZeroArrayOffset() {
        byte[] data = "0123456789".getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb1 = ByteBuffer.wrap(data, 3, 4);
        // create a ByteBuffer with a non-zero array offset
        ByteBuffer bb2 = bb1.slice();
        // The purpose of this test is to ensure ByteBufferUtil code works when arrayOffset is non-zero. The following asserts are not to test ByteBuffer, but
        // ensure the behavior of slice() is as expected.
        Assert.assertEquals(3, bb2.arrayOffset());
        Assert.assertEquals(0, bb2.position());
        Assert.assertEquals(4, bb2.limit());
        // start test with non zero arrayOffset
        ByteBufferUtilTest.assertEquals("3456", bb2);
        // read one byte from byte buffer... this should cause position to be non-zero in addition to array offset
        bb2.get();
        ByteBufferUtilTest.assertEquals("456", bb2);
    }

    @Test
    public void testZeroArrayOffsetAndNonZeroPosition() {
        byte[] data = "0123456789".getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb1 = ByteBuffer.wrap(data, 3, 4);
        ByteBufferUtil.toText(bb1).toString();
        ByteBufferUtilTest.assertEquals("3456", bb1);
    }

    @Test
    public void testZeroArrayOffsetAndPosition() {
        byte[] data = "0123456789".getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb1 = ByteBuffer.wrap(data, 0, 4);
        ByteBufferUtilTest.assertEquals("0123", bb1);
    }

    @Test
    public void testDirectByteBuffer() {
        // allocate direct so it does not have a backing array
        ByteBuffer bb = ByteBuffer.allocateDirect(10);
        bb.put("0123456789".getBytes(StandardCharsets.UTF_8));
        bb.rewind();
        ByteBufferUtilTest.assertEquals("0123456789", bb);
        // advance byte buffer position
        bb.get();
        ByteBufferUtilTest.assertEquals("123456789", bb);
    }
}

