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
package net.openhft.chronicle.map;


import net.openhft.chronicle.bytes.BytesMarshallable;
import org.junit.Assert;
import org.junit.Test;


public class BytesMarshallableValueTest {
    @Test
    public void bytesMarshallableValueTest() {
        try (ChronicleMap<Integer, BytesMarshallableValueTest.Value> map = ChronicleMap.of(Integer.class, BytesMarshallableValueTest.Value.class).averageValue(new BytesMarshallableValueTest.Value(1, "foo")).entries(10).create()) {
            map.put(1, new BytesMarshallableValueTest.Value(1, "bar"));
            Assert.assertEquals("bar", map.replace(1, new BytesMarshallableValueTest.Value(2, "baz")).foo);
            map.remove(1);
        }
    }

    public static class Value implements BytesMarshallable {
        int x;

        String foo;

        public Value(int x, String foo) {
            this.x = x;
            this.foo = foo;
        }
    }
}

