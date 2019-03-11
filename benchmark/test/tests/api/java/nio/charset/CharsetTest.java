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
package tests.api.java.nio.charset;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import junit.framework.TestCase;


/**
 * Test class java.nio.Charset.
 */
public class CharsetTest extends TestCase {
    public void test_allAvailableCharsets() throws Exception {
        // Check that we can instantiate every Charset, CharsetDecoder, and CharsetEncoder.
        for (String charsetName : Charset.availableCharsets().keySet()) {
            if (charsetName.equals("UTF-32")) {
                // Our UTF-32 is broken. http://b/2702411
                // TODO: remove this hack when UTF-32 is fixed.
                continue;
            }
            Charset cs = Charset.forName(charsetName);
            TestCase.assertNotNull(cs.newDecoder());
            if (cs.canEncode()) {
                CharsetEncoder enc = cs.newEncoder();
                TestCase.assertNotNull(enc);
                TestCase.assertNotNull(enc.replacement());
            }
        }
    }

    public void test_defaultCharset() {
        TestCase.assertEquals("UTF-8", Charset.defaultCharset().name());
    }

    public void test_isRegistered() {
        // Regression for HARMONY-45
        // Will contain names of charsets registered with IANA
        Set<String> knownRegisteredCharsets = new HashSet<String>();
        // Will contain names of charsets not known to be registered with IANA
        Set<String> unknownRegisteredCharsets = new HashSet<String>();
        Set<String> names = Charset.availableCharsets().keySet();
        for (Iterator nameItr = names.iterator(); nameItr.hasNext();) {
            String name = ((String) (nameItr.next()));
            if (name.toLowerCase(Locale.ROOT).startsWith("x-")) {
                unknownRegisteredCharsets.add(name);
            } else {
                knownRegisteredCharsets.add(name);
            }
        }
        for (Iterator nameItr = knownRegisteredCharsets.iterator(); nameItr.hasNext();) {
            String name = ((String) (nameItr.next()));
            Charset cs = Charset.forName(name);
            if (!(cs.isRegistered())) {
                System.err.println(((((("isRegistered was false for " + name) + " ") + (cs.name())) + " ") + (cs.aliases())));
            }
            TestCase.assertTrue(((((("isRegistered was false for " + name) + " ") + (cs.name())) + " ") + (cs.aliases())), cs.isRegistered());
        }
        for (Iterator nameItr = unknownRegisteredCharsets.iterator(); nameItr.hasNext();) {
            String name = ((String) (nameItr.next()));
            Charset cs = Charset.forName(name);
            TestCase.assertFalse(((((("isRegistered was true for " + name) + " ") + (cs.name())) + " ") + (cs.aliases())), cs.isRegistered());
        }
    }

    public void test_guaranteedCharsetsAvailable() throws Exception {
        // All Java implementations must support these charsets.
        TestCase.assertNotNull(Charset.forName("ISO-8859-1"));
        TestCase.assertNotNull(Charset.forName("US-ASCII"));
        TestCase.assertNotNull(Charset.forName("UTF-16"));
        TestCase.assertNotNull(Charset.forName("UTF-16BE"));
        TestCase.assertNotNull(Charset.forName("UTF-16LE"));
        TestCase.assertNotNull(Charset.forName("UTF-8"));
    }

    // http://code.google.com/p/android/issues/detail?id=42769
    public void test_42769() throws Exception {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; ++i) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 50; ++i) {
                        Charset.availableCharsets();
                    }
                }
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    public void test_have_canonical_EUC_JP() throws Exception {
        TestCase.assertEquals("EUC-JP", Charset.forName("EUC-JP").name());
    }

    public void test_EUC_JP_replacement_character() throws Exception {
        // We have text either side of the replacement character, because all kinds of errors
        // could lead to a replacement character being returned.
        assertEncodes(Charset.forName("EUC-JP"), " \ufffd ", ' ', 244, 254, ' ');
        assertDecodes(Charset.forName("EUC-JP"), " \ufffd ", ' ', 244, 254, ' ');
    }

    public void test_SCSU_replacement_character() throws Exception {
        // We have text either side of the replacement character, because all kinds of errors
        // could lead to a replacement character being returned.
        assertEncodes(Charset.forName("SCSU"), " \ufffd ", ' ', 14, 255, 253, ' ');
        assertDecodes(Charset.forName("SCSU"), " \ufffd ", ' ', 14, 255, 253, ' ');
    }

    public void test_Shift_JIS_replacement_character() throws Exception {
        // We have text either side of the replacement character, because all kinds of errors
        // could lead to a replacement character being returned.
        assertEncodes(Charset.forName("Shift_JIS"), " \ufffd ", ' ', 252, 252, ' ');
        assertDecodes(Charset.forName("Shift_JIS"), " \ufffd ", ' ', 252, 252, ' ');
    }

    public void test_UTF_16() throws Exception {
        Charset cs = Charset.forName("UTF-16");
        // Writes big-endian, with a big-endian BOM.
        assertEncodes(cs, "a\u0666", 254, 255, 0, 'a', 6, 102);
        // Reads whatever the BOM tells it to read...
        assertDecodes(cs, "a\u0666", 254, 255, 0, 'a', 6, 102);
        assertDecodes(cs, "a\u0666", 255, 254, 'a', 0, 102, 6);
        // ...and defaults to reading big-endian if there's no BOM.
        assertDecodes(cs, "a\u0666", 0, 'a', 6, 102);
    }

    public void test_UTF_16BE() throws Exception {
        Charset cs = Charset.forName("UTF-16BE");
        // Writes big-endian, with no BOM.
        assertEncodes(cs, "a\u0666", 0, 'a', 6, 102);
        // Treats a little-endian BOM as an error and continues to read big-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 255, 254, 0, 'a', 6, 102);
        // Accepts a big-endian BOM and includes U+FEFF in the decoded output.
        assertDecodes(cs, "\ufeffa\u0666", 254, 255, 0, 'a', 6, 102);
        // Defaults to reading big-endian.
        assertDecodes(cs, "a\u0666", 0, 'a', 6, 102);
    }

    public void test_UTF_16LE() throws Exception {
        Charset cs = Charset.forName("UTF-16LE");
        // Writes little-endian, with no BOM.
        assertEncodes(cs, "a\u0666", 'a', 0, 102, 6);
        // Accepts a little-endian BOM and includes U+FEFF in the decoded output.
        assertDecodes(cs, "\ufeffa\u0666", 255, 254, 'a', 0, 102, 6);
        // Treats a big-endian BOM as an error and continues to read little-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 254, 255, 'a', 0, 102, 6);
        // Defaults to reading little-endian.
        assertDecodes(cs, "a\u0666", 'a', 0, 102, 6);
    }

    public void test_x_UTF_16LE_BOM() throws Exception {
        Charset cs = Charset.forName("x-UTF-16LE-BOM");
        // Writes little-endian, with a BOM.
        assertEncodes(cs, "a\u0666", 255, 254, 'a', 0, 102, 6);
        // Accepts a little-endian BOM and swallows the BOM.
        assertDecodes(cs, "a\u0666", 255, 254, 'a', 0, 102, 6);
        // Swallows a big-endian BOM, but continues to read little-endian!
        assertDecodes(cs, "\u6100\u6606", 254, 255, 'a', 0, 102, 6);
        // Defaults to reading little-endian.
        assertDecodes(cs, "a\u0666", 'a', 0, 102, 6);
    }

    public void test_UTF_32() throws Exception {
        Charset cs = Charset.forName("UTF-32");
        // Writes big-endian, with no BOM.
        assertEncodes(cs, "a\u0666", 0, 0, 0, 'a', 0, 0, 6, 102);
        // Reads whatever the BOM tells it to read...
        assertDecodes(cs, "a\u0666", 0, 0, 254, 255, 0, 0, 0, 'a', 0, 0, 6, 102);
        assertDecodes(cs, "a\u0666", 255, 254, 0, 0, 'a', 0, 0, 0, 102, 6, 0, 0);
        // ...and defaults to reading big-endian if there's no BOM.
        assertDecodes(cs, "a\u0666", 0, 0, 0, 'a', 0, 0, 6, 102);
    }

    public void test_UTF_32BE() throws Exception {
        Charset cs = Charset.forName("UTF-32BE");
        // Writes big-endian, with no BOM.
        assertEncodes(cs, "a\u0666", 0, 0, 0, 'a', 0, 0, 6, 102);
        // Treats a little-endian BOM as an error and continues to read big-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 255, 254, 0, 0, 0, 0, 0, 'a', 0, 0, 6, 102);
        // Accepts a big-endian BOM and swallows the BOM.
        assertDecodes(cs, "a\u0666", 0, 0, 254, 255, 0, 0, 0, 'a', 0, 0, 6, 102);
        // Defaults to reading big-endian.
        assertDecodes(cs, "a\u0666", 0, 0, 0, 'a', 0, 0, 6, 102);
    }

    public void test_UTF_32LE() throws Exception {
        Charset cs = Charset.forName("UTF-32LE");
        // Writes little-endian, with no BOM.
        assertEncodes(cs, "a\u0666", 'a', 0, 0, 0, 102, 6, 0, 0);
        // Accepts a little-endian BOM and swallows the BOM.
        assertDecodes(cs, "a\u0666", 255, 254, 0, 0, 'a', 0, 0, 0, 102, 6, 0, 0);
        // Treats a big-endian BOM as an error and continues to read little-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 0, 0, 254, 255, 'a', 0, 0, 0, 102, 6, 0, 0);
        // Defaults to reading little-endian.
        assertDecodes(cs, "a\u0666", 'a', 0, 0, 0, 102, 6, 0, 0);
    }

    public void test_X_UTF_32BE_BOM() throws Exception {
        Charset cs = Charset.forName("X-UTF-32BE-BOM");
        // Writes big-endian, with a big-endian BOM.
        assertEncodes(cs, "a\u0666", 0, 0, 254, 255, 0, 0, 0, 'a', 0, 0, 6, 102);
        // Treats a little-endian BOM as an error and continues to read big-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 255, 254, 0, 0, 0, 0, 0, 'a', 0, 0, 6, 102);
        // Swallows a big-endian BOM, and continues to read big-endian.
        assertDecodes(cs, "a\u0666", 0, 0, 254, 255, 0, 0, 0, 'a', 0, 0, 6, 102);
        // Defaults to reading big-endian.
        assertDecodes(cs, "a\u0666", 0, 0, 0, 'a', 0, 0, 6, 102);
    }

    public void test_X_UTF_32LE_BOM() throws Exception {
        Charset cs = Charset.forName("X-UTF-32LE-BOM");
        // Writes little-endian, with a little-endian BOM.
        assertEncodes(cs, "a\u0666", 255, 254, 0, 0, 'a', 0, 0, 0, 102, 6, 0, 0);
        // Accepts a little-endian BOM and swallows the BOM.
        assertDecodes(cs, "a\u0666", 255, 254, 0, 0, 'a', 0, 0, 0, 102, 6, 0, 0);
        // Treats a big-endian BOM as an error and continues to read little-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(cs, "\ufffda\u0666", 0, 0, 254, 255, 'a', 0, 0, 0, 102, 6, 0, 0);
        // Defaults to reading little-endian.
        assertDecodes(cs, "a\u0666", 'a', 0, 0, 0, 102, 6, 0, 0);
    }

    public void test_forNameLjava_lang_String() {
        // Invoke forName two times with the same canonical name.
        // It should return the same reference.
        Charset cs1 = Charset.forName("UTF-8");
        Charset cs2 = Charset.forName("UTF-8");
        TestCase.assertSame(cs1, cs2);
        // test forName: invoke forName two times for the same Charset using
        // canonical name and alias, it should return the same reference.
        Charset cs3 = Charset.forName("ASCII");
        Charset cs4 = Charset.forName("US-ASCII");
        TestCase.assertSame(cs3, cs4);
    }

    static CharsetTest.MockCharset charset1 = new CharsetTest.MockCharset("mockCharset00", new String[]{ "mockCharset01", "mockCharset02" });

    static CharsetTest.MockCharset charset2 = new CharsetTest.MockCharset("mockCharset10", new String[]{ "mockCharset11", "mockCharset12" });

    // Test the required 6 charsets are supported.
    public void testRequiredCharsetSupported() {
        TestCase.assertTrue(Charset.isSupported("US-ASCII"));
        TestCase.assertTrue(Charset.isSupported("ASCII"));
        TestCase.assertTrue(Charset.isSupported("ISO-8859-1"));
        TestCase.assertTrue(Charset.isSupported("ISO8859_1"));
        TestCase.assertTrue(Charset.isSupported("UTF-8"));
        TestCase.assertTrue(Charset.isSupported("UTF8"));
        TestCase.assertTrue(Charset.isSupported("UTF-16"));
        TestCase.assertTrue(Charset.isSupported("UTF-16BE"));
        TestCase.assertTrue(Charset.isSupported("UTF-16LE"));
        Charset c1 = Charset.forName("US-ASCII");
        TestCase.assertEquals("US-ASCII", Charset.forName("US-ASCII").name());
        TestCase.assertEquals("US-ASCII", Charset.forName("ASCII").name());
        TestCase.assertEquals("ISO-8859-1", Charset.forName("ISO-8859-1").name());
        TestCase.assertEquals("ISO-8859-1", Charset.forName("ISO8859_1").name());
        TestCase.assertEquals("UTF-8", Charset.forName("UTF-8").name());
        TestCase.assertEquals("UTF-8", Charset.forName("UTF8").name());
        TestCase.assertEquals("UTF-16", Charset.forName("UTF-16").name());
        TestCase.assertEquals("UTF-16BE", Charset.forName("UTF-16BE").name());
        TestCase.assertEquals("UTF-16LE", Charset.forName("UTF-16LE").name());
        TestCase.assertNotSame(Charset.availableCharsets(), Charset.availableCharsets());
        // assertSame(Charset.forName("US-ASCII"), Charset.availableCharsets().get("US-ASCII"));
        // assertSame(Charset.forName("US-ASCII"), c1);
        TestCase.assertTrue(Charset.availableCharsets().containsKey("US-ASCII"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("ISO-8859-1"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("UTF-8"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("UTF-16"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("UTF-16BE"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("UTF-16LE"));
    }

    public void testIsSupported_Null() {
        try {
            Charset.isSupported(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testIsSupported_EmptyString() {
        try {
            Charset.isSupported("");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testIsSupported_InvalidInitialCharacter() {
        try {
            Charset.isSupported(".char");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testIsSupported_IllegalName() {
        try {
            Charset.isSupported(" ///#$$");
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testIsSupported_NotSupported() {
        TestCase.assertFalse(Charset.isSupported("well-formed-name-of-a-charset-that-does-not-exist"));
    }

    public void testForName_Null() {
        try {
            Charset.forName(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testForName_EmptyString() {
        try {
            Charset.forName("");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testForName_InvalidInitialCharacter() {
        try {
            Charset.forName(".char");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testForName_IllegalName() {
        try {
            Charset.forName(" ///#$$");
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testForName_NotSupported() {
        try {
            Charset.forName("impossible");
            TestCase.fail();
        } catch (UnsupportedCharsetException expected) {
        }
    }

    public void testConstructor_Normal() {
        final String mockName = "mockChar1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:-_";
        CharsetTest.MockCharset c = new CharsetTest.MockCharset(mockName, new String[]{ "mock" });
        TestCase.assertEquals(mockName, c.name());
        TestCase.assertEquals(mockName, c.displayName());
        TestCase.assertEquals(mockName, c.displayName(Locale.getDefault()));
        TestCase.assertEquals("mock", c.aliases().toArray()[0]);
        TestCase.assertEquals(1, c.aliases().toArray().length);
    }

    public void testConstructor_EmptyCanonicalName() {
        try {
            new CharsetTest.MockCharset("", new String[0]);
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testConstructor_IllegalCanonicalName_Initial() {
        try {
            new CharsetTest.MockCharset("-123", new String[]{ "mock" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testConstructor_IllegalCanonicalName_Middle() {
        try {
            new CharsetTest.MockCharset("1%%23", new String[]{ "mock" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
        try {
            new CharsetTest.MockCharset("1//23", new String[]{ "mock" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testConstructor_NullCanonicalName() {
        try {
            CharsetTest.MockCharset c = new CharsetTest.MockCharset(null, new String[]{ "mock" });
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testConstructor_NullAliases() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mockChar", null);
        TestCase.assertEquals("mockChar", c.name());
        TestCase.assertEquals("mockChar", c.displayName());
        TestCase.assertEquals("mockChar", c.displayName(Locale.getDefault()));
        TestCase.assertEquals(0, c.aliases().toArray().length);
    }

    public void testConstructor_NullAliase() {
        try {
            new CharsetTest.MockCharset("mockChar", new String[]{ "mock", null });
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testConstructor_NoAliases() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mockChar", new String[0]);
        TestCase.assertEquals("mockChar", c.name());
        TestCase.assertEquals("mockChar", c.displayName());
        TestCase.assertEquals("mockChar", c.displayName(Locale.getDefault()));
        TestCase.assertEquals(0, c.aliases().toArray().length);
    }

    public void testConstructor_EmptyAliases() {
        try {
            new CharsetTest.MockCharset("mockChar", new String[]{ "" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    // Test the constructor with illegal aliases: starting with neither a digit nor a letter.
    public void testConstructor_IllegalAliases_Initial() {
        try {
            new CharsetTest.MockCharset("mockChar", new String[]{ "mock", "-123" });
            TestCase.fail();
        } catch (IllegalCharsetNameException e) {
        }
    }

    public void testConstructor_IllegalAliases_Middle() {
        try {
            new CharsetTest.MockCharset("mockChar", new String[]{ "mock", "22##ab" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
        try {
            new CharsetTest.MockCharset("mockChar", new String[]{ "mock", "22%%ab" });
            TestCase.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    public void testAliases_Multiple() {
        final String mockName = "mockChar1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:-_";
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mockChar", new String[]{ "mock", mockName, "mock2" });
        TestCase.assertEquals("mockChar", c.name());
        TestCase.assertEquals(3, c.aliases().size());
        TestCase.assertTrue(c.aliases().contains("mock"));
        TestCase.assertTrue(c.aliases().contains(mockName));
        TestCase.assertTrue(c.aliases().contains("mock2"));
        try {
            c.aliases().clear();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testAliases_Duplicate() {
        final String mockName = "mockChar1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:-_";
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mockChar", new String[]{ "mockChar", "mock", mockName, "mock", "mockChar", "mock", "mock2" });
        TestCase.assertEquals("mockChar", c.name());
        TestCase.assertEquals(4, c.aliases().size());
        TestCase.assertTrue(c.aliases().contains("mockChar"));
        TestCase.assertTrue(c.aliases().contains("mock"));
        TestCase.assertTrue(c.aliases().contains(mockName));
        TestCase.assertTrue(c.aliases().contains("mock2"));
    }

    public void testCanEncode() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        TestCase.assertTrue(c.canEncode());
    }

    public void testIsRegistered() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        TestCase.assertTrue(c.isRegistered());
    }

    public void testDisplayName_Locale_Null() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        TestCase.assertEquals("mock", c.displayName(null));
    }

    public void testCompareTo_Normal() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        TestCase.assertEquals(0, c1.compareTo(c1));
        CharsetTest.MockCharset c2 = new CharsetTest.MockCharset("Mock", null);
        TestCase.assertEquals(0, c1.compareTo(c2));
        c2 = new CharsetTest.MockCharset("mock2", null);
        TestCase.assertTrue(((c1.compareTo(c2)) < 0));
        TestCase.assertTrue(((c2.compareTo(c1)) > 0));
        c2 = new CharsetTest.MockCharset("mack", null);
        TestCase.assertTrue(((c1.compareTo(c2)) > 0));
        TestCase.assertTrue(((c2.compareTo(c1)) < 0));
        c2 = new CharsetTest.MockCharset("m.", null);
        TestCase.assertTrue(((c1.compareTo(c2)) > 0));
        TestCase.assertTrue(((c2.compareTo(c1)) < 0));
        c2 = new CharsetTest.MockCharset("m:", null);
        TestCase.assertEquals("mock".compareToIgnoreCase("m:"), c1.compareTo(c2));
        TestCase.assertEquals("m:".compareToIgnoreCase("mock"), c2.compareTo(c1));
        c2 = new CharsetTest.MockCharset("m-", null);
        TestCase.assertTrue(((c1.compareTo(c2)) > 0));
        TestCase.assertTrue(((c2.compareTo(c1)) < 0));
        c2 = new CharsetTest.MockCharset("m_", null);
        TestCase.assertTrue(((c1.compareTo(c2)) > 0));
        TestCase.assertTrue(((c2.compareTo(c1)) < 0));
    }

    public void testCompareTo_Null() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        try {
            c1.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testCompareTo_DiffCharsetClass() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        CharsetTest.MockCharset2 c2 = new CharsetTest.MockCharset2("Mock", new String[]{ "myname" });
        TestCase.assertEquals(0, c1.compareTo(c2));
        TestCase.assertEquals(0, c2.compareTo(c1));
    }

    public void testEquals_Normal() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        CharsetTest.MockCharset2 c2 = new CharsetTest.MockCharset2("mock", null);
        TestCase.assertTrue(c1.equals(c2));
        TestCase.assertTrue(c2.equals(c1));
        c2 = new CharsetTest.MockCharset2("Mock", null);
        TestCase.assertFalse(c1.equals(c2));
        TestCase.assertFalse(c2.equals(c1));
    }

    public void testEquals_Null() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        TestCase.assertFalse(c1.equals(null));
    }

    public void testEquals_NonCharsetObject() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        TestCase.assertFalse(c1.equals("test"));
    }

    public void testEquals_DiffCharsetClass() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        CharsetTest.MockCharset2 c2 = new CharsetTest.MockCharset2("mock", null);
        TestCase.assertTrue(c1.equals(c2));
        TestCase.assertTrue(c2.equals(c1));
    }

    public void testHashCode_DiffCharsetClass() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        TestCase.assertEquals(c1.hashCode(), "mock".hashCode());
        final String mockName = "mockChar1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:-_";
        c1 = new CharsetTest.MockCharset(mockName, new String[]{ "mockChar", "mock", mockName, "mock", "mockChar", "mock", "mock2" });
        TestCase.assertEquals(mockName.hashCode(), c1.hashCode());
    }

    public void testEncode_CharBuffer_Normal() throws Exception {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("testEncode_CharBuffer_Normal_mock", null);
        ByteBuffer bb = c1.encode(CharBuffer.wrap("abcdefg"));
        TestCase.assertEquals("abcdefg", new String(bb.array(), "iso8859-1"));
        bb = c1.encode(CharBuffer.wrap(""));
        TestCase.assertEquals("", new String(bb.array(), "iso8859-1"));
    }

    public void testEncode_CharBuffer_Unmappable() throws Exception {
        Charset c1 = Charset.forName("iso8859-1");
        ByteBuffer bb = c1.encode(CharBuffer.wrap("abcd\u5d14efg"));
        TestCase.assertEquals(new String(bb.array(), "iso8859-1"), (("abcd" + (new String(c1.newEncoder().replacement(), "iso8859-1"))) + "efg"));
    }

    public void testEncode_CharBuffer_NullCharBuffer() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        try {
            c.encode(((CharBuffer) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testEncode_CharBuffer_NullEncoder() {
        CharsetTest.MockCharset2 c = new CharsetTest.MockCharset2("mock2", null);
        try {
            c.encode(CharBuffer.wrap("hehe"));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testEncode_String_Normal() throws Exception {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("testEncode_String_Normal_mock", null);
        ByteBuffer bb = c1.encode("abcdefg");
        TestCase.assertEquals("abcdefg", new String(bb.array(), "iso8859-1"));
        bb = c1.encode("");
        TestCase.assertEquals("", new String(bb.array(), "iso8859-1"));
    }

    public void testEncode_String_Unmappable() throws Exception {
        Charset c1 = Charset.forName("iso8859-1");
        ByteBuffer bb = c1.encode("abcd\u5d14efg");
        TestCase.assertEquals(new String(bb.array(), "iso8859-1"), (("abcd" + (new String(c1.newEncoder().replacement(), "iso8859-1"))) + "efg"));
    }

    public void testEncode_String_NullString() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        try {
            c.encode(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testEncode_String_NullEncoder() {
        CharsetTest.MockCharset2 c = new CharsetTest.MockCharset2("mock2", null);
        try {
            c.encode("hehe");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testDecode_Normal() throws Exception {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        CharBuffer cb = c1.decode(ByteBuffer.wrap("abcdefg".getBytes("iso8859-1")));
        TestCase.assertEquals("abcdefg", new String(cb.array()));
        cb = c1.decode(ByteBuffer.wrap("".getBytes("iso8859-1")));
        TestCase.assertEquals("", new String(cb.array()));
    }

    public void testDecode_Malformed() throws Exception {
        Charset c1 = Charset.forName("iso8859-1");
        CharBuffer cb = c1.decode(ByteBuffer.wrap("abcd\u5d14efg".getBytes("iso8859-1")));
        byte[] replacement = c1.newEncoder().replacement();
        TestCase.assertEquals(new String(cb.array()).trim(), (("abcd" + (new String(replacement, "iso8859-1"))) + "efg"));
    }

    public void testDecode_NullByteBuffer() {
        CharsetTest.MockCharset c = new CharsetTest.MockCharset("mock", null);
        try {
            c.decode(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testDecode_NullDecoder() {
        CharsetTest.MockCharset2 c = new CharsetTest.MockCharset2("mock2", null);
        try {
            c.decode(ByteBuffer.wrap("hehe".getBytes()));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testToString() {
        CharsetTest.MockCharset c1 = new CharsetTest.MockCharset("mock", null);
        TestCase.assertTrue(((-1) != (c1.toString().indexOf("mock"))));
    }

    static final class MockCharset extends Charset {
        public MockCharset(String canonicalName, String[] aliases) {
            super(canonicalName, aliases);
        }

        public boolean contains(Charset cs) {
            return false;
        }

        public CharsetDecoder newDecoder() {
            return new CharsetTest.MockDecoder(this);
        }

        public CharsetEncoder newEncoder() {
            return new CharsetTest.MockEncoder(this);
        }
    }

    static class MockCharset2 extends Charset {
        public MockCharset2(String canonicalName, String[] aliases) {
            super(canonicalName, aliases);
        }

        public boolean contains(Charset cs) {
            return false;
        }

        public CharsetDecoder newDecoder() {
            return null;
        }

        public CharsetEncoder newEncoder() {
            return null;
        }
    }

    static class MockEncoder extends CharsetEncoder {
        public MockEncoder(Charset cs) {
            super(cs, 1, 3, new byte[]{ ((byte) ('?')) });
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            while ((in.remaining()) > 0) {
                out.put(((byte) (in.get())));
                // out.put((byte) '!');
            } 
            return CoderResult.UNDERFLOW;
        }
    }

    static class MockDecoder extends CharsetDecoder {
        public MockDecoder(Charset cs) {
            super(cs, 1, 10);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            while ((in.remaining()) > 0) {
                out.put(((char) (in.get())));
            } 
            return CoderResult.UNDERFLOW;
        }
    }

    // Test the method isSupported(String) with charset supported by multiple providers.
    public void testIsSupported_And_ForName_NormalProvider() throws Exception {
        TestCase.assertTrue(Charset.isSupported("mockCharset10"));
        // ignore case problem in mock, intended
        TestCase.assertTrue(Charset.isSupported("MockCharset11"));
        TestCase.assertTrue(Charset.isSupported("MockCharset12"));
        TestCase.assertTrue(Charset.isSupported("MOCKCharset10"));
        // intended case problem in mock
        TestCase.assertTrue(Charset.isSupported("MOCKCharset11"));
        TestCase.assertTrue(Charset.isSupported("MOCKCharset12"));
        TestCase.assertTrue(((Charset.forName("mockCharset10")) instanceof CharsetTest.MockCharset));
        TestCase.assertTrue(((Charset.forName("mockCharset11")) instanceof CharsetTest.MockCharset));
        TestCase.assertTrue(((Charset.forName("mockCharset12")) instanceof CharsetTest.MockCharset));
        TestCase.assertTrue(((Charset.forName("mockCharset10")) == (CharsetTest.charset2)));
        // intended case problem in mock
        Charset.forName("mockCharset11");
        TestCase.assertTrue(((Charset.forName("mockCharset12")) == (CharsetTest.charset2)));
    }

    // Test the method availableCharsets() with charset supported by multiple providers.
    public void testAvailableCharsets_NormalProvider() throws Exception {
        TestCase.assertTrue(Charset.availableCharsets().containsKey("mockCharset00"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("MOCKCharset00"));
        TestCase.assertTrue(((Charset.availableCharsets().get("mockCharset00")) instanceof CharsetTest.MockCharset));
        TestCase.assertTrue(((Charset.availableCharsets().get("MOCKCharset00")) instanceof CharsetTest.MockCharset));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset01"));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset02"));
        TestCase.assertTrue(((Charset.availableCharsets().get("mockCharset10")) == (CharsetTest.charset2)));
        TestCase.assertTrue(((Charset.availableCharsets().get("MOCKCharset10")) == (CharsetTest.charset2)));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset11"));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset12"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("mockCharset10"));
        TestCase.assertTrue(Charset.availableCharsets().containsKey("MOCKCharset10"));
        TestCase.assertTrue(((Charset.availableCharsets().get("mockCharset10")) == (CharsetTest.charset2)));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset11"));
        TestCase.assertFalse(Charset.availableCharsets().containsKey("mockCharset12"));
    }

    // Test the method forName(String) when the charset provider supports a
    // built-in charset.
    public void testForName_DuplicateWithBuiltInCharset() throws Exception {
        TestCase.assertFalse(((Charset.forName("us-ascii")) instanceof CharsetTest.MockCharset));
        TestCase.assertFalse(((Charset.availableCharsets().get("us-ascii")) instanceof CharsetTest.MockCharset));
    }

    public static class MockCharsetProvider extends CharsetProvider {
        public Charset charsetForName(String charsetName) {
            if ((("MockCharset00".equalsIgnoreCase(charsetName)) || ("MockCharset01".equalsIgnoreCase(charsetName))) || ("MockCharset02".equalsIgnoreCase(charsetName))) {
                return CharsetTest.charset1;
            } else
                if ((("MockCharset10".equalsIgnoreCase(charsetName)) || ("MockCharset11".equalsIgnoreCase(charsetName))) || ("MockCharset12".equalsIgnoreCase(charsetName))) {
                    return CharsetTest.charset2;
                }

            return null;
        }

        public Iterator charsets() {
            Vector v = new Vector();
            v.add(CharsetTest.charset1);
            v.add(CharsetTest.charset2);
            return v.iterator();
        }
    }

    // Another mock charset provider attempting to provide the built-in charset "ascii" again.
    public static class MockCharsetProviderASCII extends CharsetProvider {
        public Charset charsetForName(String charsetName) {
            if (("US-ASCII".equalsIgnoreCase(charsetName)) || ("ASCII".equalsIgnoreCase(charsetName))) {
                return new CharsetTest.MockCharset("US-ASCII", new String[]{ "ASCII" });
            }
            return null;
        }

        public Iterator charsets() {
            Vector v = new Vector();
            v.add(new CharsetTest.MockCharset("US-ASCII", new String[]{ "ASCII" }));
            return v.iterator();
        }
    }
}

