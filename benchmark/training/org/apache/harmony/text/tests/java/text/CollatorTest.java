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
package org.apache.harmony.text.tests.java.text;


import java.text.Collator;
import java.util.Locale;
import junit.framework.TestCase;


public class CollatorTest extends TestCase {
    /**
     *
     *
     * @unknown java.text.Collator#clone()
     */
    public void test_clone() {
        Collator c = Collator.getInstance(Locale.GERMAN);
        Collator c2 = ((Collator) (c.clone()));
        TestCase.assertTrue("Clones answered false to equals", c.equals(c2));
        TestCase.assertTrue("Clones were equivalent", (c != c2));
    }

    /**
     *
     *
     * @unknown java.text.Collator#compare(java.lang.Object, java.lang.Object)
     */
    public void test_compareLjava_lang_ObjectLjava_lang_Object() {
        Collator c = Collator.getInstance(Locale.FRENCH);
        Object o;
        Object o2;
        c.setStrength(Collator.IDENTICAL);
        o = "E";
        o2 = "F";
        TestCase.assertTrue("a) Failed on primary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "\u00e9";
        TestCase.assertTrue("a) Failed on secondary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "E";
        TestCase.assertTrue("a) Failed on tertiary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "e";
        TestCase.assertEquals("a) Failed on equivalence", 0, c.compare(o, o2));
        TestCase.assertTrue("a) Failed on primary expansion", ((c.compare("\u01db", "v")) < 0));
        c.setStrength(Collator.TERTIARY);
        o = "E";
        o2 = "F";
        TestCase.assertTrue("b) Failed on primary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "\u00e9";
        TestCase.assertTrue("b) Failed on secondary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "E";
        TestCase.assertTrue("b) Failed on tertiary difference", ((c.compare(o, o2)) < 0));
        o = "\u0001";
        o2 = "\u0002";
        TestCase.assertEquals("b) Failed on identical", 0, c.compare(o, o2));
        o = "e";
        o2 = "e";
        TestCase.assertEquals("b) Failed on equivalence", 0, c.compare(o, o2));
        c.setStrength(Collator.SECONDARY);
        o = "E";
        o2 = "F";
        TestCase.assertTrue("c) Failed on primary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "\u00e9";
        TestCase.assertTrue("c) Failed on secondary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "E";
        TestCase.assertEquals("c) Failed on tertiary difference", 0, c.compare(o, o2));
        o = "\u0001";
        o2 = "\u0002";
        TestCase.assertEquals("c) Failed on identical", 0, c.compare(o, o2));
        o = "e";
        o2 = "e";
        TestCase.assertEquals("c) Failed on equivalence", 0, c.compare(o, o2));
        c.setStrength(Collator.PRIMARY);
        o = "E";
        o2 = "F";
        TestCase.assertTrue("d) Failed on primary difference", ((c.compare(o, o2)) < 0));
        o = "e";
        o2 = "\u00e9";
        TestCase.assertEquals("d) Failed on secondary difference", 0, c.compare(o, o2));
        o = "e";
        o2 = "E";
        TestCase.assertEquals("d) Failed on tertiary difference", 0, c.compare(o, o2));
        o = "\u0001";
        o2 = "\u0002";
        TestCase.assertEquals("d) Failed on identical", 0, c.compare(o, o2));
        o = "e";
        o2 = "e";
        TestCase.assertEquals("d) Failed on equivalence", 0, c.compare(o, o2));
        try {
            c.compare("e", new StringBuffer("Blah"));
        } catch (ClassCastException e) {
            // correct
            return;
        }
        TestCase.fail("Failed to throw ClassCastException");
    }

    /**
     *
     *
     * @unknown java.text.Collator#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        Collator c = Collator.getInstance(Locale.ENGLISH);
        Collator c2 = ((Collator) (c.clone()));
        TestCase.assertTrue("Cloned collators not equal", c.equals(c2));
        c2.setStrength(Collator.SECONDARY);
        TestCase.assertTrue("Collators with different strengths equal", (!(c.equals(c2))));
    }

    /**
     *
     *
     * @unknown java.text.Collator#equals(java.lang.String, java.lang.String)
     */
    public void test_equalsLjava_lang_StringLjava_lang_String() {
        Collator c = Collator.getInstance(Locale.FRENCH);
        c.setStrength(Collator.IDENTICAL);
        TestCase.assertTrue("a) Failed on primary difference", (!(c.equals("E", "F"))));
        TestCase.assertTrue("a) Failed on secondary difference", (!(c.equals("e", "\u00e9"))));
        TestCase.assertTrue("a) Failed on tertiary difference", (!(c.equals("e", "E"))));
        TestCase.assertTrue("a) Failed on equivalence", c.equals("e", "e"));
        c.setStrength(Collator.TERTIARY);
        TestCase.assertTrue("b) Failed on primary difference", (!(c.equals("E", "F"))));
        TestCase.assertTrue("b) Failed on secondary difference", (!(c.equals("e", "\u00e9"))));
        TestCase.assertTrue("b) Failed on tertiary difference", (!(c.equals("e", "E"))));
        TestCase.assertTrue("b) Failed on identical", c.equals("\u0001", "\u0002"));
        TestCase.assertTrue("b) Failed on equivalence", c.equals("e", "e"));
        c.setStrength(Collator.SECONDARY);
        TestCase.assertTrue("c) Failed on primary difference", (!(c.equals("E", "F"))));
        TestCase.assertTrue("c) Failed on secondary difference", (!(c.equals("e", "\u00e9"))));
        TestCase.assertTrue("c) Failed on tertiary difference", c.equals("e", "E"));
        TestCase.assertTrue("c) Failed on identical", c.equals("\u0001", "\u0002"));
        TestCase.assertTrue("c) Failed on equivalence", c.equals("e", "e"));
        c.setStrength(Collator.PRIMARY);
        TestCase.assertTrue("d) Failed on primary difference", (!(c.equals("E", "F"))));
        TestCase.assertTrue("d) Failed on secondary difference", c.equals("e", "\u00e9"));
        TestCase.assertTrue("d) Failed on tertiary difference", c.equals("e", "E"));
        TestCase.assertTrue("d) Failed on identical", c.equals("\u0001", "\u0002"));
        TestCase.assertTrue("d) Failed on equivalence", c.equals("e", "e"));
    }

    /**
     *
     *
     * @unknown java.text.Collator#getInstance()
     */
    public void test_getInstance() {
        Collator c1 = Collator.getInstance();
        Collator c2 = Collator.getInstance(Locale.getDefault());
        TestCase.assertTrue("Wrong locale", c1.equals(c2));
    }

    /**
     *
     *
     * @unknown java.text.Collator#getInstance(java.util.Locale)
     */
    public void test_getInstanceLjava_util_Locale() {
        TestCase.assertTrue("Used to test", true);
    }

    /**
     *
     *
     * @unknown java.text.Collator#setStrength(int)
     */
    public void test_setStrengthI() {
        TestCase.assertTrue("Used to test", true);
    }
}

