package com.orientechnologies.orient.object.db;


import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.entity.NestedContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 17/07/17.
 */
public class NestedCollectionsTest {
    private ODatabaseObject database;

    @Test
    public void testNestedCollections() {
        NestedContainer container = new NestedContainer("first");
        NestedContainer saved = database.save(container);
        Assert.assertEquals(1, saved.getFoo().size());
        Assert.assertEquals(3, saved.getFoo().get("key-1").size());
    }
}

