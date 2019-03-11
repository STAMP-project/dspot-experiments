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
package libcore.java.lang;


import junit.framework.TestCase;


public class OldCharacterTest extends TestCase {
    public void test_codePointCountLjava_lang_CharArrayII() {
        TestCase.assertEquals(1, Character.codePointCount("\ud800\udc00".toCharArray(), 0, 2));
        TestCase.assertEquals(3, Character.codePointCount("a\ud800\udc00b".toCharArray(), 0, 4));
        TestCase.assertEquals(4, Character.codePointCount("a\ud800\udc00b\ud800".toCharArray(), 0, 5));
        TestCase.assertEquals(4, Character.codePointCount("ab\ud800\udc00b\ud800".toCharArray(), 1, 5));
        try {
            Character.codePointCount(((char[]) (null)), 0, 1);
            TestCase.fail("No NPE, null char sequence.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointCount("abc".toCharArray(), (-1), 1);
            TestCase.fail("No IOOBE, negative start.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointCount("abc".toCharArray(), 0, 4);
            TestCase.fail("No IOOBE, end greater than length.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointCount("abc".toCharArray(), 1, 3);
            TestCase.fail("No IOOBE, end greater than start.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_getDirectionality() throws Exception {
        byte[] directionalities = new byte[]{ // BEGIN android-changed
        // Unicode 5.1 defines U+0370 to be Greek capital letter Heta.
        Character.DIRECTIONALITY_LEFT_TO_RIGHT, // END android-changed.
        Character.DIRECTIONALITY_LEFT_TO_RIGHT, Character.DIRECTIONALITY_RIGHT_TO_LEFT, // BEGIN android-changed
        // Unicode standard 5.1 changed category of unicode point 0x0600 from AL to AN
        Character.DIRECTIONALITY_ARABIC_NUMBER, // END android-changed.
        Character.DIRECTIONALITY_EUROPEAN_NUMBER, // Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR,
        Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR, Character.DIRECTIONALITY_ARABIC_NUMBER, Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR, Character.DIRECTIONALITY_NONSPACING_MARK, Character.DIRECTIONALITY_BOUNDARY_NEUTRAL, Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR, Character.DIRECTIONALITY_SEGMENT_SEPARATOR, Character.DIRECTIONALITY_WHITESPACE, Character.DIRECTIONALITY_OTHER_NEUTRALS, Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING, Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE, Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING, Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE, Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT };
        char[] characters = new char[]{ // BEGIN android-changed
        // Unicode 5.1 defines U+0370 to be Greek capital letter Heta.
        '\u0370'// 1
        , // END android-changed
        '\u00b5'// 0
        , '\u05be'// 1
        , // BEGIN android-changed
        '\u0600'// 6
        , // END android-changed
        '\u00b2'// 3
        , // '', // No common char in this group on android and java.
        '\u00b1'// 5
        , '\u0660'// 6
        , '\u00a0'// 7
        , '\u0300'// 8
        , '\u009f'// 9
        , '\u0085'// 10
        , '\u001f'// 11
        , ' '// 12
        , '\u00ab'// 13
        , '\u202a'// 14
        , '\u202d'// 15
        , '\u202b'// 16
        , '\u202e'// 17
        , '\u202c'// 18
         };
        for (int i = 0; i < (directionalities.length); i++) {
            TestCase.assertEquals(directionalities[i], Character.getDirectionality(characters[i]));
        }
    }

    public void test_digitCI() {
        TestCase.assertEquals((-1), Character.digit('\uffff', 1));
    }

    public void test_isUpperCaseC() {
        TestCase.assertFalse("Incorrect case value", Character.isUpperCase('1'));
        TestCase.assertFalse("Incorrect case value", Character.isUpperCase('?'));
    }

    public void test_toLowerCaseC() {
        TestCase.assertEquals("Failed to change case", 't', Character.toLowerCase('t'));
        TestCase.assertEquals("Failed to change case", '1', Character.toLowerCase('1'));
    }

    public void test_toString() {
        TestCase.assertEquals("Incorrect String returned", "T", new Character('T').toString());
        TestCase.assertEquals("Incorrect String returned", "1", new Character('1').toString());
        TestCase.assertEquals("Incorrect String returned", "$", new Character('$').toString());
    }

    public void test_toString_char() {
        TestCase.assertEquals("Incorrect String returned", "T", Character.toString('T'));
    }
}

