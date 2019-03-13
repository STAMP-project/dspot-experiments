/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.CharSetUtils}.
 */
public class CharSetUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new CharSetUtils());
        final Constructor<?>[] cons = CharSetUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(CharSetUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(CharSetUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSqueeze_StringString() {
        Assertions.assertNull(CharSetUtils.squeeze(null, ((String) (null))));
        Assertions.assertNull(CharSetUtils.squeeze(null, ""));
        Assertions.assertEquals("", CharSetUtils.squeeze("", ((String) (null))));
        Assertions.assertEquals("", CharSetUtils.squeeze("", ""));
        Assertions.assertEquals("", CharSetUtils.squeeze("", "a-e"));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", ((String) (null))));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", ""));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", "a-e"));
        Assertions.assertEquals("helo", CharSetUtils.squeeze("hello", "l-p"));
        Assertions.assertEquals("heloo", CharSetUtils.squeeze("helloo", "l"));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("helloo", "^l"));
    }

    @Test
    public void testSqueeze_StringStringarray() {
        Assertions.assertNull(CharSetUtils.squeeze(null, ((String[]) (null))));
        Assertions.assertNull(CharSetUtils.squeeze(null, new String[0]));
        Assertions.assertNull(CharSetUtils.squeeze(null, new String[]{ null }));
        Assertions.assertNull(CharSetUtils.squeeze(null, new String[]{ "el" }));
        Assertions.assertEquals("", CharSetUtils.squeeze("", ((String[]) (null))));
        Assertions.assertEquals("", CharSetUtils.squeeze(""));
        Assertions.assertEquals("", CharSetUtils.squeeze("", new String[]{ null }));
        Assertions.assertEquals("", CharSetUtils.squeeze("", "a-e"));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", ((String[]) (null))));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello"));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", new String[]{ null }));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", "a-e"));
        Assertions.assertEquals("helo", CharSetUtils.squeeze("hello", "el"));
        Assertions.assertEquals("hello", CharSetUtils.squeeze("hello", "e"));
        Assertions.assertEquals("fofof", CharSetUtils.squeeze("fooffooff", "of"));
        Assertions.assertEquals("fof", CharSetUtils.squeeze("fooooff", "fo"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContainsAny_StringString() {
        Assertions.assertFalse(CharSetUtils.containsAny(null, ((String) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny(null, ""));
        Assertions.assertFalse(CharSetUtils.containsAny("", ((String) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny("", ""));
        Assertions.assertFalse(CharSetUtils.containsAny("", "a-e"));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", ((String) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", ""));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "a-e"));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "l-p"));
    }

    @Test
    public void testContainsAny_StringStringarray() {
        Assertions.assertFalse(CharSetUtils.containsAny(null, ((String[]) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny(null));
        Assertions.assertFalse(CharSetUtils.containsAny(null, new String[]{ null }));
        Assertions.assertFalse(CharSetUtils.containsAny(null, "a-e"));
        Assertions.assertFalse(CharSetUtils.containsAny("", ((String[]) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny(""));
        Assertions.assertFalse(CharSetUtils.containsAny("", new String[]{ null }));
        Assertions.assertFalse(CharSetUtils.containsAny("", "a-e"));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", ((String[]) (null))));
        Assertions.assertFalse(CharSetUtils.containsAny("hello"));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", new String[]{ null }));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "a-e"));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "el"));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", "x"));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "e-i"));
        Assertions.assertTrue(CharSetUtils.containsAny("hello", "a-z"));
        Assertions.assertFalse(CharSetUtils.containsAny("hello", ""));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCount_StringString() {
        Assertions.assertEquals(0, CharSetUtils.count(null, ((String) (null))));
        Assertions.assertEquals(0, CharSetUtils.count(null, ""));
        Assertions.assertEquals(0, CharSetUtils.count("", ((String) (null))));
        Assertions.assertEquals(0, CharSetUtils.count("", ""));
        Assertions.assertEquals(0, CharSetUtils.count("", "a-e"));
        Assertions.assertEquals(0, CharSetUtils.count("hello", ((String) (null))));
        Assertions.assertEquals(0, CharSetUtils.count("hello", ""));
        Assertions.assertEquals(1, CharSetUtils.count("hello", "a-e"));
        Assertions.assertEquals(3, CharSetUtils.count("hello", "l-p"));
    }

    @Test
    public void testCount_StringStringarray() {
        Assertions.assertEquals(0, CharSetUtils.count(null, ((String[]) (null))));
        Assertions.assertEquals(0, CharSetUtils.count(null));
        Assertions.assertEquals(0, CharSetUtils.count(null, new String[]{ null }));
        Assertions.assertEquals(0, CharSetUtils.count(null, "a-e"));
        Assertions.assertEquals(0, CharSetUtils.count("", ((String[]) (null))));
        Assertions.assertEquals(0, CharSetUtils.count(""));
        Assertions.assertEquals(0, CharSetUtils.count("", new String[]{ null }));
        Assertions.assertEquals(0, CharSetUtils.count("", "a-e"));
        Assertions.assertEquals(0, CharSetUtils.count("hello", ((String[]) (null))));
        Assertions.assertEquals(0, CharSetUtils.count("hello"));
        Assertions.assertEquals(0, CharSetUtils.count("hello", new String[]{ null }));
        Assertions.assertEquals(1, CharSetUtils.count("hello", "a-e"));
        Assertions.assertEquals(3, CharSetUtils.count("hello", "el"));
        Assertions.assertEquals(0, CharSetUtils.count("hello", "x"));
        Assertions.assertEquals(2, CharSetUtils.count("hello", "e-i"));
        Assertions.assertEquals(5, CharSetUtils.count("hello", "a-z"));
        Assertions.assertEquals(0, CharSetUtils.count("hello", ""));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testKeep_StringString() {
        Assertions.assertNull(CharSetUtils.keep(null, ((String) (null))));
        Assertions.assertNull(CharSetUtils.keep(null, ""));
        Assertions.assertEquals("", CharSetUtils.keep("", ((String) (null))));
        Assertions.assertEquals("", CharSetUtils.keep("", ""));
        Assertions.assertEquals("", CharSetUtils.keep("", "a-e"));
        Assertions.assertEquals("", CharSetUtils.keep("hello", ((String) (null))));
        Assertions.assertEquals("", CharSetUtils.keep("hello", ""));
        Assertions.assertEquals("", CharSetUtils.keep("hello", "xyz"));
        Assertions.assertEquals("hello", CharSetUtils.keep("hello", "a-z"));
        Assertions.assertEquals("hello", CharSetUtils.keep("hello", "oleh"));
        Assertions.assertEquals("ell", CharSetUtils.keep("hello", "el"));
    }

    @Test
    public void testKeep_StringStringarray() {
        Assertions.assertNull(CharSetUtils.keep(null, ((String[]) (null))));
        Assertions.assertNull(CharSetUtils.keep(null, new String[0]));
        Assertions.assertNull(CharSetUtils.keep(null, new String[]{ null }));
        Assertions.assertNull(CharSetUtils.keep(null, new String[]{ "a-e" }));
        Assertions.assertEquals("", CharSetUtils.keep("", ((String[]) (null))));
        Assertions.assertEquals("", CharSetUtils.keep(""));
        Assertions.assertEquals("", CharSetUtils.keep("", new String[]{ null }));
        Assertions.assertEquals("", CharSetUtils.keep("", "a-e"));
        Assertions.assertEquals("", CharSetUtils.keep("hello", ((String[]) (null))));
        Assertions.assertEquals("", CharSetUtils.keep("hello"));
        Assertions.assertEquals("", CharSetUtils.keep("hello", new String[]{ null }));
        Assertions.assertEquals("e", CharSetUtils.keep("hello", "a-e"));
        Assertions.assertEquals("e", CharSetUtils.keep("hello", "a-e"));
        Assertions.assertEquals("ell", CharSetUtils.keep("hello", "el"));
        Assertions.assertEquals("hello", CharSetUtils.keep("hello", "elho"));
        Assertions.assertEquals("hello", CharSetUtils.keep("hello", "a-z"));
        Assertions.assertEquals("----", CharSetUtils.keep("----", "-"));
        Assertions.assertEquals("ll", CharSetUtils.keep("hello", "l"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDelete_StringString() {
        Assertions.assertNull(CharSetUtils.delete(null, ((String) (null))));
        Assertions.assertNull(CharSetUtils.delete(null, ""));
        Assertions.assertEquals("", CharSetUtils.delete("", ((String) (null))));
        Assertions.assertEquals("", CharSetUtils.delete("", ""));
        Assertions.assertEquals("", CharSetUtils.delete("", "a-e"));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", ((String) (null))));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", ""));
        Assertions.assertEquals("hllo", CharSetUtils.delete("hello", "a-e"));
        Assertions.assertEquals("he", CharSetUtils.delete("hello", "l-p"));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", "z"));
    }

    @Test
    public void testDelete_StringStringarray() {
        Assertions.assertNull(CharSetUtils.delete(null, ((String[]) (null))));
        Assertions.assertNull(CharSetUtils.delete(null, new String[0]));
        Assertions.assertNull(CharSetUtils.delete(null, new String[]{ null }));
        Assertions.assertNull(CharSetUtils.delete(null, new String[]{ "el" }));
        Assertions.assertEquals("", CharSetUtils.delete("", ((String[]) (null))));
        Assertions.assertEquals("", CharSetUtils.delete(""));
        Assertions.assertEquals("", CharSetUtils.delete("", new String[]{ null }));
        Assertions.assertEquals("", CharSetUtils.delete("", "a-e"));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", ((String[]) (null))));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello"));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", new String[]{ null }));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", "xyz"));
        Assertions.assertEquals("ho", CharSetUtils.delete("hello", "el"));
        Assertions.assertEquals("", CharSetUtils.delete("hello", "elho"));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", ""));
        Assertions.assertEquals("hello", CharSetUtils.delete("hello", ""));
        Assertions.assertEquals("", CharSetUtils.delete("hello", "a-z"));
        Assertions.assertEquals("", CharSetUtils.delete("----", "-"));
        Assertions.assertEquals("heo", CharSetUtils.delete("hello", "l"));
    }
}

