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


import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import junit.framework.TestCase;


public class BreakIteratorTest extends TestCase {
    BreakIterator iterator;

    public void testGetAvailableLocales() {
        Locale[] locales = BreakIterator.getAvailableLocales();
        TestCase.assertTrue("Array available locales is null", (locales != null));
        TestCase.assertTrue("Array available locales is 0-length", ((locales != null) && ((locales.length) != 0)));
        boolean found = false;
        for (Locale l : locales) {
            if (l.equals(Locale.US)) {
                // expected
                found = true;
            }
        }
        TestCase.assertTrue((("At least locale " + (Locale.US)) + " must be presented"), found);
    }

    public void testGetWordInstanceLocale() {
        BreakIterator it1 = BreakIterator.getWordInstance(Locale.CANADA_FRENCH);
        TestCase.assertTrue("Incorrect BreakIterator", (it1 != (BreakIterator.getWordInstance())));
        BreakIterator it2 = BreakIterator.getWordInstance(new Locale("bad locale"));
        TestCase.assertTrue("Incorrect BreakIterator", (it2 != (BreakIterator.getWordInstance())));
    }

    // http://b/7307154 - we used to pin an unbounded number of char[]s, relying on finalization.
    public void testStress() throws Exception {
        char[] cs = new char[]{ 'a' };
        for (int i = 0; i < 4096; ++i) {
            BreakIterator it = BreakIterator.getWordInstance(Locale.US);
            it.setText(new String(cs));
        }
    }

    public void testWordBoundaries() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append("12345");
        }
        String s = sb.toString();
        BreakIterator it = BreakIterator.getWordInstance(Locale.US);
        it.setText(s);
        // Check we're not leaking global references. 2048 would bust the VM's hard-coded limit.
        for (int i = 0; i < 2048; ++i) {
            it.setText(s);
        }
        BreakIterator clone = ((BreakIterator) (it.clone()));
        assertExpectedWordBoundaries(it, s);
        assertExpectedWordBoundaries(clone, s);
    }

    public void testIsBoundary() {
        BreakIterator it = BreakIterator.getCharacterInstance(Locale.US);
        it.setText("hello");
        try {
            it.isBoundary((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Note that this exception is not listed in the Java API documentation
        }
        TestCase.assertTrue(it.isBoundary(0));
        TestCase.assertTrue(it.isBoundary(1));
        TestCase.assertTrue(it.isBoundary(4));
        TestCase.assertTrue(it.isBoundary(5));
        try {
            it.isBoundary(6);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Note that this exception is not listed in the Java API documentation
        }
    }

    public void testFollowing() {
        BreakIterator it = BreakIterator.getCharacterInstance(Locale.US);
        it.setText("hello");
        try {
            it.following((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Expected exception
        }
        TestCase.assertEquals(1, it.following(0));
        TestCase.assertEquals(2, it.following(1));
        TestCase.assertEquals(5, it.following(4));
        TestCase.assertEquals(BreakIterator.DONE, it.following(5));
        try {
            it.following(6);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Expected exception
        }
    }

    public void testPreceding() {
        BreakIterator it = BreakIterator.getCharacterInstance(Locale.US);
        it.setText("hello");
        try {
            it.preceding((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Expected exception
        }
        TestCase.assertEquals(BreakIterator.DONE, it.preceding(0));
        TestCase.assertEquals(0, it.preceding(1));
        TestCase.assertEquals(4, it.preceding(5));
        try {
            it.preceding(6);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            // Expected exception
        }
    }

    // http://code.google.com/p/android/issues/detail?id=41143
    // This code is inherently unsafe and crazy;
    // we're just trying to provoke native crashes!
    public void testConcurrentBreakIteratorAccess() throws Exception {
        final BreakIterator it = BreakIterator.getCharacterInstance();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; ++i) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 4096; ++i) {
                        it.setText("some example text");
                        for (int index = it.first(); index != (BreakIterator.DONE); index = it.next()) {
                        }
                    }
                }
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}

