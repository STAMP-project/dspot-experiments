/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
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
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
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
package org.janusgraph.diskstorage.es;


import Cmp.EQUAL;
import Cmp.GREATER_THAN;
import Cmp.GREATER_THAN_EQUAL;
import Cmp.LESS_THAN;
import Cmp.LESS_THAN_EQUAL;
import Cmp.NOT_EQUAL;
import Geo.DISJOINT;
import Geo.INTERSECT;
import Geo.WITHIN;
import GraphDatabaseConfiguration.INDEX_NAME;
import Mapping.PREFIX_TREE;
import Mapping.STRING;
import Mapping.TEXT;
import Text.CONTAINS;
import Text.CONTAINS_FUZZY;
import Text.CONTAINS_PREFIX;
import Text.CONTAINS_REGEX;
import Text.FUZZY;
import Text.PREFIX;
import Text.REGEX;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraphException;
import org.janusgraph.core.schema.Parameter;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.graphdb.query.condition.PredicateCondition;
import org.janusgraph.graphdb.types.ParameterType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
@Testcontainers
public class ElasticsearchIndexTest extends IndexProviderTest {
    @Container
    public static JanusGraphElasticsearchContainer esr = new JanusGraphElasticsearchContainer();

    static HttpHost host;

    static CloseableHttpClient httpClient;

    static ObjectMapper objectMapper;

    private static char REPLACEMENT_CHAR = '\u2022';

    @Test
    public void testSupport() {
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, TEXT.asParameter()), CONTAINS_PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, TEXT.asParameter()), CONTAINS_REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, TEXT.asParameter()), CONTAINS_FUZZY));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, TEXT.asParameter()), REGEX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), CONTAINS));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), FUZZY));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), EQUAL));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, STRING.asParameter()), NOT_EQUAL));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), EQUAL));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), LESS_THAN_EQUAL));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), LESS_THAN));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), GREATER_THAN));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), GREATER_THAN_EQUAL));
        Assertions.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), NOT_EQUAL));
        Assertions.assertTrue(index.supports(of(Boolean.class, Cardinality.SINGLE), EQUAL));
        Assertions.assertTrue(index.supports(of(Boolean.class, Cardinality.SINGLE), NOT_EQUAL));
        Assertions.assertTrue(index.supports(of(UUID.class, Cardinality.SINGLE), EQUAL));
        Assertions.assertTrue(index.supports(of(UUID.class, Cardinality.SINGLE), NOT_EQUAL));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), WITHIN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), INTERSECT));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), DISJOINT));
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE), Geo.CONTAINS));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, PREFIX_TREE.asParameter()), WITHIN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, PREFIX_TREE.asParameter()), INTERSECT));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, PREFIX_TREE.asParameter()), Geo.CONTAINS));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, PREFIX_TREE.asParameter()), DISJOINT));
    }

    @Test
    public void testErrorInBatch() throws Exception {
        initialize("vertex");
        Multimap<String, Object> doc1 = HashMultimap.create();
        doc1.put(TIME, "not a time");
        add("vertex", "failing-doc", doc1, true);
        add("vertex", "non-failing-doc", getRandomDocument(), true);
        JanusGraphException janusGraphException = Assertions.assertThrows(JanusGraphException.class, () -> {
            tx.commit();
        }, "Commit should not have succeeded.");
        String message = Throwables.getRootCause(janusGraphException).getMessage();
        switch (JanusGraphElasticsearchContainer.getEsMajorVersion().value) {
            case 6 :
                Assertions.assertTrue(message.contains("mapper_parsing_exception"));
                break;
            case 5 :
            case 2 :
                Assertions.assertTrue(message.contains("number_format_exception"));
                break;
            case 1 :
                Assertions.assertTrue(message.contains("NumberFormatException"));
                break;
            default :
                Assertions.fail();
                break;
        }
        tx = null;
    }

    @Test
    public void testUnescapedDollarInSet() throws Exception {
        initialize("vertex");
        Multimap<String, Object> initialDoc = HashMultimap.create();
        initialDoc.put(PHONE_SET, "12345");
        add("vertex", "unescaped", initialDoc, true);
        clopen();
        Multimap<String, Object> updateDoc = HashMultimap.create();
        updateDoc.put(PHONE_SET, "$123");
        add("vertex", "unescaped", updateDoc, false);
        add("vertex", "other", getRandomDocument(), true);
        clopen();
        Assertions.assertEquals("unescaped", tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(PHONE_SET, EQUAL, "$123"))).toArray()[0]);
        Assertions.assertEquals("unescaped", tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(PHONE_SET, EQUAL, "12345"))).toArray()[0]);
    }

    /**
     * Test adding and overwriting with long string content.
     */
    @Test
    public void testUpdateAdditionWithLongString() throws Exception {
        initialize("vertex");
        Multimap<String, Object> initialDoc = HashMultimap.create();
        initialDoc.put(TEXT, (((RandomStringUtils.randomAlphanumeric(500000)) + " bob ") + (RandomStringUtils.randomAlphanumeric(500000))));
        add("vertex", "long", initialDoc, true);
        clopen();
        Assertions.assertEquals(1, tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(TEXT, CONTAINS, "bob"))).count());
        Assertions.assertEquals(0, tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(TEXT, CONTAINS, "world"))).count());
        tx.add("vertex", "long", TEXT, (((RandomStringUtils.randomAlphanumeric(500000)) + " world ") + (RandomStringUtils.randomAlphanumeric(500000))), false);
        clopen();
        Assertions.assertEquals(0, tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(TEXT, CONTAINS, "bob"))).count());
        Assertions.assertEquals(1, tx.queryStream(new IndexQuery("vertex", PredicateCondition.of(TEXT, CONTAINS, "world"))).count());
    }

    /**
     * Test ingest pipeline.
     */
    @Test
    public void testIngestPipeline() throws Exception {
        if ((ElasticsearchIndexTest.esr.getEsMajorVersion().value) > 2) {
            initialize("ingestvertex");
            final Multimap<String, Object> docs = HashMultimap.create();
            docs.put(TEXT, "bob");
            add("ingestvertex", "pipeline", docs, true);
            clopen();
            Assertions.assertEquals(1, tx.queryStream(new IndexQuery("ingestvertex", PredicateCondition.of(TEXT, CONTAINS, "bob"))).count());
            Assertions.assertEquals(1, tx.queryStream(new IndexQuery("ingestvertex", PredicateCondition.of(STRING, EQUAL, "hello"))).count());
        }
    }

    @Test
    public void testMapKey2Field_IllegalCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.mapKey2Field(("here is an illegal character: " + (ElasticsearchIndexTest.REPLACEMENT_CHAR)), null);
        });
    }

    @Test
    public void testMapKey2Field_MappingSpaces() {
        String expected = ((((("field" + (ElasticsearchIndexTest.REPLACEMENT_CHAR)) + "name") + (ElasticsearchIndexTest.REPLACEMENT_CHAR)) + "with") + (ElasticsearchIndexTest.REPLACEMENT_CHAR)) + "spaces";
        Assertions.assertEquals(expected, index.mapKey2Field("field name with spaces", null));
    }

    @Test
    public void testClearStorageWithAliases() throws Exception {
        IOUtils.closeQuietly(ElasticsearchIndexTest.httpClient.execute(ElasticsearchIndexTest.host, new HttpPut("test1")));
        IOUtils.closeQuietly(ElasticsearchIndexTest.httpClient.execute(ElasticsearchIndexTest.host, new HttpPut("test2")));
        final HttpPost addAlias = new HttpPost("_aliases");
        addAlias.setHeader("Content-Type", "application/json");
        addAlias.setEntity(new StringEntity("{\"actions\": [{\"add\": {\"indices\": [\"test1\", \"test2\"], \"alias\": \"alias1\"}}]}", Charset.forName("UTF-8")));
        IOUtils.closeQuietly(ElasticsearchIndexTest.httpClient.execute(ElasticsearchIndexTest.host, addAlias));
        initialize("vertex");
        Assertions.assertTrue(indexExists(INDEX_NAME.getDefaultValue()));
        index.clearStorage();
        Assertions.assertFalse(indexExists(INDEX_NAME.getDefaultValue()));
        Assertions.assertTrue(indexExists("test1"));
        Assertions.assertTrue(indexExists("test2"));
    }

    @Test
    public void testCustomMappingProperty() throws IOException, BackendException, ParseException {
        String mappingTypeName = "vertex";
        String indexPrefix = "janusgraph";
        String parameterName = "boost";
        Double parameterValue = 5.5;
        String field = "field_with_custom_prop";
        KeyInformation keyInfo = new StandardKeyInformation(String.class, Cardinality.SINGLE, STRING.asParameter(), Parameter.of(ParameterType.customParameterName(parameterName), parameterValue));
        index.register(mappingTypeName, field, keyInfo, tx);
        String indexName = (indexPrefix + "_") + mappingTypeName;
        CloseableHttpResponse response = getESMapping(indexName, mappingTypeName);
        // Fallback to multitype index
        if ((response.getStatusLine().getStatusCode()) != 200) {
            indexName = indexPrefix;
            response = getESMapping(indexName, mappingTypeName);
        }
        HttpEntity entity = response.getEntity();
        JSONObject json = ((JSONObject) (new JSONParser().parse(EntityUtils.toString(entity))));
        String returnedProperty = retrieveValueFromJSON(json, indexName, "mappings", mappingTypeName, "properties", field, parameterName);
        Assertions.assertEquals(parameterValue.toString(), returnedProperty);
        IOUtils.closeQuietly(response);
    }
}

