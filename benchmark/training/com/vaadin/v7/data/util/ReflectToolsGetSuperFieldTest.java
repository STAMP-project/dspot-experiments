package com.vaadin.v7.data.util;


import com.vaadin.annotations.PropertyId;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class ReflectToolsGetSuperFieldTest {
    @Test
    public void getFieldFromSuperClass() {
        class MyClass {
            @PropertyId("testProperty")
            TextField test = new TextField("This is a test");
        }
        // no fields here
        class MySubClass extends MyClass {}
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("testProperty", new ObjectProperty<String>("Value of testProperty"));
        MySubClass form = new MySubClass();
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);
        Assert.assertTrue("Value of testProperty".equals(form.test.getValue()));
    }
}

