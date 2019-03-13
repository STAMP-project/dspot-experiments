/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.text;


import java.text.CharacterIterator;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;
import junit.framework.TestCase;


public class CollatorTest extends TestCase {
    public void test_setStrengthI() throws Exception {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);
        TestCase.assertEquals(Collator.PRIMARY, collator.getStrength());
        collator.setStrength(Collator.SECONDARY);
        TestCase.assertEquals(Collator.SECONDARY, collator.getStrength());
        collator.setStrength(Collator.TERTIARY);
        TestCase.assertEquals(Collator.TERTIARY, collator.getStrength());
        collator.setStrength(Collator.IDENTICAL);
        TestCase.assertEquals(Collator.IDENTICAL, collator.getStrength());
        try {
            collator.setStrength((-1));
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void test_stackCorruption() throws Exception {
        // This used to crash Android.
        Collator mColl = Collator.getInstance();
        mColl.setStrength(Collator.PRIMARY);
        mColl.getCollationKey("2d294f2d3739433565147655394f3762f3147312d3731641452f310");
    }

    public void test_collationKeySize() throws Exception {
        // Test to verify that very large collation keys are not truncated.
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            b.append("0123456789ABCDEF");
        }
        String sixteen = b.toString();
        b.append("_THE_END");
        String sixteenplus = b.toString();
        Collator mColl = Collator.getInstance();
        mColl.setStrength(Collator.PRIMARY);
        byte[] arr = mColl.getCollationKey(sixteen).toByteArray();
        int len = arr.length;
        TestCase.assertTrue("Collation key not 0 terminated", ((arr[((arr.length) - 1)]) == 0));
        len--;
        String foo = new String(arr, 0, len, "iso8859-1");
        arr = mColl.getCollationKey(sixteen).toByteArray();
        len = arr.length;
        TestCase.assertTrue("Collation key not 0 terminated", ((arr[((arr.length) - 1)]) == 0));
        len--;
        String bar = new String(arr, 0, len, "iso8859-1");
        TestCase.assertTrue("Collation keys should differ", foo.equals(bar));
    }

    public void test_decompositionCompatibility() throws Exception {
        Collator myCollator = Collator.getInstance();
        myCollator.setDecomposition(Collator.NO_DECOMPOSITION);
        TestCase.assertFalse("Error: \u00e0\u0325 should not equal to a\u0325\u0300 without decomposition", ((myCollator.compare("\u00e0\u0325", "a\u0325\u0300")) == 0));
        myCollator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        TestCase.assertTrue("Error: \u00e0\u0325 should equal to a\u0325\u0300 with decomposition", ((myCollator.compare("\u00e0\u0325", "a\u0325\u0300")) == 0));
    }

    public void testEqualsObject() throws ParseException {
        String rule = "< a < b < c < d < e";
        RuleBasedCollator coll = new RuleBasedCollator(rule);
        TestCase.assertEquals(Collator.TERTIARY, coll.getStrength());
        TestCase.assertEquals(Collator.NO_DECOMPOSITION, coll.getDecomposition());
        RuleBasedCollator other = new RuleBasedCollator(rule);
        TestCase.assertTrue(coll.equals(other));
        coll.setStrength(Collator.PRIMARY);
        TestCase.assertFalse(coll.equals(other));
        coll.setStrength(Collator.TERTIARY);
        coll.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        other.setDecomposition(Collator.NO_DECOMPOSITION);// See comment above.

        TestCase.assertFalse(coll.equals(other));
    }

    public void test_Harmony_1352() throws Exception {
        // Regression test for HARMONY-1352, that doesn't get run in the harmony test suite because
        // of an earlier failure.
        try {
            new RuleBasedCollator("< a< b< c< d").getCollationElementIterator(((CharacterIterator) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException expected) {
        }
    }

    public void testGetCollationElementIteratorString_es() throws Exception {
        assertGetCollationElementIteratorString(new Locale("es", "", ""), "cha", 0, 1, 2, 3);
    }

    public void testGetCollationElementIteratorString_de_DE() throws Exception {
        assertGetCollationElementIteratorString(new Locale("de", "DE", ""), "\u00e6b", 0, 1, 1, 1, 1, 2);
    }

    public void testGetCollationElementIteratorCharacterIterator_es() throws Exception {
        assertGetCollationElementIteratorCharacterIterator(new Locale("es", "", ""), "cha", 0, 1, 2, 3);
    }

    public void testGetCollationElementIteratorCharacterIterator_de_DE() throws Exception {
        assertGetCollationElementIteratorCharacterIterator(new Locale("de", "DE", ""), "\u00e6b", 0, 1, 1, 1, 1, 2);
    }
}

