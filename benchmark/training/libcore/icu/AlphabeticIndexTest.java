/**
 * Copyright (C) 2013 The Android Open Source Project
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
package libcore.icu;


import AlphabeticIndex.ImmutableIndex;
import java.util.Locale;
import junit.framework.TestCase;


public class AlphabeticIndexTest extends TestCase {
    public void test_en() throws Exception {
        // English [A-Z]
        AlphabeticIndex.ImmutableIndex en = AlphabeticIndexTest.createIndex(Locale.ENGLISH);
        AlphabeticIndexTest.assertHasLabel(en, "Allen", "A");
        AlphabeticIndexTest.assertHasLabel(en, "allen", "A");
    }

    public void test_ja() throws Exception {
        AlphabeticIndex.ImmutableIndex ja = AlphabeticIndexTest.createIndex(Locale.JAPANESE);
        // Japanese
        // sorts hiragana/katakana, Kanji/Chinese, English, other
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        // hiragana "a"
        AlphabeticIndexTest.assertHasLabel(ja, "Allen", "A");
        AlphabeticIndexTest.assertHasLabel(ja, "\u3041", "\u3042");
        // katakana "a"
        AlphabeticIndexTest.assertHasLabel(ja, "\u30a1", "\u3042");
        // Kanji (sorts to inflow section)
        AlphabeticIndexTest.assertHasLabel(ja, "\u65e5", "");
        // http://bugs.icu-project.org/trac/ticket/10423 / http://b/10809397
        AlphabeticIndexTest.assertHasLabel(ja, "\u95c7", "");
        // English
        AlphabeticIndexTest.assertHasLabel(ja, "Smith", "S");
        // Chinese (sorts to inflow section)
        /* Shen/Chen */
        AlphabeticIndexTest.assertHasLabel(ja, "\u6c88", "");
        // Korean Hangul (sorts to overflow section)
        AlphabeticIndexTest.assertHasLabel(ja, "\u1100", "");
    }

    public void test_ko() throws Exception {
        // Korean (sorts Korean, then English)
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        AlphabeticIndex.ImmutableIndex ko = AlphabeticIndexTest.createIndex(Locale.KOREAN);
        AlphabeticIndexTest.assertHasLabel(ko, "\u1100", "\u3131");
        AlphabeticIndexTest.assertHasLabel(ko, "\u3131", "\u3131");
        AlphabeticIndexTest.assertHasLabel(ko, "\u1101", "\u3131");
        AlphabeticIndexTest.assertHasLabel(ko, "\u1161", "\u314e");
    }

    public void test_cs() throws Exception {
        // Czech
        // ?, [A-C], ?,[D-H], CH, [I-R], ?, S, ?, [T-Z], ?, ?
        AlphabeticIndex.ImmutableIndex cs = AlphabeticIndexTest.createIndex(new Locale("cs"));
        AlphabeticIndexTest.assertHasLabel(cs, "Cena", "C");
        AlphabeticIndexTest.assertHasLabel(cs, "??p", "\u010c");
        AlphabeticIndexTest.assertHasLabel(cs, "Ruda", "R");
        AlphabeticIndexTest.assertHasLabel(cs, "?ada", "\u0158");
        AlphabeticIndexTest.assertHasLabel(cs, "Selka", "S");
        AlphabeticIndexTest.assertHasLabel(cs, "??la", "\u0160");
        AlphabeticIndexTest.assertHasLabel(cs, "Zebra", "Z");
        AlphabeticIndexTest.assertHasLabel(cs, "??ba", "\u017d");
        AlphabeticIndexTest.assertHasLabel(cs, "Chata", "CH");
    }

    public void test_fr() throws Exception {
        // French: [A-Z] (no accented chars)
        AlphabeticIndex.ImmutableIndex fr = AlphabeticIndexTest.createIndex(Locale.FRENCH);
        AlphabeticIndexTest.assertHasLabel(fr, "?fer", "O");
        AlphabeticIndexTest.assertHasLabel(fr, "?ster", "O");
    }

    public void test_da() throws Exception {
        // Danish: [A-Z], ?, ?, ?
        AlphabeticIndex.ImmutableIndex da = AlphabeticIndexTest.createIndex(new Locale("da"));
        AlphabeticIndexTest.assertHasLabel(da, "?nes", "\u00c6");
        AlphabeticIndexTest.assertHasLabel(da, "?fer", "\u00d8");
        AlphabeticIndexTest.assertHasLabel(da, "?ster", "\u00d8");
        AlphabeticIndexTest.assertHasLabel(da, "?g?rd", "\u00c5");
    }

    public void test_de() throws Exception {
        // German: [A-S,Sch,St,T-Z] (no ? or umlauted characters in standard alphabet)
        AlphabeticIndex.ImmutableIndex de = AlphabeticIndexTest.createIndex(Locale.GERMAN);
        AlphabeticIndexTest.assertHasLabel(de, "?ind", "S");
        AlphabeticIndexTest.assertHasLabel(de, "Sacher", "S");
        AlphabeticIndexTest.assertHasLabel(de, "Schiller", "Sch");
        AlphabeticIndexTest.assertHasLabel(de, "Steiff", "St");
    }

    public void test_th() throws Exception {
        // Thai (sorts English then Thai)
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
        AlphabeticIndex.ImmutableIndex th = AlphabeticIndexTest.createIndex(new Locale("th"));
        AlphabeticIndexTest.assertHasLabel(th, "\u0e2d\u0e07\u0e04\u0e4c\u0e40\u0e25\u0e47\u0e01", "\u0e2d");
        AlphabeticIndexTest.assertHasLabel(th, "\u0e2a\u0e34\u0e07\u0e2b\u0e40\u0e2a\u0e19\u0e35", "\u0e2a");
    }

    public void test_ar() throws Exception {
        // Arabic (sorts English then Arabic)
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        AlphabeticIndex.ImmutableIndex ar = AlphabeticIndexTest.createIndex(new Locale("ar"));
        /* Noor */
        AlphabeticIndexTest.assertHasLabel(ar, "\u0646\u0648\u0631", "\u0646");
    }

    public void test_he() throws Exception {
        // Hebrew (sorts English then Hebrew)
        // ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        AlphabeticIndex.ImmutableIndex he = AlphabeticIndexTest.createIndex(new Locale("he"));
        AlphabeticIndexTest.assertHasLabel(he, "\u05e4\u05e8\u05d9\u05d3\u05de\u05df", "\u05e4");
    }

    public void test_zh_CN() throws Exception {
        // Simplified Chinese (default collator Pinyin): [A-Z]
        // Shen/Chen (simplified): should be, usually, 'S' for name collator and 'C' for apps/other
        AlphabeticIndex.ImmutableIndex zh_CN = AlphabeticIndexTest.createIndex(new Locale("zh", "CN"));
        // Jia/Gu: should be, usually, 'J' for name collator and 'G' for apps/other
        AlphabeticIndexTest.assertHasLabel(zh_CN, "\u8d3e", "J");
        // Shen/Chen
        AlphabeticIndexTest.assertHasLabel(zh_CN, "\u6c88", "C");// icu4c 50 does not specialize for names.

        // Shen/Chen (traditional)
        AlphabeticIndexTest.assertHasLabel(zh_CN, "\u700b", "S");
    }

    public void test_zh_HK() throws Exception {
        // Traditional Chinese (strokes).
        // ?, [1-33, 35, 36, 39, 48]?, ?
        // Shen/Chen
        AlphabeticIndex.ImmutableIndex zh_HK = AlphabeticIndexTest.createIndex(new Locale("zh", "HK"));
        AlphabeticIndexTest.assertHasLabel(zh_HK, "\u6c88", "7\u5283");
        AlphabeticIndexTest.assertHasLabel(zh_HK, "\u700b", "18\u5283");
        // Jia/Gu
        AlphabeticIndexTest.assertHasLabel(zh_HK, "\u8d3e", "10\u5283");
    }

    public void test_constructor_NPE() throws Exception {
        try {
            new AlphabeticIndex(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_addLabels_NPE() throws Exception {
        AlphabeticIndex ai = new AlphabeticIndex(Locale.US);
        try {
            ai.addLabels(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    // ICU 51 default max label count is 99. Test to make sure can create an
    // index with a larger number of labels.
    public void test_setMaxLabelCount() throws Exception {
        final int MAX_LABEL_COUNT = 500;
        AlphabeticIndex ai = new AlphabeticIndex(Locale.US).setMaxLabelCount(MAX_LABEL_COUNT).addLabels(Locale.JAPANESE).addLabels(Locale.KOREAN).addLabels(new Locale("th")).addLabels(new Locale("ar")).addLabels(new Locale("he")).addLabels(new Locale("el")).addLabels(new Locale("ru"));
        TestCase.assertEquals(MAX_LABEL_COUNT, ai.getMaxLabelCount());
        TestCase.assertEquals(208, ai.getBucketCount());
        AlphabeticIndex.ImmutableIndex ii = ai.getImmutableIndex();
        TestCase.assertEquals(ai.getBucketCount(), ii.getBucketCount());
    }

    public void test_getBucketIndex_NPE() throws Exception {
        AlphabeticIndex.ImmutableIndex ii = AlphabeticIndexTest.createIndex(Locale.US);
        try {
            ii.getBucketIndex(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_getBucketLabel_invalid() throws Exception {
        AlphabeticIndex.ImmutableIndex ii = AlphabeticIndexTest.createIndex(Locale.US);
        try {
            ii.getBucketLabel((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ii.getBucketLabel(123456);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

