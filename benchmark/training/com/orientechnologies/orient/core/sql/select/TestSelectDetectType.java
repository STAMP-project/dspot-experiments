package com.orientechnologies.orient.core.sql.select;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestSelectDetectType {
    private ODatabaseDocument db;

    @Test
    public void testFloatDetection() {
        List<ODocument> res = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select ty.type() from ( select 1.021484375 as ty)"));
        System.out.println(res.get(0));
        Assert.assertEquals(res.get(0).field("ty"), "DOUBLE");
    }
}

