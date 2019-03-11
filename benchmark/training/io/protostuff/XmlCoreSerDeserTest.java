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


import SerializableObjects.bar;
import SerializableObjects.foo;
import SerializableObjects.negativeBar;
import io.protostuff.StringSerializer.STRING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing for xml ser/deser against messages.
 *
 * @author David Yu
 * @unknown May 24, 2010
 */
public class XmlCoreSerDeserTest {
    @Test
    public void testFoo() throws Exception {
        Foo fooCompare = SerializableObjects.foo;
        Foo dfoo = new Foo();
        byte[] data = XmlIOUtil.toByteArray(fooCompare, fooCompare.cachedSchema());
        XmlIOUtil.mergeFrom(data, dfoo, dfoo.cachedSchema());
        SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    @Test
    public void testBar() throws Exception {
        for (Bar barCompare : new Bar[]{ SerializableObjects.bar, SerializableObjects.negativeBar }) {
            Bar dbar = new Bar();
            byte[] data = XmlIOUtil.toByteArray(barCompare, barCompare.cachedSchema());
            XmlIOUtil.mergeFrom(data, dbar, dbar.cachedSchema());
            SerializableObjects.assertEquals(barCompare, dbar);
        }
    }

    @Test
    public void testBarWithEmptyStringAndByteString() throws Exception {
        Bar barCompare = new Bar();
        barCompare.setSomeBytes(ByteString.copyFromUtf8(new String("")));
        barCompare.setSomeString(new String(""));
        Bar dbar = new Bar();
        byte[] data = XmlIOUtil.toByteArray(barCompare, barCompare.cachedSchema());
        XmlIOUtil.mergeFrom(data, dbar, dbar.cachedSchema());
        SerializableObjects.assertEquals(barCompare, dbar);
        // test equal serialization on both xml outputs
        byte[] actuals = XmlXIOUtil.toByteArray(barCompare, barCompare.cachedSchema(), LinkedBuffer.allocate(256));
        Assert.assertArrayEquals(data, actuals);
    }

    @Test
    public void testBaz() throws Exception {
        for (Baz bazCompare : new Baz[]{ SerializableObjects.baz, SerializableObjects.negativeBaz }) {
            Baz dbaz = new Baz();
            byte[] data = XmlIOUtil.toByteArray(bazCompare, bazCompare.cachedSchema());
            XmlIOUtil.mergeFrom(data, dbaz, dbaz.cachedSchema());
            SerializableObjects.assertEquals(bazCompare, dbaz);
        }
    }

    @Test
    public void testUnknownScalarFields() throws Exception {
        String[] regularMessages = new String[]{ "<?xml version='1.0' encoding='UTF-8'?>" + ("<Baz><int>1</int><string>string</string>" + "<double>555.444</double><id>1</id></Baz>"), "<?xml version='1.0' encoding='UTF-8'?>" + ("<Baz><int>1</int><string>string</string>" + "<id>2</id><double>555.444</double></Baz>"), "<?xml version='1.0' encoding='UTF-8'?>" + (("<Baz><id>3</id><int>1</int>" + "<string>string</string><double>555.444</double>") + "<bytes><![CDATA[b2]]></bytes></Baz>") };
        for (int i = 0; i < (regularMessages.length); i++) {
            Baz b = new Baz();
            XmlIOUtil.mergeFrom(STRING.ser(regularMessages[i]), b, b.cachedSchema());
            Assert.assertTrue(((i + 1) == (b.getId())));
        }
    }

    @Test
    public void testListIO() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        bars.add(bar);
        bars.add(negativeBar);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlIOUtil.writeListTo(out, bars, bar.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = XmlIOUtil.parseListFrom(in, bar.cachedSchema());
        Assert.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (Bar b : parsedBars)
            SerializableObjects.assertEquals(bars.get((i++)), b);

    }

    @Test
    public void testListIOEmpty() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlIOUtil.writeListTo(out, bars, bar.cachedSchema());
        byte[] data = out.toByteArray();
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<list/>"); */
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = XmlIOUtil.parseListFrom(in, bar.cachedSchema());
        Assert.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (Bar b : parsedBars)
            SerializableObjects.assertEquals(bars.get((i++)), b);

    }

    @Test
    public void testListIOWithArrays() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        foos.add(foo);
        foos.add(foo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlIOUtil.writeListTo(out, foos, foo.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsed = XmlIOUtil.parseListFrom(in, foo.cachedSchema());
        Assert.assertTrue(((parsed.size()) == (foos.size())));
        int i = 0;
        for (Foo f : parsed)
            SerializableObjects.assertEquals(foos.get((i++)), f);

    }

    @Test
    public void testEmptyMessage() throws Exception {
        Bar bar = new Bar();
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar/>"); */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testEmptyMessageInner() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        // method name is setSomeBaz, should have been someBaz!
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" + "<someBaz>" +
        "<Baz/>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessage() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeInt(1);
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" +
        "<someInt>1</someInt>" + "<someBaz>" + "<Baz/>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessageWithString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeString("someString");
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" +
        "<someString>someString</someString>" + "<someBaz>" + "<Baz/>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessageWithEmptyString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        bar.setSomeString("");
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" +
        "<someString></someString>" + "<someBaz>" + "<Baz/>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessageInner() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setId(2);
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" + "<someBaz>" +
        "<Baz>" + "<id>2</id>" + "</Baz>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessageInnerWithString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setName("asdfsf");
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" + "<someBaz>" +
        "<Baz>" + "<name>asdfsf</name>" + "</Baz>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testPartialEmptyMessageInnerWithEmptyString() throws Exception {
        Baz baz = new Baz();
        Bar bar = new Bar();
        baz.setName("");
        bar.setSomeBaz(baz);
        byte[] data = XmlIOUtil.toByteArray(bar, bar.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Bar>" + "<someBaz>" +
        "<Baz>" + "<name></name>" + "</Baz>" + "</someBaz>" + "</Bar>");
         */
        Bar parsedBar = new Bar();
        XmlIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        SerializableObjects.assertEquals(bar, parsedBar);
    }

    @Test
    public void testEmptyFoo() throws Exception {
        Foo foo = new Foo();
        byte[] data = XmlIOUtil.toByteArray(foo, foo.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Foo/>"); */
        Foo parsedFoo = new Foo();
        XmlIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema());
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    @Test
    public void testEmptyFooInner() throws Exception {
        Foo foo = new Foo();
        ArrayList<Bar> bars = new ArrayList<Bar>();
        bars.add(new Bar());
        foo.setSomeBar(bars);
        byte[] data = XmlIOUtil.toByteArray(foo, foo.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Foo>" + "<someBar>" +
        "<Bar/>" + "</someBar>" + "</Foo>");
         */
        Foo parsedFoo = new Foo();
        XmlIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema());
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    @Test
    public void testEmptyFooDeeper() throws Exception {
        Foo foo = new Foo();
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar bar = new Bar();
        bar.setSomeBaz(new Baz());
        bars.add(bar);
        foo.setSomeBar(bars);
        byte[] data = XmlIOUtil.toByteArray(foo, foo.cachedSchema());
        /* assertEquals(new String(data, "UTF-8"), "<?xml version='1.0' encoding='UTF-8'?>" + "<Foo>" + "<someBar>" +
        "<Bar>" + "<someBaz>" + "<Baz/>" + "</someBaz>" + "</Bar>" + "</someBar>" + "</Foo>");
         */
        Foo parsedFoo = new Foo();
        XmlIOUtil.mergeFrom(data, parsedFoo, parsedFoo.cachedSchema());
        SerializableObjects.assertEquals(foo, parsedFoo);
    }

    @Test
    public void testNestedRequiredField() {
        Schema<XmlCoreSerDeserTest.WrapperPojo> schema = XmlCoreSerDeserTest.WrapperPojo.SCHEMA;
        XmlCoreSerDeserTest.WrapperPojo message = new XmlCoreSerDeserTest.WrapperPojo();
        message.requiresName = new XmlCoreSerDeserTest.RequiresName();
        message.requiresName.description = "some description";
        byte[] data = XmlIOUtil.toByteArray(message, schema);
        XmlCoreSerDeserTest.WrapperPojo parsed = new XmlCoreSerDeserTest.WrapperPojo();
        try {
            XmlIOUtil.mergeFrom(data, parsed, schema);
        } catch (UninitializedMessageException e) {
            // expected
            return;
        }
        Assert.assertTrue(false);
    }

    static class RequiresName {
        String name;

        String description;

        static final Schema<XmlCoreSerDeserTest.RequiresName> SCHEMA = new Schema<XmlCoreSerDeserTest.RequiresName>() {
            @Override
            public String getFieldName(int number) {
                switch (number) {
                    case 1 :
                        return "n";
                    case 2 :
                        return "d";
                    default :
                        return null;
                }
            }

            @Override
            public int getFieldNumber(String name) {
                if ((name.length()) != 1)
                    return 0;

                switch (name.charAt(0)) {
                    case 'n' :
                        return 1;
                    case 'd' :
                        return 2;
                    default :
                        return 0;
                }
            }

            @Override
            public boolean isInitialized(XmlCoreSerDeserTest.RequiresName message) {
                return (message.name) != null;
            }

            @Override
            public void mergeFrom(Input input, XmlCoreSerDeserTest.RequiresName message) throws IOException {
                for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                    switch (number) {
                        case 0 :
                            return;
                        case 1 :
                            message.name = input.readString();
                            break;
                        case 2 :
                            message.description = input.readString();
                            break;
                        default :
                            input.handleUnknownField(number, this);
                    }
                }
            }

            @Override
            public String messageFullName() {
                return XmlCoreSerDeserTest.RequiresName.class.getName();
            }

            @Override
            public String messageName() {
                return XmlCoreSerDeserTest.RequiresName.class.getSimpleName();
            }

            @Override
            public XmlCoreSerDeserTest.RequiresName newMessage() {
                return new XmlCoreSerDeserTest.RequiresName();
            }

            @Override
            public Class<? super XmlCoreSerDeserTest.RequiresName> typeClass() {
                return XmlCoreSerDeserTest.RequiresName.class;
            }

            @Override
            public void writeTo(Output output, XmlCoreSerDeserTest.RequiresName message) throws IOException {
                if ((message.name) != null)
                    output.writeString(1, ((CharSequence) (message.name)), false);

                if ((message.description) != null)
                    output.writeString(2, ((CharSequence) (message.description)), false);

            }
        };
    }

    static class WrapperPojo {
        XmlCoreSerDeserTest.RequiresName requiresName;

        static final Schema<XmlCoreSerDeserTest.WrapperPojo> SCHEMA = new Schema<XmlCoreSerDeserTest.WrapperPojo>() {
            @Override
            public String getFieldName(int number) {
                return number == 1 ? "w" : null;
            }

            @Override
            public int getFieldNumber(String name) {
                return ((name.length()) == 1) && ((name.charAt(0)) == 'w') ? 1 : 0;
            }

            @Override
            public boolean isInitialized(XmlCoreSerDeserTest.WrapperPojo message) {
                return true;
            }

            @Override
            public void mergeFrom(Input input, XmlCoreSerDeserTest.WrapperPojo message) throws IOException {
                for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                    switch (number) {
                        case 0 :
                            return;
                        case 1 :
                            message.requiresName = input.mergeObject(message.requiresName, XmlCoreSerDeserTest.RequiresName.SCHEMA);
                            break;
                        default :
                            input.handleUnknownField(number, this);
                    }
                }
            }

            @Override
            public String messageFullName() {
                return XmlCoreSerDeserTest.WrapperPojo.class.getName();
            }

            @Override
            public String messageName() {
                return XmlCoreSerDeserTest.WrapperPojo.class.getSimpleName();
            }

            @Override
            public XmlCoreSerDeserTest.WrapperPojo newMessage() {
                return new XmlCoreSerDeserTest.WrapperPojo();
            }

            @Override
            public Class<? super XmlCoreSerDeserTest.WrapperPojo> typeClass() {
                return XmlCoreSerDeserTest.WrapperPojo.class;
            }

            @Override
            public void writeTo(Output output, XmlCoreSerDeserTest.WrapperPojo message) throws IOException {
                if ((message.requiresName) != null)
                    output.writeObject(1, message.requiresName, XmlCoreSerDeserTest.RequiresName.SCHEMA, false);

            }
        };
    }
}

