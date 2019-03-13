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
package org.janusgraph.diskstorage.lucene;


import Cmp.EQUAL;
import Cmp.GREATER_THAN;
import Cmp.GREATER_THAN_EQUAL;
import Cmp.LESS_THAN;
import Cmp.LESS_THAN_EQUAL;
import Cmp.NOT_EQUAL;
import Geo.DISJOINT;
import Geo.INTERSECT;
import Geo.WITHIN;
import Text.CONTAINS;
import Text.CONTAINS_FUZZY;
import Text.CONTAINS_PREFIX;
import Text.CONTAINS_REGEX;
import Text.FUZZY;
import Text.PREFIX;
import Text.REGEX;
import java.util.Date;
import java.util.UUID;
import org.janusgraph.core.schema.Mapping;
import org.janusgraph.diskstorage.indexing.IndexProviderTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class LuceneIndexTest extends IndexProviderTest {
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexTest.class);

    private static char REPLACEMENT_CHAR = '\u2022';

    private static final String MAPPING = "mapping";

    @Test
    public void testSupport() {
        // DEFAULT(=TEXT) support
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS_PREFIX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS_REGEX));// TODO Not supported yet

        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), REGEX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), PREFIX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), EQUAL));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), NOT_EQUAL));
        // Same tests as above, except explicitly specifying TEXT instead of relying on DEFAULT
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), CONTAINS));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), CONTAINS_PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), CONTAINS_FUZZY));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), CONTAINS_REGEX));// TODO Not supported yet

        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), REGEX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), PREFIX));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), EQUAL));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.TEXT)), NOT_EQUAL));
        // STRING support
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), CONTAINS));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), CONTAINS_PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), FUZZY));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), EQUAL));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.STRING)), NOT_EQUAL));
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
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE), DISJOINT));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.PREFIX_TREE)), WITHIN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.PREFIX_TREE)), Geo.CONTAINS));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.PREFIX_TREE)), INTERSECT));
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter(LuceneIndexTest.MAPPING, Mapping.PREFIX_TREE)), DISJOINT));
    }

    @Test
    public void testMapKey2Field_IllegalCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.mapKey2Field(("here is an illegal character: " + (LuceneIndexTest.REPLACEMENT_CHAR)), null);
        });
    }

    @Test
    public void testMapKey2Field_MappingSpaces() {
        String expected = ((((("field" + (LuceneIndexTest.REPLACEMENT_CHAR)) + "name") + (LuceneIndexTest.REPLACEMENT_CHAR)) + "with") + (LuceneIndexTest.REPLACEMENT_CHAR)) + "spaces";
        Assertions.assertEquals(expected, index.mapKey2Field("field name with spaces", null));
    }
}

