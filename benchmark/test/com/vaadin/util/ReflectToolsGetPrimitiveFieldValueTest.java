package com.vaadin.util;


import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;


public class ReflectToolsGetPrimitiveFieldValueTest {
    @Test
    public void getFieldValueViaGetter() {
        class MyClass {
            public int field = 1;
        }
        MyClass myInstance = new MyClass();
        Field memberField;
        Object fieldValue = false;
        try {
            memberField = myInstance.getClass().getField("field");
            fieldValue = ReflectTools.getJavaFieldValue(myInstance, memberField);
        } catch (Exception e) {
        }
        Assert.assertFalse((fieldValue instanceof Boolean));
    }
}

