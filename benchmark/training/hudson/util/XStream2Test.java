/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.util;


import Jenkins.XSTREAM2;
import Run.XSTREAM;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import hudson.model.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


/**
 * Tests for XML serialization of java objects.
 *
 * @author Kohsuke Kawaguchi, Mike Dillon, Alan Harder, Richard Mortimer
 */
public class XStream2Test {
    public static final class Foo {
        Result r1;

        Result r2;
    }

    @Test
    public void marshalValue() {
        XStream2Test.Foo f = new XStream2Test.Foo();
        f.r1 = f.r2 = Result.FAILURE;
        String xml = XSTREAM.toXML(f);
        // we should find two "FAILURE"s as they should be written out twice
        Assert.assertEquals(xml, 3, xml.split("FAILURE").length);
    }

    private static class Bar {
        String s;
    }

    /**
     * Test ability to read old XML from Hudson 1.105 or older.
     */
    @Test
    public void xStream11Compatibility() {
        XStream2Test.Bar b = ((XStream2Test.Bar) (new XStream2().fromXML("<hudson.util.XStream2Test-Bar><s>foo</s></hudson.util.XStream2Test-Bar>")));
        Assert.assertEquals("foo", b.s);
    }

    public static final class __Foo_Bar$Class {
        String under_1 = "1";

        String under__2 = "2";

        String _leadUnder1 = "L1";

        String __leadUnder2 = "L2";

        String $dollar = "D1";

        String dollar$2 = "D2";
    }

    /**
     * Test marshal/unmarshal round trip for class/field names with _ and $ characters.
     */
    @Issue("HUDSON-5768")
    @Test
    public void xmlRoundTrip() {
        XStream2 xs = new XStream2();
        XStream2Test.__Foo_Bar$Class b = new XStream2Test.__Foo_Bar$Class();
        String xml = xs.toXML(b);
        XStream2Test.__Foo_Bar$Class b2 = ((XStream2Test.__Foo_Bar$Class) (xs.fromXML(xml)));
        Assert.assertEquals(xml, b.under_1, b2.under_1);
        Assert.assertEquals(xml, b.under__2, b2.under__2);
        Assert.assertEquals(xml, b._leadUnder1, b2._leadUnder1);
        Assert.assertEquals(xml, b.__leadUnder2, b2.__leadUnder2);
        Assert.assertEquals(xml, b.$dollar, b2.$dollar);
        Assert.assertEquals(xml, b.dollar$2, b2.dollar$2);
    }

    private static class Baz {
        private Exception myFailure;
    }

    /**
     * Verify RobustReflectionConverter can handle missing fields in a class extending
     * Throwable/Exception (default ThrowableConverter registered by XStream calls
     * ReflectionConverter directly, rather than our RobustReflectionConverter replacement).
     */
    @Issue("HUDSON-5769")
    @Test
    public void unmarshalThrowableMissingField() {
        Level oldLevel = disableLogging();
        XStream2Test.Baz baz = new XStream2Test.Baz();
        baz.myFailure = new Exception("foo");
        XStream2 xs = new XStream2();
        String xml = xs.toXML(baz);
        baz = ((XStream2Test.Baz) (xs.fromXML(xml)));
        Assert.assertEquals("foo", baz.myFailure.getMessage());
        baz = ((XStream2Test.Baz) (xs.fromXML(("<hudson.util.XStream2Test_-Baz><myFailure>" + ((((("<missingField>true</missingField>" + "<detailMessage>hoho</detailMessage>") + "<stackTrace><trace>") + "hudson.util.XStream2Test.testUnmarshalThrowableMissingField(XStream2Test.java:97)") + "</trace></stackTrace>") + "</myFailure></hudson.util.XStream2Test_-Baz>")))));
        // Object should load, despite "missingField" in XML above
        Assert.assertEquals("hoho", baz.myFailure.getMessage());
        enableLogging(oldLevel);
    }

    private static class ImmutableMapHolder {
        ImmutableMap<?, ?> m;
    }

    private static class MapHolder {
        Map<?, ?> m;
    }

    @Test
    public void immutableMap() {
        XStream2 xs = new XStream2();
        roundtripImmutableMap(xs, ImmutableMap.of());
        roundtripImmutableMap(xs, ImmutableMap.of("abc", "xyz"));
        roundtripImmutableMap(xs, ImmutableMap.of("abc", "xyz", "def", "ghi"));
        roundtripImmutableMapAsPlainMap(xs, ImmutableMap.of());
        roundtripImmutableMapAsPlainMap(xs, ImmutableMap.of("abc", "xyz"));
        roundtripImmutableMapAsPlainMap(xs, ImmutableMap.of("abc", "xyz", "def", "ghi"));
    }

    private static class ImmutableListHolder {
        ImmutableList<?> l;
    }

    private static class ListHolder {
        List<?> l;
    }

    @Test
    public void immutableList() {
        XStream2 xs = new XStream2();
        roundtripImmutableList(xs, ImmutableList.of());
        roundtripImmutableList(xs, ImmutableList.of("abc"));
        roundtripImmutableList(xs, ImmutableList.of("abc", "def"));
        roundtripImmutableListAsPlainList(xs, ImmutableList.of());
        roundtripImmutableListAsPlainList(xs, ImmutableList.of("abc"));
        roundtripImmutableListAsPlainList(xs, ImmutableList.of("abc", "def"));
    }

    // Previously a null entry in an array caused NPE
    @Issue("JENKINS-8006")
    @Test
    public void emptyStack() {
        Assert.assertEquals("<object-array><null/><null/></object-array>", XSTREAM.toXML(new Object[2]).replaceAll("[ \n\r\t]+", ""));
    }

    @Issue("JENKINS-9843")
    @Test
    public void compatibilityAlias() {
        XStream2 xs = new XStream2();
        xs.addCompatibilityAlias("legacy.Point", XStream2Test.Point.class);
        XStream2Test.Point pt = ((XStream2Test.Point) (xs.fromXML("<legacy.Point><x>1</x><y>2</y></legacy.Point>")));
        Assert.assertEquals(1, pt.x);
        Assert.assertEquals(2, pt.y);
        String xml = xs.toXML(pt);
        // System.out.println(xml);
        Assert.assertFalse("Shouldn't use the alias when writing back", xml.contains("legacy"));
    }

    public static class Point {
        public int x;

        public int y;
    }

    public static class Foo2 {
        ConcurrentHashMap<String, String> m = new ConcurrentHashMap<String, String>();
    }

    @Issue("SECURITY-105")
    @Test
    public void dynamicProxyBlocked() {
        try {
            ((Runnable) (new XStream2().fromXML((("<dynamic-proxy><interface>java.lang.Runnable</interface><handler class='java.beans.EventHandler'><target class='" + (XStream2Test.Hacked.class.getName())) + "'/><action>oops</action></handler></dynamic-proxy>")))).run();
        } catch (XStreamException x) {
            // good
        }
        Assert.assertFalse("should never have run that", XStream2Test.Hacked.tripped);
    }

    public static final class Hacked {
        static boolean tripped;

        public void oops() {
            XStream2Test.Hacked.tripped = true;
        }
    }

    @Test
    public void trimVersion() {
        Assert.assertEquals("3.2", XStream2.trimVersion("3.2"));
        Assert.assertEquals("3.2.1", XStream2.trimVersion("3.2.1"));
        Assert.assertEquals("3.2-SNAPSHOT", XStream2.trimVersion("3.2-SNAPSHOT (private-09/23/2012 12:26-jhacker)"));
    }

    @Issue("JENKINS-21017")
    @Test
    public void unmarshalToDefault_populated() {
        String populatedXml = "<hudson.util.XStream2Test_-WithDefaults>\n" + (((((((((((((((((((((((((((((((("  <stringDefaultValue>my string</stringDefaultValue>\n" + "  <stringDefaultNull>not null</stringDefaultNull>\n") + "  <arrayDefaultValue>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </arrayDefaultValue>\n") + "  <arrayDefaultEmpty>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </arrayDefaultEmpty>\n") + "  <arrayDefaultNull>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </arrayDefaultNull>\n") + "  <listDefaultValue>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </listDefaultValue>\n") + "  <listDefaultEmpty>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </listDefaultEmpty>\n") + "  <listDefaultNull>\n") + "    <string>1</string>\n") + "    <string>2</string>\n") + "    <string>3</string>\n") + "  </listDefaultNull>\n") + "</hudson.util.XStream2Test_-WithDefaults>");
        XStream2Test.WithDefaults existingInstance = new XStream2Test.WithDefaults("foobar", "foobar", new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"));
        XStream2Test.WithDefaults newInstance = new XStream2Test.WithDefaults();
        String xmlA = XSTREAM2.toXML(fromXMLNullingOut(populatedXml, existingInstance));
        String xmlB = XSTREAM2.toXML(fromXMLNullingOut(populatedXml, newInstance));
        String xmlC = XSTREAM2.toXML(fromXMLNullingOut(populatedXml, null));
        Assert.assertThat("Deserializing over an existing instance is the same as with no root", xmlA, CoreMatchers.is(xmlC));
        Assert.assertThat("Deserializing over an new instance is the same as with no root", xmlB, CoreMatchers.is(xmlC));
    }

    @Issue("JENKINS-21017")
    @Test
    public void unmarshalToDefault_default() {
        String defaultXml = "<hudson.util.XStream2Test_-WithDefaults>\n" + ((((((((((("  <stringDefaultValue>defaultValue</stringDefaultValue>\n" + "  <arrayDefaultValue>\n") + "    <string>first</string>\n") + "    <string>second</string>\n") + "  </arrayDefaultValue>\n") + "  <arrayDefaultEmpty/>\n") + "  <listDefaultValue>\n") + "    <string>first</string>\n") + "    <string>second</string>\n") + "  </listDefaultValue>\n") + "  <listDefaultEmpty/>\n") + "</hudson.util.XStream2Test_-WithDefaults>");
        XStream2Test.WithDefaults existingInstance = new XStream2Test.WithDefaults("foobar", "foobar", new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"));
        XStream2Test.WithDefaults newInstance = new XStream2Test.WithDefaults();
        String xmlA = XSTREAM2.toXML(fromXMLNullingOut(defaultXml, existingInstance));
        String xmlB = XSTREAM2.toXML(fromXMLNullingOut(defaultXml, newInstance));
        String xmlC = XSTREAM2.toXML(fromXMLNullingOut(defaultXml, null));
        Assert.assertThat("Deserializing over an existing instance is the same as with no root", xmlA, CoreMatchers.is(xmlC));
        Assert.assertThat("Deserializing over an new instance is the same as with no root", xmlB, CoreMatchers.is(xmlC));
    }

    @Issue("JENKINS-21017")
    @Test
    public void unmarshalToDefault_empty() {
        String emptyXml = "<hudson.util.XStream2Test_-WithDefaults/>";
        XStream2Test.WithDefaults existingInstance = new XStream2Test.WithDefaults("foobar", "foobar", new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, new String[]{ "foobar", "barfoo", "fumanchu" }, Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"), Arrays.asList("foobar", "barfoo", "fumanchu"));
        XStream2Test.WithDefaults newInstance = new XStream2Test.WithDefaults();
        Object reloaded = fromXMLNullingOut(emptyXml, existingInstance);
        Assert.assertSame(existingInstance, reloaded);
        String xmlA = XSTREAM2.toXML(reloaded);
        String xmlB = XSTREAM2.toXML(fromXMLNullingOut(emptyXml, newInstance));
        String xmlC = XSTREAM2.toXML(fromXMLNullingOut(emptyXml, null));
        Assert.assertThat("Deserializing over an existing instance is the same as with no root", xmlA, CoreMatchers.is(xmlC));
        Assert.assertThat("Deserializing over an new instance is the same as with no root", xmlB, CoreMatchers.is(xmlC));
    }

    public static class WithDefaults {
        private String stringDefaultValue = "defaultValue";

        private String stringDefaultNull;

        private String[] arrayDefaultValue = new String[]{ "first", "second" };

        private String[] arrayDefaultEmpty = new String[0];

        private String[] arrayDefaultNull;

        private List<String> listDefaultValue = new ArrayList<>(Arrays.asList("first", "second"));

        private List<String> listDefaultEmpty = new ArrayList<>();

        private List<String> listDefaultNull;

        public WithDefaults() {
        }

        public WithDefaults(String stringDefaultValue, String stringDefaultNull, String[] arrayDefaultValue, String[] arrayDefaultEmpty, String[] arrayDefaultNull, List<String> listDefaultValue, List<String> listDefaultEmpty, List<String> listDefaultNull) {
            this.stringDefaultValue = stringDefaultValue;
            this.stringDefaultNull = stringDefaultNull;
            this.arrayDefaultValue = (arrayDefaultValue == null) ? null : arrayDefaultValue.clone();
            this.arrayDefaultEmpty = (arrayDefaultEmpty == null) ? null : arrayDefaultEmpty.clone();
            this.arrayDefaultNull = (arrayDefaultNull == null) ? null : arrayDefaultNull.clone();
            this.listDefaultValue = (listDefaultValue == null) ? null : new ArrayList<>(listDefaultValue);
            this.listDefaultEmpty = (listDefaultEmpty == null) ? null : new ArrayList<>(listDefaultEmpty);
            this.listDefaultNull = (listDefaultNull == null) ? null : new ArrayList<>(listDefaultNull);
        }

        public String getStringDefaultValue() {
            return stringDefaultValue;
        }

        public void setStringDefaultValue(String stringDefaultValue) {
            this.stringDefaultValue = stringDefaultValue;
        }

        public String getStringDefaultNull() {
            return stringDefaultNull;
        }

        public void setStringDefaultNull(String stringDefaultNull) {
            this.stringDefaultNull = stringDefaultNull;
        }

        public String[] getArrayDefaultValue() {
            return arrayDefaultValue;
        }

        public void setArrayDefaultValue(String[] arrayDefaultValue) {
            this.arrayDefaultValue = arrayDefaultValue;
        }

        public String[] getArrayDefaultEmpty() {
            return arrayDefaultEmpty;
        }

        public void setArrayDefaultEmpty(String[] arrayDefaultEmpty) {
            this.arrayDefaultEmpty = arrayDefaultEmpty;
        }

        public String[] getArrayDefaultNull() {
            return arrayDefaultNull;
        }

        public void setArrayDefaultNull(String[] arrayDefaultNull) {
            this.arrayDefaultNull = arrayDefaultNull;
        }

        public List<String> getListDefaultValue() {
            return listDefaultValue;
        }

        public void setListDefaultValue(List<String> listDefaultValue) {
            this.listDefaultValue = listDefaultValue;
        }

        public List<String> getListDefaultEmpty() {
            return listDefaultEmpty;
        }

        public void setListDefaultEmpty(List<String> listDefaultEmpty) {
            this.listDefaultEmpty = listDefaultEmpty;
        }

        public List<String> getListDefaultNull() {
            return listDefaultNull;
        }

        public void setListDefaultNull(List<String> listDefaultNull) {
            this.listDefaultNull = listDefaultNull;
        }
    }

    @Issue("SECURITY-503")
    @Test
    public void crashXstream() throws Exception {
        try {
            new XStream2().fromXML("<void/>");
            Assert.fail("expected to throw ForbiddenClassException, but why are we still alive?");
        } catch (ForbiddenClassException ex) {
            // pass
        }
    }
}

