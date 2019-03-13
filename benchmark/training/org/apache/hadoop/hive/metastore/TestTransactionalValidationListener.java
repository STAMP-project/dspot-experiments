package org.apache.hadoop.hive.metastore;


import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.MetaStoreClientTest;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.hadoop.hive.ql.io.AcidUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestTransactionalValidationListener extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private boolean createdCatalogs = false;

    public TestTransactionalValidationListener(String name, AbstractMetaStoreService metaStore) throws Exception {
        this.metaStore = metaStore;
    }

    @Test
    public void testCreateAsAcid() throws Exception {
        // Table created in hive catalog should have been automatically set to transactional
        Table createdTable = createOrcTable("hive");
        Assert.assertTrue(AcidUtils.isTransactionalTable(createdTable));
        // Non-hive catalogs should not be transactional
        createdTable = createOrcTable("spark");
        Assert.assertFalse(AcidUtils.isTransactionalTable(createdTable));
        createdTable = createOrcTable("myapp");
        Assert.assertFalse(AcidUtils.isTransactionalTable(createdTable));
    }
}

