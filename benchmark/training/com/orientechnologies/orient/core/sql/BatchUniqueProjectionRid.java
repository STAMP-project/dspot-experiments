package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class BatchUniqueProjectionRid {
    private ODatabaseDocument database;

    @Test
    public void testBatchUniqueRid() {
        List<List<ODocument>> res = database.command(new OCommandScript("begin;let $a = select \"a\" as a ; let $b = select \"a\" as b; return [$a,$b] ")).execute();
        Assert.assertFalse(((res.get(0).get(0).getIdentity().getClusterPosition()) == (res.get(1).get(0).getIdentity().getClusterPosition())));
        // assertEquals(1, res.get(0).get(0).getIdentity().getClusterPosition());
        // assertEquals(2, res.get(1).get(0).getIdentity().getClusterPosition());
    }
}

