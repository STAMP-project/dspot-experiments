/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import junit.framework.Assert;
import junit.framework.TestCase;


public class OldAndroidDataInputStreamTest extends TestCase {
    public void testDataInputStream() throws Exception {
        String str = "AbCdEfGhIjKlM\nOpQ\rStUvWxYz";
        ByteArrayInputStream aa = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream ba = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream ca = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream da = new ByteArrayInputStream(str.getBytes());
        DataInputStream a = new DataInputStream(aa);
        try {
            Assert.assertEquals(str, OldAndroidDataInputStreamTest.read(a));
        } finally {
            a.close();
        }
        DataInputStream b = new DataInputStream(ba);
        try {
            Assert.assertEquals("AbCdEfGhIj", OldAndroidDataInputStreamTest.read(b, 10));
        } finally {
            b.close();
        }
        DataInputStream c = new DataInputStream(ca);
        try {
            Assert.assertEquals("bdfhjl\np\rtvxz", OldAndroidDataInputStreamTest.skipRead(c));
        } finally {
            c.close();
        }
        DataInputStream d = new DataInputStream(da);
        try {
            TestCase.assertEquals("AbCdEfGhIjKlM", d.readLine());
            TestCase.assertEquals("OpQ", d.readLine());
            TestCase.assertEquals("StUvWxYz", d.readLine());
        } finally {
            d.close();
        }
        ByteArrayOutputStream e = new ByteArrayOutputStream();
        DataOutputStream f = new DataOutputStream(e);
        try {
            f.writeBoolean(true);
            f.writeByte('a');
            f.writeBytes("BCD");
            f.writeChar('e');
            f.writeChars("FGH");
            f.writeUTF("ijklm");
            f.writeDouble(1);
            f.writeFloat(2);
            f.writeInt(3);
            f.writeLong(4);
            f.writeShort(5);
        } finally {
            f.close();
        }
        ByteArrayInputStream ga = new ByteArrayInputStream(e.toByteArray());
        DataInputStream g = new DataInputStream(ga);
        try {
            TestCase.assertTrue(g.readBoolean());
            TestCase.assertEquals('a', g.readByte());
            TestCase.assertEquals(2, g.skipBytes(2));
            TestCase.assertEquals('D', g.readByte());
            TestCase.assertEquals('e', g.readChar());
            TestCase.assertEquals('F', g.readChar());
            TestCase.assertEquals('G', g.readChar());
            TestCase.assertEquals('H', g.readChar());
            TestCase.assertEquals("ijklm", g.readUTF());
            TestCase.assertEquals(1, g.readDouble(), 0);
            TestCase.assertEquals(2.0F, g.readFloat(), 0.0F);
            TestCase.assertEquals(3, g.readInt());
            TestCase.assertEquals(4, g.readLong());
            TestCase.assertEquals(5, g.readShort());
        } finally {
            g.close();
        }
    }
}

