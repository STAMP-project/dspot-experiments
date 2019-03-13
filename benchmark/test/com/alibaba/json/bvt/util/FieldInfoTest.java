package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldInfoTest extends TestCase {
    public void test_null() throws Exception {
        FieldInfo fieldInfo = new FieldInfo("getValue", FieldInfoTest.Entity.class.getMethod("getValue"), null, null, null, 0, 0, 0, null, null, null);
        Assert.assertEquals(null, fieldInfo.getAnnotation());
        Field field = FieldInfoTest.GenericFieldEntity.class.getField("value");
        Type type = new ParameterizedTypeImpl(new Type[]{ FieldInfoTest.ValueObject.class }, null, FieldInfoTest.GenericFieldEntity.class);
        FieldInfo fieldInfoOfField = new FieldInfo("value", null, field, FieldInfoTest.GenericFieldEntity.class, type, 0, 0, 0, null, null, null);
        Assert.assertEquals(fieldInfoOfField.fieldType, FieldInfoTest.ValueObject.class);
        Assert.assertEquals(fieldInfoOfField.fieldClass, FieldInfoTest.ValueObject.class);
        field = FieldInfoTest.GenericListFieldEntity.class.getField("value");
        type = new ParameterizedTypeImpl(new Type[]{ FieldInfoTest.ValueObject.class }, null, FieldInfoTest.GenericListFieldEntity.class);
        FieldInfo fieldInfoOfListField = new FieldInfo("value", null, field, FieldInfoTest.GenericListFieldEntity.class, type, 0, 0, 0, null, null, null);
        ParameterizedTypeImpl actualFieldType = ((ParameterizedTypeImpl) (fieldInfoOfListField.fieldType));
        Assert.assertEquals(actualFieldType.getActualTypeArguments()[0], FieldInfoTest.ValueObject.class);
        Assert.assertEquals(actualFieldType.getRawType(), List.class);
        Assert.assertEquals(fieldInfoOfListField.fieldClass, List.class);
        Assert.assertEquals(null, getOwnerType());
        Method method = FieldInfoTest.GenericSetterEntity.class.getMethod("setValue", Object.class);
        type = new ParameterizedTypeImpl(new Type[]{ FieldInfoTest.ValueObject.class }, null, FieldInfoTest.GenericSetterEntity.class);
        FieldInfo fieldInfoOfSetter = new FieldInfo("value", method, null, FieldInfoTest.GenericSetterEntity.class, type, 0, 0, 0, null, null, null);
        Assert.assertEquals(fieldInfoOfSetter.fieldType, FieldInfoTest.ValueObject.class);
        Assert.assertEquals(fieldInfoOfSetter.fieldClass, FieldInfoTest.ValueObject.class);
        fieldInfoOfSetter.toString();
        method = FieldInfoTest.GenericListSetterEntity.class.getMethod("setValue", List.class);
        type = new ParameterizedTypeImpl(new Type[]{ FieldInfoTest.ValueObject.class }, null, FieldInfoTest.GenericListSetterEntity.class);
        FieldInfo fieldInfoOfListSetter = new FieldInfo("value", method, null, FieldInfoTest.GenericListSetterEntity.class, type, 0, 0, 0, null, null, null);
        Assert.assertEquals(actualFieldType.getActualTypeArguments()[0], FieldInfoTest.ValueObject.class);
        Assert.assertEquals(actualFieldType.getRawType(), List.class);
        Assert.assertEquals(fieldInfoOfListSetter.fieldClass, List.class);
    }

    public static class Entity {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class GenericSetterEntity<T> {
        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    public static class GenericListSetterEntity<T> {
        private List<T> value;

        public List<T> getValue() {
            return value;
        }

        public void setValue(List<T> value) {
            this.value = value;
        }
    }

    public static class GenericFieldEntity<T> {
        public T value;
    }

    public static class GenericListFieldEntity<T> {
        public List<T> value;
    }

    public static class ValueObject {
        private String name;

        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

