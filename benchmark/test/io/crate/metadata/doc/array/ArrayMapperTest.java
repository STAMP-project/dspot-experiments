/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.metadata.doc.array;


import ArrayMapper.CONTENT_TYPE;
import ArrayMapper.INNER_TYPE;
import ParseContext.Document;
import XContentType.JSON;
import io.crate.Constants;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.index.IndexableField;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.mapper.ArrayMapper;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.KeywordFieldMapper;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.Mapping;
import org.elasticsearch.index.mapper.ObjectArrayMapper;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.mapper.ParsedDocument;
import org.elasticsearch.index.mapper.SourceToParse;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ArrayMapperTest extends CrateDummyClusterServiceUnitTest {
    public static final String INDEX = "my_index";

    public static final String TYPE = Constants.DEFAULT_MAPPING_TYPE;

    @Test
    public void testSimpleArrayMapping() throws Exception {
        // @formatter:off
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "keyword").endObject().endObject().endObject().endObject().endObject());
        // @formatter:on
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        assertThat(mapper.mappers().getMapper("array_field"), Matchers.is(Matchers.instanceOf(ArrayMapper.class)));
        BytesReference bytesReference = BytesReference.bytes(JsonXContent.contentBuilder().startObject().array("array_field", "a", "b", "c").endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        assertThat(((doc.dynamicMappingsUpdate()) == null), Matchers.is(true));
        assertThat(doc.docs().size(), Matchers.is(1));
        ParseContext.Document fields = doc.docs().get(0);
        Set<String> values = ArrayMapperTest.uniqueValuesFromFields(fields, "array_field");
        assertThat(values, Matchers.containsInAnyOrder("a", "b", "c"));
        assertThat(mapper.mappingSource().string(), Matchers.is(("{\"default\":{" + (((((((("\"properties\":{" + "\"array_field\":{") + "\"type\":\"array\",") + "\"inner\":{") + "\"type\":\"keyword\"") + "}") + "}") + "}") + "}}"))));
    }

    @Test
    public void testInvalidArraySimpleField() throws Exception {
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject("type").startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "string").endObject().endObject().endObject().endObject().endObject());
        expectedException.expect(MapperParsingException.class);
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
    }

    @Test
    public void testInvalidArrayNonConvertableType() throws Exception {
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "double").endObject().endObject().endObject().endObject().endObject());
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        expectedException.expect(MapperParsingException.class);
        expectedException.expectMessage("failed to parse field [array_field] of type [double]");
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().array("array_field", true, false, true).endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        mapper.parse(sourceToParse);
    }

    @Test
    public void testObjectArrayMapping() throws Exception {
        // @formatter: off
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "object").field("dynamic", true).startObject("properties").startObject("s").field("type", "keyword").endObject().endObject().endObject().endObject().endObject().endObject().endObject());
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        // child object mapper
        assertThat(mapper.objectMappers().get("array_field"), Matchers.is(Matchers.instanceOf(ObjectArrayMapper.class)));
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().startArray("array_field").startObject().field("s", "a").endObject().startObject().field("s", "b").endObject().startObject().field("s", "c").endObject().endArray().endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        // @formatter: off
        assertThat(doc.dynamicMappingsUpdate(), Matchers.nullValue());
        assertThat(doc.docs().size(), Matchers.is(1));
        assertThat(ArrayMapperTest.uniqueValuesFromFields(doc.docs().get(0), "array_field.s"), Matchers.containsInAnyOrder("a", "b", "c"));
        assertThat(mapper.mappers().getMapper("array_field.s"), Matchers.instanceOf(KeywordFieldMapper.class));
        assertThat(mapper.mappingSource().string(), Matchers.is(("{\"default\":{" + (((((((((((("\"properties\":{" + "\"array_field\":{") + "\"type\":\"array\",") + "\"inner\":{") + "\"dynamic\":\"true\",") + "\"properties\":{") + "\"s\":{") + "\"type\":\"keyword\"") + "}") + "}") + "}") + "}") + "}}}"))));
    }

    @Test
    public void testObjectArrayMappingNewColumn() throws Exception {
        // @formatter: off
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "object").field("dynamic", true).startObject("properties").startObject("s").field("type", "keyword").endObject().endObject().endObject().endObject().endObject().endObject().endObject());
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        // child object mapper
        assertThat(mapper.objectMappers().get("array_field"), Matchers.is(Matchers.instanceOf(ObjectArrayMapper.class)));
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().startArray("array_field").startObject().field("s", "a").field("new", true).endObject().endArray().endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        Mapping mappingUpdate = doc.dynamicMappingsUpdate();
        assertThat(mappingUpdate, Matchers.notNullValue());
        mapper = mapper.merge(mappingUpdate, true);
        assertThat(doc.docs().size(), Matchers.is(1));
        String[] values = doc.docs().get(0).getValues("array_field.new");
        assertThat(values, Matchers.arrayContainingInAnyOrder(Matchers.is("T"), Matchers.is("1")));
        String mappingSourceString = string();
        assertThat(mappingSourceString, Matchers.is(("{\"default\":{" + ((((((((((((("\"properties\":{" + "\"array_field\":{") + "\"type\":\"array\",") + "\"inner\":{") + "\"dynamic\":\"true\",") + "\"properties\":{") + "\"new\":{\"type\":\"boolean\"},") + "\"s\":{") + "\"type\":\"keyword\"") + "}") + "}") + "}") + "}") + "}}}"))));
    }

    @Test
    public void testNestedArrayMapping() throws Exception {
        expectedException.expect(MapperParsingException.class);
        expectedException.expectMessage("nested arrays are not supported");
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "double").endObject().endObject().endObject().endObject().endObject().endObject());
        mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
    }

    @Test
    public void testParseNull() throws Exception {
        // @formatter: off
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "double").endObject().endObject().endObject().endObject().endObject());
        // @formatter: on
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().nullField("array_field").endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument parsedDoc = mapper.parse(sourceToParse);
        assertThat(parsedDoc.docs().size(), Matchers.is(1));
        assertThat(parsedDoc.docs().get(0).getField("array_field"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testParseNullOnObjectArray() throws Exception {
        // @formatter: on
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("array_field").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "object").startObject("properties").endObject().endObject().endObject().endObject().endObject().endObject());
        // @formatter: off
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().nullField("array_field").endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument parsedDoc = mapper.parse(sourceToParse);
        assertThat(parsedDoc.docs().size(), Matchers.is(1));
        assertThat(parsedDoc.docs().get(0).getField("array_field"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testParseDynamicEmptyArray() throws Exception {
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").endObject().endObject().endObject());
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        // parse source with empty array
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().array("new_array_field").endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        assertThat(doc.docs().get(0).getField("new_array_field"), Matchers.is(Matchers.nullValue()));
        assertThat(mapper.mappers().getMapper("new_array_field"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testParseDynamicNullArray() throws Exception {
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").endObject().endObject().endObject());
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        // parse source with null array
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().startArray("new_array_field").nullValue().endArray().endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "abc", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        assertThat(doc.docs().get(0).getField("new_array_field"), Matchers.is(Matchers.nullValue()));
        assertThat(mapper.mappers().getMapper("new_array_field"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testCopyToFieldsOfInnerMapping() throws Exception {
        // @formatter:off
        String mapping = Strings.toString(XContentFactory.jsonBuilder().startObject().startObject(ArrayMapperTest.TYPE).startObject("properties").startObject("string_array").field("type", CONTENT_TYPE).startObject(INNER_TYPE).field("type", "text").field("index", "true").field("copy_to", "string_array_ft").endObject().endObject().endObject().endObject().endObject());
        // @formatter:on
        DocumentMapper mapper = mapper(ArrayMapperTest.INDEX, ArrayMapperTest.TYPE, mapping);
        FieldMapper arrayMapper = ((FieldMapper) (mapper.mappers().getMapper("string_array")));
        assertThat(arrayMapper, Matchers.is(Matchers.instanceOf(ArrayMapper.class)));
        assertThat(arrayMapper.copyTo().copyToFields(), Matchers.contains("string_array_ft"));
        // @formatter:on
        BytesReference bytesReference = BytesReference.bytes(XContentFactory.jsonBuilder().startObject().startArray("string_array").value("foo").value("bar").endArray().endObject());
        SourceToParse sourceToParse = SourceToParse.source(ArrayMapperTest.TYPE, "1", bytesReference, JSON);
        ParsedDocument doc = mapper.parse(sourceToParse);
        // @formatter:off
        List<String> copyValues = new ArrayList<>();
        for (IndexableField field : doc.docs().get(0).getFields()) {
            if (field.name().equals("string_array_ft")) {
                copyValues.add(field.stringValue());
            }
        }
        assertThat(copyValues, Matchers.containsInAnyOrder("foo", "bar"));
    }
}

