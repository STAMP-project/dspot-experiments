/**
 * ========================================================================
 */
/**
 * Copyright 2012 David Yu
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff;


/**
 * Tests for {@link CodedInput}.
 *
 * @author Max Lanin
 * @unknown Dec 22, 2012
 */
public class AmplCodedInputTest extends io.protostuff.AbstractTest {
    public void testSkipFieldOverTheBufferBoundary() throws java.lang.Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
        int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
        int msgLength = 10;
        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
        for (int i = 1; i <= msgLength; i++)
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);

        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
        byte[] data = out.toByteArray();
        io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
        ci.pushLimit((msgLength + 2));// +2 for tag and length

        junit.framework.TestCase.assertEquals(tag, ci.readTag());
        ci.skipField(tag);
        junit.framework.TestCase.assertEquals(0, ci.readTag());
    }
}

