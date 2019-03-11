/**
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.utils;


import com.google.common.base.Objects;
import io.atomix.utils.misc.Match;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Unit tests for Match.
 */
public class MatchTest {
    @Test
    public void testMatches() {
        Match<String> m1 = Match.any();
        TestCase.assertTrue(m1.matches(null));
        TestCase.assertTrue(m1.matches("foo"));
        TestCase.assertTrue(m1.matches("bar"));
        Match<String> m2 = Match.ifNull();
        TestCase.assertTrue(m2.matches(null));
        TestCase.assertFalse(m2.matches("foo"));
        Match<String> m3 = Match.ifValue("foo");
        TestCase.assertFalse(m3.matches(null));
        TestCase.assertFalse(m3.matches("bar"));
        TestCase.assertTrue(m3.matches("foo"));
        Match<byte[]> m4 = Match.ifValue(new byte[8]);
        TestCase.assertTrue(m4.matches(new byte[8]));
        TestCase.assertFalse(m4.matches(new byte[7]));
    }

    @Test
    public void testEquals() {
        Match<String> m1 = Match.any();
        Match<String> m2 = Match.any();
        Match<String> m3 = Match.ifNull();
        Match<String> m4 = Match.ifValue("bar");
        TestCase.assertEquals(m1, m2);
        TestCase.assertFalse(Objects.equal(m1, m3));
        TestCase.assertFalse(Objects.equal(m3, m4));
        Object o = new Object();
        TestCase.assertFalse(Objects.equal(m1, o));
    }

    @Test
    public void testMap() {
        Match<String> m1 = Match.ifNull();
        TestCase.assertEquals(m1.map(( s) -> "bar"), Match.ifNull());
        Match<String> m2 = Match.ifValue("foo");
        Match<String> m3 = m2.map(( s) -> "bar");
        TestCase.assertTrue(m3.matches("bar"));
    }

    @Test
    public void testIfNotNull() {
        Match<String> m = Match.ifNotNull();
        TestCase.assertFalse(m.matches(null));
        TestCase.assertTrue(m.matches("foo"));
    }

    @Test
    public void testIfNotValue() {
        Match<String> m1 = Match.ifNotValue(null);
        Match<String> m2 = Match.ifNotValue("foo");
        TestCase.assertFalse(m1.matches(null));
        TestCase.assertFalse(m2.matches("foo"));
    }

    @Test
    public void testToString() {
        Match<String> m1 = Match.any();
        Match<String> m2 = Match.any();
        Match<String> m3 = Match.ifValue("foo");
        Match<String> m4 = Match.ifValue("foo");
        Match<String> m5 = Match.ifNotValue("foo");
        String note = "Results of toString() should be consistent -- ";
        TestCase.assertTrue(note, m1.toString().equals(m2.toString()));
        TestCase.assertTrue(note, m3.toString().equals(m4.toString()));
        TestCase.assertFalse(note, m4.toString().equals(m5.toString()));
    }

    @Test
    public void testHashCode() {
        Match<String> m1 = Match.ifValue("foo");
        Match<String> m2 = Match.ifNotValue("foo");
        Match<String> m3 = Match.ifValue("foo");
        Match<String> m4 = Match.ifNotNull();
        Match<String> m5 = Match.ifNull();
        TestCase.assertTrue(((m1.hashCode()) == (m3.hashCode())));
        TestCase.assertFalse(((m2.hashCode()) == (m1.hashCode())));
        TestCase.assertFalse(((m4.hashCode()) == (m5.hashCode())));
    }
}

