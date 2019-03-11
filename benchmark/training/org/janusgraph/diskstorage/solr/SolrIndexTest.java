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
package org.janusgraph.diskstorage.solr;


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
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.Mapping;
import org.janusgraph.diskstorage.indexing.IndexProviderTest;
import org.janusgraph.diskstorage.indexing.KeyInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Jared Holmberg (jholmberg@bericotechnologies.com)
 */
public class SolrIndexTest extends IndexProviderTest {
    @Test
    public void testSupport() {
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT))));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING))));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXTSTRING))));
        Assertions.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE)));
        Assertions.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT))));
        Assertions.assertTrue(index.supports(of(Long.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Long.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.DEFAULT))));
        Assertions.assertTrue(index.supports(of(Integer.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Short.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Byte.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Float.class, Cardinality.SINGLE)));
        Assertions.assertFalse(index.supports(of(Object.class, Cardinality.SINGLE)));
        Assertions.assertFalse(index.supports(of(Exception.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.DEFAULT)), CONTAINS_PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_FUZZY));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXTSTRING)), REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.DEFAULT)), PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING)), PREFIX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING)), REGEX));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING)), FUZZY));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING)), EQUAL));
        Assertions.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.STRING)), NOT_EQUAL));
        Assertions.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXTSTRING)), NOT_EQUAL));
        Assertions.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), EQUAL));
        Assertions.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), GREATER_THAN_EQUAL));
        Assertions.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), LESS_THAN));
        Assertions.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.DEFAULT)), LESS_THAN));
        Assertions.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.TEXT)), LESS_THAN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE)));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), WITHIN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), INTERSECT));
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE), Geo.CONTAINS));
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE), DISJOINT));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.PREFIX_TREE)), Geo.CONTAINS));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.PREFIX_TREE)), WITHIN));
        Assertions.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.PREFIX_TREE)), INTERSECT));
        Assertions.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE, new org.janusgraph.core.schema.Parameter("mapping", Mapping.PREFIX_TREE)), DISJOINT));
        Assertions.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE), INTERSECT));
        Assertions.assertFalse(index.supports(of(Long.class, Cardinality.SINGLE), CONTAINS));
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
    }

    @Test
    public void testMapKey2Field_IllegalCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            KeyInformation keyInfo = new org.janusgraph.diskstorage.indexing.StandardKeyInformation(Boolean.class, Cardinality.SINGLE);
            index.mapKey2Field("here is an illegal character: ?", keyInfo);
        });
    }

    @Test
    public void testMapKey2Field_MappingSpaces() {
        KeyInformation keyInfo = new org.janusgraph.diskstorage.indexing.StandardKeyInformation(Boolean.class, Cardinality.SINGLE);
        Assertions.assertEquals("field?name?with?spaces_b", index.mapKey2Field("field name with spaces", keyInfo));
    }
}

