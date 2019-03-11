package com.orientechnologies.orient.core.db;


import ODatabaseType.MEMORY;
import OGlobalConfiguration.DB_POOL_MAX;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


public class ODatabasePoolTest {
    @Test
    public void testPool() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        if (!(orientDb.exists("test")))
            orientDb.create("test", MEMORY);

        ODatabasePool pool = new ODatabasePool(orientDb, "test", "admin", "admin");
        ODatabaseDocument db = pool.acquire();
        db.save(new ODocument(), db.getClusterNameById(db.getDefaultClusterId()));
        db.close();
        pool.close();
        orientDb.close();
    }

    @Test
    public void testPoolCloseTx() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.builder().addConfig(DB_POOL_MAX, 1).build());
        if (!(orientDb.exists("test"))) {
            orientDb.create("test", MEMORY);
        }
        ODatabasePool pool = new ODatabasePool(orientDb, "test", "admin", "admin");
        ODatabaseDocument db = pool.acquire();
        db.createClass("Test");
        db.begin();
        db.save(new ODocument("Test"));
        db.close();
        db = pool.acquire();
        Assert.assertEquals(db.countClass("Test"), 0);
        pool.close();
        orientDb.close();
    }

    @Test
    public void testPoolDoubleClose() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.builder().addConfig(DB_POOL_MAX, 1).build());
        if (!(orientDb.exists("test"))) {
            orientDb.create("test", MEMORY);
        }
        ODatabasePool pool = new ODatabasePool(orientDb, "test", "admin", "admin");
        ODatabaseDocument db = pool.acquire();
        db.close();
        db.close();
        orientDb.close();
    }
}

