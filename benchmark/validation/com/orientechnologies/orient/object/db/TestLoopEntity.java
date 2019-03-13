package com.orientechnologies.orient.object.db;


import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.entity.LoopEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 09/05/16.
 */
public class TestLoopEntity {
    @Test
    public void testLoop() {
        ODatabaseObject object = new OObjectDatabaseTx(("memory:" + (TestLoopEntity.class.getSimpleName())));
        object.create();
        try {
            object.getEntityManager().registerEntityClasses(LoopEntity.class, true);
            Assert.assertTrue(object.getMetadata().getSchema().existsClass("LoopEntity"));
            Assert.assertTrue(object.getMetadata().getSchema().existsClass("LoopEntityLink"));
        } finally {
            object.drop();
        }
    }
}

