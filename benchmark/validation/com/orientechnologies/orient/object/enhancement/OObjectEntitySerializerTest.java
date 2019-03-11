package com.orientechnologies.orient.object.enhancement;


import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author LomakiA <a href="mailto:Andrey.Lomakin@exigenservices.com">Andrey Lomakin</a>
 * @since 18.05.12
 */
public class OObjectEntitySerializerTest {
    private OObjectDatabaseTx databaseTx;

    @Test
    public void testCallbacksHierarchy() {
        ExactEntity entity = new ExactEntity();
        databaseTx.save(entity);
        Assert.assertTrue(entity.callbackExecuted());
    }

    @Test
    public void testCallbacksHierarchyUpdate() {
        ExactEntity entity = new ExactEntity();
        entity = databaseTx.save(entity);
        entity.reset();
        databaseTx.save(entity);
        Assert.assertTrue(entity.callbackExecuted());
    }
}

