/**
 * Copyright (C) 2009 The Android Open Source Project
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


import NativePluralRules.FEW;
import NativePluralRules.MANY;
import NativePluralRules.ONE;
import NativePluralRules.OTHER;
import NativePluralRules.TWO;
import NativePluralRules.ZERO;
import java.util.Locale;
import junit.framework.TestCase;


public class NativePluralRulesTest extends TestCase {
    public void testEnglish() throws Exception {
        NativePluralRules npr = NativePluralRules.forLocale(new Locale("en", "US"));
        TestCase.assertEquals(OTHER, npr.quantityForInt((-1)));
        TestCase.assertEquals(OTHER, npr.quantityForInt(0));
        TestCase.assertEquals(ONE, npr.quantityForInt(1));
        TestCase.assertEquals(OTHER, npr.quantityForInt(2));
    }

    public void testCzech() throws Exception {
        NativePluralRules npr = NativePluralRules.forLocale(new Locale("cs", "CZ"));
        TestCase.assertEquals(OTHER, npr.quantityForInt((-1)));
        TestCase.assertEquals(OTHER, npr.quantityForInt(0));
        TestCase.assertEquals(ONE, npr.quantityForInt(1));
        TestCase.assertEquals(FEW, npr.quantityForInt(2));
        TestCase.assertEquals(FEW, npr.quantityForInt(3));
        TestCase.assertEquals(FEW, npr.quantityForInt(4));
        TestCase.assertEquals(OTHER, npr.quantityForInt(5));
    }

    public void testArabic() throws Exception {
        NativePluralRules npr = NativePluralRules.forLocale(new Locale("ar"));
        TestCase.assertEquals(OTHER, npr.quantityForInt((-1)));
        TestCase.assertEquals(ZERO, npr.quantityForInt(0));
        TestCase.assertEquals(ONE, npr.quantityForInt(1));
        TestCase.assertEquals(TWO, npr.quantityForInt(2));
        for (int i = 3; i <= 10; ++i) {
            TestCase.assertEquals(FEW, npr.quantityForInt(i));
        }
        TestCase.assertEquals(MANY, npr.quantityForInt(11));
        TestCase.assertEquals(MANY, npr.quantityForInt(99));
        TestCase.assertEquals(OTHER, npr.quantityForInt(100));
        TestCase.assertEquals(OTHER, npr.quantityForInt(101));
        TestCase.assertEquals(OTHER, npr.quantityForInt(102));
        TestCase.assertEquals(FEW, npr.quantityForInt(103));
        TestCase.assertEquals(MANY, npr.quantityForInt(111));
    }

    public void testHebrew() throws Exception {
        // java.util.Locale will translate "he" to the deprecated "iw".
        NativePluralRules he = NativePluralRules.forLocale(new Locale("he"));
        TestCase.assertEquals(ONE, he.quantityForInt(1));
        TestCase.assertEquals(TWO, he.quantityForInt(2));
        TestCase.assertEquals(OTHER, he.quantityForInt(3));
        TestCase.assertEquals(MANY, he.quantityForInt(10));
    }
}

