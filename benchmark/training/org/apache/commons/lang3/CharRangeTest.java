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
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.CharRange}.
 */
public class CharRangeTest {
    // -----------------------------------------------------------------------
    @Test
    public void testClass() {
        // class changed to non-public in 3.0
        Assertions.assertFalse(Modifier.isPublic(CharRange.class.getModifiers()));
        Assertions.assertTrue(Modifier.isFinal(CharRange.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructorAccessors_is() {
        final CharRange rangea = CharRange.is('a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('a', rangea.getEnd());
        Assertions.assertFalse(rangea.isNegated());
        Assertions.assertEquals("a", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isNot() {
        final CharRange rangea = CharRange.isNot('a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('a', rangea.getEnd());
        Assertions.assertTrue(rangea.isNegated());
        Assertions.assertEquals("^a", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isIn_Same() {
        final CharRange rangea = CharRange.isIn('a', 'a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('a', rangea.getEnd());
        Assertions.assertFalse(rangea.isNegated());
        Assertions.assertEquals("a", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isIn_Normal() {
        final CharRange rangea = CharRange.isIn('a', 'e');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('e', rangea.getEnd());
        Assertions.assertFalse(rangea.isNegated());
        Assertions.assertEquals("a-e", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isIn_Reversed() {
        final CharRange rangea = CharRange.isIn('e', 'a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('e', rangea.getEnd());
        Assertions.assertFalse(rangea.isNegated());
        Assertions.assertEquals("a-e", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isNotIn_Same() {
        final CharRange rangea = CharRange.isNotIn('a', 'a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('a', rangea.getEnd());
        Assertions.assertTrue(rangea.isNegated());
        Assertions.assertEquals("^a", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isNotIn_Normal() {
        final CharRange rangea = CharRange.isNotIn('a', 'e');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('e', rangea.getEnd());
        Assertions.assertTrue(rangea.isNegated());
        Assertions.assertEquals("^a-e", rangea.toString());
    }

    @Test
    public void testConstructorAccessors_isNotIn_Reversed() {
        final CharRange rangea = CharRange.isNotIn('e', 'a');
        Assertions.assertEquals('a', rangea.getStart());
        Assertions.assertEquals('e', rangea.getEnd());
        Assertions.assertTrue(rangea.isNegated());
        Assertions.assertEquals("^a-e", rangea.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEquals_Object() {
        final CharRange rangea = CharRange.is('a');
        final CharRange rangeae = CharRange.isIn('a', 'e');
        final CharRange rangenotbf = CharRange.isIn('b', 'f');
        Assertions.assertNotEquals(null, rangea);
        Assertions.assertEquals(rangea, rangea);
        Assertions.assertEquals(rangea, CharRange.is('a'));
        Assertions.assertEquals(rangeae, rangeae);
        Assertions.assertEquals(rangeae, CharRange.isIn('a', 'e'));
        Assertions.assertEquals(rangenotbf, rangenotbf);
        Assertions.assertEquals(rangenotbf, CharRange.isIn('b', 'f'));
        Assertions.assertNotEquals(rangea, rangeae);
        Assertions.assertNotEquals(rangea, rangenotbf);
        Assertions.assertNotEquals(rangeae, rangea);
        Assertions.assertNotEquals(rangeae, rangenotbf);
        Assertions.assertNotEquals(rangenotbf, rangea);
        Assertions.assertNotEquals(rangenotbf, rangeae);
    }

    @Test
    public void testHashCode() {
        final CharRange rangea = CharRange.is('a');
        final CharRange rangeae = CharRange.isIn('a', 'e');
        final CharRange rangenotbf = CharRange.isIn('b', 'f');
        Assertions.assertEquals(rangea.hashCode(), rangea.hashCode());
        Assertions.assertEquals(rangea.hashCode(), CharRange.is('a').hashCode());
        Assertions.assertEquals(rangeae.hashCode(), rangeae.hashCode());
        Assertions.assertEquals(rangeae.hashCode(), CharRange.isIn('a', 'e').hashCode());
        Assertions.assertEquals(rangenotbf.hashCode(), rangenotbf.hashCode());
        Assertions.assertEquals(rangenotbf.hashCode(), CharRange.isIn('b', 'f').hashCode());
        Assertions.assertNotEquals(rangea.hashCode(), rangeae.hashCode());
        Assertions.assertNotEquals(rangea.hashCode(), rangenotbf.hashCode());
        Assertions.assertNotEquals(rangeae.hashCode(), rangea.hashCode());
        Assertions.assertNotEquals(rangeae.hashCode(), rangenotbf.hashCode());
        Assertions.assertNotEquals(rangenotbf.hashCode(), rangea.hashCode());
        Assertions.assertNotEquals(rangenotbf.hashCode(), rangeae.hashCode());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContains_Char() {
        CharRange range = CharRange.is('c');
        Assertions.assertFalse(range.contains('b'));
        Assertions.assertTrue(range.contains('c'));
        Assertions.assertFalse(range.contains('d'));
        Assertions.assertFalse(range.contains('e'));
        range = CharRange.isIn('c', 'd');
        Assertions.assertFalse(range.contains('b'));
        Assertions.assertTrue(range.contains('c'));
        Assertions.assertTrue(range.contains('d'));
        Assertions.assertFalse(range.contains('e'));
        range = CharRange.isIn('d', 'c');
        Assertions.assertFalse(range.contains('b'));
        Assertions.assertTrue(range.contains('c'));
        Assertions.assertTrue(range.contains('d'));
        Assertions.assertFalse(range.contains('e'));
        range = CharRange.isNotIn('c', 'd');
        Assertions.assertTrue(range.contains('b'));
        Assertions.assertFalse(range.contains('c'));
        Assertions.assertFalse(range.contains('d'));
        Assertions.assertTrue(range.contains('e'));
        Assertions.assertTrue(range.contains(((char) (0))));
        Assertions.assertTrue(range.contains(Character.MAX_VALUE));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContains_Charrange() {
        final CharRange a = CharRange.is('a');
        final CharRange b = CharRange.is('b');
        final CharRange c = CharRange.is('c');
        final CharRange c2 = CharRange.is('c');
        final CharRange d = CharRange.is('d');
        final CharRange e = CharRange.is('e');
        final CharRange cd = CharRange.isIn('c', 'd');
        final CharRange bd = CharRange.isIn('b', 'd');
        final CharRange bc = CharRange.isIn('b', 'c');
        final CharRange ab = CharRange.isIn('a', 'b');
        final CharRange de = CharRange.isIn('d', 'e');
        final CharRange ef = CharRange.isIn('e', 'f');
        final CharRange ae = CharRange.isIn('a', 'e');
        // normal/normal
        Assertions.assertFalse(c.contains(b));
        Assertions.assertTrue(c.contains(c));
        Assertions.assertTrue(c.contains(c2));
        Assertions.assertFalse(c.contains(d));
        Assertions.assertFalse(c.contains(cd));
        Assertions.assertFalse(c.contains(bd));
        Assertions.assertFalse(c.contains(bc));
        Assertions.assertFalse(c.contains(ab));
        Assertions.assertFalse(c.contains(de));
        Assertions.assertTrue(cd.contains(c));
        Assertions.assertTrue(bd.contains(c));
        Assertions.assertTrue(bc.contains(c));
        Assertions.assertFalse(ab.contains(c));
        Assertions.assertFalse(de.contains(c));
        Assertions.assertTrue(ae.contains(b));
        Assertions.assertTrue(ae.contains(ab));
        Assertions.assertTrue(ae.contains(bc));
        Assertions.assertTrue(ae.contains(cd));
        Assertions.assertTrue(ae.contains(de));
        final CharRange notb = CharRange.isNot('b');
        final CharRange notc = CharRange.isNot('c');
        final CharRange notd = CharRange.isNot('d');
        final CharRange notab = CharRange.isNotIn('a', 'b');
        final CharRange notbc = CharRange.isNotIn('b', 'c');
        final CharRange notbd = CharRange.isNotIn('b', 'd');
        final CharRange notcd = CharRange.isNotIn('c', 'd');
        final CharRange notde = CharRange.isNotIn('d', 'e');
        final CharRange notae = CharRange.isNotIn('a', 'e');
        final CharRange all = CharRange.isIn(((char) (0)), Character.MAX_VALUE);
        final CharRange allbutfirst = CharRange.isIn(((char) (1)), Character.MAX_VALUE);
        // normal/negated
        Assertions.assertFalse(c.contains(notc));
        Assertions.assertFalse(c.contains(notbd));
        Assertions.assertTrue(all.contains(notc));
        Assertions.assertTrue(all.contains(notbd));
        Assertions.assertFalse(allbutfirst.contains(notc));
        Assertions.assertFalse(allbutfirst.contains(notbd));
        // negated/normal
        Assertions.assertTrue(notc.contains(a));
        Assertions.assertTrue(notc.contains(b));
        Assertions.assertFalse(notc.contains(c));
        Assertions.assertTrue(notc.contains(d));
        Assertions.assertTrue(notc.contains(e));
        Assertions.assertTrue(notc.contains(ab));
        Assertions.assertFalse(notc.contains(bc));
        Assertions.assertFalse(notc.contains(bd));
        Assertions.assertFalse(notc.contains(cd));
        Assertions.assertTrue(notc.contains(de));
        Assertions.assertFalse(notc.contains(ae));
        Assertions.assertFalse(notc.contains(all));
        Assertions.assertFalse(notc.contains(allbutfirst));
        Assertions.assertTrue(notbd.contains(a));
        Assertions.assertFalse(notbd.contains(b));
        Assertions.assertFalse(notbd.contains(c));
        Assertions.assertFalse(notbd.contains(d));
        Assertions.assertTrue(notbd.contains(e));
        Assertions.assertTrue(notcd.contains(ab));
        Assertions.assertFalse(notcd.contains(bc));
        Assertions.assertFalse(notcd.contains(bd));
        Assertions.assertFalse(notcd.contains(cd));
        Assertions.assertFalse(notcd.contains(de));
        Assertions.assertFalse(notcd.contains(ae));
        Assertions.assertTrue(notcd.contains(ef));
        Assertions.assertFalse(notcd.contains(all));
        Assertions.assertFalse(notcd.contains(allbutfirst));
        // negated/negated
        Assertions.assertFalse(notc.contains(notb));
        Assertions.assertTrue(notc.contains(notc));
        Assertions.assertFalse(notc.contains(notd));
        Assertions.assertFalse(notc.contains(notab));
        Assertions.assertTrue(notc.contains(notbc));
        Assertions.assertTrue(notc.contains(notbd));
        Assertions.assertTrue(notc.contains(notcd));
        Assertions.assertFalse(notc.contains(notde));
        Assertions.assertFalse(notbd.contains(notb));
        Assertions.assertFalse(notbd.contains(notc));
        Assertions.assertFalse(notbd.contains(notd));
        Assertions.assertFalse(notbd.contains(notab));
        Assertions.assertFalse(notbd.contains(notbc));
        Assertions.assertTrue(notbd.contains(notbd));
        Assertions.assertFalse(notbd.contains(notcd));
        Assertions.assertFalse(notbd.contains(notde));
        Assertions.assertTrue(notbd.contains(notae));
    }

    @Test
    public void testContainsNullArg() {
        final CharRange range = CharRange.is('a');
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> range.contains(null));
        Assertions.assertEquals("The Range must not be null", e.getMessage());
    }

    @Test
    public void testIterator() {
        final CharRange a = CharRange.is('a');
        final CharRange ad = CharRange.isIn('a', 'd');
        final CharRange nota = CharRange.isNot('a');
        final CharRange emptySet = CharRange.isNotIn(((char) (0)), Character.MAX_VALUE);
        final CharRange notFirst = CharRange.isNotIn(((char) (1)), Character.MAX_VALUE);
        final CharRange notLast = CharRange.isNotIn(((char) (0)), ((char) ((Character.MAX_VALUE) - 1)));
        final Iterator<Character> aIt = a.iterator();
        Assertions.assertNotNull(aIt);
        Assertions.assertTrue(aIt.hasNext());
        Assertions.assertEquals(Character.valueOf('a'), aIt.next());
        Assertions.assertFalse(aIt.hasNext());
        final Iterator<Character> adIt = ad.iterator();
        Assertions.assertNotNull(adIt);
        Assertions.assertTrue(adIt.hasNext());
        Assertions.assertEquals(Character.valueOf('a'), adIt.next());
        Assertions.assertEquals(Character.valueOf('b'), adIt.next());
        Assertions.assertEquals(Character.valueOf('c'), adIt.next());
        Assertions.assertEquals(Character.valueOf('d'), adIt.next());
        Assertions.assertFalse(adIt.hasNext());
        final Iterator<Character> notaIt = nota.iterator();
        Assertions.assertNotNull(notaIt);
        Assertions.assertTrue(notaIt.hasNext());
        while (notaIt.hasNext()) {
            final Character c = notaIt.next();
            Assertions.assertNotEquals('a', c.charValue());
        } 
        final Iterator<Character> emptySetIt = emptySet.iterator();
        Assertions.assertNotNull(emptySetIt);
        Assertions.assertFalse(emptySetIt.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, emptySetIt::next);
        final Iterator<Character> notFirstIt = notFirst.iterator();
        Assertions.assertNotNull(notFirstIt);
        Assertions.assertTrue(notFirstIt.hasNext());
        Assertions.assertEquals(Character.valueOf(((char) (0))), notFirstIt.next());
        Assertions.assertFalse(notFirstIt.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, notFirstIt::next);
        final Iterator<Character> notLastIt = notLast.iterator();
        Assertions.assertNotNull(notLastIt);
        Assertions.assertTrue(notLastIt.hasNext());
        Assertions.assertEquals(Character.valueOf(Character.MAX_VALUE), notLastIt.next());
        Assertions.assertFalse(notLastIt.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, notLastIt::next);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSerialization() {
        CharRange range = CharRange.is('a');
        Assertions.assertEquals(range, SerializationUtils.clone(range));
        range = CharRange.isIn('a', 'e');
        Assertions.assertEquals(range, SerializationUtils.clone(range));
        range = CharRange.isNotIn('a', 'e');
        Assertions.assertEquals(range, SerializationUtils.clone(range));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIteratorRemove() {
        final CharRange a = CharRange.is('a');
        final Iterator<Character> aIt = a.iterator();
        Assertions.assertThrows(UnsupportedOperationException.class, aIt::remove);
    }
}

