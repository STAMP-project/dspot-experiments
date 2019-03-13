/**
 * Copyright (C)2009 - SSHJ Contributors
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
/**
 * Copyright 2010, 2011 sshj contributors
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
package net.schmizz.sshj.util;


import Buffer.BufferException;
import Buffer.PlainBuffer;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import net.schmizz.sshj.common.Buffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Buffer} functionality
 */
public class BufferTest {
    private PlainBuffer posBuf;

    private PlainBuffer handyBuf;

    @Test
    public void testDataTypes() throws BufferException {
        // bool
        Assert.assertEquals(handyBuf.putBoolean(true).readBoolean(), true);
        // byte
        Assert.assertEquals(handyBuf.putByte(((byte) (10))).readByte(), ((byte) (10)));
        // byte array
        Assert.assertArrayEquals(handyBuf.putBytes("some string".getBytes()).readBytes(), "some string".getBytes());
        // mpint
        BigInteger bi = new BigInteger("1111111111111111111111111111111");
        Assert.assertEquals(handyBuf.putMPInt(bi).readMPInt(), bi);
        // string
        Assert.assertEquals(handyBuf.putString("some string").readString(), "some string");
        // uint32
        Assert.assertEquals(handyBuf.putUInt32(4294967295L).readUInt32(), 4294967295L);
    }

    @Test
    public void testPassword() throws BufferException {
        char[] pass = "lolcatz".toCharArray();
        // test if put correctly as a string
        Assert.assertEquals(new Buffer.PlainBuffer().putSensitiveString(pass).readString(), "lolcatz");
        // test that char[] was blanked out
        Assert.assertArrayEquals(pass, "       ".toCharArray());
    }

    @Test
    public void testPosition() throws BufferException, UnsupportedEncodingException {
        Assert.assertEquals(5, posBuf.wpos());
        Assert.assertEquals(0, posBuf.rpos());
        Assert.assertEquals(5, posBuf.available());
        // read some bytes
        byte b = posBuf.readByte();
        Assert.assertEquals(b, ((byte) ('H')));
        Assert.assertEquals(1, posBuf.rpos());
        Assert.assertEquals(4, posBuf.available());
    }

    @Test
    public void testPublickey() {
        // TODO stub
    }

    @Test
    public void testSignature() {
        // TODO stub
    }

    @Test(expected = BufferException.class)
    public void testUnderflow() throws BufferException {
        // exhaust the buffer
        for (int i = 0; i < 5; ++i)
            posBuf.readByte();

        // underflow
        posBuf.readByte();
    }
}

