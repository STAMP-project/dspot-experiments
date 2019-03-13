/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.codec;


import Codec2.DecodeResult.NEED_MORE_INPUT;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Codec2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class TelnetCodecTest {
    protected Codec2 codec;

    byte[] UP = new byte[]{ 27, 91, 65 };

    byte[] DOWN = new byte[]{ 27, 91, 66 };

    // ======================================================
    URL url = URL.valueOf("dubbo://10.20.30.40:20880");

    @Test
    public void testDecode_String_ClientSide() throws IOException {
        testDecode_assertEquals("aaa".getBytes(), "aaa", false);
    }

    @Test
    public void testDecode_BlankMessage() throws IOException {
        testDecode_assertEquals(new byte[]{  }, NEED_MORE_INPUT);
    }

    @Test
    public void testDecode_String_NoEnter() throws IOException {
        testDecode_assertEquals("aaa", NEED_MORE_INPUT);
    }

    @Test
    public void testDecode_String_WithEnter() throws IOException {
        testDecode_assertEquals("aaa\n", "aaa");
    }

    @Test
    public void testDecode_String_MiddleWithEnter() throws IOException {
        testDecode_assertEquals("aaa\r\naaa", NEED_MORE_INPUT);
    }

    @Test
    public void testDecode_Person_ObjectOnly() throws IOException {
        testDecode_assertEquals(new TelnetCodecTest.Person(), NEED_MORE_INPUT);
    }

    @Test
    public void testDecode_Person_WithEnter() throws IOException {
        testDecode_PersonWithEnterByte(new byte[]{ '\r', '\n' }, false);// windows end

        testDecode_PersonWithEnterByte(new byte[]{ '\n', '\r' }, true);
        testDecode_PersonWithEnterByte(new byte[]{ '\n' }, false);// linux end

        testDecode_PersonWithEnterByte(new byte[]{ '\r' }, true);
        testDecode_PersonWithEnterByte(new byte[]{ '\r', 100 }, true);
    }

    @Test
    public void testDecode_WithExitByte() throws IOException {
        HashMap<byte[], Boolean> exitbytes = new HashMap<byte[], Boolean>();
        exitbytes.put(new byte[]{ 3 }, true);/* Windows Ctrl+C */

        exitbytes.put(new byte[]{ 1, 3 }, false);// must equal the bytes

        exitbytes.put(new byte[]{ -1, -12, -1, -3, 6 }, true);/* Linux Ctrl+C */

        exitbytes.put(new byte[]{ 1, -1, -12, -1, -3, 6 }, false);// must equal the bytes

        exitbytes.put(new byte[]{ -1, -19, -1, -3, 6 }, true);/* Linux Pause */

        for (Map.Entry<byte[], Boolean> entry : exitbytes.entrySet()) {
            testDecode_WithExitByte(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void testDecode_Backspace() throws IOException {
        // 32 8 first add space and then add backspace.
        testDecode_assertEquals(new byte[]{ '\b' }, NEED_MORE_INPUT, new String(new byte[]{ 32, 8 }));
        // test chinese
        byte[] chineseBytes = "?".getBytes();
        byte[] request = join(chineseBytes, new byte[]{ '\b' });
        testDecode_assertEquals(request, NEED_MORE_INPUT, new String(new byte[]{ 32, 32, 8, 8 }));
        // There may be some problem handling chinese (negative number recognition). Ignoring this problem, the backspace key is only meaningfully input in a real telnet program.
        testDecode_assertEquals(new byte[]{ 'a', 'x', -1, 'x', '\b' }, NEED_MORE_INPUT, new String(new byte[]{ 32, 32, 8, 8 }));
    }

    @Test
    public void testDecode_Backspace_WithError() throws IOException {
        Assertions.assertThrows(IOException.class, () -> {
            url = url.addParameter(AbstractMockChannel.ERROR_WHEN_SEND, Boolean.TRUE.toString());
            testDecode_Backspace();
            url = url.removeParameter(AbstractMockChannel.ERROR_WHEN_SEND);
        });
    }

    @Test
    public void testDecode_History_UP() throws IOException {
        // init channel
        AbstractMockChannel channel = getServerSideChannel(url);
        testDecode_assertEquals(channel, UP, NEED_MORE_INPUT, null);
        String request1 = "aaa\n";
        Object expected1 = "aaa";
        // init history
        testDecode_assertEquals(channel, request1, expected1, null);
        testDecode_assertEquals(channel, UP, NEED_MORE_INPUT, expected1);
    }

    @Test
    public void testDecode_UPorDOWN_WithError() throws IOException {
        Assertions.assertThrows(IOException.class, () -> {
            url = url.addParameter(AbstractMockChannel.ERROR_WHEN_SEND, Boolean.TRUE.toString());
            // init channel
            AbstractMockChannel channel = getServerSideChannel(url);
            testDecode_assertEquals(channel, UP, NEED_MORE_INPUT, null);
            String request1 = "aaa\n";
            Object expected1 = "aaa";
            // init history
            testDecode_assertEquals(channel, request1, expected1, null);
            testDecode_assertEquals(channel, UP, NEED_MORE_INPUT, expected1);
            url = url.removeParameter(AbstractMockChannel.ERROR_WHEN_SEND);
        });
    }

    // =============================================================================================================================
    @Test
    public void testEncode_String_ClientSide() throws IOException {
        testEecode_assertEquals("aaa", "aaa\r\n".getBytes(), false);
    }

    /* @Test()
    public void testDecode_History_UP_DOWN_MULTI() throws IOException{
    AbstractMockChannel channel = getServerSideChannel(url);

    String request1 = "aaa\n"; 
    Object expected1 = request1.replace("\n", "");
    //init history 
    testDecode_assertEquals(channel, request1, expected1, null);

    String request2 = "bbb\n"; 
    Object expected2 = request2.replace("\n", "");
    //init history 
    testDecode_assertEquals(channel, request2, expected2, null);

    String request3 = "ccc\n"; 
    Object expected3= request3.replace("\n", "");
    //init history 
    testDecode_assertEquals(channel, request3, expected3, null);

    byte[] UP = new byte[] {27, 91, 65};
    byte[] DOWN = new byte[] {27, 91, 66};
    //history[aaa,bbb,ccc]
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected3);
    testDecode_assertEquals(channel, DOWN, Codec.NEED_MORE_INPUT, expected3);
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected2);
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected1);
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected1);
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected1);
    testDecode_assertEquals(channel, DOWN, Codec.NEED_MORE_INPUT, expected2);
    testDecode_assertEquals(channel, DOWN, Codec.NEED_MORE_INPUT, expected3);
    testDecode_assertEquals(channel, DOWN, Codec.NEED_MORE_INPUT, expected3);
    testDecode_assertEquals(channel, DOWN, Codec.NEED_MORE_INPUT, expected3);
    testDecode_assertEquals(channel, UP, Codec.NEED_MORE_INPUT, expected2);
    }
     */
    // ======================================================
    public static class Person implements Serializable {
        private static final long serialVersionUID = 3362088148941547337L;

        public String name;

        public String sex;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            result = (prime * result) + ((sex) == null ? 0 : sex.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            TelnetCodecTest.Person other = ((TelnetCodecTest.Person) (obj));
            if ((name) == null) {
                if ((other.name) != null)
                    return false;

            } else
                if (!(name.equals(other.name)))
                    return false;


            if ((sex) == null) {
                if ((other.sex) != null)
                    return false;

            } else
                if (!(sex.equals(other.sex)))
                    return false;


            return true;
        }
    }
}

