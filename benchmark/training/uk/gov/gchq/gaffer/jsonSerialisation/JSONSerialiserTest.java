/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.jsonSerialisation;


import JSONSerialiser.JSON_SERIALISER_CLASS_KEY;
import JSONSerialiser.JSON_SERIALISER_MODULES;
import JSONSerialiser.STRICT_JSON;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.CommonConstants;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.gaffer.serialisation.ParameterisedTestObject;
import uk.gov.gchq.gaffer.serialisation.SimpleTestObject;


public class JSONSerialiserTest {
    private final Pair<Object, byte[]>[] historicSerialisationPairs;

    @SuppressWarnings("unchecked")
    public JSONSerialiserTest() {
        ParameterisedTestObject<Object> paramTest = new ParameterisedTestObject<>();
        paramTest.setX("Test");
        paramTest.setK(2);
        SimpleTestObject simpleTestObject = new SimpleTestObject();
        simpleTestObject.setX("Test");
        this.historicSerialisationPairs = new Pair[]{ new Pair(simpleTestObject, new byte[]{ 123, 34, 120, 34, 58, 34, 84, 101, 115, 116, 34, 125 }), new Pair(paramTest, new byte[]{ 123, 34, 120, 34, 58, 34, 84, 101, 115, 116, 34, 44, 34, 107, 34, 58, 50, 125 }) };
    }

    @Test
    public void testPrimitiveSerialisation() throws IOException {
        byte[] b = JSONSerialiser.serialise(2);
        Object o = JSONSerialiser.deserialise(b, Object.class);
        Assert.assertEquals(Integer.class, o.getClass());
        Assert.assertEquals(2, o);
    }

    @Test
    public void canHandleUnParameterisedDAO() throws SerialisationException {
        Assert.assertTrue(JSONSerialiser.canHandle(SimpleTestObject.class));
    }

    @Test
    public void testDAOSerialisation() throws SerialisationException {
        SimpleTestObject test = new SimpleTestObject();
        test.setX("Test");
        byte[] b = JSONSerialiser.serialise(test);
        Object o = JSONSerialiser.deserialise(b, SimpleTestObject.class);
        Assert.assertEquals(SimpleTestObject.class, o.getClass());
        Assert.assertEquals("Test", ((SimpleTestObject) (o)).getX());
    }

    @Test
    public void shouldNotPrettyPrintByDefaultWhenSerialising() throws SerialisationException {
        SimpleTestObject test = new SimpleTestObject();
        test.setX("TestValue1");
        byte[] bytes = JSONSerialiser.serialise(test);
        Assert.assertEquals("{\"x\":\"TestValue1\"}", new String(bytes));
    }

    @Test
    public void shouldPrettyPrintWhenSerialisingAndSetToPrettyPrint() throws SerialisationException {
        SimpleTestObject test = new SimpleTestObject();
        test.setX("TestValue1");
        byte[] bytes = JSONSerialiser.serialise(test, true);
        JsonAssert.assertEquals(String.format("{%n  \"x\" : \"TestValue1\"%n}"), new String(bytes));
    }

    @Test
    public void canHandleParameterisedDAO() throws SerialisationException {
        Assert.assertTrue(JSONSerialiser.canHandle(ParameterisedTestObject.class));
    }

    @Test
    public void testParameterisedDAOSerialisation() throws SerialisationException {
        ParameterisedTestObject<Integer> test = new ParameterisedTestObject<>();
        test.setX("Test");
        test.setK(2);
        byte[] b = JSONSerialiser.serialise(test);
        Object o = JSONSerialiser.deserialise(b, ParameterisedTestObject.class);
        Assert.assertEquals(ParameterisedTestObject.class, o.getClass());
        Assert.assertEquals("Test", ((ParameterisedTestObject) (o)).getX());
        Assert.assertEquals(Integer.class, ((ParameterisedTestObject) (o)).getK().getClass());
        Assert.assertEquals(2, ((ParameterisedTestObject) (o)).getK());
    }

    @Test
    public void testParameterisedDAOTypeRefDeserialisation() throws SerialisationException {
        ParameterisedTestObject<Integer> test = new ParameterisedTestObject<>();
        test.setX("Test");
        test.setK(2);
        byte[] b = JSONSerialiser.serialise(test);
        ParameterisedTestObject<Integer> o = JSONSerialiser.deserialise(b, new TypeReference<ParameterisedTestObject<Integer>>() {});
        Assert.assertEquals("Test", o.getX());
        Assert.assertEquals(Integer.valueOf(2), o.getK());
    }

    @Test
    public void testParameterisedDeserialisationOfComplexObject() throws SerialisationException {
        SimpleTestObject test = new SimpleTestObject();
        test.setX("Test");
        byte[] b = JSONSerialiser.serialise(test);
        SimpleTestObject o = JSONSerialiser.deserialise(b, SimpleTestObject.class);
        Assert.assertEquals(SimpleTestObject.class, o.getClass());
        Assert.assertEquals("Test", o.getX());
    }

    @Test
    public void testParameterisedDeserialisationOfParameterisedComplexObject() throws SerialisationException {
        ParameterisedTestObject<Integer> test = new ParameterisedTestObject<>();
        test.setX("Test");
        test.setK(2);
        byte[] b = JSONSerialiser.serialise(test);
        ParameterisedTestObject o = JSONSerialiser.deserialise(b, ParameterisedTestObject.class);
        Assert.assertEquals(ParameterisedTestObject.class, o.getClass());
        Assert.assertEquals("Test", o.getX());
        Assert.assertEquals(Integer.class, o.getK().getClass());
        Assert.assertEquals(2, o.getK());
    }

    @Test(expected = SerialisationException.class)
    public void testParameterisedDeserialisationOfComplexObjectToIncorrectType() throws SerialisationException {
        SimpleTestObject test = new SimpleTestObject();
        test.setX("Test");
        byte[] b = JSONSerialiser.serialise(test);
        JSONSerialiser.deserialise(b, Integer.class);
    }

    @Test
    public void shouldSerialiseObjectWithoutFieldX() throws Exception {
        // Given
        final SimpleTestObject obj = new SimpleTestObject();
        // When
        final String json = new String(JSONSerialiser.serialise(obj, "x"), CommonConstants.UTF_8);
        // Then
        Assert.assertFalse(json.contains("x"));
    }

    @Test
    public void shouldSerialiseObjectWithFieldX() throws Exception {
        // Given
        final SimpleTestObject obj = new SimpleTestObject();
        // When
        final String json = new String(JSONSerialiser.serialise(obj), CommonConstants.UTF_8);
        // Then
        Assert.assertTrue(json.contains("x"));
    }

    @Test
    public void shouldSerialiseWithHistoricValues() throws Exception {
        Assert.assertNotNull(historicSerialisationPairs);
        for (final Pair<Object, byte[]> pair : historicSerialisationPairs) {
            serialiseFirst(pair);
            deserialiseSecond(pair);
        }
    }

    @Test
    public void shouldThrowExceptionWhenUpdateInstanceWithInvalidClassName() throws Exception {
        // Given
        System.setProperty(JSON_SERIALISER_CLASS_KEY, "invalidClassName");
        // When / Then
        try {
            JSONSerialiser.update();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("invalidClassName"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenUpdateInstanceWithInvalidModuleClass() throws Exception {
        // Given
        System.setProperty(JSON_SERIALISER_MODULES, "module1");
        // When / Then
        try {
            JSONSerialiser.update();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("module1"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenUpdateInstanceWithInvalidModulesValue() throws Exception {
        // Given
        final String invalidValue = ((JSONSerialiserTest.TestCustomJsonModules1.class.getName()) + "-") + (JSONSerialiserTest.TestCustomJsonModules2.class.getName());
        System.setProperty(JSON_SERIALISER_MODULES, invalidValue);
        // When / Then
        try {
            JSONSerialiser.update();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(invalidValue));
        }
    }

    @Test
    public void shouldUpdateInstanceWithCustomSerialiser() throws Exception {
        // Given
        JSONSerialiserTest.TestCustomJsonSerialiser1.mapper = Mockito.mock(ObjectMapper.class);
        System.setProperty(JSON_SERIALISER_CLASS_KEY, JSONSerialiserTest.TestCustomJsonSerialiser1.class.getName());
        // When
        JSONSerialiser.update();
        // Then
        Assert.assertEquals(JSONSerialiserTest.TestCustomJsonSerialiser1.class, JSONSerialiser.getInstance().getClass());
        Assert.assertSame(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper, JSONSerialiser.getMapper());
    }

    @Test
    public void shouldUpdateInstanceWithCustomModule() throws Exception {
        // Given
        final JsonSerializer<String> serialiser = Mockito.mock(JsonSerializer.class);
        JSONSerialiserTest.TestCustomJsonModules1.modules = Collections.singletonList(new SimpleModule("module1", new Version(1, 0, 0, null, null, null)).addSerializer(String.class, serialiser));
        System.setProperty(JSON_SERIALISER_MODULES, JSONSerialiserTest.TestCustomJsonModules1.class.getName());
        // When
        JSONSerialiser.update();
        // Then
        Assert.assertEquals(JSONSerialiser.class, JSONSerialiser.getInstance().getClass());
        JSONSerialiser.serialise("test");
        Mockito.verify(serialiser).serialize(Mockito.eq("test"), Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldUpdateInstanceWithCustomProperties() throws Exception {
        // Given
        JSONSerialiserTest.TestCustomJsonSerialiser1.mapper = Mockito.mock(ObjectMapper.class);
        System.setProperty(JSON_SERIALISER_CLASS_KEY, JSONSerialiserTest.TestCustomJsonSerialiser1.class.getName());
        JSONSerialiserTest.TestCustomJsonModules1.modules = Arrays.asList(Mockito.mock(Module.class), Mockito.mock(Module.class));
        JSONSerialiserTest.TestCustomJsonModules2.modules = Arrays.asList(Mockito.mock(Module.class), Mockito.mock(Module.class));
        System.setProperty(JSON_SERIALISER_MODULES, (((JSONSerialiserTest.TestCustomJsonModules1.class.getName()) + ",") + (JSONSerialiserTest.TestCustomJsonModules2.class.getName())));
        System.setProperty(STRICT_JSON, "false");
        // When
        JSONSerialiser.update();
        // Then
        Assert.assertEquals(JSONSerialiserTest.TestCustomJsonSerialiser1.class, JSONSerialiser.getInstance().getClass());
        Assert.assertSame(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper, JSONSerialiser.getMapper());
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper).registerModules(JSONSerialiserTest.TestCustomJsonModules1.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper).registerModules(JSONSerialiserTest.TestCustomJsonModules2.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldUpdateInstanceTwiceWithCustomProperties() throws Exception {
        // Given
        JSONSerialiserTest.TestCustomJsonSerialiser1.mapper = Mockito.mock(ObjectMapper.class);
        JSONSerialiserTest.TestCustomJsonSerialiser2.mapper = Mockito.mock(ObjectMapper.class);
        JSONSerialiserTest.TestCustomJsonModules1.modules = Arrays.asList(Mockito.mock(Module.class), Mockito.mock(Module.class));
        JSONSerialiserTest.TestCustomJsonModules2.modules = Arrays.asList(Mockito.mock(Module.class), Mockito.mock(Module.class));
        // When - initial update
        JSONSerialiser.update(JSONSerialiserTest.TestCustomJsonSerialiser1.class.getName(), JSONSerialiserTest.TestCustomJsonModules1.class.getName(), false);
        // Then
        Assert.assertEquals(JSONSerialiserTest.TestCustomJsonSerialiser1.class, JSONSerialiser.getInstance().getClass());
        Assert.assertSame(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper, JSONSerialiser.getMapper());
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper).registerModules(JSONSerialiserTest.TestCustomJsonModules1.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper, Mockito.never()).registerModules(JSONSerialiserTest.TestCustomJsonModules2.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // When - second update
        JSONSerialiser.update(JSONSerialiserTest.TestCustomJsonSerialiser2.class.getName(), JSONSerialiserTest.TestCustomJsonModules2.class.getName(), true);
        // Then
        Assert.assertEquals(JSONSerialiserTest.TestCustomJsonSerialiser2.class, JSONSerialiser.getInstance().getClass());
        Assert.assertSame(JSONSerialiserTest.TestCustomJsonSerialiser2.mapper, JSONSerialiser.getMapper());
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser2.mapper).registerModules(JSONSerialiserTest.TestCustomJsonModules1.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser2.mapper).registerModules(JSONSerialiserTest.TestCustomJsonModules2.modules);
        Mockito.verify(JSONSerialiserTest.TestCustomJsonSerialiser2.mapper).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Test
    public void shouldDeserialiseClassWithUnknownFields() throws Exception {
        // Given
        JSONSerialiser.update(null, null, false);
        // When
        final JSONSerialiserTest.TestPojo pojo = JSONSerialiser.deserialise("{\"field\": \"value\", \"unknown\": \"otherValue\"}", JSONSerialiserTest.TestPojo.class);
        // Then
        Assert.assertEquals("value", pojo.field);
    }

    @Test
    public void shouldThrowExceptionWhenDeserialiseClassWithUnknownFieldsWhenStrict() {
        // Given
        JSONSerialiser.update(null, null, true);
        // When / Then
        try {
            JSONSerialiser.deserialise("{\"field\": \"value\", \"unknown\": \"otherValue\"}", JSONSerialiserTest.TestPojo.class);
            Assert.fail("Exception expected");
        } catch (final SerialisationException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("Unrecognized field \"unknown\""));
        }
    }

    public static final class TestCustomJsonSerialiser1 extends JSONSerialiser {
        public static ObjectMapper mapper;

        public TestCustomJsonSerialiser1() {
            super(JSONSerialiserTest.TestCustomJsonSerialiser1.mapper);
        }
    }

    public static final class TestCustomJsonSerialiser2 extends JSONSerialiser {
        public static ObjectMapper mapper;

        public TestCustomJsonSerialiser2() {
            super(JSONSerialiserTest.TestCustomJsonSerialiser2.mapper);
        }
    }

    public static final class TestCustomJsonModules1 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return JSONSerialiserTest.TestCustomJsonModules1.modules;
        }
    }

    public static final class TestCustomJsonModules2 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return JSONSerialiserTest.TestCustomJsonModules2.modules;
        }
    }

    private static final class TestPojo {
        public String field;
    }
}

