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


import java.text.Normalizer;
import junit.framework.TestCase;

import static java.text.Normalizer.Form.NFC;
import static java.text.Normalizer.Form.NFD;
import static java.text.Normalizer.Form.NFKC;
import static java.text.Normalizer.Form.NFKD;


public class NormalizerTest extends TestCase {
    public void testNormalize() {
        final String src = "\u03d3\u03d4\u1e9b";
        // Should already be canonical composed
        TestCase.assertEquals(src, Normalizer.normalize(src, NFC));
        // Composed to canonical decomposed
        TestCase.assertEquals("\u03d2\u0301\u03d2\u0308\u017f\u0307", Normalizer.normalize(src, NFD));
        // Composed to compatibility composed
        TestCase.assertEquals("\u038e\u03ab\u1e61", Normalizer.normalize(src, NFKC));
        // Composed to compatibility decomposed
        TestCase.assertEquals("\u03a5\u0301\u03a5\u0308s\u0307", Normalizer.normalize(src, NFKD));
        // Decomposed to canonical composed
        TestCase.assertEquals("\u00e9", Normalizer.normalize("e\u0301", NFC));
        // Decomposed to compatibility composed
        TestCase.assertEquals("\u1e69", Normalizer.normalize("\u1e9b\u0323", NFKC));
        try {
            Normalizer.normalize(null, NFC);
            TestCase.fail("Did not throw error on null argument");
        } catch (NullPointerException e) {
            // pass
        }
    }

    public void testIsNormalized() {
        String target;
        target = new String(new char[]{ 979, 980, 7835 });
        TestCase.assertTrue(Normalizer.isNormalized(target, NFC));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFD));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKC));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKD));
        target = new String(new char[]{ 978, 769, 978, 776, 383, 775 });
        TestCase.assertFalse(Normalizer.isNormalized(target, NFC));
        TestCase.assertTrue(Normalizer.isNormalized(target, NFD));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKC));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKD));
        target = new String(new char[]{ 910, 939, 7777 });
        TestCase.assertTrue(Normalizer.isNormalized(target, NFC));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFD));
        TestCase.assertTrue(Normalizer.isNormalized(target, NFKC));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKD));
        target = new String(new char[]{ 933, 769, 933, 776, 115, 775 });
        TestCase.assertFalse(Normalizer.isNormalized(target, NFC));
        TestCase.assertTrue(Normalizer.isNormalized(target, NFD));
        TestCase.assertFalse(Normalizer.isNormalized(target, NFKC));
        TestCase.assertTrue(Normalizer.isNormalized(target, NFKD));
        try {
            Normalizer.isNormalized(null, NFC);
            TestCase.fail("Did not throw NullPointerException on null argument");
        } catch (NullPointerException e) {
            // pass
        }
    }
}

