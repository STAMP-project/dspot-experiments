package com.orientechnologies.orient.object.db;


import OType.LINK;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.entity.ObjectWithSet;
import com.orientechnologies.orient.object.db.entity.SimpleChild;
import com.orientechnologies.orient.object.db.entity.SimpleParent;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 17/02/17.
 */
public class SimpleParentChildTest {
    private ODatabaseObject database;

    String url = "memory:" + (SimpleParentChildTest.class.getSimpleName());

    @Test
    public void testParentChild() {
        SimpleChild sc = new SimpleChild();
        sc.setName("aa");
        SimpleParent sa = new SimpleParent();
        sa.setChild(sc);
        SimpleParent ret = database.save(sa);
        database.getLocalCache().clear();
        ODocument doc = getUnderlying().load(ret.getId().getIdentity());
        Assert.assertEquals(doc.fieldType("child"), LINK);
    }

    @Test
    public void testWithSets() {
        ObjectWithSet parent = new ObjectWithSet();
        ObjectWithSet child = new ObjectWithSet();
        parent.addFriend(child);
        child.setName("child1");
        ObjectWithSet savedParent = database.save(parent);
        String parentId = savedParent.getId();
        this.database.close();
        this.database = new OObjectDatabaseTx(url);
        this.database.open("admin", "admin");
        ObjectWithSet retrievedParent = this.database.load(new ORecordId(parentId));
        ObjectWithSet retrievedChild = retrievedParent.getFriends().iterator().next();
        retrievedChild.setName("child2");
        this.database.save(retrievedParent);
        this.database.close();
        this.database = new OObjectDatabaseTx(url);
        this.database.open("admin", "admin");
        retrievedParent = this.database.load(new ORecordId(parentId));
        Assert.assertEquals("child2", retrievedParent.getFriends().iterator().next().getName());
    }
}

