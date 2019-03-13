package com.thinkaurelius.titan.diskstorage.solr;


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
import Text.CONTAINS_PREFIX;
import Text.CONTAINS_REGEX;
import Text.PREFIX;
import Text.REGEX;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProviderTest;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jared Holmberg (jholmberg@bericotechnologies.com)
 */
public class SolrIndexTest extends IndexProviderTest {
    @Test
    public void testSupport() {
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT))));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING))));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXTSTRING))));
        Assert.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE)));
        Assert.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT))));
        Assert.assertTrue(index.supports(of(Long.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(Long.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.DEFAULT))));
        Assert.assertTrue(index.supports(of(Integer.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(Short.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(Byte.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(Float.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE)));
        Assert.assertFalse(index.supports(of(Object.class, Cardinality.SINGLE)));
        Assert.assertFalse(index.supports(of(Exception.class, Cardinality.SINGLE)));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.DEFAULT)), CONTAINS_PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_REGEX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXTSTRING)), REGEX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.DEFAULT)), PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), REGEX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), EQUAL));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), NOT_EQUAL));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXTSTRING)), NOT_EQUAL));
        Assert.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), EQUAL));
        Assert.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), GREATER_THAN_EQUAL));
        Assert.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE), LESS_THAN));
        Assert.assertTrue(index.supports(of(Double.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.DEFAULT)), LESS_THAN));
        Assert.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), LESS_THAN));
        Assert.assertTrue(index.supports(of(Geoshape.class, Cardinality.SINGLE), WITHIN));
        Assert.assertFalse(index.supports(of(Double.class, Cardinality.SINGLE), INTERSECT));
        Assert.assertFalse(index.supports(of(Long.class, Cardinality.SINGLE), CONTAINS));
        Assert.assertFalse(index.supports(of(Geoshape.class, Cardinality.SINGLE), DISJOINT));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), EQUAL));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), LESS_THAN_EQUAL));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), LESS_THAN));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), GREATER_THAN));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), GREATER_THAN_EQUAL));
        Assert.assertTrue(index.supports(of(Date.class, Cardinality.SINGLE), NOT_EQUAL));
        Assert.assertTrue(index.supports(of(Boolean.class, Cardinality.SINGLE), EQUAL));
        Assert.assertTrue(index.supports(of(Boolean.class, Cardinality.SINGLE), NOT_EQUAL));
        Assert.assertTrue(index.supports(of(UUID.class, Cardinality.SINGLE), EQUAL));
        Assert.assertTrue(index.supports(of(UUID.class, Cardinality.SINGLE), NOT_EQUAL));
    }
}

