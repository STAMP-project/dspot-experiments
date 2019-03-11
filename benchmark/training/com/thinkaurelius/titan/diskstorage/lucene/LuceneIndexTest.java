package com.thinkaurelius.titan.diskstorage.lucene;


import Cmp.EQUAL;
import Cmp.GREATER_THAN;
import Cmp.GREATER_THAN_EQUAL;
import Cmp.LESS_THAN;
import Cmp.LESS_THAN_EQUAL;
import Cmp.NOT_EQUAL;
import Text.CONTAINS;
import Text.CONTAINS_PREFIX;
import Text.CONTAINS_REGEX;
import Text.PREFIX;
import Text.REGEX;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProviderTest;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
// @Override
// public void testDeleteDocumentThenModifyField() {
// // This fails under Lucene but works in ES
// log.info("Skipping " + getClass().getSimpleName() + "." + methodName.getMethodName());
// }
public class LuceneIndexTest extends IndexProviderTest {
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexTest.class);

    @Rule
    public TestName methodName = new TestName();

    @Test
    public void testSupport() {
        // DEFAULT(=TEXT) support
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS_PREFIX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS_REGEX));// TODO Not supported yet

        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), REGEX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), PREFIX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), EQUAL));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE), NOT_EQUAL));
        // Same tests as above, except explicitly specifying TEXT instead of relying on DEFAULT
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_PREFIX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_REGEX));// TODO Not supported yet

        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), REGEX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), PREFIX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), EQUAL));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), NOT_EQUAL));
        // STRING support
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), CONTAINS));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), CONTAINS_PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), REGEX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), EQUAL));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), NOT_EQUAL));
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

