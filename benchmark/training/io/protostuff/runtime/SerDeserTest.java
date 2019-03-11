/**
 * ========================================================================
 */
/**
 * Copyright 2007-2009 David Yu dyuproject@gmail.com
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


import io.protostuff.ComputedSizeOutput;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import junit.framework.TestCase;

import static RuntimeEnv.COLLECTION_SCHEMA_ON_REPEATED_FIELDS;


/**
 * Serialization and deserialization test cases.
 *
 * @author David Yu
 * @unknown Nov 18, 2009
 */
public class SerDeserTest extends TestCase {
    static final int BUF_SIZE = 256;

    public void testFoo() throws Exception {
        Schema<Foo> schema = RuntimeSchema.getSchema(Foo.class);
        Foo fooCompare = SerializableObjects.foo;
        Foo dfoo = new Foo();
        byte[] deferred = toByteArray(fooCompare, schema);
        // ComputedSizeOutput is format compatible with protobuf
        // E.g collections are not serialized ... only its members/elements are.
        if (!(COLLECTION_SCHEMA_ON_REPEATED_FIELDS))
            TestCase.assertTrue(((deferred.length) == (ComputedSizeOutput.getSize(fooCompare, schema))));

        ProtostuffIOUtil.mergeFrom(deferred, dfoo, schema);
        SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    public void testBar() throws Exception {
        Schema<Bar> schema = RuntimeSchema.getSchema(Bar.class);
        for (Bar barCompare : new Bar[]{ SerializableObjects.bar, SerializableObjects.negativeBar }) {
            Bar dbar = new Bar();
            int expectedSize = ComputedSizeOutput.getSize(barCompare, schema);
            byte[] deferred = toByteArray(barCompare, schema);
            TestCase.assertTrue(((deferred.length) == expectedSize));
            ProtostuffIOUtil.mergeFrom(deferred, dbar, schema);
            SerializableObjects.assertEquals(barCompare, dbar);
            // System.err.println(dbar.getSomeInt());
            // System.err.println(dbar.getSomeLong());
            // System.err.println(dbar.getSomeFloat());
            // System.err.println(dbar.getSomeDouble());
            // System.err.println(dbar.getSomeBytes());
            // System.err.println(dbar.getSomeString());
            // System.err.println(dbar.getSomeEnum());
            // System.err.println(dbar.getSomeBoolean());
        }
    }

    public void testBaz() throws Exception {
        Schema<Baz> schema = RuntimeSchema.getSchema(Baz.class);
        for (Baz bazCompare : new Baz[]{ SerializableObjects.baz, SerializableObjects.negativeBaz }) {
            Baz dbaz = new Baz();
            int expectedSize = ComputedSizeOutput.getSize(bazCompare, schema);
            byte[] deferred = toByteArray(bazCompare, schema);
            TestCase.assertTrue(((deferred.length) == expectedSize));
            ProtostuffIOUtil.mergeFrom(deferred, dbaz, schema);
            SerializableObjects.assertEquals(bazCompare, dbaz);
            // System.err.println(dbaz.getId());
            // System.err.println(dbaz.getName());
            // System.err.println(dbaz.getTimestamp());
        }
    }

    /**
     * HasHasBar wraps an object without a schema. That object will have to be serialized via the default java
     * serialization and it will be delimited.
     * <p>
     * HasBar wraps a message {@link Bar}.
     */
    public void testJavaSerializable() throws Exception {
        Schema<HasHasBar> schema = RuntimeSchema.getSchema(HasHasBar.class);
        HasHasBar hhbCompare = new HasHasBar("hhb", new HasBar(12345, "hb", SerializableObjects.bar));
        HasHasBar dhhb = new HasHasBar();
        int expectedSize = ComputedSizeOutput.getSize(hhbCompare, schema);
        byte[] deferred = toByteArray(hhbCompare, schema);
        TestCase.assertTrue(((deferred.length) == expectedSize));
        ProtostuffIOUtil.mergeFrom(deferred, dhhb, schema);
        SerDeserTest.assertEquals(hhbCompare, dhhb);
    }

    public void testPojoWithArrayAndSet() throws Exception {
        PojoWithArrayAndSet pojoCompare = SerDeserTest.filledPojoWithArrayAndSet();
        Schema<PojoWithArrayAndSet> schema = RuntimeSchema.getSchema(PojoWithArrayAndSet.class);
        PojoWithArrayAndSet dpojo = new PojoWithArrayAndSet();
        int expectedSize = ComputedSizeOutput.getSize(pojoCompare, schema, true);
        byte[] deferred = toByteArray(pojoCompare, schema);
        TestCase.assertTrue(((deferred.length) == expectedSize));
        ProtostuffIOUtil.mergeFrom(deferred, dpojo, schema);
        TestCase.assertEquals(pojoCompare, dpojo);
        // System.err.println(dpojo.getSomeEnumAsSet());
        // System.err.println(dpojo.getSomeFloatAsSet());
    }
}

