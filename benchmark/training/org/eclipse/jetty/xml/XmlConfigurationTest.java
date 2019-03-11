/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.xml;


import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class XmlConfigurationTest {
    protected String[] _configure = new String[]{ "org/eclipse/jetty/xml/configureWithAttr.xml", "org/eclipse/jetty/xml/configureWithElements.xml" };

    private static final String STRING_ARRAY_XML = "<Array type=\"String\"><Item type=\"String\">String1</Item><Item type=\"String\">String2</Item></Array>";

    private static final String INT_ARRAY_XML = "<Array type=\"int\"><Item type=\"int\">1</Item><Item type=\"int\">2</Item></Array>";

    @Test
    public void testMortBay() throws Exception {
        URL url = XmlConfigurationTest.class.getClassLoader().getResource("org/eclipse/jetty/xml/mortbay.xml");
        XmlConfiguration configuration = new XmlConfiguration(url);
        configuration.configure();
    }

    @Test
    public void testPassedObject() throws Exception {
        for (String configure : _configure) {
            Map<String, String> properties = new HashMap<>();
            properties.put("whatever", "xxx");
            TestConfiguration.VALUE = 77;
            URL url = XmlConfigurationTest.class.getClassLoader().getResource(configure);
            XmlConfiguration configuration = new XmlConfiguration(url);
            TestConfiguration tc = new TestConfiguration("tc");
            configuration.getProperties().putAll(properties);
            configuration.configure(tc);
            Assertions.assertEquals("SetValue", tc.testObject, "Set String");
            Assertions.assertEquals(2, tc.testInt, "Set Type");
            Assertions.assertEquals(18080, tc.propValue);
            Assertions.assertEquals("PutValue", tc.get("Test"), "Put");
            Assertions.assertEquals("2", tc.get("TestDft"), "Put dft");
            Assertions.assertEquals(2, tc.get("TestInt"), "Put type");
            Assertions.assertEquals("PutValue", tc.get("Trim"), "Trim");
            Assertions.assertEquals(null, tc.get("Null"), "Null");
            Assertions.assertEquals(null, tc.get("NullTrim"), "NullTrim");
            Assertions.assertEquals(1.2345, tc.get("ObjectTrim"), "ObjectTrim");
            Assertions.assertEquals("-1String", tc.get("Objects"), "Objects");
            Assertions.assertEquals("-1String", tc.get("ObjectsTrim"), "ObjectsTrim");
            Assertions.assertEquals("\n    PutValue\n  ", tc.get("String"), "String");
            Assertions.assertEquals("", tc.get("NullString"), "NullString");
            Assertions.assertEquals("\n  ", tc.get("WhiteSpace"), "WhiteSpace");
            Assertions.assertEquals("\n    1.2345\n  ", tc.get("ObjectString"), "ObjectString");
            Assertions.assertEquals("-1String", tc.get("ObjectsString"), "ObjectsString");
            Assertions.assertEquals("-1\n  String", tc.get("ObjectsWhiteString"), "ObjectsWhiteString");
            Assertions.assertEquals(((System.getProperty("user.dir")) + "/stuff"), tc.get("SystemProperty"), "SystemProperty");
            Assertions.assertEquals(System.getenv("HOME"), tc.get("Env"), "Env");
            Assertions.assertEquals("xxx", tc.get("Property"), "Property");
            Assertions.assertEquals("Yes", tc.get("Called"), "Called");
            Assertions.assertTrue(TestConfiguration.called);
            Assertions.assertEquals("Blah", tc.oa[0], "oa[0]");
            Assertions.assertEquals("1.2.3.4:5678", tc.oa[1], "oa[1]");
            Assertions.assertEquals(1.2345, tc.oa[2], "oa[2]");
            Assertions.assertEquals(null, tc.oa[3], "oa[3]");
            Assertions.assertEquals(1, tc.ia[0], "ia[0]");
            Assertions.assertEquals(2, tc.ia[1], "ia[1]");
            Assertions.assertEquals(3, tc.ia[2], "ia[2]");
            Assertions.assertEquals(0, tc.ia[3], "ia[3]");
            TestConfiguration tc2 = tc.nested;
            Assertions.assertTrue((tc2 != null));
            Assertions.assertEquals(true, tc2.get("Arg"), "Called(bool)");
            Assertions.assertEquals(null, tc.get("Arg"), "nested config");
            Assertions.assertEquals(true, tc2.get("Arg"), "nested config");
            Assertions.assertEquals("Call1", tc2.testObject, "nested config");
            Assertions.assertEquals(4, tc2.testInt, "nested config");
            Assertions.assertEquals("http://www.eclipse.com/", tc2.url.toString(), "nested call");
            Assertions.assertEquals(tc.testField1, 77, "static to field");
            Assertions.assertEquals(tc.testField2, 2, "field to field");
            Assertions.assertEquals(TestConfiguration.VALUE, 42, "literal to static");
            Assertions.assertEquals(((Map<String, String>) (configuration.getIdMap().get("map"))).get("key0"), "value0");
            Assertions.assertEquals(((Map<String, String>) (configuration.getIdMap().get("map"))).get("key1"), "value1");
        }
    }

    @Test
    public void testNewObject() throws Exception {
        for (String configure : _configure) {
            TestConfiguration.VALUE = 71;
            Map<String, String> properties = new HashMap<>();
            properties.put("whatever", "xxx");
            URL url = XmlConfigurationTest.class.getClassLoader().getResource(configure);
            final AtomicInteger count = new AtomicInteger(0);
            XmlConfiguration configuration = new XmlConfiguration(url) {
                @Override
                public void initializeDefaults(Object object) {
                    if (object instanceof TestConfiguration) {
                        count.incrementAndGet();
                        ((TestConfiguration) (object)).setNested(null);
                        ((TestConfiguration) (object)).setTestString("NEW DEFAULT");
                    }
                }
            };
            configuration.getProperties().putAll(properties);
            TestConfiguration tc = ((TestConfiguration) (configuration.configure()));
            Assertions.assertEquals(3, count.get());
            Assertions.assertEquals("NEW DEFAULT", tc.getTestString());
            Assertions.assertEquals("nested", tc.getNested().getTestString());
            Assertions.assertEquals("NEW DEFAULT", tc.getNested().getNested().getTestString());
            Assertions.assertEquals("SetValue", tc.testObject, "Set String");
            Assertions.assertEquals(2, tc.testInt, "Set Type");
            Assertions.assertEquals(18080, tc.propValue);
            Assertions.assertEquals("PutValue", tc.get("Test"), "Put");
            Assertions.assertEquals("2", tc.get("TestDft"), "Put dft");
            Assertions.assertEquals(2, tc.get("TestInt"), "Put type");
            Assertions.assertEquals("PutValue", tc.get("Trim"), "Trim");
            Assertions.assertEquals(null, tc.get("Null"), "Null");
            Assertions.assertEquals(null, tc.get("NullTrim"), "NullTrim");
            Assertions.assertEquals(1.2345, tc.get("ObjectTrim"), "ObjectTrim");
            Assertions.assertEquals("-1String", tc.get("Objects"), "Objects");
            Assertions.assertEquals("-1String", tc.get("ObjectsTrim"), "ObjectsTrim");
            Assertions.assertEquals("\n    PutValue\n  ", tc.get("String"), "String");
            Assertions.assertEquals("", tc.get("NullString"), "NullString");
            Assertions.assertEquals("\n  ", tc.get("WhiteSpace"), "WhiteSpace");
            Assertions.assertEquals("\n    1.2345\n  ", tc.get("ObjectString"), "ObjectString");
            Assertions.assertEquals("-1String", tc.get("ObjectsString"), "ObjectsString");
            Assertions.assertEquals("-1\n  String", tc.get("ObjectsWhiteString"), "ObjectsWhiteString");
            Assertions.assertEquals(((System.getProperty("user.dir")) + "/stuff"), tc.get("SystemProperty"), "SystemProperty");
            Assertions.assertEquals("xxx", tc.get("Property"), "Property");
            Assertions.assertEquals("Yes", tc.get("Called"), "Called");
            Assertions.assertTrue(TestConfiguration.called);
            Assertions.assertEquals("Blah", tc.oa[0], "oa[0]");
            Assertions.assertEquals("1.2.3.4:5678", tc.oa[1], "oa[1]");
            Assertions.assertEquals(1.2345, tc.oa[2], "oa[2]");
            Assertions.assertEquals(null, tc.oa[3], "oa[3]");
            Assertions.assertEquals(1, tc.ia[0], "ia[0]");
            Assertions.assertEquals(2, tc.ia[1], "ia[1]");
            Assertions.assertEquals(3, tc.ia[2], "ia[2]");
            Assertions.assertEquals(0, tc.ia[3], "ia[3]");
            TestConfiguration tc2 = tc.nested;
            Assertions.assertTrue((tc2 != null));
            Assertions.assertEquals(true, tc2.get("Arg"), "Called(bool)");
            Assertions.assertEquals(null, tc.get("Arg"), "nested config");
            Assertions.assertEquals(true, tc2.get("Arg"), "nested config");
            Assertions.assertEquals("Call1", tc2.testObject, "nested config");
            Assertions.assertEquals(4, tc2.testInt, "nested config");
            Assertions.assertEquals("http://www.eclipse.com/", tc2.url.toString(), "nested call");
            Assertions.assertEquals(71, tc.testField1, "static to field");
            Assertions.assertEquals(2, tc.testField2, "field to field");
            Assertions.assertEquals(42, TestConfiguration.VALUE, "literal to static");
        }
    }

    @Test
    public void testGetClass() throws Exception {
        XmlConfiguration configuration = new XmlConfiguration("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"Test\"><Get name=\"class\"/></Set></Configure>");
        TestConfiguration tc = new TestConfiguration();
        configuration.configure(tc);
        Assertions.assertEquals(TestConfiguration.class, tc.testObject);
        configuration = new XmlConfiguration("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"Test\"><Get class=\"java.lang.String\" name=\"class\"><Get id=\"simple\" name=\"simpleName\"/></Get></Set></Configure>");
        configuration.configure(tc);
        Assertions.assertEquals(String.class, tc.testObject);
        Assertions.assertEquals("String", configuration.getIdMap().get("simple"));
    }

    @Test
    public void testStringConfiguration() throws Exception {
        XmlConfiguration configuration = new XmlConfiguration("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"Test\">SetValue</Set><Set name=\"Test\" type=\"int\">2</Set></Configure>");
        TestConfiguration tc = new TestConfiguration();
        configuration.configure(tc);
        Assertions.assertEquals("SetValue", tc.testObject, "Set String 3");
        Assertions.assertEquals(2, tc.testInt, "Set Type 3");
    }

    @Test
    public void testMeaningfullSetException() throws Exception {
        XmlConfiguration configuration = new XmlConfiguration("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"PropertyTest\"><Property name=\"null\"/></Set></Configure>");
        TestConfiguration tc = new TestConfiguration();
        NoSuchMethodException e = Assertions.assertThrows(NoSuchMethodException.class, () -> {
            configuration.configure(tc);
        });
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Found setters for int"));
    }

    @Test
    public void testListConstructorArg() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "<Set name=\"constructorArgTestClass\"><New class=\"org.eclipse.jetty.xml.ConstructorArgTestClass\"><Arg type=\"List\">") + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Arg></New></Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getList() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getList() returns not null", tc.getList(), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat("tc.getList() has two entries as specified in the xml", tc.getList().size(), CoreMatchers.is(2));
    }

    @Test
    public void testTwoArgumentListConstructorArg() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(((((((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + ("<Set name=\"constructorArgTestClass\"><New class=\"org.eclipse.jetty.xml.ConstructorArgTestClass\">" + "<Arg type=\"List\">")) + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Arg>") + "<Arg type=\"List\">") + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Arg>") + "</New></Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getList() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getList() returns not null", tc.getList(), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat("tc.getList() has two entries as specified in the xml", tc.getList().size(), CoreMatchers.is(2));
    }

    @Test
    public void testListNotContainingArray() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "<New class=\"org.eclipse.jetty.xml.ConstructorArgTestClass\"><Arg type=\"List\">Some String</Arg></New></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            xmlConfiguration.configure(tc);
        });
    }

    @Test
    public void testSetConstructorArg() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "<Set name=\"constructorArgTestClass\"><New class=\"org.eclipse.jetty.xml.ConstructorArgTestClass\"><Arg type=\"Set\">") + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Arg></New></Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getList() returns null as it's not configured yet", tc.getSet(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getList() returns not null", tc.getSet(), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat("tc.getList() has two entries as specified in the xml", tc.getSet().size(), CoreMatchers.is(2));
    }

    @Test
    public void testSetNotContainingArray() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "<New class=\"org.eclipse.jetty.xml.ConstructorArgTestClass\"><Arg type=\"Set\">Some String</Arg></New></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            xmlConfiguration.configure(tc);
        });
    }

    @Test
    public void testListSetterWithStringArray() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"List\">" + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getList() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getList() has two entries as specified in the xml", tc.getList().size(), CoreMatchers.is(2));
    }

    @Test
    public void testListSetterWithPrimitiveArray() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"List\">" + (XmlConfigurationTest.INT_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getList() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getList() has two entries as specified in the xml", tc.getList().size(), CoreMatchers.is(2));
    }

    @Test
    public void testNotSupportedLinkedListSetter() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"LinkedList\">" + (XmlConfigurationTest.INT_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getSet() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            xmlConfiguration.configure(tc);
        });
    }

    @Test
    public void testArrayListSetter() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"ArrayList\">" + (XmlConfigurationTest.INT_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getSet() returns null as it's not configured yet", tc.getList(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getSet() has two entries as specified in the xml", tc.getList().size(), CoreMatchers.is(2));
    }

    @Test
    public void testSetSetter() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"Set\">" + (XmlConfigurationTest.STRING_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getSet() returns null as it's not configured yet", tc.getSet(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getSet() has two entries as specified in the xml", tc.getSet().size(), CoreMatchers.is(2));
    }

    @Test
    public void testSetSetterWithPrimitiveArray() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\"><Set name=\"Set\">" + (XmlConfigurationTest.INT_ARRAY_XML)) + "</Set></Configure>"));
        TestConfiguration tc = new TestConfiguration();
        MatcherAssert.assertThat("tc.getSet() returns null as it's not configured yet", tc.getSet(), CoreMatchers.is(CoreMatchers.nullValue()));
        xmlConfiguration.configure(tc);
        MatcherAssert.assertThat("tc.getSet() has two entries as specified in the xml", tc.getSet().size(), CoreMatchers.is(2));
    }

    @Test
    public void testMap() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((((("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "    <Set name=\"map\">") + "        <Map>") + "            <Entry>") + "                <Item>key1</Item>") + "                <Item>value1</Item>") + "            </Entry>") + "            <Entry>") + "                <Item>key2</Item>") + "                <Item>value2</Item>") + "            </Entry>") + "        </Map>") + "    </Set>") + "</Configure>")));
        TestConfiguration tc = new TestConfiguration();
        Assertions.assertNull(tc.map, "tc.map is null as it's not configured yet");
        xmlConfiguration.configure(tc);
        Assertions.assertEquals(2, tc.map.size(), "tc.map is has two entries as specified in the XML");
    }

    @Test
    public void testConstructorNamedInjection() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg>arg1</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg>arg3</Arg>  ") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
    }

    @Test
    public void testConstructorNamedInjectionOrdered() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg name=\"second\">arg2</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
    }

    @Test
    public void testConstructorNamedInjectionUnOrdered() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Arg name=\"second\">arg2</Arg>  ") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
    }

    @Test
    public void testConstructorNamedInjectionOrderedMixed() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
    }

    @Test
    public void testConstructorNamedInjectionUnorderedMixed() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg name=\"first\">arg1</Arg>  ") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
    }

    @Test
    public void testNestedConstructorNamedInjection() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg>arg1</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg>arg3</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">") + "      <Arg>arg1</Arg>  ") + "      <Arg>arg2</Arg>  ") + "      <Arg>arg3</Arg>  ") + "    </New>") + "  </Set>") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testNestedConstructorNamedInjectionOrdered() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg name=\"second\">arg2</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">") + "      <Arg name=\"first\">arg1</Arg>  ") + "      <Arg name=\"second\">arg2</Arg>  ") + "      <Arg name=\"third\">arg3</Arg>  ") + "    </New>") + "  </Set>") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testNestedConstructorNamedInjectionUnOrdered() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Arg name=\"second\">arg2</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">") + "      <Arg name=\"first\">arg1</Arg>  ") + "      <Arg name=\"third\">arg3</Arg>  ") + "      <Arg name=\"second\">arg2</Arg>  ") + "    </New>") + "  </Set>") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testNestedConstructorNamedInjectionOrderedMixed() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">") + "      <Arg name=\"first\">arg1</Arg>  ") + "      <Arg>arg2</Arg>  ") + "      <Arg name=\"third\">arg3</Arg>  ") + "    </New>") + "  </Set>") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testArgumentsGetIgnoredMissingDTD() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(new ByteArrayInputStream(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg>arg1</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg>arg3</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">\n") + "      <Arg>arg1</Arg>\n") + "      <Arg>arg2</Arg>\n") + "      <Arg>arg3</Arg>\n") + "    </New>") + "  </Set>") + "</Configure>")).getBytes(StandardCharsets.ISO_8859_1)));
        // XmlConfiguration xmlConfiguration = new XmlConfiguration(url);
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testSetGetIgnoredMissingDTD() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(new ByteArrayInputStream(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\">arg1</Set>  ") + "  <Set name=\"second\">arg2</Set>  ") + "  <Set name=\"third\">arg3</Set>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">\n") + "      <Set name=\"first\">arg1</Set>  ") + "      <Set name=\"second\">arg2</Set>  ") + "      <Set name=\"third\">arg3</Set>  ") + "    </New>") + "  </Set>") + "</Configure>")).getBytes(StandardCharsets.UTF_8)));
        // XmlConfiguration xmlConfiguration = new XmlConfiguration(url);
        DefaultTestConfiguration atc = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    @Test
    public void testNestedConstructorNamedInjectionUnorderedMixed() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + ((((((((((("<Configure class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">" + "  <Arg name=\"third\">arg3</Arg>  ") + "  <Arg>arg2</Arg>  ") + "  <Arg name=\"first\">arg1</Arg>  ") + "  <Set name=\"nested\">  ") + "    <New class=\"org.eclipse.jetty.xml.AnnotatedTestConfiguration\">") + "      <Arg name=\"third\">arg3</Arg>  ") + "      <Arg>arg2</Arg>  ") + "      <Arg name=\"first\">arg1</Arg>  ") + "    </New>") + "  </Set>") + "</Configure>")));
        AnnotatedTestConfiguration atc = ((AnnotatedTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals("arg1", atc.getFirst(), "first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getSecond(), "second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getThird(), "third parameter not wired correctly");
        Assertions.assertEquals("arg1", atc.getNested().getFirst(), "nested first parameter not wired correctly");
        Assertions.assertEquals("arg2", atc.getNested().getSecond(), "nested second parameter not wired correctly");
        Assertions.assertEquals("arg3", atc.getNested().getThird(), "nested third parameter not wired correctly");
    }

    public static class NativeHolder {
        private boolean _boolean;

        private int _integer;

        private float _float;

        public boolean getBoolean() {
            return _boolean;
        }

        public void setBoolean(boolean value) {
            this._boolean = value;
        }

        public int getInteger() {
            return _integer;
        }

        public void setInteger(int integer) {
            _integer = integer;
        }

        public float getFloat() {
            return _float;
        }

        public void setFloat(float f) {
            _float = f;
        }
    }

    @Test
    public void testSetBooleanTrue() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (("<Configure class=\"org.eclipse.jetty.xml.XmlConfigurationTest$NativeHolder\">" + "  <Set name=\"boolean\">true</Set>") + "</Configure>")));
        XmlConfigurationTest.NativeHolder bh = ((XmlConfigurationTest.NativeHolder) (xmlConfiguration.configure()));
        Assertions.assertTrue(bh.getBoolean());
    }

    @Test
    public void testSetBooleanFalse() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (("<Configure class=\"org.eclipse.jetty.xml.XmlConfigurationTest$NativeHolder\">" + "  <Set name=\"boolean\">false</Set>") + "</Configure>")));
        XmlConfigurationTest.NativeHolder bh = ((XmlConfigurationTest.NativeHolder) (xmlConfiguration.configure()));
        Assertions.assertFalse(bh.getBoolean());
    }

    @Test
    public void testSetBadInteger() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (("<Configure class=\"org.eclipse.jetty.xml.XmlConfigurationTest$NativeHolder\">" + "  <Set name=\"integer\">bad</Set>") + "</Configure>")));
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            xmlConfiguration.configure();
        });
    }

    @Test
    public void testSetBadExtraInteger() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (("<Configure class=\"org.eclipse.jetty.xml.XmlConfigurationTest$NativeHolder\">" + "  <Set name=\"integer\">100 bas</Set>") + "</Configure>")));
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            xmlConfiguration.configure();
        });
    }

    @Test
    public void testSetBadFloatInteger() throws Exception {
        XmlConfiguration xmlConfiguration = new XmlConfiguration(("" + (("<Configure class=\"org.eclipse.jetty.xml.XmlConfigurationTest$NativeHolder\">" + "  <Set name=\"integer\">1.5</Set>") + "</Configure>")));
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            xmlConfiguration.configure();
        });
    }

    @Test
    public void testWithMultiplePropertyNamesWithNoPropertyThenDefaultIsChosen() throws Exception {
        // No properties
        String defolt = "baz";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((("" + ("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\"><Property name=\"wibble\" deprecated=\"foo,bar\" default=\"")) + defolt) + "\"/></Set>  ") + "</Configure>"));
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(defolt, config.getFirst());
    }

    @Test
    public void testWithMultiplePropertyNamesWithFirstPropertyThenFirstIsChosen() throws Exception {
        String name = "foo";
        String value = "foo";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((("" + ("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\"><Property name=\"")) + name) + "\" deprecated=\"other,bar\" default=\"baz\"/></Set>  ") + "</Configure>"));
        xmlConfiguration.getProperties().put(name, value);
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(value, config.getFirst());
    }

    @Test
    public void testWithMultiplePropertyNamesWithSecondPropertyThenSecondIsChosen() throws Exception {
        String name = "bar";
        String value = "bar";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((("" + ("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\"><Property name=\"foo\" deprecated=\"")) + name) + "\" default=\"baz\"/></Set>  ") + "</Configure>"));
        xmlConfiguration.getProperties().put(name, value);
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(value, config.getFirst());
    }

    @Test
    public void testWithMultiplePropertyNamesWithDeprecatedThenThirdIsChosen() throws Exception {
        String name = "bar";
        String value = "bar";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((("" + ("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\"><Property name=\"foo\" deprecated=\"other,")) + name) + "\" default=\"baz\"/></Set>  ") + "</Configure>"));
        xmlConfiguration.getProperties().put(name, value);
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(value, config.getFirst());
    }

    @Test
    public void testWithMultiplePropertyNameElementsWithDeprecatedThenThirdIsChosen() throws Exception {
        String name = "bar";
        String value = "bar";
        XmlConfiguration xmlConfiguration = new XmlConfiguration(((((((("" + ((((("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\">") + "  <Property>  ") + "    <Name>foo</Name>") + "    <Deprecated>foo</Deprecated>") + "    <Deprecated>")) + name) + "</Deprecated>") + "    <Default>baz</Default>") + "  </Property>  ") + "  </Set>  ") + "</Configure>"));
        xmlConfiguration.getProperties().put(name, value);
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(value, config.getFirst());
    }

    @Test
    public void testPropertyNotFoundWithPropertyInDefaultValue() throws Exception {
        String name = "bar";
        String value = "bar";
        String defaultValue = "_<Property name=\"bar\"/>_<Property name=\"bar\"/>_";
        String expectedValue = ((("_" + value) + "_") + value) + "_";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((((("" + (((("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\">") + "    <Property>") + "      <Name>not_found</Name>") + "      <Default>")) + defaultValue) + "</Default>") + "    </Property>") + "  </Set>  ") + "</Configure>"));
        xmlConfiguration.getProperties().put(name, value);
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(expectedValue, config.getFirst());
    }

    @Test
    public void testPropertyNotFoundWithPropertyInDefaultValueNotFoundWithDefault() throws Exception {
        String value = "bar";
        XmlConfiguration xmlConfiguration = new XmlConfiguration((((((("" + ((("<Configure class=\"org.eclipse.jetty.xml.DefaultTestConfiguration\">" + "  <Set name=\"first\">") + "    <Property name=\"not_found\">") + "      <Default><Property name=\"also_not_found\" default=\"")) + value) + "\"/></Default>") + "    </Property>") + "  </Set>  ") + "</Configure>"));
        DefaultTestConfiguration config = ((DefaultTestConfiguration) (xmlConfiguration.configure()));
        Assertions.assertEquals(value, config.getFirst());
    }

    @Test
    public void testJettyStandardIdsAndProperties_JettyHome_JettyBase() throws Exception {
        String[] propNames = new String[]{ "jetty.base", "jetty.home" };
        for (String propName : propNames) {
            XmlConfiguration configuration = new XmlConfiguration(((((("" + (("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "  <Set name=\"TestString\">") + "    <Property name=\"")) + propName) + "\"/>") + "  </Set>") + "</Configure>"));
            configuration.setJettyStandardIdsAndProperties(null, null);
            TestConfiguration tc = new TestConfiguration();
            configuration.configure(tc);
            MatcherAssert.assertThat(propName, tc.getTestString(), CoreMatchers.is(CoreMatchers.notNullValue()));
            MatcherAssert.assertThat(propName, tc.getTestString(), CoreMatchers.not(Matchers.startsWith("file:")));
        }
    }

    @Test
    public void testJettyStandardIdsAndProperties_JettyHomeUri_JettyBaseUri() throws Exception {
        String[] propNames = new String[]{ "jetty.base.uri", "jetty.home.uri" };
        for (String propName : propNames) {
            XmlConfiguration configuration = new XmlConfiguration(((((("" + (("<Configure class=\"org.eclipse.jetty.xml.TestConfiguration\">" + "  <Set name=\"TestString\">") + "    <Property name=\"")) + propName) + "\"/>") + "  </Set>") + "</Configure>"));
            configuration.setJettyStandardIdsAndProperties(null, null);
            TestConfiguration tc = new TestConfiguration();
            configuration.configure(tc);
            MatcherAssert.assertThat(propName, tc.getTestString(), CoreMatchers.is(CoreMatchers.notNullValue()));
            MatcherAssert.assertThat(propName, tc.getTestString(), Matchers.startsWith("file:"));
        }
    }
}

