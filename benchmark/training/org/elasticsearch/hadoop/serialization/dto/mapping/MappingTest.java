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
package org.elasticsearch.hadoop.serialization.dto.mapping;


import java.util.Collections;
import java.util.List;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.elasticsearch.hadoop.serialization.FieldType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MappingTest {
    boolean typeless;

    String mappingDirectory;

    public MappingTest(boolean typeless, String mappingTypes) {
        this.typeless = typeless;
        this.mappingDirectory = mappingTypes;
    }

    @Test
    public void testNestedObjectParsing() throws Exception {
        MappingSet mappings = getMappingsForResource("multi_level_field_with_same_name.json");
        Mapping mapping = ensureAndGet("index", "artiststimestamp", mappings);
        Field[] properties = mapping.getFields();
        Field first = properties[0];
        Assert.assertEquals("date", first.name());
        Assert.assertEquals(FieldType.DATE, first.type());
        Field second = properties[1];
        Assert.assertEquals(FieldType.OBJECT, second.type());
        Assert.assertEquals("links", second.name());
        Field[] secondProps = second.properties();
        Assert.assertEquals("url", secondProps[0].name());
        Assert.assertEquals(FieldType.STRING, secondProps[0].type());
    }

    @Test
    public void testBasicParsing() throws Exception {
        getMappingsForResource("basic.json");
    }

    @Test
    public void testPrimitivesParsing() throws Exception {
        MappingSet mappings = getMappingsForResource("primitives.json");
        Mapping mapping = ensureAndGet("index", "primitives", mappings);
        Field[] props = mapping.getFields();
        Assert.assertEquals(14, props.length);
        Assert.assertEquals("field01", props[0].name());
        Assert.assertEquals(FieldType.BOOLEAN, props[0].type());
        Assert.assertEquals("field02", props[1].name());
        Assert.assertEquals(FieldType.BYTE, props[1].type());
        Assert.assertEquals("field03", props[2].name());
        Assert.assertEquals(FieldType.SHORT, props[2].type());
        Assert.assertEquals("field04", props[3].name());
        Assert.assertEquals(FieldType.INTEGER, props[3].type());
        Assert.assertEquals("field05", props[4].name());
        Assert.assertEquals(FieldType.LONG, props[4].type());
        Assert.assertEquals("field06", props[5].name());
        Assert.assertEquals(FieldType.FLOAT, props[5].type());
        Assert.assertEquals("field07", props[6].name());
        Assert.assertEquals(FieldType.DOUBLE, props[6].type());
        Assert.assertEquals("field08", props[7].name());
        Assert.assertEquals(FieldType.STRING, props[7].type());
        Assert.assertEquals("field09", props[8].name());
        Assert.assertEquals(FieldType.DATE, props[8].type());
        Assert.assertEquals("field10", props[9].name());
        Assert.assertEquals(FieldType.BINARY, props[9].type());
        Assert.assertEquals("field11", props[10].name());
        Assert.assertEquals(FieldType.TEXT, props[10].type());
        Assert.assertEquals("field12", props[11].name());
        Assert.assertEquals(FieldType.KEYWORD, props[11].type());
        Assert.assertEquals("field13", props[12].name());
        Assert.assertEquals(FieldType.HALF_FLOAT, props[12].type());
        Assert.assertEquals("field14", props[13].name());
        Assert.assertEquals(FieldType.SCALED_FLOAT, props[13].type());
    }

    @Test
    public void testGeoParsingWithOptions() throws Exception {
        MappingSet mappings = getMappingsForResource("geo.json");
        Mapping mapping = ensureAndGet("index", "restaurant", mappings);
        System.out.println(mapping);
        Field[] props = mapping.getFields();
        Assert.assertEquals(1, props.length);
        Assert.assertEquals("location", props[0].name());
        Assert.assertEquals(FieldType.GEO_POINT, props[0].type());
    }

    @Test
    public void testCompletionParsing() throws Exception {
        MappingSet mappings = getMappingsForResource("completion.json");
        Mapping mapping = ensureAndGet("index", "song", mappings);
        Field[] props = mapping.getFields();
        Assert.assertEquals(1, props.length);
        Assert.assertEquals("name", props[0].name());
    }

    @Test
    public void testIpParsing() throws Exception {
        MappingSet mappings = getMappingsForResource("ip.json");
        Mapping mapping = ensureAndGet("index", "client", mappings);
        Assert.assertEquals(1, mapping.getFields().length);
    }

    @Test
    public void testUnsupportedParsing() throws Exception {
        MappingSet mappings = getMappingsForResource("attachment.json");
        Mapping mapping = ensureAndGet("index", "person", mappings);
        Assert.assertEquals(0, mapping.getFields().length);
    }

    @Test
    public void testFieldValidation() throws Exception {
        MappingSet mappings = getMappingsForResource("multi_level_field_with_same_name.json");
        Mapping mapping = ensureAndGet("index", "artiststimestamp", mappings);
        List<String>[] findFixes = MappingUtils.findTypos(Collections.singletonList("nam"), mapping);
        Assert.assertThat(findFixes[1], Matchers.contains("name"));
        findFixes = MappingUtils.findTypos(Collections.singletonList("link.url"), mapping);
        Assert.assertThat(findFixes[1], Matchers.contains("links.url"));
        findFixes = MappingUtils.findTypos(Collections.singletonList("ulr"), mapping);
        Assert.assertThat(findFixes[1], Matchers.contains("links.url"));
        findFixes = MappingUtils.findTypos(Collections.singletonList("likn"), mapping);
        Assert.assertThat(findFixes[1], Matchers.contains("links"));
        findFixes = MappingUtils.findTypos(Collections.singletonList("_uid"), mapping);
        Assert.assertThat(findFixes, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testFieldInclude() throws Exception {
        MappingSet mappings = getMappingsForResource("multi_level_field_with_same_name.json");
        Mapping mapping = ensureAndGet("index", "artiststimestamp", mappings);
        Mapping filtered = mapping.filter(Collections.singleton("*a*e"), Collections.<String>emptyList());
        Assert.assertThat(mapping.getIndex(), Matchers.is(filtered.getIndex()));
        Assert.assertThat(mapping.getType(), Matchers.is(filtered.getType()));
        Field[] props = filtered.getFields();
        Assert.assertThat(props.length, Matchers.is(2));
        Assert.assertThat(props[0].name(), Matchers.is("date"));
        Assert.assertThat(props[1].name(), Matchers.is("name"));
    }

    @Test
    public void testFieldNestedInclude() throws Exception {
        MappingSet mappings = getMappingsForResource("multi_level_field_with_same_name.json");
        Mapping mapping = ensureAndGet("index", "artiststimestamp", mappings);
        Mapping filtered = mapping.filter(Collections.singleton("links.image.url"), Collections.<String>emptyList());
        Assert.assertThat(mapping.getIndex(), Matchers.is(filtered.getIndex()));
        Assert.assertThat(mapping.getType(), Matchers.is(filtered.getType()));
        Field[] props = filtered.getFields();
        Assert.assertThat(props.length, Matchers.is(1));
        Assert.assertThat(props[0].name(), Matchers.is("links"));
        Assert.assertThat(props[0].properties().length, Matchers.is(1));
        Assert.assertThat(props[0].properties()[0].name(), Matchers.is("image"));
        Assert.assertThat(props[0].properties()[0].properties().length, Matchers.is(1));
        Assert.assertThat(props[0].properties()[0].properties()[0].name(), Matchers.is("url"));
    }

    @Test
    public void testFieldExclude() throws Exception {
        MappingSet mappings = getMappingsForResource("nested_arrays_mapping.json");
        Mapping mapping = ensureAndGet("index", "nested-array-exclude", mappings);
        Mapping filtered = mapping.filter(Collections.<String>emptyList(), Collections.singleton("nested.bar"));
        Assert.assertThat(mapping.getIndex(), Matchers.is(filtered.getIndex()));
        Assert.assertThat(mapping.getType(), Matchers.is(filtered.getType()));
        Field[] props = filtered.getFields();
        Assert.assertThat(props.length, Matchers.is(2));
        Assert.assertThat(props[0].name(), Matchers.is("foo"));
        Assert.assertThat(props[1].name(), Matchers.is("nested"));
        Assert.assertThat(props[1].properties().length, Matchers.is(1));
        Assert.assertThat(props[1].properties()[0].name(), Matchers.is("what"));
    }

    @Test
    public void testNestedMapping() throws Exception {
        MappingSet mappings = getMappingsForResource("nested-mapping.json");
        Mapping mapping = ensureAndGet("index", "company", mappings);
        Field[] properties = mapping.getFields();
        Assert.assertEquals(3, properties.length);
        Field first = properties[0];
        Assert.assertEquals("name", first.name());
        Assert.assertEquals(FieldType.STRING, first.type());
        Field second = properties[1];
        Assert.assertEquals("description", second.name());
        Assert.assertEquals(FieldType.STRING, second.type());
        Field nested = properties[2];
        Assert.assertEquals("employees", nested.name());
        Assert.assertEquals(FieldType.NESTED, nested.type());
        Field[] nestedProps = nested.properties();
        Assert.assertEquals("name", nestedProps[0].name());
        Assert.assertEquals(FieldType.LONG, nestedProps[1].type());
    }

    @Test
    public void testMappingWithFieldsNamedPropertiesAndType() throws Exception {
        MappingSet mappings = getMappingsForResource("mapping_with_fields_named_properties_and_type.json");
        Mapping mapping = ensureAndGet("index", "properties", mappings);
        Assert.assertEquals("field1", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[0].type());
        Assert.assertEquals("properties", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.OBJECT, mapping.getFields()[1].type());
        Assert.assertEquals("subfield1", mapping.getFields()[1].properties()[0].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[1].properties()[0].type());
        Assert.assertEquals("subfield2", mapping.getFields()[1].properties()[1].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[1].properties()[1].type());
        Assert.assertEquals("field2", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.OBJECT, mapping.getFields()[2].type());
        Assert.assertEquals("subfield3", mapping.getFields()[2].properties()[0].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[2].properties()[0].type());
        Assert.assertEquals("properties", mapping.getFields()[2].properties()[1].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[2].properties()[1].type());
        Assert.assertEquals("type", mapping.getFields()[2].properties()[2].name());
        Assert.assertEquals(FieldType.OBJECT, mapping.getFields()[2].properties()[2].type());
        Assert.assertEquals("properties", mapping.getFields()[2].properties()[2].properties()[0].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[2].properties()[2].properties()[1].type());
        Assert.assertEquals("subfield5", mapping.getFields()[2].properties()[2].properties()[1].name());
        Assert.assertEquals(FieldType.OBJECT, mapping.getFields()[2].properties()[2].properties()[0].type());
        Assert.assertEquals("properties", mapping.getFields()[2].properties()[2].properties()[0].properties()[0].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[2].properties()[2].properties()[0].properties()[0].type());
        Assert.assertEquals("subfield4", mapping.getFields()[2].properties()[2].properties()[0].properties()[1].name());
        Assert.assertEquals(FieldType.STRING, mapping.getFields()[2].properties()[2].properties()[0].properties()[1].type());
    }

    @Test
    public void testJoinField() throws Exception {
        MappingSet mappings = getMappingsForResource("join-type.json");
        Mapping mapping = ensureAndGet("index", "join", mappings);
        Assert.assertEquals("id", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[0].type());
        Assert.assertEquals("company", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.TEXT, mapping.getFields()[1].type());
        Assert.assertEquals("name", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.TEXT, mapping.getFields()[2].type());
        Assert.assertEquals("joiner", mapping.getFields()[3].name());
        Assert.assertEquals(FieldType.JOIN, mapping.getFields()[3].type());
        Assert.assertEquals("name", mapping.getFields()[3].properties()[0].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[3].properties()[0].type());
        Assert.assertEquals("parent", mapping.getFields()[3].properties()[1].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[3].properties()[1].type());
    }

    @Test
    public void testMultipleFields() throws Exception {
        Assume.assumeThat("Cannot read multiple types when using typeless mappings", typeless, Matchers.is(false));
        MappingSet mappings = getMappingsForResource("multiple-types.json");
        Assert.assertNotNull(ensureAndGet("index", "type1", mappings));
        Assert.assertNotNull(ensureAndGet("index", "type2", mappings));
        Mapping mapping = mappings.getResolvedView();
        Assert.assertEquals("*", mapping.getIndex());
        Assert.assertEquals("*", mapping.getType());
        Assert.assertEquals("field1", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[0].type());
        Assert.assertEquals("field2", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.FLOAT, mapping.getFields()[1].type());
        Assert.assertEquals("field3", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.INTEGER, mapping.getFields()[2].type());
    }

    @Test
    public void testMultipleIndexMultipleFields() throws Exception {
        MappingSet mappings = getMappingsForResource("multiple-indices-multiple-types.json");
        Assert.assertNotNull(ensureAndGet("index1", "type1", mappings));
        Assert.assertNotNull(ensureAndGet("index2", "type2", mappings));
        Mapping mapping = mappings.getResolvedView();
        Assert.assertEquals("*", mapping.getIndex());
        Assert.assertEquals("*", mapping.getType());
        Assert.assertEquals("field1", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[0].type());
        Assert.assertEquals("field2", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.FLOAT, mapping.getFields()[1].type());
        Assert.assertEquals("field3", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.INTEGER, mapping.getFields()[2].type());
    }

    @Test
    public void testDynamicTemplateIndex() throws Exception {
        MappingSet mappings = getMappingsForResource("dynamic-template.json");
        Mapping mapping = ensureAndGet("index", "friend", mappings);
        Assert.assertEquals("hobbies", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.TEXT, mapping.getFields()[0].type());
        Assert.assertEquals("job", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.TEXT, mapping.getFields()[1].type());
        Assert.assertEquals("name", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.TEXT, mapping.getFields()[2].type());
    }

    @Test
    public void testMultipleIndexMultipleUpcastableFields() throws Exception {
        MappingSet mappings = getMappingsForResource("multiple-indices-multiple-upcastable-types.json");
        Assert.assertNotNull(ensureAndGet("index1", "type1", mappings));
        Assert.assertNotNull(ensureAndGet("index2", "type2", mappings));
        Mapping mapping = mappings.getResolvedView();
        Assert.assertEquals("*", mapping.getIndex());
        Assert.assertEquals("*", mapping.getType());
        Assert.assertEquals("field1_keyword", mapping.getFields()[0].name());
        Assert.assertEquals("field2_keyword", mapping.getFields()[1].name());
        Assert.assertEquals("field3_keyword", mapping.getFields()[2].name());
        Assert.assertEquals("field4_integer", mapping.getFields()[3].name());
        Assert.assertEquals("field5_keyword", mapping.getFields()[4].name());
        Assert.assertEquals("field6_float", mapping.getFields()[5].name());
        Assert.assertEquals("field7_keyword", mapping.getFields()[6].name());
        Assert.assertEquals("field8_float", mapping.getFields()[7].name());
        Assert.assertEquals("field9_integer", mapping.getFields()[8].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[0].type());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[1].type());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[2].type());
        Assert.assertEquals(FieldType.INTEGER, mapping.getFields()[3].type());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[4].type());
        Assert.assertEquals(FieldType.FLOAT, mapping.getFields()[5].type());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[6].type());
        Assert.assertEquals(FieldType.FLOAT, mapping.getFields()[7].type());
        Assert.assertEquals(FieldType.INTEGER, mapping.getFields()[8].type());
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void testMultipleIndexMultipleConflictingFields() throws Exception {
        MappingSet mappings = getMappingsForResource("multiple-indices-multiple-conflicting-types.json");
        Assert.assertNotNull(ensureAndGet("index1", "type1", mappings));
        Assert.assertNotNull(ensureAndGet("index2", "type2", mappings));
        Mapping mapping = mappings.getResolvedView();
        Assert.assertEquals("*", mapping.getIndex());
        Assert.assertEquals("*", mapping.getType());
        Assert.assertEquals("field1", mapping.getFields()[0].name());
        Assert.assertEquals(FieldType.KEYWORD, mapping.getFields()[0].type());
        Assert.assertEquals("field3", mapping.getFields()[1].name());
        Assert.assertEquals(FieldType.FLOAT, mapping.getFields()[1].type());
        Assert.assertEquals("field4", mapping.getFields()[2].name());
        Assert.assertEquals(FieldType.INTEGER, mapping.getFields()[2].type());
    }
}

