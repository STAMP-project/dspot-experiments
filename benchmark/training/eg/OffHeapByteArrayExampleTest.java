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
package eg;


import net.openhft.chronicle.core.values.ByteValue;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.values.Array;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Test;


public class OffHeapByteArrayExampleTest {
    public static final char EXPECTED = 'b';

    private static ChronicleMap<LongValue, OffHeapByteArrayExampleTest.ByteArray> chm;

    @Test
    public void test() {
        // this objects will be reused
        ByteValue byteValue = Values.newHeapInstance(ByteValue.class);
        OffHeapByteArrayExampleTest.ByteArray value = Values.newHeapInstance(OffHeapByteArrayExampleTest.ByteArray.class);
        LongValue key = Values.newHeapInstance(LongValue.class);
        key.setValue(1);
        // this is kind of like byteValue[1] = 'b'
        byteValue.setValue(((byte) (OffHeapByteArrayExampleTest.EXPECTED)));
        value.setByteValueAt(1, byteValue);
        OffHeapByteArrayExampleTest.chm.put(key, value);
        // clear the value to prove it works
        byteValue.setValue(((byte) (0)));
        value.setByteValueAt(1, byteValue);
        OffHeapByteArrayExampleTest.chm.getUsing(key, value);
        Assert.assertEquals(0, value.getByteValueAt(2).getValue());
        Assert.assertEquals(0, value.getByteValueAt(3).getValue());
        byte actual = value.getByteValueAt(1).getValue();
        Assert.assertEquals(OffHeapByteArrayExampleTest.EXPECTED, actual);
    }

    interface ByteArray {
        @Array(length = 7)
        void setByteValueAt(int index, ByteValue value);

        ByteValue getByteValueAt(int index);
    }
}

