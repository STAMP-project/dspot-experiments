/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by john on 7/11/16.
 */
public class IdentityCollectionTest {
    private IdentityCollection identityCollection;

    @Test
    public void testWriteLifecycle() {
        Assert.assertEquals((-1), identityCollection.getKey("test"));
        int id = identityCollection.put("test");
        Assert.assertEquals("test", identityCollection.get(id));
        identityCollection.put(id, "test2");
        Assert.assertEquals(id, identityCollection.getKey("test2"));
    }

    @Test
    public void testReadLifecycle() {
        for (int id = 1; id < 3; id++) {
            String value = "test " + id;
            Assert.assertFalse(identityCollection.containsKey(id));
            identityCollection.put(id, value);
            Assert.assertTrue(identityCollection.containsKey(id));
            Assert.assertFalse(identityCollection.isReserved(id));
            Assert.assertEquals(value, identityCollection.get(id));
        }
    }

    @Test
    public void testReservation() {
        int reservation = identityCollection.reserve();
        Assert.assertTrue(identityCollection.containsKey(reservation));
        Assert.assertTrue(identityCollection.isReserved(reservation));
        identityCollection.put(reservation, "test reservation");
        Assert.assertTrue(identityCollection.containsKey(reservation));
        Assert.assertFalse(identityCollection.isReserved(reservation));
    }
}

