/**
 * Copyright (C) 2014 RoboVM AB
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
package org.robovm.rt.bro.ptr;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.VM;
import org.robovm.rt.bro.Struct;


/**
 * Tests {@link BooleanPtr}.
 */
public class BooleanPtrTest {
    @Test
    public void testGetSet() {
        BooleanPtr p = new BooleanPtr();
        Assert.assertFalse(p.get());
        Assert.assertEquals(0, VM.getByte(p.getHandle()));
        p.set(true);
        Assert.assertTrue(p.get());
        Assert.assertEquals(1, VM.getByte(p.getHandle()));
        VM.setByte(p.getHandle(), ((byte) (100)));
        Assert.assertTrue(p.get());
        VM.setByte(p.getHandle(), ((byte) (-100)));
        Assert.assertTrue(p.get());
    }

    @Test
    public void testGetSetArray() throws Exception {
        BooleanPtr p = Struct.allocate(BooleanPtr.class, 5);
        try {
            p.get(new boolean[1], 0, 5);
            Assert.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            p.get(new boolean[1], 2, 1);
            Assert.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        boolean[] array = new boolean[5];
        p.get(array);
        Assert.assertTrue(Arrays.equals(new boolean[5], array));
        VM.setByte(p.getHandle(), ((byte) (1)));
        VM.setByte(((p.getHandle()) + 2), ((byte) (2)));
        VM.setByte(((p.getHandle()) + 4), ((byte) (3)));
        p.get(array);
        Assert.assertTrue(Arrays.equals(new boolean[]{ true, false, true, false, true }, array));
        Arrays.fill(array, false);
        p.set(array);
        p.get(array);
        Assert.assertTrue(Arrays.equals(new boolean[5], array));
        array[0] = false;
        array[1] = true;
        array[2] = false;
        array[3] = true;
        array[4] = false;
        p.set(array);
        Assert.assertEquals(0, VM.getByte(p.getHandle()));
        Assert.assertEquals(1, VM.getByte(((p.getHandle()) + 1)));
        Assert.assertEquals(0, VM.getByte(((p.getHandle()) + 2)));
        Assert.assertEquals(1, VM.getByte(((p.getHandle()) + 3)));
        Assert.assertEquals(0, VM.getByte(((p.getHandle()) + 4)));
    }
}

