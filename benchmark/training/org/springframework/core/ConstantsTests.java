/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core;


import java.util.Locale;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 28.04.2003
 */
public class ConstantsTests {
    @Test
    public void constants() {
        Constants c = new Constants(ConstantsTests.A.class);
        Assert.assertEquals(ConstantsTests.A.class.getName(), c.getClassName());
        Assert.assertEquals(9, c.getSize());
        Assert.assertEquals(ConstantsTests.A.DOG, c.asNumber("DOG").intValue());
        Assert.assertEquals(ConstantsTests.A.DOG, c.asNumber("dog").intValue());
        Assert.assertEquals(ConstantsTests.A.CAT, c.asNumber("cat").intValue());
        try {
            c.asNumber("bogus");
            Assert.fail("Can't get bogus field");
        } catch (Constants expected) {
        }
        Assert.assertTrue(c.asString("S1").equals(ConstantsTests.A.S1));
        try {
            c.asNumber("S1");
            Assert.fail("Wrong type");
        } catch (Constants expected) {
        }
    }

    @Test
    public void getNames() {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<?> names = c.getNames("");
        Assert.assertEquals(c.getSize(), names.size());
        Assert.assertTrue(names.contains("DOG"));
        Assert.assertTrue(names.contains("CAT"));
        Assert.assertTrue(names.contains("S1"));
        names = c.getNames("D");
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.contains("DOG"));
        names = c.getNames("d");
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.contains("DOG"));
    }

    @Test
    public void getValues() {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<?> values = c.getValues("");
        Assert.assertEquals(7, values.size());
        Assert.assertTrue(values.contains(Integer.valueOf(0)));
        Assert.assertTrue(values.contains(Integer.valueOf(66)));
        Assert.assertTrue(values.contains(""));
        values = c.getValues("D");
        Assert.assertEquals(1, values.size());
        Assert.assertTrue(values.contains(Integer.valueOf(0)));
        values = c.getValues("prefix");
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(values.contains(Integer.valueOf(1)));
        Assert.assertTrue(values.contains(Integer.valueOf(2)));
        values = c.getValuesForProperty("myProperty");
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(values.contains(Integer.valueOf(1)));
        Assert.assertTrue(values.contains(Integer.valueOf(2)));
    }

    @Test
    public void getValuesInTurkey() {
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", ""));
        try {
            Constants c = new Constants(ConstantsTests.A.class);
            Set<?> values = c.getValues("");
            Assert.assertEquals(7, values.size());
            Assert.assertTrue(values.contains(Integer.valueOf(0)));
            Assert.assertTrue(values.contains(Integer.valueOf(66)));
            Assert.assertTrue(values.contains(""));
            values = c.getValues("D");
            Assert.assertEquals(1, values.size());
            Assert.assertTrue(values.contains(Integer.valueOf(0)));
            values = c.getValues("prefix");
            Assert.assertEquals(2, values.size());
            Assert.assertTrue(values.contains(Integer.valueOf(1)));
            Assert.assertTrue(values.contains(Integer.valueOf(2)));
            values = c.getValuesForProperty("myProperty");
            Assert.assertEquals(2, values.size());
            Assert.assertTrue(values.contains(Integer.valueOf(1)));
            Assert.assertTrue(values.contains(Integer.valueOf(2)));
        } finally {
            Locale.setDefault(oldLocale);
        }
    }

    @Test
    public void suffixAccess() {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<?> names = c.getNamesForSuffix("_PROPERTY");
        Assert.assertEquals(2, names.size());
        Assert.assertTrue(names.contains("NO_PROPERTY"));
        Assert.assertTrue(names.contains("YES_PROPERTY"));
        Set<?> values = c.getValuesForSuffix("_PROPERTY");
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(values.contains(Integer.valueOf(3)));
        Assert.assertTrue(values.contains(Integer.valueOf(4)));
    }

    @Test
    public void toCode() {
        Constants c = new Constants(ConstantsTests.A.class);
        Assert.assertEquals("DOG", c.toCode(Integer.valueOf(0), ""));
        Assert.assertEquals("DOG", c.toCode(Integer.valueOf(0), "D"));
        Assert.assertEquals("DOG", c.toCode(Integer.valueOf(0), "DO"));
        Assert.assertEquals("DOG", c.toCode(Integer.valueOf(0), "DoG"));
        Assert.assertEquals("DOG", c.toCode(Integer.valueOf(0), null));
        Assert.assertEquals("CAT", c.toCode(Integer.valueOf(66), ""));
        Assert.assertEquals("CAT", c.toCode(Integer.valueOf(66), "C"));
        Assert.assertEquals("CAT", c.toCode(Integer.valueOf(66), "ca"));
        Assert.assertEquals("CAT", c.toCode(Integer.valueOf(66), "cAt"));
        Assert.assertEquals("CAT", c.toCode(Integer.valueOf(66), null));
        Assert.assertEquals("S1", c.toCode("", ""));
        Assert.assertEquals("S1", c.toCode("", "s"));
        Assert.assertEquals("S1", c.toCode("", "s1"));
        Assert.assertEquals("S1", c.toCode("", null));
        try {
            c.toCode("bogus", "bogus");
            Assert.fail("Should have thrown ConstantException");
        } catch (Constants expected) {
        }
        try {
            c.toCode("bogus", null);
            Assert.fail("Should have thrown ConstantException");
        } catch (Constants expected) {
        }
        Assert.assertEquals("MY_PROPERTY_NO", c.toCodeForProperty(Integer.valueOf(1), "myProperty"));
        Assert.assertEquals("MY_PROPERTY_YES", c.toCodeForProperty(Integer.valueOf(2), "myProperty"));
        try {
            c.toCodeForProperty("bogus", "bogus");
            Assert.fail("Should have thrown ConstantException");
        } catch (Constants expected) {
        }
        Assert.assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), ""));
        Assert.assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "G"));
        Assert.assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "OG"));
        Assert.assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "DoG"));
        Assert.assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), null));
        Assert.assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), ""));
        Assert.assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "T"));
        Assert.assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "at"));
        Assert.assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "cAt"));
        Assert.assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), null));
        Assert.assertEquals("S1", c.toCodeForSuffix("", ""));
        Assert.assertEquals("S1", c.toCodeForSuffix("", "1"));
        Assert.assertEquals("S1", c.toCodeForSuffix("", "s1"));
        Assert.assertEquals("S1", c.toCodeForSuffix("", null));
        try {
            c.toCodeForSuffix("bogus", "bogus");
            Assert.fail("Should have thrown ConstantException");
        } catch (Constants expected) {
        }
        try {
            c.toCodeForSuffix("bogus", null);
            Assert.fail("Should have thrown ConstantException");
        } catch (Constants expected) {
        }
    }

    @Test
    public void getValuesWithNullPrefix() throws Exception {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<?> values = c.getValues(null);
        Assert.assertEquals("Must have returned *all* public static final values", 7, values.size());
    }

    @Test
    public void getValuesWithEmptyStringPrefix() throws Exception {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<Object> values = c.getValues("");
        Assert.assertEquals("Must have returned *all* public static final values", 7, values.size());
    }

    @Test
    public void getValuesWithWhitespacedStringPrefix() throws Exception {
        Constants c = new Constants(ConstantsTests.A.class);
        Set<?> values = c.getValues(" ");
        Assert.assertEquals("Must have returned *all* public static final values", 7, values.size());
    }

    @Test
    public void withClassThatExposesNoConstants() throws Exception {
        Constants c = new Constants(ConstantsTests.NoConstants.class);
        Assert.assertEquals(0, c.getSize());
        final Set<?> values = c.getValues("");
        Assert.assertNotNull(values);
        Assert.assertEquals(0, values.size());
    }

    @Test
    public void ctorWithNullClass() throws Exception {
        try {
            new Constants(null);
            Assert.fail("Must have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    private static final class NoConstants {}

    @SuppressWarnings("unused")
    private static final class A {
        public static final int DOG = 0;

        public static final int CAT = 66;

        public static final String S1 = "";

        public static final int PREFIX_NO = 1;

        public static final int PREFIX_YES = 2;

        public static final int MY_PROPERTY_NO = 1;

        public static final int MY_PROPERTY_YES = 2;

        public static final int NO_PROPERTY = 3;

        public static final int YES_PROPERTY = 4;

        /**
         * ignore these
         */
        protected static final int P = -1;

        protected boolean f;

        static final Object o = new Object();
    }
}

