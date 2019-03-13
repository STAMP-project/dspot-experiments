package com.orientechnologies.orient.object.db;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Anders Heintz on 20/06/15.
 */
public class OObjectLazyListTest {
    private OObjectDatabaseTx databaseTx;

    private int count;

    @Test
    public void positionTest() {
        OObjectLazyListTest.EntityObjectWithList listObject = getTestObject();
        listObject = databaseTx.save(listObject);
        OObjectLazyListTest.EntityObject newObject = new OObjectLazyListTest.EntityObject();
        newObject.setFieldValue("NewObject");
        OObjectLazyListTest.EntityObject newObject2 = new OObjectLazyListTest.EntityObject();
        newObject2.setFieldValue("NewObject2");
        listObject.getEntityObjects().add(0, newObject);
        listObject.getEntityObjects().add(listObject.getEntityObjects().size(), newObject2);
        listObject = databaseTx.save(listObject);
        assert listObject.getEntityObjects().get(0).getFieldValue().equals("NewObject");
        assert listObject.getEntityObjects().get(((listObject.getEntityObjects().size()) - 1)).getFieldValue().equals("NewObject2");
        listObject.getEntityObjects().stream().forEach(( entityObject) -> {
            Assert.assertNotNull(entityObject);
        });
    }

    @Test
    public void stream() {
        OObjectLazyListTest.EntityObjectWithList listObject = getTestObject();
        listObject = databaseTx.save(listObject);
        OObjectLazyListTest.EntityObject newObject = new OObjectLazyListTest.EntityObject();
        newObject.setFieldValue("NewObject");
        OObjectLazyListTest.EntityObject newObject2 = new OObjectLazyListTest.EntityObject();
        newObject2.setFieldValue("NewObject2");
        listObject.getEntityObjects().add(0, newObject);
        listObject.getEntityObjects().add(listObject.getEntityObjects().size(), newObject2);
        listObject = databaseTx.save(listObject);
        count = 0;
        listObject.getEntityObjects().stream().forEach(( entityObject) -> {
            Assert.assertNotNull(entityObject);
            (count)++;
        });
        Assert.assertEquals(listObject.getEntityObjects().size(), count);
    }

    private static class EntityObjectWithList {
        private List<OObjectLazyListTest.EntityObject> entityObjects = new ArrayList<OObjectLazyListTest.EntityObject>();

        public EntityObjectWithList() {
        }

        public List<OObjectLazyListTest.EntityObject> getEntityObjects() {
            return entityObjects;
        }

        public void setEntityObjects(List<OObjectLazyListTest.EntityObject> entityObjects) {
            this.entityObjects = entityObjects;
        }
    }

    private static class EntityObject {
        private String fieldValue = null;

        public EntityObject() {
        }

        public String getFieldValue() {
            return fieldValue;
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
        }
    }
}

