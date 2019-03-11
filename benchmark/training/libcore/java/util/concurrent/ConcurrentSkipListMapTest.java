/**
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */
package libcore.java.util.concurrent;


import java.util.concurrent.ConcurrentSkipListMap;
import junit.framework.TestCase;
import libcore.java.util.MapDefaultMethodTester;


public class ConcurrentSkipListMapTest extends TestCase {
    public void test_getOrDefault() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        /* getAcceptsAnyObject */
        MapDefaultMethodTester.test_getOrDefault(new ConcurrentSkipListMap<>(), false, false, false);
    }

    public void test_forEach() {
        MapDefaultMethodTester.test_forEach(new ConcurrentSkipListMap<>());
    }

    public void test_putIfAbsent() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        MapDefaultMethodTester.test_putIfAbsent(new ConcurrentSkipListMap<>(), false, false);
    }

    public void test_remove() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        MapDefaultMethodTester.test_remove(new ConcurrentSkipListMap<>(), false, false);
    }

    public void test_replace$K$V$V() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        MapDefaultMethodTester.test_replace$K$V$V(new ConcurrentSkipListMap<>(), false, false);
    }

    public void test_replace$K$V() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        MapDefaultMethodTester.test_replace$K$V(new ConcurrentSkipListMap<>(), false, false);
    }

    public void test_computeIfAbsent() {
        /* doesNotAcceptNullKey */
        /* doesNotAcceptNullValue */
        MapDefaultMethodTester.test_computeIfAbsent(new ConcurrentSkipListMap<>(), false, false);
    }

    public void test_computeIfPresent() {
        /* doesNotAcceptNullKey */
        MapDefaultMethodTester.test_computeIfPresent(new ConcurrentSkipListMap<>(), false);
    }

    public void test_compute() {
        /* doesNotAcceptNullKey */
        MapDefaultMethodTester.test_compute(new ConcurrentSkipListMap<>(), false);
    }

    public void test_merge() {
        /* doesNotAcceptNullKey */
        MapDefaultMethodTester.test_merge(new ConcurrentSkipListMap<>(), false);
    }
}

