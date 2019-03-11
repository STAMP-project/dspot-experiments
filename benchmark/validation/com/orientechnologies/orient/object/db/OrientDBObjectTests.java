package com.orientechnologies.orient.object.db;


import ODatabaseType.MEMORY;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 13/01/17.
 */
public class OrientDBObjectTests {
    @Test
    public void createAndUseEmbeddedDatabase() {
        OrientDBObject factory = new OrientDBObject("embedded:.", null);
        if (!(factory.exists("test")))
            factory.create("test", MEMORY);

        ODatabaseObject db = factory.open("test", "admin", "admin");
        db.close();
        factory.close();
    }

    @Test(expected = ODatabaseException.class)
    public void testEmbeddedDoubleCreate() {
        OrientDBObject factory = new OrientDBObject("embedded:.", null);
        try {
            factory.create("test", MEMORY);
            factory.create("test", MEMORY);
        } finally {
            factory.close();
        }
    }

    @Test
    public void createDropEmbeddedDatabase() {
        OrientDBObject factory = new OrientDBObject("embedded:.", null);
        try {
            factory.create("test", MEMORY);
            Assert.assertTrue(factory.exists("test"));
            factory.drop("test");
            Assert.assertFalse(factory.exists("test"));
        } finally {
            factory.close();
        }
    }

    @Test
    public void testPool() {
        OrientDBObject factory = new OrientDBObject("embedded:.", null);
        if (!(factory.exists("test")))
            factory.create("test", MEMORY);

        ODatabaseObjectPool pool = new ODatabaseObjectPool(factory, "test", "admin", "admin");
        ODatabaseObject db = pool.acquire();
        db.close();
        pool.close();
        factory.close();
    }

    @Test
    public void testListDatabases() {
        OrientDBObject factory = new OrientDBObject("embedded:.", null);
        Assert.assertEquals(factory.list().size(), 0);
        factory.create("test", MEMORY);
        List<String> databases = factory.list();
        Assert.assertEquals(databases.size(), 1);
        Assert.assertTrue(databases.contains("test"));
        factory.close();
    }
}

