/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;


import com.querydsl.sql.types.BlobType;
import com.querydsl.sql.types.ByteType;
import com.querydsl.sql.types.CharacterType;
import com.querydsl.sql.types.DoubleType;
import com.querydsl.sql.types.FloatType;
import com.querydsl.sql.types.IntegerType;
import com.querydsl.sql.types.LongType;
import com.querydsl.sql.types.ObjectType;
import com.querydsl.sql.types.ShortType;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class JavaTypeMappingTest {
    private JavaTypeMapping typeMapping = new JavaTypeMapping();

    @Test
    public void getType_with_subtypes() {
        typeMapping.register(new InputStreamType());
        Assert.assertNotNull(typeMapping.getType(InputStream.class));
        Assert.assertNotNull(typeMapping.getType(FileInputStream.class));
    }

    @Test
    public void getType_with_interfaces() {
        Assert.assertEquals(BlobType.class, typeMapping.getType(DummyBlob.class).getClass());
    }

    @Test
    public void getType_for_object() {
        Assert.assertEquals(ObjectType.class, typeMapping.getType(Object.class).getClass());
    }

    @Test
    public void getType_for_primitive() {
        Assert.assertEquals(ByteType.class, typeMapping.getType(byte.class).getClass());
        Assert.assertEquals(ShortType.class, typeMapping.getType(short.class).getClass());
        Assert.assertEquals(IntegerType.class, typeMapping.getType(int.class).getClass());
        Assert.assertEquals(LongType.class, typeMapping.getType(long.class).getClass());
        Assert.assertEquals(FloatType.class, typeMapping.getType(float.class).getClass());
        Assert.assertEquals(DoubleType.class, typeMapping.getType(double.class).getClass());
        Assert.assertEquals(BooleanType.class, typeMapping.getType(boolean.class).getClass());
        Assert.assertEquals(CharacterType.class, typeMapping.getType(char.class).getClass());
    }
}

