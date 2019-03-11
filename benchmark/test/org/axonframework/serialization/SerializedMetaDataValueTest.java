/**
 * Copyright (c) 2010-2018. Axon Framework
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
package org.axonframework.serialization;


import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class SerializedMetaDataValueTest {
    @Test
    public void testSerializeMetaData() {
        byte[] stubData = new byte[]{  };
        SerializedMetaData<byte[]> serializedMetaData = new SerializedMetaData(stubData, byte[].class);
        Assert.assertEquals(stubData, serializedMetaData.getData());
        Assert.assertEquals(byte[].class, serializedMetaData.getContentType());
        Assert.assertNull(serializedMetaData.getType().getRevision());
        Assert.assertEquals(MetaData.class.getName(), serializedMetaData.getType().getName());
    }

    @Test
    public void testIsSerializedMetaData() {
        SerializedMetaData<byte[]> serializedMetaData = new SerializedMetaData(new byte[]{  }, byte[].class);
        Assert.assertTrue(SerializedMetaData.isSerializedMetaData(serializedMetaData));
        Assert.assertFalse(SerializedMetaData.isSerializedMetaData(new SimpleSerializedObject("test", String.class, "type", "rev")));
    }
}

