package com.orientechnologies.orient.object;


import OType.STRING;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import org.junit.Assert;
import org.junit.Test;


public class CustomDatatypeTest {
    @Test
    public void reproduce() throws Exception {
        final OObjectDatabaseTx db = new OObjectDatabaseTx("memory:CustomDatatypeTest");
        db.create();
        try {
            // WrappedString custom datatype registration (storing it as
            // OType.STRING)
            OObjectSerializerContext serializerContext = new OObjectSerializerContext();
            serializerContext.bind(new com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer<CustomDatatypeTest.WrappedString, String>() {
                @Override
                public String serializeFieldValue(Class<?> iClass, CustomDatatypeTest.WrappedString iFieldValue) {
                    return iFieldValue.getValue();
                }

                @Override
                public CustomDatatypeTest.WrappedString unserializeFieldValue(Class<?> iClass, String iFieldValue) {
                    final CustomDatatypeTest.WrappedString result = new CustomDatatypeTest.WrappedString();
                    result.setValue(iFieldValue);
                    return result;
                }
            }, db);
            OObjectSerializerHelper.bindSerializerContext(CustomDatatypeTest.WrappedString.class, serializerContext);
            // we want schema to be generated
            db.setAutomaticSchemaGeneration(true);
            // register our entity
            db.getEntityManager().registerEntityClass(CustomDatatypeTest.Entity.class);
            // Validate DB did figure out schema properly
            Assert.assertEquals(db.getMetadata().getSchema().getClass(CustomDatatypeTest.Entity.class).getProperty("data").getType(), STRING);
        } finally {
            db.drop();
        }
    }

    public static class WrappedString {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return ("Key [value=" + (getValue())) + "]";
        }
    }

    public static class Entity {
        private String name;

        private CustomDatatypeTest.WrappedString data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CustomDatatypeTest.WrappedString getData() {
            return data;
        }

        public void setData(CustomDatatypeTest.WrappedString value) {
            this.data = value;
        }

        @Override
        public String toString() {
            return ((("Entity [name=" + (getName())) + ", data=") + (getData())) + "]";
        }
    }
}

