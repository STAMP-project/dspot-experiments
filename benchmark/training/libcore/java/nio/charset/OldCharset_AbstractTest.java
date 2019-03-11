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
package libcore.java.nio.charset;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import junit.framework.TestCase;


/**
 * Super class for concrete charset test suites.
 */
public abstract class OldCharset_AbstractTest extends TestCase {
    static String charsetName;

    private static Charset charset;

    static CharsetDecoder decoder;

    static CharsetEncoder encoder;

    static final int[] codes = Charset_TestGenerator.codes;

    static final char[] chars = new char[OldCharset_AbstractTest.codes.length];// Is filled with


    // contents of codes.
    static char[] testChars;

    static byte[] testBytes;

    public void test_nameMatch() {
        TestCase.assertEquals("Name of charset must match!", OldCharset_AbstractTest.charsetName, OldCharset_AbstractTest.charset.name());
    }

    public void test_dumpEncodableChars() {
        if ((OldCharset_AbstractTest.testChars) == null)
            return;

        if ((OldCharset_AbstractTest.testChars.length) > 0)
            return;

        System.out.format("\ntest_dumpEncodableChars() for name %s => %s (class = %s)\n", OldCharset_AbstractTest.charsetName, OldCharset_AbstractTest.charset.name(), getClass().getName());
        Charset_TestGenerator.Dumper out = new Charset_TestGenerator.Dumper1(16);
        int code = 0;
        while (code < 256) {
            while (!(OldCharset_AbstractTest.encoder.canEncode(((char) (code)))))
                code++;

            if (code < 65536) {
                out.consume(code);
                code += 1;
            }
        } 
        while (code < 65536) {
            while (!(OldCharset_AbstractTest.encoder.canEncode(((char) (code)))))
                code++;

            if (code < 65536) {
                out.consume(code);
                code += 20;
            }
        } 
        System.out.println();
        System.out.println(("Encodable Chars dumped for Test Class " + (getClass().getName())));
        TestCase.fail(("Encodable Chars dumped for Test Class " + (getClass().getName())));
    }

    public void test_dumpEncoded() throws CharacterCodingException {
        if ((OldCharset_AbstractTest.testChars) == null)
            return;

        if ((OldCharset_AbstractTest.testChars.length) == 0)
            return;

        if ((OldCharset_AbstractTest.testBytes) != null)
            return;

        System.out.format("\ntest_dumpEncoded() for name %s => %s (class = %s)\n", OldCharset_AbstractTest.charsetName, OldCharset_AbstractTest.charset.name(), getClass().getName());
        Charset_TestGenerator.Dumper out = new Charset_TestGenerator.Dumper1();
        CharBuffer inputCB = CharBuffer.wrap(OldCharset_AbstractTest.testChars);
        ByteBuffer outputBB;
        OldCharset_AbstractTest.encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        outputBB = OldCharset_AbstractTest.encoder.encode(inputCB);
        outputBB.rewind();
        while (outputBB.hasRemaining()) {
            out.consume(((outputBB.get()) & 255));
        } 
        System.out.println();
        System.out.println(("Encoded Bytes dumped for Test Class " + (getClass().getName())));
        TestCase.fail(("Encoded Bytes dumped for Test Class " + (getClass().getName())));
    }

    public void test_Decode() throws CharacterCodingException {
        OldCharset_AbstractTest.decode(OldCharset_AbstractTest.testBytes, OldCharset_AbstractTest.testChars);
    }

    public void test_Encode() throws CharacterCodingException {
        CharBuffer inputCB = CharBuffer.wrap(OldCharset_AbstractTest.testChars);
        ByteBuffer outputBB;
        OldCharset_AbstractTest.encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        outputBB = OldCharset_AbstractTest.encoder.encode(inputCB);
        outputBB.rewind();
        // assertTrue("Encoded bytes must match!",
        // Arrays.equals(testBytes, outputBB.array()));
        OldCharset_AbstractTest.assertEqualBytes("Encoded bytes must match!", OldCharset_AbstractTest.testBytes, outputBB);
    }

    public void test_CodecDynamic() throws CharacterCodingException {
        OldCharset_AbstractTest.encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        OldCharset_AbstractTest.decoder.onMalformedInput(CodingErrorAction.REPORT);
        CharBuffer inputCB = CharBuffer.allocate(65536);
        for (int code = 32; code <= 65533; ++code) {
            // icu4c seems to accept any surrogate as a sign that "more is coming",
            // even for charsets like US-ASCII. http://b/10310751
            if ((code >= 55296) && (code <= 57343)) {
                continue;
            }
            if (OldCharset_AbstractTest.encoder.canEncode(((char) (code)))) {
                inputCB.put(((char) (code)));
            }
        }
        inputCB.rewind();
        ByteBuffer intermediateBB = OldCharset_AbstractTest.encoder.encode(inputCB);
        inputCB.rewind();
        intermediateBB.rewind();
        CharBuffer outputCB = OldCharset_AbstractTest.decoder.decode(intermediateBB);
        outputCB.rewind();
        OldCharset_AbstractTest.assertEqualCBs("decode(encode(A)) must be identical with A!", inputCB, outputCB);
    }

    abstract static class CodesGenerator {
        int row = 0;

        int col = 0;

        abstract void consume(int code);

        boolean isAccepted(int code) {
            return Character.isLetterOrDigit(code);
        }
    }
}

