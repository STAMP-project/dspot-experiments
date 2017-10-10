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


package io.protostuff;


/**
 * Base for all ser/deser test cases.
 *
 * @author David Yu
 * @created Oct 8, 2010
 */
public abstract class AmplStandardTest extends io.protostuff.AbstractTest {
    /**
     * Serializes the {@code message} into a byte array.
     */
    protected abstract <T> byte[] toByteArray(T message, io.protostuff.Schema<T> schema);

    /**
     * Serializes the {@code message} into a byte array.
     */
    protected <T extends io.protostuff.Message<T>> byte[] toByteArray(T message) {
        return toByteArray(message, message.cachedSchema());
    }

    /**
     * Deserializes from the byte array and data is merged/saved to the message.
     */
    protected abstract <T> void mergeFrom(byte[] data, int offset, int length, T message, io.protostuff.Schema<T> schema) throws java.io.IOException;

    public void testFoo() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = io.protostuff.SerializableObjects.foo;
        io.protostuff.Foo dfoo = new io.protostuff.Foo();
        byte[] output = toByteArray(fooCompare);
        mergeFrom(output, 0, output.length, dfoo, dfoo.cachedSchema());
        io.protostuff.SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    public void testBar() throws java.lang.Exception {
        for (io.protostuff.Bar barCompare : new io.protostuff.Bar[]{ io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar }) {
            io.protostuff.Bar dbar = new io.protostuff.Bar();
            byte[] output = toByteArray(barCompare);
            mergeFrom(output, 0, output.length, dbar, dbar.cachedSchema());
            io.protostuff.SerializableObjects.assertEquals(barCompare, dbar);
        }
    }

    public void testBaz() throws java.lang.Exception {
        for (io.protostuff.Baz bazCompare : new io.protostuff.Baz[]{ io.protostuff.SerializableObjects.baz , io.protostuff.SerializableObjects.negativeBaz }) {
            io.protostuff.Baz dbaz = new io.protostuff.Baz();
            byte[] output = toByteArray(bazCompare);
            mergeFrom(output, 0, output.length, dbaz, dbaz.cachedSchema());
            io.protostuff.SerializableObjects.assertEquals(bazCompare, dbaz);
        }
    }

    // empty foo
    public void testEmptyFoo() throws java.lang.Exception {
        io.protostuff.Foo foo = new io.protostuff.Foo();
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testEmptyFooInner() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(bar);
        foo.setSomeBar(bars);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFoo() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(bar);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<java.lang.Integer> someInt = new java.util.ArrayList<java.lang.Integer>();
        someInt.add(1);
        foo.setSomeInt(someInt);
        foo.setSomeBar(bars);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFooWithString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        foo.setSomeBar(bars);
        java.util.ArrayList<java.lang.String> strings = new java.util.ArrayList<java.lang.String>();
        strings.add("someString");
        foo.setSomeString(strings);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFooWithEmptyString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        foo.setSomeBar(bars);
        java.util.ArrayList<java.lang.String> strings = new java.util.ArrayList<java.lang.String>();
        strings.add("");
        foo.setSomeString(strings);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFooInner() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        foo.setSomeBar(bars);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFooInnerWithString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        baz.setName("asdfsf");
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        foo.setSomeBar(bars);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    public void testPartialEmptyFooInnerWithEmptyString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        baz.setName("");
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        foo.setSomeBar(bars);
        byte[] output = toByteArray(foo);
        io.protostuff.Foo parsedFoo = new io.protostuff.Foo();
        mergeFrom(output, 0, output.length, parsedFoo, parsedFoo.cachedSchema());
    }

    // bar
    public void testEmptyBar() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testEmptyBarInner() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBar() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeInt(1);
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBarWithString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeString("someString");
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBarWithEmptyString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeString("");
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBarInner() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        baz.setId(2);
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBarInnerWithString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        baz.setName("asdfsf");
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }

    public void testPartialEmptyBarInnerWithEmptyString() throws java.lang.Exception {
        io.protostuff.Baz baz = new io.protostuff.Baz();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        baz.setName("");
        bar.setSomeBaz(baz);
        byte[] output = toByteArray(bar);
        io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        mergeFrom(output, 0, output.length, parsedBar, parsedBar.cachedSchema());
    }
}

