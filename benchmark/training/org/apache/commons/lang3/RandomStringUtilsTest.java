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
import java.nio.charset.Charset;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.RandomStringUtils}.
 */
public class RandomStringUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new RandomStringUtils());
        final Constructor<?>[] cons = RandomStringUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(RandomStringUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(RandomStringUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    /**
     * Test the implementation
     */
    @Test
    public void testRandomStringUtils() {
        String r1 = RandomStringUtils.random(50);
        Assertions.assertEquals(50, r1.length(), "random(50) length");
        String r2 = RandomStringUtils.random(50);
        Assertions.assertEquals(50, r2.length(), "random(50) length");
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomAscii(50);
        Assertions.assertEquals(50, r1.length(), "randomAscii(50) length");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue((((r1.charAt(i)) >= 32) && ((r1.charAt(i)) <= 127)), "char between 32 and 127");
        }
        r2 = RandomStringUtils.randomAscii(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomAlphabetic(50);
        Assertions.assertEquals(50, r1.length(), "randomAlphabetic(50)");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue(((Character.isLetter(r1.charAt(i))) && (!(Character.isDigit(r1.charAt(i))))), "r1 contains alphabetic");
        }
        r2 = RandomStringUtils.randomAlphabetic(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomAlphanumeric(50);
        Assertions.assertEquals(50, r1.length(), "randomAlphanumeric(50)");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue(Character.isLetterOrDigit(r1.charAt(i)), "r1 contains alphanumeric");
        }
        r2 = RandomStringUtils.randomAlphabetic(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomGraph(50);
        Assertions.assertEquals(50, r1.length(), "randomGraph(50) length");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue((((r1.charAt(i)) >= 33) && ((r1.charAt(i)) <= 126)), "char between 33 and 126");
        }
        r2 = RandomStringUtils.randomGraph(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomNumeric(50);
        Assertions.assertEquals(50, r1.length(), "randomNumeric(50)");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue(((Character.isDigit(r1.charAt(i))) && (!(Character.isLetter(r1.charAt(i))))), "r1 contains numeric");
        }
        r2 = RandomStringUtils.randomNumeric(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.randomPrint(50);
        Assertions.assertEquals(50, r1.length(), "randomPrint(50) length");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue((((r1.charAt(i)) >= 32) && ((r1.charAt(i)) <= 126)), "char between 32 and 126");
        }
        r2 = RandomStringUtils.randomPrint(50);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        String set = "abcdefg";
        r1 = RandomStringUtils.random(50, set);
        Assertions.assertEquals(50, r1.length(), "random(50, \"abcdefg\")");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue(((set.indexOf(r1.charAt(i))) > (-1)), "random char in set");
        }
        r2 = RandomStringUtils.random(50, set);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.random(50, ((String) (null)));
        Assertions.assertEquals(50, r1.length(), "random(50) length");
        r2 = RandomStringUtils.random(50, ((String) (null)));
        Assertions.assertEquals(50, r2.length(), "random(50) length");
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        set = "stuvwxyz";
        r1 = RandomStringUtils.random(50, set.toCharArray());
        Assertions.assertEquals(50, r1.length(), "random(50, \"stuvwxyz\")");
        for (int i = 0; i < (r1.length()); i++) {
            Assertions.assertTrue(((set.indexOf(r1.charAt(i))) > (-1)), "random char in set");
        }
        r2 = RandomStringUtils.random(50, set);
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        r1 = RandomStringUtils.random(50, ((char[]) (null)));
        Assertions.assertEquals(50, r1.length(), "random(50) length");
        r2 = RandomStringUtils.random(50, ((char[]) (null)));
        Assertions.assertEquals(50, r2.length(), "random(50) length");
        Assertions.assertTrue((!(r1.equals(r2))), "!r1.equals(r2)");
        final long seed = System.currentTimeMillis();
        r1 = RandomStringUtils.random(50, 0, 0, true, true, null, new Random(seed));
        r2 = RandomStringUtils.random(50, 0, 0, true, true, null, new Random(seed));
        Assertions.assertEquals(r1, r2, "r1.equals(r2)");
        r1 = RandomStringUtils.random(0);
        Assertions.assertEquals("", r1, "random(0).equals(\"\")");
    }

    @Test
    public void testLANG805() {
        final long seed = System.currentTimeMillis();
        Assertions.assertEquals("aaa", RandomStringUtils.random(3, 0, 0, false, false, new char[]{ 'a' }, new Random(seed)));
    }

    @Test
    public void testLANG807() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random(3, 5, 5, false, false));
        final String msg = ex.getMessage();
        Assertions.assertTrue(msg.contains("start"), (("Message (" + msg) + ") must contain 'start'"));
        Assertions.assertTrue(msg.contains("end"), (("Message (" + msg) + ") must contain 'end'"));
    }

    @Test
    public void testExceptions() {
        final char[] DUMMY = new char[]{ 'a' };// valid char array

        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), true, true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), DUMMY));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random(1, new char[0]));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), ((String) (null))));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), 'a', 'z', false, false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), 'a', 'z', false, false, DUMMY));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random((-1), 'a', 'z', false, false, DUMMY, new Random()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random(8, 32, 48, false, true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.random(8, 32, 65, true, false));
    }

    /**
     * Make sure boundary alphanumeric characters are generated by randomAlphaNumeric
     * This test will fail randomly with probability = 6 * (61/62)**1000 ~ 5.2E-7
     */
    @Test
    public void testRandomAlphaNumeric() {
        final char[] testChars = new char[]{ 'a', 'z', 'A', 'Z', '0', '9' };
        final boolean[] found = new boolean[]{ false, false, false, false, false, false };
        for (int i = 0; i < 100; i++) {
            final String randString = RandomStringUtils.randomAlphanumeric(10);
            for (int j = 0; j < (testChars.length); j++) {
                if ((randString.indexOf(testChars[j])) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < (testChars.length); i++) {
            Assertions.assertTrue(found[i], (("alphanumeric character not generated in 1000 attempts: " + (testChars[i])) + " -- repeated failures indicate a problem "));
        }
    }

    /**
     * Make sure '0' and '9' are generated by randomNumeric
     * This test will fail randomly with probability = 2 * (9/10)**1000 ~ 3.5E-46
     */
    @Test
    public void testRandomNumeric() {
        final char[] testChars = new char[]{ '0', '9' };
        final boolean[] found = new boolean[]{ false, false };
        for (int i = 0; i < 100; i++) {
            final String randString = RandomStringUtils.randomNumeric(10);
            for (int j = 0; j < (testChars.length); j++) {
                if ((randString.indexOf(testChars[j])) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < (testChars.length); i++) {
            Assertions.assertTrue(found[i], (("digit not generated in 1000 attempts: " + (testChars[i])) + " -- repeated failures indicate a problem "));
        }
    }

    /**
     * Make sure boundary alpha characters are generated by randomAlphabetic
     * This test will fail randomly with probability = 4 * (51/52)**1000 ~ 1.58E-8
     */
    @Test
    public void testRandomAlphabetic() {
        final char[] testChars = new char[]{ 'a', 'z', 'A', 'Z' };
        final boolean[] found = new boolean[]{ false, false, false, false };
        for (int i = 0; i < 100; i++) {
            final String randString = RandomStringUtils.randomAlphabetic(10);
            for (int j = 0; j < (testChars.length); j++) {
                if ((randString.indexOf(testChars[j])) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < (testChars.length); i++) {
            Assertions.assertTrue(found[i], (("alphanumeric character not generated in 1000 attempts: " + (testChars[i])) + " -- repeated failures indicate a problem "));
        }
    }

    /**
     * Make sure 32 and 127 are generated by randomNumeric
     * This test will fail randomly with probability = 2*(95/96)**1000 ~ 5.7E-5
     */
    @Test
    public void testRandomAscii() {
        final char[] testChars = new char[]{ ((char) (32)), ((char) (126)) };
        final boolean[] found = new boolean[]{ false, false };
        for (int i = 0; i < 100; i++) {
            final String randString = RandomStringUtils.randomAscii(10);
            for (int j = 0; j < (testChars.length); j++) {
                if ((randString.indexOf(testChars[j])) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < (testChars.length); i++) {
            Assertions.assertTrue(found[i], (("ascii character not generated in 1000 attempts: " + ((int) (testChars[i]))) + " -- repeated failures indicate a problem"));
        }
    }

    @Test
    public void testRandomAsciiRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{ASCII}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomAscii(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    @Test
    public void testRandomAlphabeticRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{Alpha}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomAlphabetic(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    @Test
    public void testRandomAlphanumericRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{Alnum}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomAlphanumeric(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    @Test
    public void testRandomGraphRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{Graph}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomGraph(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    @Test
    public void testRandomNumericRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{Digit}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomNumeric(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    @Test
    public void testRandomPrintRange() {
        final int expectedMinLengthInclusive = 1;
        final int expectedMaxLengthExclusive = 11;
        final String pattern = ((("^\\p{Print}{" + expectedMinLengthInclusive) + ',') + expectedMaxLengthExclusive) + "}$";
        int maxCreatedLength = expectedMinLengthInclusive;
        int minCreatedLength = expectedMaxLengthExclusive - 1;
        for (int i = 0; i < 1000; i++) {
            final String s = RandomStringUtils.randomPrint(expectedMinLengthInclusive, expectedMaxLengthExclusive);
            MatcherAssert.assertThat("within range", s.length(), Matchers.allOf(Matchers.greaterThanOrEqualTo(expectedMinLengthInclusive), Matchers.lessThanOrEqualTo((expectedMaxLengthExclusive - 1))));
            Assertions.assertTrue(s.matches(pattern), s);
            if ((s.length()) < minCreatedLength) {
                minCreatedLength = s.length();
            }
            if ((s.length()) > maxCreatedLength) {
                maxCreatedLength = s.length();
            }
        }
        MatcherAssert.assertThat("min generated, may fail randomly rarely", minCreatedLength, Matchers.is(expectedMinLengthInclusive));
        MatcherAssert.assertThat("max generated, may fail randomly rarely", maxCreatedLength, Matchers.is((expectedMaxLengthExclusive - 1)));
    }

    /**
     * Test homogeneity of random strings generated --
     * i.e., test that characters show up with expected frequencies
     * in generated strings.  Will fail randomly about 1 in 1000 times.
     * Repeated failures indicate a problem.
     */
    @Test
    public void testRandomStringUtilsHomog() {
        final String set = "abc";
        final char[] chars = set.toCharArray();
        String gen = "";
        final int[] counts = new int[]{ 0, 0, 0 };
        final int[] expected = new int[]{ 200, 200, 200 };
        for (int i = 0; i < 100; i++) {
            gen = RandomStringUtils.random(6, chars);
            for (int j = 0; j < 6; j++) {
                switch (gen.charAt(j)) {
                    case 'a' :
                        {
                            (counts[0])++;
                            break;
                        }
                    case 'b' :
                        {
                            (counts[1])++;
                            break;
                        }
                    case 'c' :
                        {
                            (counts[2])++;
                            break;
                        }
                    default :
                        {
                            Assertions.fail("generated character not in set");
                        }
                }
            }
        }
        // Perform chi-square test with df = 3-1 = 2, testing at .001 level
        Assertions.assertTrue(((chiSquare(expected, counts)) < 13.82), "test homogeneity -- will fail about 1 in 1000 times");
    }

    /**
     * Checks if the string got by {@link RandomStringUtils#random(int)}
     * can be converted to UTF-8 and back without loss.
     *
     * @see <a href="http://issues.apache.org/jira/browse/LANG-100">LANG-100</a>
     */
    @Test
    public void testLang100() {
        final int size = 5000;
        final Charset charset = Charset.forName("UTF-8");
        final String orig = RandomStringUtils.random(size);
        final byte[] bytes = orig.getBytes(charset);
        final String copy = new String(bytes, charset);
        // for a verbose compare:
        for (int i = 0; (i < (orig.length())) && (i < (copy.length())); i++) {
            final char o = orig.charAt(i);
            final char c = copy.charAt(i);
            Assertions.assertEquals(o, c, (((((("differs at " + i) + "(") + (Integer.toHexString(new Character(o).hashCode()))) + ",") + (Integer.toHexString(new Character(c).hashCode()))) + ")"));
        }
        // compare length also
        Assertions.assertEquals(orig.length(), copy.length());
        // just to be complete
        Assertions.assertEquals(orig, copy);
    }

    /**
     * Test for LANG-1286. Creates situation where old code would
     * overflow a char and result in a code point outside the specified
     * range.
     */
    @Test
    public void testCharOverflow() {
        final int start = Character.MAX_VALUE;
        final int end = Integer.MAX_VALUE;
        @SuppressWarnings("serial")
        final Random fixedRandom = new Random() {
            @Override
            public int nextInt(final int n) {
                // Prevents selection of 'start' as the character
                return (super.nextInt((n - 1))) + 1;
            }
        };
        final String result = RandomStringUtils.random(2, start, end, false, false, null, fixedRandom);
        final int c = result.codePointAt(0);
        Assertions.assertTrue(((c >= start) && (c < end)), String.format("Character '%d' not in range [%d,%d).", c, start, end));
    }
}

