package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndexException;
import org.junit.Test;


/**
 * Created by tglman on 02/02/16.
 */
public class CreateIndexCommandTest {
    private ODatabaseDocument database;

    @Test(expected = OIndexException.class)
    public void testCreateIndexOnMissingPropertyWithCollate() {
        database.getMetadata().getSchema().createClass("Test");
        database.command(new OCommandSQL(" create index Test.test on Test(test collate ci) UNIQUE")).execute();
    }
}

