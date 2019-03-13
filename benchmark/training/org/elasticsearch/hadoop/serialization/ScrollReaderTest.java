/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization;


import ConfigurationOptions.ES_OUTPUT_JSON;
import ConfigurationOptions.ES_READ_FIELD_AS_ARRAY_INCLUDE;
import ConfigurationOptions.ES_READ_METADATA;
import ConfigurationOptions.ES_READ_METADATA_FIELD;
import DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLERS;
import ScrollReader.Scroll;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.elasticsearch.hadoop.EsHadoopException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.handler.ErrorCollector;
import org.elasticsearch.hadoop.handler.EsHadoopAbortHandlerException;
import org.elasticsearch.hadoop.handler.HandlerResult;
import org.elasticsearch.hadoop.rest.EsHadoopParsingException;
import org.elasticsearch.hadoop.serialization.builder.JdkValueReader;
import org.elasticsearch.hadoop.serialization.dto.mapping.MappingSet;
import org.elasticsearch.hadoop.serialization.handler.read.DeserializationErrorHandler;
import org.elasticsearch.hadoop.serialization.handler.read.DeserializationFailure;
import org.elasticsearch.hadoop.serialization.handler.read.impl.DeserializationHandlerLoader;
import org.elasticsearch.hadoop.util.ObjectUtils;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.Matchers;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ScrollReaderTest {
    private boolean readMetadata = false;

    private final String metadataField;

    private final boolean readAsJson = false;

    private ScrollReader reader;

    public ScrollReaderTest(boolean readMetadata, String metadataField) {
        this.readMetadata = readMetadata;
        this.metadataField = metadataField;
        reader = new ScrollReader(getScrollReaderCfg());
    }

    @Test
    public void testScrollWithFields() throws IOException {
        InputStream stream = getClass().getResourceAsStream(scrollData("fields"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(3, read.size());
        Object[] objects = read.get(0);
        Assert.assertTrue(((Map) (objects[1])).containsKey("fields"));
    }

    @Test
    public void testScrollWithMatchedQueries() throws IOException {
        InputStream stream = getClass().getResourceAsStream(scrollData("matched-queries"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(3, read.size());
        Object[] objects = read.get(0);
        Assert.assertTrue(((Map) (objects[1])).containsKey("foo"));
    }

    @Test
    public void testScrollWithNestedFields() throws IOException {
        MappingSet fl = getMappingSet("source");
        ScrollReaderConfigBuilder scrollReaderConfig = getScrollReaderCfg().setResolvedMapping(fl.getResolvedView());
        reader = new ScrollReader(scrollReaderConfig);
        InputStream stream = getClass().getResourceAsStream(scrollData("source"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(3, read.size());
        Object[] objects = read.get(0);
        Assert.assertTrue(((Map) (objects[1])).containsKey("source"));
        Map map = ((Map) (read.get(2)[1]));
        Map number = ((Map) (map.get("links")));
        Object value = number.get("number");
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof Short));
        Assert.assertEquals(Short.valueOf(((short) (125))), value);
    }

    @Test
    public void testScrollWithSource() throws IOException {
        reader = new ScrollReader(getScrollReaderCfg());
        InputStream stream = getClass().getResourceAsStream(scrollData("source"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(3, read.size());
        Object[] objects = read.get(0);
        Assert.assertTrue(((Map) (objects[1])).containsKey("source"));
    }

    @Test
    public void testScrollWithoutSource() throws IOException {
        reader = new ScrollReader(getScrollReaderCfg());
        InputStream stream = getClass().getResourceAsStream(scrollData("empty-source"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(2, read.size());
        Object[] objects = read.get(0);
        if (readMetadata) {
            Assert.assertTrue(((Map) (objects[1])).containsKey(metadataField));
        } else {
            Assert.assertTrue(((Map) (objects[1])).isEmpty());
        }
    }

    @Test
    public void testScrollMultiValueList() throws IOException {
        reader = new ScrollReader(getScrollReaderCfg());
        InputStream stream = getClass().getResourceAsStream(scrollData("list"));
        List<Object[]> read = reader.read(stream).getHits();
        Assert.assertEquals(1, read.size());
        Object[] objects = read.get(0);
        Map map = ((Map) (read.get(0)[1]));
        List links = ((List) (map.get("links")));
        Assert.assertTrue(links.contains(null));
    }

    @Test
    public void testScrollWithJoinField() throws Exception {
        MappingSet mappings = getMappingSet("join");
        // Make our own scroll reader, that ignores unmapped values like the rest of the code
        ScrollReaderConfigBuilder scrollCfg = getScrollReaderCfg().setResolvedMapping(mappings.getResolvedView());
        ScrollReader myReader = new ScrollReader(scrollCfg);
        InputStream stream = getClass().getResourceAsStream(scrollData("join"));
        List<Object[]> read = myReader.read(stream).getHits();
        Assert.assertEquals(7, read.size());
        // Elastic
        {
            Object[] row1 = read.get(0);
            Assert.assertTrue(((Map) (row1[1])).containsKey("id"));
            Assert.assertEquals("1", ((Map) (row1[1])).get("id"));
            Assert.assertTrue(((Map) (row1[1])).containsKey("company"));
            Assert.assertEquals("Elastic", ((Map) (row1[1])).get("company"));
            Assert.assertTrue(((Map) (row1[1])).containsKey("joiner"));
            Object joinfield1 = ((Map) (row1[1])).get("joiner");
            Assert.assertTrue((joinfield1 instanceof Map));
            Assert.assertTrue(((Map) (joinfield1)).containsKey("name"));
            Assert.assertEquals("company", ((Map) (joinfield1)).get("name"));
            Assert.assertFalse(((Map) (joinfield1)).containsKey("parent"));
        }
        // kimchy
        {
            Object[] row2 = read.get(1);
            Assert.assertTrue(((Map) (row2[1])).containsKey("id"));
            Assert.assertEquals("10", ((Map) (row2[1])).get("id"));
            Assert.assertTrue(((Map) (row2[1])).containsKey("name"));
            Assert.assertEquals("kimchy", ((Map) (row2[1])).get("name"));
            Assert.assertTrue(((Map) (row2[1])).containsKey("joiner"));
            Object joinfield2 = ((Map) (row2[1])).get("joiner");
            Assert.assertTrue((joinfield2 instanceof Map));
            Assert.assertTrue(((Map) (joinfield2)).containsKey("name"));
            Assert.assertEquals("employee", ((Map) (joinfield2)).get("name"));
            Assert.assertTrue(((Map) (joinfield2)).containsKey("parent"));
            Assert.assertEquals("1", ((Map) (joinfield2)).get("parent"));
        }
        // Fringe Cafe
        {
            Object[] row3 = read.get(2);
            Assert.assertTrue(((Map) (row3[1])).containsKey("id"));
            Assert.assertEquals("2", ((Map) (row3[1])).get("id"));
            Assert.assertTrue(((Map) (row3[1])).containsKey("company"));
            Assert.assertEquals("Fringe Cafe", ((Map) (row3[1])).get("company"));
            Assert.assertTrue(((Map) (row3[1])).containsKey("joiner"));
            Object joinfield3 = ((Map) (row3[1])).get("joiner");
            Assert.assertTrue((joinfield3 instanceof Map));
            Assert.assertTrue(((Map) (joinfield3)).containsKey("name"));
            Assert.assertEquals("company", ((Map) (joinfield3)).get("name"));
            Assert.assertFalse(((Map) (joinfield3)).containsKey("parent"));
        }
        // April Ryan
        {
            Object[] row4 = read.get(3);
            Assert.assertTrue(((Map) (row4[1])).containsKey("id"));
            Assert.assertEquals("20", ((Map) (row4[1])).get("id"));
            Assert.assertTrue(((Map) (row4[1])).containsKey("name"));
            Assert.assertEquals("April Ryan", ((Map) (row4[1])).get("name"));
            Assert.assertTrue(((Map) (row4[1])).containsKey("joiner"));
            Object joinfield4 = ((Map) (row4[1])).get("joiner");
            Assert.assertTrue((joinfield4 instanceof Map));
            Assert.assertTrue(((Map) (joinfield4)).containsKey("name"));
            Assert.assertEquals("employee", ((Map) (joinfield4)).get("name"));
            Assert.assertTrue(((Map) (joinfield4)).containsKey("parent"));
            Assert.assertEquals("2", ((Map) (joinfield4)).get("parent"));
        }
        // Charlie
        {
            Object[] row5 = read.get(4);
            Assert.assertTrue(((Map) (row5[1])).containsKey("id"));
            Assert.assertEquals("21", ((Map) (row5[1])).get("id"));
            Assert.assertTrue(((Map) (row5[1])).containsKey("name"));
            Assert.assertEquals("Charlie", ((Map) (row5[1])).get("name"));
            Assert.assertTrue(((Map) (row5[1])).containsKey("joiner"));
            Object joinfield5 = ((Map) (row5[1])).get("joiner");
            Assert.assertTrue((joinfield5 instanceof Map));
            Assert.assertTrue(((Map) (joinfield5)).containsKey("name"));
            Assert.assertEquals("employee", ((Map) (joinfield5)).get("name"));
            Assert.assertTrue(((Map) (joinfield5)).containsKey("parent"));
            Assert.assertEquals("2", ((Map) (joinfield5)).get("parent"));
        }
        // WATI corp
        {
            Object[] row6 = read.get(5);
            Assert.assertTrue(((Map) (row6[1])).containsKey("id"));
            Assert.assertEquals("3", ((Map) (row6[1])).get("id"));
            Assert.assertTrue(((Map) (row6[1])).containsKey("company"));
            Assert.assertEquals("WATIcorp", ((Map) (row6[1])).get("company"));
            Assert.assertTrue(((Map) (row6[1])).containsKey("joiner"));
            Object joinfield6 = ((Map) (row6[1])).get("joiner");
            Assert.assertTrue((joinfield6 instanceof Map));
            Assert.assertTrue(((Map) (joinfield6)).containsKey("name"));
            Assert.assertEquals("company", ((Map) (joinfield6)).get("name"));
            Assert.assertFalse(((Map) (joinfield6)).containsKey("parent"));
        }
        // Alvin Peats
        {
            Object[] row7 = read.get(6);
            Assert.assertTrue(((Map) (row7[1])).containsKey("id"));
            Assert.assertEquals("30", ((Map) (row7[1])).get("id"));
            Assert.assertTrue(((Map) (row7[1])).containsKey("name"));
            Assert.assertEquals("Alvin Peats", ((Map) (row7[1])).get("name"));
            Assert.assertTrue(((Map) (row7[1])).containsKey("joiner"));
            Object joinfield5 = ((Map) (row7[1])).get("joiner");
            Assert.assertTrue((joinfield5 instanceof Map));
            Assert.assertTrue(((Map) (joinfield5)).containsKey("name"));
            Assert.assertEquals("employee", ((Map) (joinfield5)).get("name"));
            Assert.assertTrue(((Map) (joinfield5)).containsKey("parent"));
            Assert.assertEquals("3", ((Map) (joinfield5)).get("parent"));
        }
    }

    @Test
    public void testScrollWithMultipleTypes() throws Exception {
        MappingSet mappings = getLegacyMappingSet("multi-type");
        // Make our own scroll reader, that ignores unmapped values like the rest of the code
        ScrollReaderConfigBuilder scrollCfg = getScrollReaderCfg().setResolvedMapping(mappings.getResolvedView());
        ScrollReader myReader = new ScrollReader(scrollCfg);
        InputStream stream = getClass().getResourceAsStream(scrollData("multi-type"));
        List<Object[]> read = myReader.read(stream).getHits();
        Assert.assertEquals(3, read.size());
        Object[] row1 = read.get(0);
        Assert.assertTrue(((Map) (row1[1])).containsKey("field1"));
        Assert.assertEquals("value1", ((Map) (row1[1])).get("field1"));
        Assert.assertTrue(((Map) (row1[1])).containsKey("field2"));
        Assert.assertEquals("value2", ((Map) (row1[1])).get("field2"));
        Object[] row2 = read.get(1);
        Assert.assertTrue(((Map) (row2[1])).containsKey("field3"));
        Assert.assertEquals("value3", ((Map) (row2[1])).get("field3"));
        Object[] row3 = read.get(2);
        Assert.assertTrue(((Map) (row3[1])).containsKey("field4"));
        Assert.assertEquals("value4", ((Map) (row3[1])).get("field4"));
    }

    @Test
    public void testScrollWithNestedFieldAndArrayIncludes() throws IOException {
        MappingSet mappings = getMappingSet("nested-data");
        InputStream stream = getClass().getResourceAsStream(scrollData("nested-data"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_FIELD_AS_ARRAY_INCLUDE, "a.b.d:2,a.b.f");
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        // First document in scroll has a single object on the nested field.
        Assert.assertEquals("yo", JsonUtils.query("a").get("b").get(0).get("c").apply(scroll.getHits().get(0)[1]));
        // Second document in scroll has a nested document field
        Assert.assertEquals("hello", JsonUtils.query("a").get("b").get(0).get("c").apply(scroll.getHits().get(1)[1]));
        // Third document in scroll has a nested document with an array field. This array field should be padded to meet
        // the configured array depth in the include setting above. This should be independent of the fact that the nested
        // object will be added to a different array further up the call stack.
        Assert.assertEquals("howdy", JsonUtils.query("a").get("b").get(0).get("d").get(0).get(0).apply(scroll.getHits().get(2)[1]));
        Assert.assertEquals("partner", JsonUtils.query("a").get("b").get(0).get("d").get(0).get(1).apply(scroll.getHits().get(2)[1]));
        // Fourth document has some nested arrays next to more complex datatypes
        Assert.assertEquals(1L, JsonUtils.query("a").get("b").get(0).get("f").get(0).apply(scroll.getHits().get(3)[1]));
        Assert.assertEquals(ISODateTimeFormat.dateParser().parseDateTime("2015-01-01").toDate(), JsonUtils.query("a").get("b").get(0).get("e").apply(scroll.getHits().get(3)[1]));
        Assert.assertEquals(3L, JsonUtils.query("a").get("b").get(1).get("f").get(0).apply(scroll.getHits().get(3)[1]));
    }

    @Test
    public void testScrollWithObjectFieldAndArrayIncludes() throws IOException {
        MappingSet mappings = getMappingSet("object-fields");
        InputStream stream = getClass().getResourceAsStream(scrollData("object-fields"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_FIELD_AS_ARRAY_INCLUDE, "a.b.d:2,a.b,a.b.f");
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        // First document in scroll has a single object on the nested field.
        Assert.assertEquals("yo", JsonUtils.query("a").get("b").get(0).get("c").apply(scroll.getHits().get(0)[1]));
        // Second document in scroll has a nested document field
        Assert.assertEquals("hello", JsonUtils.query("a").get("b").get(0).get("c").apply(scroll.getHits().get(1)[1]));
        // Third document in scroll has a nested document with an array field. This array field should be padded to meet
        // the configured array depth in the include setting above. This should be independent of the fact that the nested
        // object will be added to a different array further up the call stack.
        Assert.assertEquals("howdy", JsonUtils.query("a").get("b").get(0).get("d").get(0).get(0).apply(scroll.getHits().get(2)[1]));
        Assert.assertEquals("partner", JsonUtils.query("a").get("b").get(0).get("d").get(0).get(1).apply(scroll.getHits().get(2)[1]));
        // Fourth document has some nested arrays next to more complex datatypes
        Assert.assertEquals(1L, JsonUtils.query("a").get("b").get(0).get("f").get(0).apply(scroll.getHits().get(3)[1]));
        Assert.assertEquals(ISODateTimeFormat.dateParser().parseDateTime("2015-01-01").toDate(), JsonUtils.query("a").get("b").get(0).get("e").apply(scroll.getHits().get(3)[1]));
        Assert.assertEquals(3L, JsonUtils.query("a").get("b").get(1).get("f").get(0).apply(scroll.getHits().get(3)[1]));
    }

    @Test
    public void testScrollWithNestedArrays() throws IOException {
        MappingSet mappings = getMappingSet("nested-list");
        InputStream stream = getClass().getResourceAsStream(scrollData("nested-list"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_FIELD_AS_ARRAY_INCLUDE, "a:3");
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        // Case of already correctly nested array data
        Assert.assertEquals(1L, JsonUtils.query("a").get(0).get(0).get(0).apply(scroll.getHits().get(0)[1]));
        // Case of insufficiently nested array data
        Assert.assertEquals(9L, JsonUtils.query("a").get(0).get(0).get(0).apply(scroll.getHits().get(1)[1]));
        // Case of singleton data that is not nested in ANY array levels.
        Assert.assertEquals(10L, JsonUtils.query("a").get(0).get(0).get(0).apply(scroll.getHits().get(2)[1]));
    }

    @Test(expected = EsHadoopParsingException.class)
    public void testScrollWithBreakOnInvalidMapping() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        reader.read(stream);
        Assert.fail("Should not be able to parse string as long");
    }

    @Test(expected = EsHadoopException.class)
    public void testScrollWithThrowingErrorHandler() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "throw");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".throw"), ScrollReaderTest.ExceptionThrowingHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        reader.read(stream);
        Assert.fail("Should not be able to parse string as long");
    }

    @Test(expected = EsHadoopParsingException.class)
    public void testScrollWithThrowingAbortErrorHandler() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "throw");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".throw"), ScrollReaderTest.AbortingExceptionThrowingHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        reader.read(stream);
        Assert.fail("Should not be able to parse string as long");
    }

    @Test(expected = EsHadoopException.class)
    public void testScrollWithNeverendingHandler() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "evil");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".evil"), ScrollReaderTest.NeverSurrenderHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        reader.read(stream);
        Assert.fail("Should not be able to parse string as long");
    }

    @Test
    public void testScrollWithIgnoringHandler() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "skipskipskip");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".skipskipskip"), ScrollReaderTest.NothingToSeeHereHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        Assert.assertThat(scroll.getTotalHits(), Matchers.equalTo(196L));
        Assert.assertThat(scroll.getHits(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testScrollWithHandlersThatPassWithMessages() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "marco,polo,skip");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".marco"), ScrollReaderTest.MarcoHandler.class.getName());
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".polo"), ScrollReaderTest.PoloHandler.class.getName());
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".skip"), ScrollReaderTest.NothingToSeeHereHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        Assert.assertThat(scroll.getTotalHits(), Matchers.equalTo(196L));
        Assert.assertThat(scroll.getHits(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testScrollWithHandlersThatCorrectsError() throws IOException {
        MappingSet mappings = getMappingSet("numbers-as-strings");
        InputStream stream = getClass().getResourceAsStream(scrollData("numbers-as-strings"));
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_READ_METADATA, ("" + (readMetadata)));
        testSettings.setProperty(ES_READ_METADATA_FIELD, ("" + (metadataField)));
        testSettings.setProperty(ES_OUTPUT_JSON, ("" + (readAsJson)));
        testSettings.setProperty(ES_READ_DATA_ERROR_HANDLERS, "fix");
        testSettings.setProperty(((DeserializationHandlerLoader.ES_READ_DATA_ERROR_HANDLER) + ".fix"), ScrollReaderTest.CorrectingHandler.class.getName());
        JdkValueReader valueReader = ObjectUtils.instantiate(JdkValueReader.class.getName(), testSettings);
        ScrollReader reader = new ScrollReader(ScrollReaderConfigBuilder.builder(valueReader, mappings.getResolvedView(), testSettings));
        ScrollReader.Scroll scroll = reader.read(stream);
        Assert.assertThat(scroll.getTotalHits(), Matchers.equalTo(196L));
        Assert.assertThat(scroll.getHits().size(), Matchers.equalTo(1));
        Assert.assertEquals(4L, JsonUtils.query("number").apply(scroll.getHits().get(0)[1]));
    }

    /**
     * Case: Handler throws random Exceptions
     * Outcome: Processing fails fast.
     */
    public static class ExceptionThrowingHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            throw new IllegalArgumentException("Whoopsie!");
        }
    }

    /**
     * Case: Handler throws exception, wrapped in abort based exception
     * Outcome: Exception is collected and used as the reason for aborting that specific document.
     */
    public static class AbortingExceptionThrowingHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            throw new EsHadoopAbortHandlerException("Abort the handler!!");
        }
    }

    /**
     * Case: Evil or incorrect handler causes infinite loop.
     */
    public static class NeverSurrenderHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            return collector.retry();// NEVER GIVE UP

        }
    }

    /**
     * Case: Handler acks the failure and expects the processing to move along.
     */
    public static class NothingToSeeHereHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            return HandlerResult.HANDLED;// Move along.

        }
    }

    /**
     * Case: Handler passes on the failure, setting a "message for why"
     */
    public static class MarcoHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            return collector.pass("MARCO!");
        }
    }

    /**
     * Case: Handler checks the pass messages and ensures that they have been set.
     * Outcome: If set, it acks and continues, and if not, it aborts.
     */
    public static class PoloHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            if (entry.previousHandlerMessages().contains("MARCO!")) {
                return collector.pass("POLO!");
            }
            throw new EsHadoopAbortHandlerException("FISH OUT OF WATER!");
        }
    }

    /**
     * Case: Handler somehow knows how to fix data.
     * Outcome: Data is deserialized correctly.
     */
    public static class CorrectingHandler extends DeserializationErrorHandler {
        @Override
        public HandlerResult onError(DeserializationFailure entry, ErrorCollector<byte[]> collector) throws Exception {
            entry.getException().printStackTrace();
            return collector.retry("{\"_index\":\"pig\",\"_type\":\"tupleartists\",\"_id\":\"23hrGo7VRCyao8lB9Uu5Kw\",\"_score\":0.0,\"_source\":{\"number\":4}}".getBytes());
        }
    }
}

