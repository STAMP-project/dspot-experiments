package com.orientechnologies.orient.object.enhancement;


import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author JN <a href="mailto:jn@brain-activit.com">Julian Neuhaus</a>
 * @author Nathan Brown (anecdotesoftware--at--gmail.com)
 * @since 18.08.2014
 */
public class OObjectProxyMethodHandlerTest {
    private OObjectDatabaseTx databaseTx;

    private Map<String, Object> fieldsAndThereDefaultValue;

    @Test
    public void reloadTestForMapsInTarget() {
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes targetObject = this.databaseTx.newInstance(OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes.class);
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes childObject = this.databaseTx.newInstance(OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        targetObject.setStringStringMap(map);
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key", "value");
        Map<String, String> map3 = childObject.getStringStringMap();
        map2.put("key", "value");
        targetObject.getListOfEntityWithDifferentFieldTypes().add(childObject);
        targetObject = this.databaseTx.save(targetObject);
        for (String key : targetObject.getStringStringMap().keySet()) {
            Assert.assertTrue(key.equals("key"));
        }
        targetObject.getStringStringMap().put("key2", "value2");
        childObject.getStringStringMap().put("key3", "value3");
        targetObject = this.databaseTx.save(targetObject);
        // targetObject = this.databaseTx.load(targetObject);
        targetObject.getStringStringMap().get("key");
        targetObject.getStringStringMap2().get("key2");
        targetObject.getStringStringMap2().put("key3", "value3");
    }

    @Test
    public void reloadTestForListsInTarget() {
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes targetObject = new OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes();
        List<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes> entitieList = new ArrayList<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes>();
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes listObject1 = new OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes();
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes listObject2 = new OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes();
        listObject1.setBooleanField(true);
        listObject1.setByteField(Byte.MIN_VALUE);
        listObject1.setDoubleField(1.1);
        listObject1.setFloatField(1.0F);
        listObject1.setIntField(13);
        listObject1.setLongField(10);
        listObject1.setShortField(Short.MIN_VALUE);
        listObject1.setStringField("TEST2");
        listObject2.setBooleanField(true);
        listObject2.setByteField(Byte.MIN_VALUE);
        listObject2.setDoubleField(1.1);
        listObject2.setFloatField(1.0F);
        listObject2.setIntField(13);
        listObject2.setLongField(10);
        listObject2.setShortField(Short.MIN_VALUE);
        listObject2.setStringField("TEST2");
        entitieList.add(listObject1);
        entitieList.add(listObject2);
        targetObject.setListOfEntityWithDifferentFieldTypes(entitieList);
        targetObject = this.databaseTx.save(targetObject);
        for (OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes entity : targetObject.getListOfEntityWithDifferentFieldTypes()) {
            Assert.assertTrue(((entity.isBooleanField()) == true));
            Assert.assertTrue(((entity.getByteField()) == (Byte.MIN_VALUE)));
            Assert.assertTrue(((entity.getDoubleField()) == 1.1));
            Assert.assertTrue(((entity.getFloatField()) == 1.0F));
            Assert.assertTrue(((entity.getIntField()) == 13));
            Assert.assertTrue(((entity.getLongField()) == 10));
            Assert.assertTrue(((entity.getObjectField()) == null));
            Assert.assertTrue(((entity.getShortField()) == (Short.MIN_VALUE)));
            Assert.assertTrue("TEST2".equals(entity.getStringField()));
            entity.setBooleanField(false);
            entity.setByteField(Byte.MAX_VALUE);
            entity.setDoubleField(3.1);
            entity.setFloatField(2.0F);
            entity.setIntField(15);
            entity.setLongField(11);
            entity.setShortField(Short.MAX_VALUE);
            entity.setStringField("TEST3");
            entity.setObjectField(new OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes());
        }
        for (OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes entity : targetObject.getListOfEntityWithDifferentFieldTypes()) {
            this.databaseTx.reload(entity);
            Assert.assertTrue(((entity.isBooleanField()) == true));
            Assert.assertTrue(((entity.getByteField()) == (Byte.MIN_VALUE)));
            Assert.assertTrue(((entity.getDoubleField()) == 1.1));
            Assert.assertTrue(((entity.getFloatField()) == 1.0F));
            Assert.assertTrue(((entity.getIntField()) == 13));
            Assert.assertTrue(((entity.getLongField()) == 10));
            Assert.assertTrue(((entity.getObjectField()) == null));
            Assert.assertTrue(((entity.getShortField()) == (Short.MIN_VALUE)));
            Assert.assertTrue("TEST2".equals(entity.getStringField()));
        }
    }

    @Test
    public void getDefaultValueForFieldTest() {
        OObjectProxyMethodHandler handler = new OObjectProxyMethodHandler(null);
        Method m = null;
        try {
            m = handler.getClass().getDeclaredMethod("getDefaultValueForField", Field.class);
            m.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception");
        }
        Assert.assertTrue((m != null));
        OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes testEntity = new OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes();
        for (String fieldName : fieldsAndThereDefaultValue.keySet()) {
            Field field = null;
            try {
                field = testEntity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Unexpected exception");
            }
            Assert.assertTrue((field != null));
            try {
                Object defaultValue = m.invoke(handler, field);
                Object expectedValue = fieldsAndThereDefaultValue.get(fieldName);
                if (expectedValue == null)
                    Assert.assertTrue((defaultValue == null));
                else
                    Assert.assertTrue(expectedValue.equals(defaultValue));

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Unexpected exception");
            }
        }
    }

    @Test
    public void testEntityWithEmbeddedFieldDetachingAllWithoutError() throws Exception {
        OObjectProxyMethodHandlerTest.EntityWithEmbeddedFields entity = new OObjectProxyMethodHandlerTest.EntityWithEmbeddedFields();
        entity.setEmbeddedType1(new OObjectProxyMethodHandlerTest.EmbeddedType1());
        entity.setEmbeddedType2(new OObjectProxyMethodHandlerTest.EmbeddedType2());
        OObjectProxyMethodHandlerTest.EntityWithEmbeddedFields saved = databaseTx.save(entity);
        databaseTx.detachAll(saved, true);
    }

    public static class EmbeddedType1 {}

    public static class EmbeddedType2 {}

    public static class EntityWithEmbeddedFields {
        @Embedded
        private OObjectProxyMethodHandlerTest.EmbeddedType1 _embeddedType1;

        @Embedded
        private OObjectProxyMethodHandlerTest.EmbeddedType2 _embeddedType2;

        public EntityWithEmbeddedFields() {
        }

        public OObjectProxyMethodHandlerTest.EmbeddedType1 getEmbeddedType1() {
            return _embeddedType1;
        }

        public void setEmbeddedType1(OObjectProxyMethodHandlerTest.EmbeddedType1 embeddedType1) {
            _embeddedType1 = embeddedType1;
        }

        public OObjectProxyMethodHandlerTest.EmbeddedType2 getEmbeddedType2() {
            return _embeddedType2;
        }

        public void setEmbeddedType2(OObjectProxyMethodHandlerTest.EmbeddedType2 embeddedType2) {
            _embeddedType2 = embeddedType2;
        }
    }

    public class EntityWithDifferentFieldTypes {
        private byte byteField;

        private short shortField;

        private int intField;

        private long longField;

        private float floatField;

        private double doubleField;

        private String stringField;

        private boolean booleanField;

        private OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes objectField;

        private Map<String, String> stringStringMap = new HashMap<String, String>();

        private Map<String, String> stringStringMap2 = new HashMap<String, String>();

        @OneToMany(cascade = CascadeType.ALL)
        private List<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes> listOfEntityWithDifferentFieldTypes;

        public EntityWithDifferentFieldTypes() {
            super();
            this.listOfEntityWithDifferentFieldTypes = new ArrayList<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes>();
        }

        public byte getByteField() {
            return byteField;
        }

        public void setByteField(byte byteField) {
            this.byteField = byteField;
        }

        public short getShortField() {
            return shortField;
        }

        public void setShortField(short shortField) {
            this.shortField = shortField;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public long getLongField() {
            return longField;
        }

        public void setLongField(long longField) {
            this.longField = longField;
        }

        public float getFloatField() {
            return floatField;
        }

        public void setFloatField(float floatField) {
            this.floatField = floatField;
        }

        public double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(double doubleField) {
            this.doubleField = doubleField;
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public boolean isBooleanField() {
            return booleanField;
        }

        public void setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
        }

        public OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes getObjectField() {
            return objectField;
        }

        public void setObjectField(OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes objectField) {
            this.objectField = objectField;
        }

        public List<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes> getListOfEntityWithDifferentFieldTypes() {
            return listOfEntityWithDifferentFieldTypes;
        }

        public void setListOfEntityWithDifferentFieldTypes(List<OObjectProxyMethodHandlerTest.EntityWithDifferentFieldTypes> listOfEntityWithDifferentFieldTypes) {
            this.listOfEntityWithDifferentFieldTypes = listOfEntityWithDifferentFieldTypes;
        }

        public Map<String, String> getStringStringMap() {
            return stringStringMap;
        }

        public void setStringStringMap(Map<String, String> stringStringMap) {
            this.stringStringMap = stringStringMap;
        }

        public Map<String, String> getStringStringMap2() {
            return stringStringMap2;
        }

        public void setStringStringMap2(Map<String, String> stringStringMap2) {
            this.stringStringMap2 = stringStringMap2;
        }
    }
}

