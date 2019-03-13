/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.CharSet}.
 */
public class CharSetTest {
    // -----------------------------------------------------------------------
    @Test
    public void testClass() {
        Assertions.assertTrue(Modifier.isPublic(CharSet.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(CharSet.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetInstance() {
        Assertions.assertSame(CharSet.EMPTY, CharSet.getInstance(((String) (null))));
        Assertions.assertSame(CharSet.EMPTY, CharSet.getInstance(""));
        Assertions.assertSame(CharSet.ASCII_ALPHA, CharSet.getInstance("a-zA-Z"));
        Assertions.assertSame(CharSet.ASCII_ALPHA, CharSet.getInstance("A-Za-z"));
        Assertions.assertSame(CharSet.ASCII_ALPHA_LOWER, CharSet.getInstance("a-z"));
        Assertions.assertSame(CharSet.ASCII_ALPHA_UPPER, CharSet.getInstance("A-Z"));
        Assertions.assertSame(CharSet.ASCII_NUMERIC, CharSet.getInstance("0-9"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetInstance_Stringarray() {
        Assertions.assertNull(CharSet.getInstance(((String[]) (null))));
        Assertions.assertEquals("[]", CharSet.getInstance(new String[0]).toString());
        Assertions.assertEquals("[]", CharSet.getInstance(new String[]{ null }).toString());
        Assertions.assertEquals("[a-e]", CharSet.getInstance(new String[]{ "a-e" }).toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_String_simple() {
        CharSet set;
        CharRange[] array;
        set = CharSet.getInstance(((String) (null)));
        array = set.getCharRanges();
        Assertions.assertEquals("[]", set.toString());
        Assertions.assertEquals(0, array.length);
        set = CharSet.getInstance("");
        array = set.getCharRanges();
        Assertions.assertEquals("[]", set.toString());
        Assertions.assertEquals(0, array.length);
        set = CharSet.getInstance("a");
        array = set.getCharRanges();
        Assertions.assertEquals("[a]", set.toString());
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals("a", array[0].toString());
        set = CharSet.getInstance("^a");
        array = set.getCharRanges();
        Assertions.assertEquals("[^a]", set.toString());
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals("^a", array[0].toString());
        set = CharSet.getInstance("a-e");
        array = set.getCharRanges();
        Assertions.assertEquals("[a-e]", set.toString());
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals("a-e", array[0].toString());
        set = CharSet.getInstance("^a-e");
        array = set.getCharRanges();
        Assertions.assertEquals("[^a-e]", set.toString());
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals("^a-e", array[0].toString());
    }

    @Test
    public void testConstructor_String_combo() {
        CharSet set;
        CharRange[] array;
        set = CharSet.getInstance("abc");
        array = set.getCharRanges();
        Assertions.assertEquals(3, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('b')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));
        set = CharSet.getInstance("a-ce-f");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', 'c')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('e', 'f')));
        set = CharSet.getInstance("ae-f");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('e', 'f')));
        set = CharSet.getInstance("e-fa");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('e', 'f')));
        set = CharSet.getInstance("ae-fm-pz");
        array = set.getCharRanges();
        Assertions.assertEquals(4, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('e', 'f')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('m', 'p')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('z')));
    }

    @Test
    public void testConstructor_String_comboNegated() {
        CharSet set;
        CharRange[] array;
        set = CharSet.getInstance("^abc");
        array = set.getCharRanges();
        Assertions.assertEquals(3, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('b')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));
        set = CharSet.getInstance("b^ac");
        array = set.getCharRanges();
        Assertions.assertEquals(3, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('b')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));
        set = CharSet.getInstance("db^ac");
        array = set.getCharRanges();
        Assertions.assertEquals(4, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('d')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('b')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));
        set = CharSet.getInstance("^b^a");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('b')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('a')));
        set = CharSet.getInstance("b^a-c^z");
        array = set.getCharRanges();
        Assertions.assertEquals(3, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('a', 'c')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('z')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('b')));
    }

    @Test
    public void testConstructor_String_oddDash() {
        CharSet set;
        CharRange[] array;
        set = CharSet.getInstance("-");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        set = CharSet.getInstance("--");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        set = CharSet.getInstance("---");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        set = CharSet.getInstance("----");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        set = CharSet.getInstance("-a");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        set = CharSet.getInstance("a-");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));
        set = CharSet.getInstance("a--");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', '-')));
        set = CharSet.getInstance("--a");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('-', 'a')));
    }

    @Test
    public void testConstructor_String_oddNegate() {
        CharSet set;
        CharRange[] array;
        set = CharSet.getInstance("^");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('^')));// "^"

        set = CharSet.getInstance("^^");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('^')));// "^^"

        set = CharSet.getInstance("^^^");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('^')));// "^^"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('^')));// "^"

        set = CharSet.getInstance("^^^^");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('^')));// "^^" x2

        set = CharSet.getInstance("a^");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('a')));// "a"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('^')));// "^"

        set = CharSet.getInstance("^a-");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('a')));// "^a"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));// "-"

        set = CharSet.getInstance("^^-c");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('^', 'c')));// "^^-c"

        set = CharSet.getInstance("^c-^");
        array = set.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('c', '^')));// "^c-^"

        set = CharSet.getInstance("^c-^d");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('c', '^')));// "^c-^"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('d')));// "d"

        set = CharSet.getInstance("^^-");
        array = set.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNot('^')));// "^^"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('-')));// "-"

    }

    @Test
    public void testConstructor_String_oddCombinations() {
        CharSet set;
        CharRange[] array = null;
        set = CharSet.getInstance("a-^c");
        array = set.getCharRanges();
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', '^')));// "a-^"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));// "c"

        Assertions.assertFalse(set.contains('b'));
        Assertions.assertTrue(set.contains('^'));
        Assertions.assertTrue(set.contains('_'));// between ^ and a

        Assertions.assertTrue(set.contains('c'));
        set = CharSet.getInstance("^a-^c");
        array = set.getCharRanges();
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('a', '^')));// "^a-^"

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.is('c')));// "c"

        Assertions.assertTrue(set.contains('b'));
        Assertions.assertFalse(set.contains('^'));
        Assertions.assertFalse(set.contains('_'));// between ^ and a

        set = CharSet.getInstance("a- ^-- ");// contains everything

        array = set.getCharRanges();
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', ' ')));// "a- "

        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isNotIn('-', ' ')));// "^-- "

        Assertions.assertTrue(set.contains('#'));
        Assertions.assertTrue(set.contains('^'));
        Assertions.assertTrue(set.contains('a'));
        Assertions.assertTrue(set.contains('*'));
        Assertions.assertTrue(set.contains('A'));
        set = CharSet.getInstance("^-b");
        array = set.getCharRanges();
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('^', 'b')));// "^-b"

        Assertions.assertTrue(set.contains('b'));
        Assertions.assertTrue(set.contains('_'));// between ^ and a

        Assertions.assertFalse(set.contains('A'));
        Assertions.assertTrue(set.contains('^'));
        set = CharSet.getInstance("b-^");
        array = set.getCharRanges();
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('^', 'b')));// "b-^"

        Assertions.assertTrue(set.contains('b'));
        Assertions.assertTrue(set.contains('^'));
        Assertions.assertTrue(set.contains('a'));// between ^ and b

        Assertions.assertFalse(set.contains('c'));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEquals_Object() {
        final CharSet abc = CharSet.getInstance("abc");
        final CharSet abc2 = CharSet.getInstance("abc");
        final CharSet atoc = CharSet.getInstance("a-c");
        final CharSet atoc2 = CharSet.getInstance("a-c");
        final CharSet notatoc = CharSet.getInstance("^a-c");
        final CharSet notatoc2 = CharSet.getInstance("^a-c");
        Assertions.assertNotEquals(null, abc);
        Assertions.assertEquals(abc, abc);
        Assertions.assertEquals(abc, abc2);
        Assertions.assertNotEquals(abc, atoc);
        Assertions.assertNotEquals(abc, notatoc);
        Assertions.assertNotEquals(atoc, abc);
        Assertions.assertEquals(atoc, atoc);
        Assertions.assertEquals(atoc, atoc2);
        Assertions.assertNotEquals(atoc, notatoc);
        Assertions.assertNotEquals(notatoc, abc);
        Assertions.assertNotEquals(notatoc, atoc);
        Assertions.assertEquals(notatoc, notatoc);
        Assertions.assertEquals(notatoc, notatoc2);
    }

    @Test
    public void testHashCode() {
        final CharSet abc = CharSet.getInstance("abc");
        final CharSet abc2 = CharSet.getInstance("abc");
        final CharSet atoc = CharSet.getInstance("a-c");
        final CharSet atoc2 = CharSet.getInstance("a-c");
        final CharSet notatoc = CharSet.getInstance("^a-c");
        final CharSet notatoc2 = CharSet.getInstance("^a-c");
        Assertions.assertEquals(abc.hashCode(), abc.hashCode());
        Assertions.assertEquals(abc.hashCode(), abc2.hashCode());
        Assertions.assertEquals(atoc.hashCode(), atoc.hashCode());
        Assertions.assertEquals(atoc.hashCode(), atoc2.hashCode());
        Assertions.assertEquals(notatoc.hashCode(), notatoc.hashCode());
        Assertions.assertEquals(notatoc.hashCode(), notatoc2.hashCode());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContains_Char() {
        final CharSet btod = CharSet.getInstance("b-d");
        final CharSet dtob = CharSet.getInstance("d-b");
        final CharSet bcd = CharSet.getInstance("bcd");
        final CharSet bd = CharSet.getInstance("bd");
        final CharSet notbtod = CharSet.getInstance("^b-d");
        Assertions.assertFalse(btod.contains('a'));
        Assertions.assertTrue(btod.contains('b'));
        Assertions.assertTrue(btod.contains('c'));
        Assertions.assertTrue(btod.contains('d'));
        Assertions.assertFalse(btod.contains('e'));
        Assertions.assertFalse(bcd.contains('a'));
        Assertions.assertTrue(bcd.contains('b'));
        Assertions.assertTrue(bcd.contains('c'));
        Assertions.assertTrue(bcd.contains('d'));
        Assertions.assertFalse(bcd.contains('e'));
        Assertions.assertFalse(bd.contains('a'));
        Assertions.assertTrue(bd.contains('b'));
        Assertions.assertFalse(bd.contains('c'));
        Assertions.assertTrue(bd.contains('d'));
        Assertions.assertFalse(bd.contains('e'));
        Assertions.assertTrue(notbtod.contains('a'));
        Assertions.assertFalse(notbtod.contains('b'));
        Assertions.assertFalse(notbtod.contains('c'));
        Assertions.assertFalse(notbtod.contains('d'));
        Assertions.assertTrue(notbtod.contains('e'));
        Assertions.assertFalse(dtob.contains('a'));
        Assertions.assertTrue(dtob.contains('b'));
        Assertions.assertTrue(dtob.contains('c'));
        Assertions.assertTrue(dtob.contains('d'));
        Assertions.assertFalse(dtob.contains('e'));
        final CharRange[] array = dtob.getCharRanges();
        Assertions.assertEquals("[b-d]", dtob.toString());
        Assertions.assertEquals(1, array.length);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSerialization() {
        CharSet set = CharSet.getInstance("a");
        Assertions.assertEquals(set, SerializationUtils.clone(set));
        set = CharSet.getInstance("a-e");
        Assertions.assertEquals(set, SerializationUtils.clone(set));
        set = CharSet.getInstance("be-f^a-z");
        Assertions.assertEquals(set, SerializationUtils.clone(set));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testStatics() {
        CharRange[] array;
        array = CharSet.EMPTY.getCharRanges();
        Assertions.assertEquals(0, array.length);
        array = CharSet.ASCII_ALPHA.getCharRanges();
        Assertions.assertEquals(2, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', 'z')));
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('A', 'Z')));
        array = CharSet.ASCII_ALPHA_LOWER.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('a', 'z')));
        array = CharSet.ASCII_ALPHA_UPPER.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('A', 'Z')));
        array = CharSet.ASCII_NUMERIC.getCharRanges();
        Assertions.assertEquals(1, array.length);
        Assertions.assertTrue(ArrayUtils.contains(array, CharRange.isIn('0', '9')));
    }

    @Test
    public void testJavadocExamples() {
        Assertions.assertFalse(CharSet.getInstance("^a-c").contains('a'));
        Assertions.assertTrue(CharSet.getInstance("^a-c").contains('d'));
        Assertions.assertTrue(CharSet.getInstance("^^a-c").contains('a'));
        Assertions.assertFalse(CharSet.getInstance("^^a-c").contains('^'));
        Assertions.assertTrue(CharSet.getInstance("^a-cd-f").contains('d'));
        Assertions.assertTrue(CharSet.getInstance("a-c^").contains('^'));
        Assertions.assertTrue(CharSet.getInstance("^", "a-c").contains('^'));
    }
}

