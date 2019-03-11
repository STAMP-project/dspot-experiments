/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff.runtime;


import io.protostuff.AbstractTest;
import io.protostuff.Bar;
import io.protostuff.Baz;
import io.protostuff.ByteString;
import io.protostuff.Foo;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static RuntimeEnv.COLLECTION_SCHEMA_ON_REPEATED_FIELDS;


/**
 * Test that the runtime schema would have the same output as hand-coded/code-generated schema.
 *
 * @author David Yu
 */
public class CompatTest {
    @Test
    public void testCompat() throws IOException {
        CompatTest.compareBar();
        if (!(COLLECTION_SCHEMA_ON_REPEATED_FIELDS)) {
            CompatTest.compareFoo();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMixed() throws Exception {
        if (COLLECTION_SCHEMA_ON_REPEATED_FIELDS)
            return;

        Schema<CompatTest.Mixed> schema = RuntimeSchema.getSchema(CompatTest.Mixed.class);
        Assert.assertTrue(RuntimeSchema.class.isAssignableFrom(schema.getClass()));
        RuntimeSchema<CompatTest.Mixed> mappedSchema = ((RuntimeSchema<CompatTest.Mixed>) (schema));
        Assert.assertTrue(RuntimeMessageField.class.isAssignableFrom(mappedSchema.getFieldByName("rfoo").getClass()));
        Assert.assertTrue(RuntimeMessageField.class.isAssignableFrom(mappedSchema.getFieldByName("rbar").getClass()));
        Assert.assertTrue(RuntimeMessageField.class.isAssignableFrom(mappedSchema.getFieldByName("rbaz").getClass()));
        RuntimeMessageField<CompatTest.Mixed, io.protostuff.Foo> rfoo = ((RuntimeMessageField<CompatTest.Mixed, io.protostuff.Foo>) (mappedSchema.getFieldByName("rfoo")));
        RuntimeMessageField<CompatTest.Mixed, io.protostuff.Bar> rbar = ((RuntimeMessageField<CompatTest.Mixed, io.protostuff.Bar>) (mappedSchema.getFieldByName("rbar")));
        RuntimeMessageField<CompatTest.Mixed, io.protostuff.Baz> rbaz = ((RuntimeMessageField<CompatTest.Mixed, io.protostuff.Baz>) (mappedSchema.getFieldByName("rbaz")));
        Assert.assertTrue(rfoo.getSchema().getClass().isAssignableFrom(CompatTest.getCachedSchema(Foo.class).getClass()));
        Assert.assertTrue(rbar.getSchema().getClass().isAssignableFrom(CompatTest.getCachedSchema(Bar.class).getClass()));
        Assert.assertTrue(rbaz.getSchema().getClass().isAssignableFrom(CompatTest.getCachedSchema(Baz.class).getClass()));
    }

    public static class Mixed {
        int id;

        Foo fo;

        Bar br;

        Baz bz;

        Foo foo;

        Bar bar;

        Baz baz;

        List<io.protostuff.Foo> rfoo;

        Set<io.protostuff.Bar> rbar;

        Collection<io.protostuff.Baz> rbaz;
    }

    @Test
    public void testByteArrayCompat() {
        CompatTest.PojoWithByteArray pwba = new CompatTest.PojoWithByteArray();
        CompatTest.PojoWithByteString pwbs = new CompatTest.PojoWithByteString();
        CompatTest.fill(pwba);
        CompatTest.fill(pwbs);
        byte[] b1 = ProtostuffIOUtil.toByteArray(pwba, RuntimeSchema.getSchema(CompatTest.PojoWithByteArray.class), AbstractTest.buf());
        byte[] b2 = ProtostuffIOUtil.toByteArray(pwbs, RuntimeSchema.getSchema(CompatTest.PojoWithByteString.class), AbstractTest.buf());
        Assert.assertTrue(Arrays.equals(b1, b2));
    }

    public enum Direction {

        NORTH,
        SOUTH,
        EAST,
        WEST;}

    public static class PojoWithByteArray {
        byte[] bytes;

        List<byte[]> bytesList;

        Map<Integer, byte[]> scalarKeyMap;

        Map<CompatTest.Direction, byte[]> enumKeyMap;

        Map<Baz, byte[]> pojoKeyMap;

        Map<byte[], Integer> scalarValueMap;

        Map<byte[], CompatTest.Direction> enumValueMap;

        Map<byte[], Baz> pojoValueMap;

        Map<byte[], byte[]> bytesMap;
    }

    public static class PojoWithByteString {
        ByteString bytes;

        List<ByteString> bytesList;

        Map<Integer, ByteString> scalarKeyMap;

        Map<CompatTest.Direction, ByteString> enumKeyMap;

        Map<Baz, ByteString> pojoKeyMap;

        Map<ByteString, Integer> scalarValueMap;

        Map<ByteString, CompatTest.Direction> enumValueMap;

        Map<ByteString, Baz> pojoValueMap;

        Map<ByteString, ByteString> bytesMap;
    }
}

