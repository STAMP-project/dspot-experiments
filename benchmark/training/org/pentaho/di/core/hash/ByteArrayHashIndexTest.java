/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.hash;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMeta;


public class ByteArrayHashIndexTest {
    @Test
    public void testArraySizeConstructor() {
        ByteArrayHashIndex obj = new ByteArrayHashIndex(new RowMeta(), 1);
        Assert.assertEquals(1, obj.getSize());
        obj = new ByteArrayHashIndex(new RowMeta(), 2);
        Assert.assertEquals(2, obj.getSize());
        obj = new ByteArrayHashIndex(new RowMeta(), 3);
        Assert.assertEquals(4, obj.getSize());
        obj = new ByteArrayHashIndex(new RowMeta(), 12);
        Assert.assertEquals(16, obj.getSize());
        obj = new ByteArrayHashIndex(new RowMeta(), 99);
        Assert.assertEquals(128, obj.getSize());
    }

    @Test
    public void testGetAndPut() throws KettleValueException {
        ByteArrayHashIndex obj = new ByteArrayHashIndex(new RowMeta(), 10);
        Assert.assertNull(obj.get(new byte[]{ 10 }));
        obj.put(new byte[]{ 10 }, new byte[]{ 53, 12 });
        Assert.assertNotNull(obj.get(new byte[]{ 10 }));
        Assert.assertArrayEquals(new byte[]{ 53, 12 }, obj.get(new byte[]{ 10 }));
    }
}

