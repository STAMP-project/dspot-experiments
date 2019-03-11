/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import org.junit.Assert;
import org.junit.Test;


public class CursorTest {
    private static final byte[] bytes1 = new byte[]{ 1, 2, 3, '%', '<', '+' };

    private static final byte[] bytes2 = new byte[]{ 10, 20, 30 };

    private Cursor cursor1;

    private Cursor cursor2;

    @Test
    public void testToFromUrlSafe() throws Exception {
        String urlSafe = cursor1.toUrlSafe();
        Assert.assertEquals(cursor1, Cursor.fromUrlSafe(urlSafe));
    }

    @Test
    public void testCopyFrom() throws Exception {
        Cursor cursor = Cursor.copyFrom(CursorTest.bytes2);
        Assert.assertEquals(cursor2, cursor);
        Assert.assertNotEquals(cursor1, cursor);
    }
}

