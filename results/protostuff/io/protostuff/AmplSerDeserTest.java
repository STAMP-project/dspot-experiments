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


package io.protostuff;


/**
 * Serialization and deserialization test cases.
 *
 * @author David Yu
 * @created Nov 10, 2009
 */
public abstract class AmplSerDeserTest extends io.protostuff.StandardTest {
    /**
     * Serializes the {@code message} (delimited) into an {@link OutputStream} via {@link DeferredOutput}.
     */
    protected <T extends io.protostuff.Message<T>> void writeDelimitedTo(java.io.OutputStream out, T message) throws java.io.IOException {
        writeDelimitedTo(out, message, message.cachedSchema());
    }

    /**
     * Serializes the {@code message} (delimited) into an {@link OutputStream} via {@link DeferredOutput} using the
     * given schema.
     */
    protected abstract <T> void writeDelimitedTo(java.io.OutputStream out, T message, io.protostuff.Schema<T> schema) throws java.io.IOException;

    /**
     * Deserializes from the byte array and data is merged/saved to the message.
     */
    protected abstract <T> void mergeDelimitedFrom(java.io.InputStream in, T message, io.protostuff.Schema<T> schema) throws java.io.IOException;

    public void testFooSkipMessage() throws java.lang.Exception {
        final io.protostuff.CustomSchema<io.protostuff.Foo> fooSchema = new io.protostuff.CustomSchema<io.protostuff.Foo>(io.protostuff.SerializableObjects.foo.cachedSchema()) {
            @java.lang.Override
            public void writeTo(io.protostuff.Output output, io.protostuff.Foo message) throws java.io.IOException {
                // 10 is an unknown field
                output.writeObject(10, io.protostuff.SerializableObjects.baz, io.protostuff.Baz.getSchema(), false);
                super.writeTo(output, message);
            }
        };
        io.protostuff.Foo fooCompare = io.protostuff.SerializableObjects.foo;
        io.protostuff.Foo dfoo = new io.protostuff.Foo();
        byte[] output = toByteArray(fooCompare, fooSchema);
        mergeFrom(output, 0, output.length, dfoo, dfoo.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    public void testBarSkipMessage() throws java.lang.Exception {
        final io.protostuff.CustomSchema<io.protostuff.Bar> barSchema = new io.protostuff.CustomSchema<io.protostuff.Bar>(io.protostuff.SerializableObjects.bar.cachedSchema()) {
            @java.lang.Override
            public void writeTo(io.protostuff.Output output, io.protostuff.Bar message) throws java.io.IOException {
                // 10 is an unknown field
                output.writeObject(10, io.protostuff.SerializableObjects.baz, io.protostuff.Baz.getSchema(), false);
                super.writeTo(output, message);
            }
        };
        for (io.protostuff.Bar barCompare : new io.protostuff.Bar[]{ io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar }) {
            io.protostuff.Bar dbar = new io.protostuff.Bar();
            byte[] output = toByteArray(barCompare, barSchema);
            mergeFrom(output, 0, output.length, dbar, barSchema);
            io.protostuff.SerializableObjects.assertEquals(barCompare, dbar);
        }
    }

    /**
     * Foo shares field numbers (and type) with Bar except that foo's fields are all repeated (w/c is alright). Bar also
     * shares the same field and type (1&2) with Baz.
     */
    public void testShareFieldNumberAndTypeAndSkipMessage() throws java.lang.Exception {
        final io.protostuff.CustomSchema<io.protostuff.Bar> barSchema = new io.protostuff.CustomSchema<io.protostuff.Bar>(io.protostuff.SerializableObjects.bar.cachedSchema()) {
            @java.lang.Override
            public void writeTo(io.protostuff.Output output, io.protostuff.Bar message) throws java.io.IOException {
                output.writeObject(10, io.protostuff.SerializableObjects.baz, io.protostuff.Baz.getSchema(), false);
                super.writeTo(output, message);
            }
        };
        final io.protostuff.Baz baz = new io.protostuff.Baz();
        baz.setId(1);
        baz.setName("baz");
        final io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        bar.setSomeInt(2);
        bar.setSomeString("bar");
        bar.setSomeDouble(100.001);
        bar.setSomeFloat(10.01F);
        byte[] coded = toByteArray(bar, barSchema);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        // we expect this to succeed, skipping the baz field.
        mergeFrom(coded, 0, coded.length, foo, foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((bar.getSomeInt()) == (foo.getSomeInt().get(0))));
        junit.framework.TestCase.assertEquals(bar.getSomeString(), foo.getSomeString().get(0));
        junit.framework.TestCase.assertTrue(((bar.getSomeDouble()) == (foo.getSomeDouble().get(0))));
        junit.framework.TestCase.assertTrue(((bar.getSomeFloat()) == (foo.getSomeFloat().get(0))));
    }

    public void testFooDelimited() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = io.protostuff.SerializableObjects.foo;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, fooCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        mergeDelimitedFrom(in, foo, foo.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(foo, fooCompare);
    }

    public void testEmptyFooDelimited() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = new io.protostuff.Foo();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, fooCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        mergeDelimitedFrom(in, foo, foo.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(foo, fooCompare);
    }

    public void testEmptyInnerFooDelimited() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        fooCompare.setSomeBar(bars);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, fooCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        mergeDelimitedFrom(in, foo, foo.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(foo, fooCompare);
    }

    public void testBarDelimited() throws java.lang.Exception {
        io.protostuff.Bar barCompare = io.protostuff.SerializableObjects.bar;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, barCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Bar bar = new io.protostuff.Bar();
        mergeDelimitedFrom(in, bar, bar.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(bar, barCompare);
    }

    public void testEmptyBarDelimited() throws java.lang.Exception {
        io.protostuff.Bar barCompare = new io.protostuff.Bar();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, barCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Bar bar = new io.protostuff.Bar();
        mergeDelimitedFrom(in, bar, bar.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(bar, barCompare);
    }

    public void testEmptyInnerBarDelimited() throws java.lang.Exception {
        io.protostuff.Bar barCompare = new io.protostuff.Bar();
        barCompare.setSomeBaz(new io.protostuff.Baz());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, barCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Bar bar = new io.protostuff.Bar();
        mergeDelimitedFrom(in, bar, bar.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(bar, barCompare);
    }

    public void testBazDelimited() throws java.lang.Exception {
        io.protostuff.Baz bazCompare = io.protostuff.SerializableObjects.baz;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, bazCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Baz baz = new io.protostuff.Baz();
        mergeDelimitedFrom(in, baz, baz.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(baz, bazCompare);
    }

    public void testEmptyBazDelimited() throws java.lang.Exception {
        io.protostuff.Baz bazCompare = new io.protostuff.Baz();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeDelimitedTo(out, bazCompare);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Baz baz = new io.protostuff.Baz();
        mergeDelimitedFrom(in, baz, baz.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(baz, bazCompare);
    }

    public void testJavaSerializableGraphIOUtil() throws java.lang.Exception {
        io.protostuff.ClubFounder founder = new io.protostuff.ClubFounder();
        java.lang.StringBuilder b = new java.lang.StringBuilder();
        for (int i = 0; i < 25; i++)
            b.append("1234567890");
        
        // 250 length string
        // ~253 length message
        founder.setName(b.toString());
        io.protostuff.WrapsClubFounder wrapper = new io.protostuff.WrapsClubFounder(founder);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(wrapper);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.WrapsClubFounder parsedWrapper = ((io.protostuff.WrapsClubFounder) (in.readObject()));
        junit.framework.TestCase.assertEquals(founder.getName(), parsedWrapper.getClubFounder().getName());
    }

    /**
     * HasHasBar wraps an object without a schema. That object will have to be serialized via the default java
     * serialization and it will be delimited.
     * <p>
     * HasBar wraps a message {@link Bar}.
     */
    public void testJavaSerializable() throws java.lang.Exception {
        io.protostuff.HasHasBar hhbCompare = new io.protostuff.HasHasBar("hhb", new io.protostuff.HasBar(12345, "hb", io.protostuff.SerializableObjects.bar));
        io.protostuff.HasHasBar dhhb = new io.protostuff.HasHasBar();
        byte[] output = toByteArray(hhbCompare);
        mergeFrom(output, 0, output.length, dhhb, dhhb.cachedSchema());
        io.protostuff.AmplSerDeserTest.assertEquals(hhbCompare, dhhb);
    }

    public void testJavaSerializableEmptyBar() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(bar);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.Bar parsedBar = ((io.protostuff.Bar) (in.readObject()));
        io.protostuff.SerializableObjects.assertEquals(parsedBar, bar);
    }

    public void testJavaSerializableEmptyBarInner() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(bar);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.Bar parsedBar = ((io.protostuff.Bar) (in.readObject()));
        io.protostuff.SerializableObjects.assertEquals(parsedBar, bar);
    }

    public void testJavaSerializableEmptyFoo() throws java.lang.Exception {
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(foo);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.Foo parsedFoo = ((io.protostuff.Foo) (in.readObject()));
        io.protostuff.SerializableObjects.assertEquals(parsedFoo, foo);
    }

    public void testJavaSerializableEmptyFoo2() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bars.add(bar);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(foo);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.Foo parsedFoo = ((io.protostuff.Foo) (in.readObject()));
        io.protostuff.SerializableObjects.assertEquals(parsedFoo, foo);
    }

    public void testJavaSerializableEmptyFooInner() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        bars.add(bar);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(out);
        oout.writeObject(foo);
        byte[] coded = out.toByteArray();
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(coded));
        io.protostuff.Foo parsedFoo = ((io.protostuff.Foo) (in.readObject()));
        io.protostuff.SerializableObjects.assertEquals(parsedFoo, foo);
    }

    public static java.lang.String repeatChar(char c, int size) {
        java.lang.StringBuilder sb = new java.lang.StringBuilder(size);
        while ((size--) > 0)
            sb.append(c);
        
        return sb.toString();
    }

    static void assertEquals(io.protostuff.HasHasBar h1, io.protostuff.HasHasBar h2) {
        // true if both are null
        if (h1 == h2)
            return ;
        
        junit.framework.TestCase.assertEquals(h1.getName(), h2.getName());
        io.protostuff.AmplSerDeserTest.assertEquals(h1.getHasBar(), h2.getHasBar());
    }

    static void assertEquals(io.protostuff.HasBar h1, io.protostuff.HasBar h2) {
        // true if both are null
        if (h1 == h2)
            return ;
        
        junit.framework.TestCase.assertTrue(((h1.getId()) == (h2.getId())));
        junit.framework.TestCase.assertEquals(h1.getName(), h2.getName());
        io.protostuff.SerializableObjects.assertEquals(h1.getBar(), h2.getBar());
    }
}

