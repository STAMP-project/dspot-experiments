/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
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
package io.objectbox;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class NonArgConstructorTest extends AbstractObjectBoxTest {
    private Box<TestEntity> box;

    @Test
    public void testPutAndGet() {
        TestEntity entity = new TestEntity();
        entity.setSimpleInt(1977);
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(((byte) (42)));
        byte[] bytes = new byte[]{ 77, 78 };
        entity.setSimpleByteArray(bytes);
        entity.setSimpleDouble(3.141);
        entity.setSimpleFloat(3.14F);
        entity.setSimpleLong(789437444354L);
        entity.setSimpleShort(((short) (233)));
        entity.setSimpleString("foo");
        long key = box.put(entity);
        TestEntity entityRead = box.get(key);
        Assert.assertNotNull(entityRead);
        Assert.assertTrue(entityRead.noArgsConstructorCalled);
        Assert.assertEquals(1977, entityRead.getSimpleInt());
        Assert.assertTrue(entityRead.getSimpleBoolean());
        Assert.assertEquals(42, entityRead.getSimpleByte());
        Assert.assertEquals(233, entityRead.getSimpleShort());
        Assert.assertEquals(789437444354L, entityRead.getSimpleLong());
        Assert.assertEquals(3.14F, entityRead.getSimpleFloat(), 1.0E-6F);
        Assert.assertEquals(3.141F, entityRead.getSimpleDouble(), 1.0E-6);
        Assert.assertTrue(Arrays.equals(bytes, entityRead.getSimpleByteArray()));
        Assert.assertEquals("foo", entityRead.getSimpleString());
    }
}

