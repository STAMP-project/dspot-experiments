/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.nio;


import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class SelectedSelectionKeySetTest {
    @Mock
    private SelectionKey mockKey;

    @Mock
    private SelectionKey mockKey2;

    @Mock
    private SelectionKey mockKey3;

    @Test
    public void addElements() {
        SelectedSelectionKeySet set = new SelectedSelectionKeySet();
        final int expectedSize = 1000000;
        for (int i = 0; i < expectedSize; ++i) {
            Assert.assertTrue(set.add(mockKey));
        }
        Assert.assertEquals(expectedSize, set.size());
        Assert.assertFalse(set.isEmpty());
    }

    @Test
    public void resetSet() {
        SelectedSelectionKeySet set = new SelectedSelectionKeySet();
        Assert.assertTrue(set.add(mockKey));
        Assert.assertTrue(set.add(mockKey2));
        set.reset(1);
        Assert.assertSame(mockKey, set.keys[0]);
        Assert.assertNull(set.keys[1]);
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void iterator() {
        SelectedSelectionKeySet set = new SelectedSelectionKeySet();
        Assert.assertTrue(set.add(mockKey));
        Assert.assertTrue(set.add(mockKey2));
        Iterator<SelectionKey> keys = set.iterator();
        Assert.assertTrue(keys.hasNext());
        Assert.assertSame(mockKey, keys.next());
        Assert.assertTrue(keys.hasNext());
        Assert.assertSame(mockKey2, keys.next());
        Assert.assertFalse(keys.hasNext());
        try {
            keys.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
            // expected
        }
        try {
            keys.remove();
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }

    @Test
    public void contains() {
        SelectedSelectionKeySet set = new SelectedSelectionKeySet();
        Assert.assertTrue(set.add(mockKey));
        Assert.assertTrue(set.add(mockKey2));
        Assert.assertFalse(set.contains(mockKey));
        Assert.assertFalse(set.contains(mockKey2));
        Assert.assertFalse(set.contains(mockKey3));
    }

    @Test
    public void remove() {
        SelectedSelectionKeySet set = new SelectedSelectionKeySet();
        Assert.assertTrue(set.add(mockKey));
        Assert.assertFalse(set.remove(mockKey));
        Assert.assertFalse(set.remove(mockKey2));
    }
}

