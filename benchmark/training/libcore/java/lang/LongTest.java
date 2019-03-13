/**
 * Copyright (C) 2011 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public class LongTest extends TestCase {
    public void test_compare() throws Exception {
        final long min = Long.MIN_VALUE;
        final long zero = 0L;
        final long max = Long.MAX_VALUE;
        TestCase.assertTrue(((Long.compare(max, max)) == 0));
        TestCase.assertTrue(((Long.compare(min, min)) == 0));
        TestCase.assertTrue(((Long.compare(zero, zero)) == 0));
        TestCase.assertTrue(((Long.compare(max, zero)) > 0));
        TestCase.assertTrue(((Long.compare(max, min)) > 0));
        TestCase.assertTrue(((Long.compare(zero, max)) < 0));
        TestCase.assertTrue(((Long.compare(zero, min)) > 0));
        TestCase.assertTrue(((Long.compare(min, zero)) < 0));
        TestCase.assertTrue(((Long.compare(min, max)) < 0));
    }

    public void test_signum() throws Exception {
        TestCase.assertEquals(0, Long.signum(0));
        TestCase.assertEquals(1, Long.signum(1));
        TestCase.assertEquals((-1), Long.signum((-1)));
        TestCase.assertEquals(1, Long.signum(Long.MAX_VALUE));
        TestCase.assertEquals((-1), Long.signum(Long.MIN_VALUE));
    }
}

