package com.orientechnologies.orient.core.metadata.schema;


import OType.SHORT;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSchemaException;
import org.junit.Assert;
import org.junit.Test;


public class OSchemaSharedGlobalPropertyTest {
    private ODatabaseDocumentTx db;

    @Test
    public void testGlobalPropertyCreate() {
        OSchema schema = db.getMetadata().getSchema();
        schema.createGlobalProperty("testaasd", SHORT, 100);
        OGlobalProperty prop = schema.getGlobalPropertyById(100);
        Assert.assertEquals(prop.getName(), "testaasd");
        Assert.assertEquals(prop.getId(), ((Integer) (100)));
        Assert.assertEquals(prop.getType(), SHORT);
    }

    @Test
    public void testGlobalPropertyCreateDoubleSame() {
        OSchema schema = db.getMetadata().getSchema();
        schema.createGlobalProperty("test", SHORT, 200);
        schema.createGlobalProperty("test", SHORT, 200);
    }

    @Test(expected = OSchemaException.class)
    public void testGlobalPropertyCreateDouble() {
        OSchema schema = db.getMetadata().getSchema();
        schema.createGlobalProperty("test", SHORT, 201);
        schema.createGlobalProperty("test1", SHORT, 201);
    }
}

