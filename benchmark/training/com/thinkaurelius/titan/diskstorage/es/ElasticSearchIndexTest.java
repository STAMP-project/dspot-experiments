package com.thinkaurelius.titan.diskstorage.es;


import Cmp.EQUAL;
import Cmp.GREATER_THAN;
import Cmp.GREATER_THAN_EQUAL;
import Cmp.LESS_THAN;
import Cmp.LESS_THAN_EQUAL;
import Cmp.NOT_EQUAL;
import GraphDatabaseConfiguration.INDEX_DIRECTORY;
import Text.CONTAINS;
import Text.CONTAINS_PREFIX;
import Text.CONTAINS_REGEX;
import Text.PREFIX;
import Text.REGEX;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.thinkaurelius.titan.StorageSetup;
import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProvider;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProviderTest;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.graphdb.query.condition.PredicateCondition;
import java.io.File;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ElasticSearchIndexTest extends IndexProviderTest {
    @Test
    public void testSupport() {
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE), CONTAINS));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), CONTAINS_REGEX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.TEXT)), REGEX));
        Assert.assertFalse(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), CONTAINS));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), PREFIX));
        Assert.assertTrue(index.supports(of(String.class, Cardinality.SINGLE, new com.thinkaurelius.titan.core.schema.Parameter("mapping", Mapping.STRING)), REGEX));
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

    @Test
    public void testConfiguration() throws BackendException {
        // Test that local-mode has precedence over hostname
        final String index = "es";
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(LOCAL_MODE, true, index);
        config.set(CLIENT_ONLY, true, index);
        config.set(INDEX_HOSTS, new String[]{ "10.0.0.1" }, index);
        config.set(INDEX_DIRECTORY, StorageSetup.getHomeDir("es"), index);
        Configuration indexConfig = config.restrictTo(index);
        IndexProvider idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);// Shouldn't throw exception

        idx.close();
        config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(LOCAL_MODE, false, index);
        config.set(CLIENT_ONLY, true, index);
        config.set(INDEX_HOSTS, new String[]{ "10.0.0.1" }, index);
        config.set(INDEX_DIRECTORY, StorageSetup.getHomeDir("es"), index);
        indexConfig = config.restrictTo(index);
        RuntimeException expectedException = null;
        try {
            idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);// Should try 10.0.0.1 and throw exception

            idx.close();
        } catch (RuntimeException re) {
            expectedException = re;
        }
        Assert.assertNotNull(expectedException);
    }

    @Test
    public void testConfigurationFile() throws BackendException {
        final String index = "es";
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(LOCAL_MODE, true, index);
        config.set(CLIENT_ONLY, true, index);
        config.set(INDEX_CONF_FILE, Joiner.on(File.separator).join("target", "test-classes", "es_nodename_foo.yml"), index);
        config.set(INDEX_DIRECTORY, StorageSetup.getHomeDir("es"), index);
        Configuration indexConfig = config.restrictTo(index);
        ElasticSearchIndex.ElasticSearchIndex idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);// Shouldn't throw exception

        idx.close();
        Assert.assertEquals("foo", idx.getNode().settings().get("node.name"));
        config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(LOCAL_MODE, true, index);
        config.set(CLIENT_ONLY, true, index);
        config.set(INDEX_CONF_FILE, Joiner.on(File.separator).join("target", "test-classes", "es_nodename_bar.yml"), index);
        config.set(INDEX_DIRECTORY, StorageSetup.getHomeDir("es"), index);
        indexConfig = config.restrictTo(index);
        idx = new ElasticSearchIndex.ElasticSearchIndex(indexConfig);// Shouldn't throw exception

        idx.close();
        Assert.assertEquals("bar", idx.getNode().settings().get("node.name"));
    }

    @Test
    public void testErrorInBatch() throws Exception {
        initialize("vertex");
        Multimap<String, Object> doc1 = HashMultimap.create();
        doc1.put(TIME, "not a time");
        add("vertex", "failing-doc", doc1, true);
        add("vertex", "non-failing-doc", getRandomDocument(), true);
        try {
            tx.commit();
            Assert.fail("Commit should not have succeeded.");
        } catch (TitanException e) {
            // Looking for a NumberFormatException since we tried to stick a string of text into a time field.
            if (!(Throwables.getRootCause(e).getMessage().contains("NumberFormatException"))) {
                throw e;
            }
        } finally {
            tx = null;
        }
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
        Assert.assertEquals("unescaped", tx.query(new com.thinkaurelius.titan.diskstorage.indexing.IndexQuery("vertex", PredicateCondition.of(PHONE_SET, EQUAL, "$123"))).get(0));
        Assert.assertEquals("unescaped", tx.query(new com.thinkaurelius.titan.diskstorage.indexing.IndexQuery("vertex", PredicateCondition.of(PHONE_SET, EQUAL, "12345"))).get(0));
    }
}

