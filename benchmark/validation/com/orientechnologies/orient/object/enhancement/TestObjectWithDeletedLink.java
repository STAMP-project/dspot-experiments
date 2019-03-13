package com.orientechnologies.orient.object.enhancement;


import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.junit.Assert;
import org.junit.Test;


public class TestObjectWithDeletedLink {
    private ODatabaseObject db;

    @Test
    public void testDeletedLink() {
        db.activateOnCurrentThread();
        SimpleSelfRef ob1 = new SimpleSelfRef();
        ob1.setName("hobby one ");
        SimpleSelfRef ob2 = new SimpleSelfRef();
        ob2.setName("2");
        ob1.setFriend(ob2);
        ob1 = db.save(ob1);
        ob1 = db.reload(ob1, "", true);
        ob2 = ob1.getFriend();
        Assert.assertNotNull(ob1.getFriend());
        db.delete(ob2);
        ob1 = db.reload(ob1, "", true);
        Assert.assertNull(ob1.getFriend());
    }
}

