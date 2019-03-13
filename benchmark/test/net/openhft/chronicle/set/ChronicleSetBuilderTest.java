/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.set;


import org.junit.Assert;
import org.junit.Test;


public class ChronicleSetBuilderTest {
    @Test
    public void test() {
        try (ChronicleSet<Integer> integers = ChronicleSet.of(Integer.class).entries(10).create()) {
            for (int i = 0; i < 10; i++) {
                integers.add(i);
            }
            Assert.assertTrue(integers.contains(5));
            Assert.assertEquals(10, integers.size());
        }
    }
}

