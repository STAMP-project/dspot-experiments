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


import Foo.EnumSample.TYPE1;
import JsonIOUtil.DEFAULT_JSON_FACTORY;
import SerializableObjects.bar;
import SerializableObjects.foo;
import SerializableObjects.negativeBar;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Testing for json ser/deser against messages.
 *
 * @author David Yu
 * @unknown Nov 20, 2009
 */
public class JsonCoreSerDeserTest extends TestCase {
    public void testFoo() throws Exception {
        Foo fooCompare = SerializableObjects.foo;
        Foo dfoo = new Foo();
        byte[] data = JsonIOUtil.toByteArray(fooCompare, fooCompare.cachedSchema(), false);
        JsonIOUtil.mergeFrom(data, dfoo, dfoo.cachedSchema(), false);
        SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    public void testBar() throws Exception {
        for (Bar barCompare : new Bar[]{ SerializableObjects.bar, SerializableObjects.negativeBar }) {
            Bar dbar = new Bar();
            byte[] data = JsonIOUtil.toByteArray(barCompare, barCompare.cachedSchema(), false);
            JsonIOUtil.mergeFrom(data, dbar, dbar.cachedSchema(), false);
            SerializableObjects.assertEquals(barCompare, dbar);
        }
    }

    public void testBaz() throws Exception {
        for (Baz bazCompare : new Baz[]{ SerializableObjects.baz, SerializableObjects.negativeBaz }) {
            Baz dbaz = new Baz();
            byte[] data = JsonIOUtil.toByteArray(bazCompare, bazCompare.cachedSchema(), false);
            JsonIOUtil.mergeFrom(data, dbaz, dbaz.cachedSchema(), false);
            SerializableObjects.assertEquals(bazCompare, dbaz);
        }
    }

    public void testUnknownScalarFields() throws Exception {
        String[] regularMessages = new String[]{ "{\"int\":1,\"string\":\"string\",\"double\":555.444,\"id\":1}", "{\"int\":1,\"string\":\"string\",\"id\":2,\"double\":555.444}", "{\"id\":3,\"int\":1,\"string\":\"string\",\"double\":555.444}" };
        for (int i = 0; i < (regularMessages.length); i++) {
            Baz b = new Baz();
            JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(regularMessages[i]), b, b.cachedSchema(), false);
            TestCase.assertTrue(((i + 1) == (b.getId())));
        }
        String[] numericMessages = new String[]{ "{\"4\":1,\"5\":\"string\",\"6\":555.444,\"1\":1}", "{\"4\":1,\"5\":\"string\",\"1\":2,\"6\":555.444}", "{\"1\":3,\"4\":1,\"5\":\"string\",\"6\":555.444}" };
        for (int i = 0; i < (numericMessages.length); i++) {
            Baz b = new Baz();
            JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(numericMessages[i]), b, b.cachedSchema(), true);
            TestCase.assertTrue(((i + 1) == (b.getId())));
        }
    }

    public void testUnknownScalarFieldsWithArray() throws Exception {
        String[] regularMessages = new String[]{ "{\"int\":[1],\"string\":\"string\",\"double\":[555.444],\"id\":1}", "{\"int\":1,\"string\":[\"string\"],\"id\":2,\"double\":[555.444]}", "{\"id\":3,\"int\":[1],\"string\":[\"string\"],\"double\":555.444}" };
        for (int i = 0; i < (regularMessages.length); i++) {
            Baz b = new Baz();
            JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(regularMessages[i]), b, b.cachedSchema(), false);
            TestCase.assertTrue(((i + 1) == (b.getId())));
        }
        String[] numericMessages = new String[]{ "{\"4\":[1],\"5\":\"string\",\"6\":[555.444],\"1\":1}", "{\"4\":1,\"5\":[\"string\"],\"1\":2,\"6\":[555.444]}", "{\"1\":3,\"4\":[1],\"5\":[\"string\"],\"6\":555.444}" };
        for (int i = 0; i < (numericMessages.length); i++) {
            Baz b = new Baz();
            JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(numericMessages[i]), b, b.cachedSchema(), true);
            TestCase.assertTrue(((i + 1) == (b.getId())));
        }
    }

    public void testListIO() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        bars.add(bar);
        bars.add(negativeBar);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonIOUtil.writeListTo(out, bars, bar.cachedSchema(), false);
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = JsonIOUtil.parseListFrom(in, bar.cachedSchema(), false);
        TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (Bar b : parsedBars)
            SerializableObjects.assertEquals(bars.get((i++)), b);

    }

    public void testListEmpty() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonIOUtil.writeListTo(out, bars, bar.cachedSchema(), false);
        byte[] data = out.toByteArray();
        TestCase.assertEquals(new String(data, "UTF-8"), "[]");
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = JsonIOUtil.parseListFrom(in, bar.cachedSchema(), false);
        TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (Bar b : parsedBars)
            SerializableObjects.assertEquals(bars.get((i++)), b);

    }

    public void testListIOWithArrays() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        foos.add(foo);
        foos.add(foo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonIOUtil.writeListTo(out, foos, foo.cachedSchema(), false);
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsed = JsonIOUtil.parseListFrom(in, foo.cachedSchema(), false);
        TestCase.assertTrue(((parsed.size()) == (foos.size())));
        int i = 0;
        for (Foo f : parsed)
            SerializableObjects.assertEquals(foos.get((i++)), f);

    }

    public void testEmptyMessage() throws Exception {
        Bar bar = new Bar();
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testEmptyMessageInner() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        // method name is setSomeBaz, should have been someBaz!
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBaz\":{}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessage() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeInt(1);
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someInt\":1,\"someBaz\":{}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessageWithString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeString("someString");
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someString\":\"someString\",\"someBaz\":{}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessageWithEmptyString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeString("");
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someString\":\"\",\"someBaz\":{}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessageInner() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setId(2);
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBaz\":{\"id\":2}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessageInnerWithString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setName("asdfsf");
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBaz\":{\"name\":\"asdfsf\"}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testPartialEmptyMessageInnerWithEmptyString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setName("");
        bar.setSomeBaz(baz);
        byte[] data = JsonIOUtil.toByteArray(bar, bar.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBaz\":{\"name\":\"\"}}");
        Bar parsedBar = new Bar();
        JsonIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema(), false);
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    public void testEmptyFoo() throws Exception {
        Foo foo = new Foo();
        byte[] data = JsonIOUtil.toByteArray(foo, foo.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{}");
        Foo parsedFoo = new Foo();
        JsonIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema(), false);
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    public void testEmptyFooInner() throws Exception {
        Foo foo = new Foo();
        ArrayList<Bar> bars = new ArrayList<Bar>();
        bars.add(new Bar());
        foo.setSomeBar(bars);
        byte[] data = JsonIOUtil.toByteArray(foo, foo.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBar\":[{}]}");
        Foo parsedFoo = new Foo();
        JsonIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema(), false);
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    public void testEmptyFooDeeper() throws Exception {
        Foo foo = new Foo();
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar bar = new Bar();
        bar.setSomeBaz(new Baz());
        bars.add(bar);
        foo.setSomeBar(bars);
        byte[] data = JsonIOUtil.toByteArray(foo, foo.cachedSchema(), false);
        TestCase.assertEquals(new String(data, "UTF-8"), "{\"someBar\":[{\"someBaz\":{}}]}");
        Foo parsedFoo = new Foo();
        JsonIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema(), false);
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    public void testFooNullFields() throws Exception {
        Foo b = new Foo();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":[null]" + (((((((",\"someString\":[null]" + ",\"someBar\":[null]") + ",\"someEnum\":[null]") + ",\"someBytes\":[null]") + ",\"someBoolean\":[null]") + ",\"someFloat\":[null]") + ",\"someDouble\":[null]") + ",\"someLong\":[null]}"))), b, b.cachedSchema(), false);
        TestCase.assertNull(b.getSomeInt());
        TestCase.assertNull(b.getSomeString());
        TestCase.assertNull(b.getSomeBar());
        TestCase.assertNull(b.getSomeEnum());
        TestCase.assertNull(b.getSomeBytes());
        TestCase.assertNull(b.getSomeBoolean());
        TestCase.assertNull(b.getSomeFloat());
        TestCase.assertNull(b.getSomeDouble());
        TestCase.assertNull(b.getSomeLong());
    }

    public void testFooNullFieldsButFirst() throws Exception {
        Foo b = new Foo();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":[1,null]" + (((((((",\"someString\":[\"string\",null]" + ",\"someBar\":[{},null]") + ",\"someEnum\":[1,null]") + ",\"someBytes\":[\"fw==\",null]")// 0x7f
         + ",\"someBoolean\":[true,null]") + ",\"someFloat\":[10.01,null]") + ",\"someDouble\":[100.001,null]") + ",\"someLong\":[1000,null]}"))), b, b.cachedSchema(), false);
        TestCase.assertEquals(b.getSomeInt(), Arrays.asList(new Integer(1)));
        TestCase.assertEquals(b.getSomeString(), Arrays.asList("string"));
        TestCase.assertEquals(b.getSomeBar(), Arrays.asList(new Bar()));
        TestCase.assertEquals(b.getSomeEnum(), Arrays.asList(TYPE1));
        TestCase.assertEquals(b.getSomeBytes(), Arrays.asList(ByteString.copyFrom(new byte[]{ 127 })));
        TestCase.assertEquals(b.getSomeBoolean(), Arrays.asList(Boolean.TRUE));
        TestCase.assertEquals(b.getSomeFloat(), Arrays.asList(new Float(10.01F)));
        TestCase.assertEquals(b.getSomeDouble(), Arrays.asList(new Double(100.001)));
        TestCase.assertEquals(b.getSomeLong(), Arrays.asList(new Long(1000L)));
    }

    public void testFooNullFieldsButMid() throws Exception {
        Foo b = new Foo();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":[null,1,null]" + (((((((",\"someString\":[null,\"string\",null]" + ",\"someBar\":[null,{},null]") + ",\"someEnum\":[null,1,null]") + ",\"someBytes\":[null,\"fw==\",null]")// 0x7f
         + ",\"someBoolean\":[null,true,null]") + ",\"someFloat\":[null,10.01,null]") + ",\"someDouble\":[null,100.001,null]") + ",\"someLong\":[null,1000,null]}"))), b, b.cachedSchema(), false);
        TestCase.assertEquals(b.getSomeInt(), Arrays.asList(new Integer(1)));
        TestCase.assertEquals(b.getSomeString(), Arrays.asList("string"));
        TestCase.assertEquals(b.getSomeBar(), Arrays.asList(new Bar()));
        TestCase.assertEquals(b.getSomeEnum(), Arrays.asList(TYPE1));
        TestCase.assertEquals(b.getSomeBytes(), Arrays.asList(ByteString.copyFrom(new byte[]{ 127 })));
        TestCase.assertEquals(b.getSomeBoolean(), Arrays.asList(Boolean.TRUE));
        TestCase.assertEquals(b.getSomeFloat(), Arrays.asList(new Float(10.01F)));
        TestCase.assertEquals(b.getSomeDouble(), Arrays.asList(new Double(100.001)));
        TestCase.assertEquals(b.getSomeLong(), Arrays.asList(new Long(1000L)));
    }

    public void testFooNullFieldsButLast() throws Exception {
        Foo b = new Foo();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":[null,1]" + (((((((",\"someString\":[null,\"string\"]" + ",\"someBar\":[null,{}]") + ",\"someEnum\":[null,1]") + ",\"someBytes\":[null,\"fw==\"]")// 0x7f
         + ",\"someBoolean\":[null,true]") + ",\"someFloat\":[null,10.01]") + ",\"someDouble\":[null,100.001]") + ",\"someLong\":[null,1000]}"))), b, b.cachedSchema(), false);
        TestCase.assertEquals(b.getSomeInt(), Arrays.asList(new Integer(1)));
        TestCase.assertEquals(b.getSomeString(), Arrays.asList("string"));
        TestCase.assertEquals(b.getSomeBar(), Arrays.asList(new Bar()));
        TestCase.assertEquals(b.getSomeEnum(), Arrays.asList(TYPE1));
        TestCase.assertEquals(b.getSomeBytes(), Arrays.asList(ByteString.copyFrom(new byte[]{ 127 })));
        TestCase.assertEquals(b.getSomeBoolean(), Arrays.asList(Boolean.TRUE));
        TestCase.assertEquals(b.getSomeFloat(), Arrays.asList(new Float(10.01F)));
        TestCase.assertEquals(b.getSomeDouble(), Arrays.asList(new Double(100.001)));
        TestCase.assertEquals(b.getSomeLong(), Arrays.asList(new Long(1000L)));
    }

    public void testFooNullFieldsButLast2() throws Exception {
        Foo b = new Foo();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":[null,null,1]" + (((((((",\"someString\":[null,null,\"string\"]" + ",\"someBar\":[null,null,{}]") + ",\"someEnum\":[null,null,1]") + ",\"someBytes\":[null,null,\"fw==\"]")// 0x7f
         + ",\"someBoolean\":[null,null,true]") + ",\"someFloat\":[null,null,10.01]") + ",\"someDouble\":[null,null,100.001]") + ",\"someLong\":[null,null,1000]}"))), b, b.cachedSchema(), false);
        TestCase.assertEquals(b.getSomeInt(), Arrays.asList(new Integer(1)));
        TestCase.assertEquals(b.getSomeString(), Arrays.asList("string"));
        TestCase.assertEquals(b.getSomeBar(), Arrays.asList(new Bar()));
        TestCase.assertEquals(b.getSomeEnum(), Arrays.asList(TYPE1));
        TestCase.assertEquals(b.getSomeBytes(), Arrays.asList(ByteString.copyFrom(new byte[]{ 127 })));
        TestCase.assertEquals(b.getSomeBoolean(), Arrays.asList(Boolean.TRUE));
        TestCase.assertEquals(b.getSomeFloat(), Arrays.asList(new Float(10.01F)));
        TestCase.assertEquals(b.getSomeDouble(), Arrays.asList(new Double(100.001)));
        TestCase.assertEquals(b.getSomeLong(), Arrays.asList(new Long(1000L)));
    }

    public void testBarNullFields() throws Exception {
        Bar b = new Bar();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser(("{\"someInt\":null" + (((((((",\"someString\":null" + ",\"someBaz\":null") + ",\"someEnum\":null") + ",\"someBytes\":null") + ",\"someBoolean\":null") + ",\"someFloat\":null") + ",\"someDouble\":null") + ",\"someLong\":null}"))), b, b.cachedSchema(), false);
        TestCase.assertEquals(0, b.getSomeInt());
        TestCase.assertNull(b.getSomeString());
        TestCase.assertNull(b.getSomeBaz());
        TestCase.assertNull(b.getSomeEnum());
        TestCase.assertNull(b.getSomeBytes());
        TestCase.assertFalse(b.getSomeBoolean());
        TestCase.assertEquals(0.0F, b.getSomeFloat());
        TestCase.assertEquals(0.0, b.getSomeDouble());
        TestCase.assertEquals(0L, b.getSomeLong());
    }

    public void testBazNullFields() throws Exception {
        Baz b = new Baz();
        JsonIOUtil.mergeFrom(DEFAULT_JSON_FACTORY.createJsonParser("{\"id\":null,\"name\":null,\"timestamp\":null}"), b, b.cachedSchema(), false);
        TestCase.assertEquals(0, b.getId());
        TestCase.assertNull(b.getName());
        TestCase.assertEquals(0L, b.getTimestamp());
    }
}

