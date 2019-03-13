/**
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.script;


import com.google.common.primitives.Bytes;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static Script.MAX_SCRIPT_ELEMENT_SIZE;


public class ScriptChunkTest {
    private static final Random RANDOM = new Random(42);

    @Test
    public void testShortestPossibleDataPush() {
        Assert.assertTrue("empty push", isShortestPossiblePushData());
        for (byte i = -1; i < 127; i++)
            Assert.assertTrue(("push of single byte " + i), isShortestPossiblePushData());

        for (int len = 2; len < (MAX_SCRIPT_ELEMENT_SIZE); len++)
            Assert.assertTrue((("push of " + len) + " bytes"), isShortestPossiblePushData());

        // non-standard chunks
        for (byte i = 1; i <= 16; i++)
            Assert.assertFalse(("push of smallnum " + i), new ScriptChunk(1, new byte[]{ i }).isShortestPossiblePushData());

        Assert.assertFalse("push of 75 bytes", isShortestPossiblePushData());
        Assert.assertFalse("push of 255 bytes", isShortestPossiblePushData());
        Assert.assertFalse("push of 65535 bytes", isShortestPossiblePushData());
    }

    @Test
    public void testToByteArray_opcode() {
        byte[] expected = new byte[]{ ScriptOpCodes.OP_IF };
        byte[] actual = toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testToByteArray_smallNum() {
        byte[] expected = new byte[]{ ScriptOpCodes.OP_0 };
        byte[] actual = toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testToByteArray_lt_OP_PUSHDATA1() {
        // < OP_PUSHDATA1
        for (byte len = 1; len < (ScriptOpCodes.OP_PUSHDATA1); len++) {
            byte[] bytes = new byte[len];
            ScriptChunkTest.RANDOM.nextBytes(bytes);
            byte[] expected = Bytes.concat(new byte[]{ len }, bytes);
            byte[] actual = new ScriptChunk(len, bytes).toByteArray();
            Assert.assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void testToByteArray_OP_PUSHDATA1() {
        // OP_PUSHDATA1
        byte[] bytes = new byte[255];
        ScriptChunkTest.RANDOM.nextBytes(bytes);
        byte[] expected = Bytes.concat(new byte[]{ ScriptOpCodes.OP_PUSHDATA1, ((byte) (255)) }, bytes);
        byte[] actual = toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testToByteArray_OP_PUSHDATA2() {
        // OP_PUSHDATA2
        byte[] bytes = new byte[258];
        ScriptChunkTest.RANDOM.nextBytes(bytes);
        byte[] expected = Bytes.concat(new byte[]{ ScriptOpCodes.OP_PUSHDATA2, 2, 1 }, bytes);
        byte[] actual = toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testToByteArray_OP_PUSHDATA4() {
        // OP_PUSHDATA4
        byte[] bytes = new byte[258];
        ScriptChunkTest.RANDOM.nextBytes(bytes);
        byte[] expected = Bytes.concat(new byte[]{ ScriptOpCodes.OP_PUSHDATA4, 2, 1, 0, 0 }, bytes);
        byte[] actual = toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }
}

