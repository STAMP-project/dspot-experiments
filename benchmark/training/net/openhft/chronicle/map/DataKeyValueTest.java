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


import net.openhft.chronicle.core.values.IntValue;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Test;


public class DataKeyValueTest {
    @Test
    public void dataKeyValueTest() {
        ChronicleMap<IntValue, LongValue> map = ChronicleMapBuilder.of(IntValue.class, LongValue.class).entries(1000).create();
        IntValue heapKey = Values.newHeapInstance(IntValue.class);
        LongValue heapValue = Values.newHeapInstance(LongValue.class);
        LongValue directValue = Values.newNativeReference(LongValue.class);
        heapKey.setValue(1);
        heapValue.setValue(1);
        map.put(heapKey, heapValue);
        Assert.assertEquals(1, map.get(heapKey).getValue());
        Assert.assertEquals(1, map.getUsing(heapKey, heapValue).getValue());
        heapKey.setValue(1);
        map.getUsing(heapKey, directValue).addValue(1);
        Assert.assertEquals(2, map.getUsing(heapKey, heapValue).getValue());
        heapKey.setValue(2);
        heapValue.setValue(3);
        map.put(heapKey, heapValue);
        Assert.assertEquals(3, map.get(heapKey).getValue());
    }
}

