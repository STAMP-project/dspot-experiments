package com.vaadin.v7.tests.server.component.fieldgroup;


import com.vaadin.tests.data.bean.BeanWithReadOnlyField;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class FieldGroupWithReadOnlyPropertiesTest {
    private TextField readOnlyField = new TextField();

    private TextField writableField = new TextField();

    @Test
    public void bindReadOnlyPropertyToFieldGroup() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);
        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);
        Assert.assertTrue(readOnlyField.isReadOnly());
        Assert.assertFalse(writableField.isReadOnly());
    }

    @Test
    public void fieldGroupSetReadOnlyTest() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);
        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setReadOnly(true);
        Assert.assertTrue(readOnlyField.isReadOnly());
        Assert.assertTrue(writableField.isReadOnly());
        fieldGroup.setReadOnly(false);
        Assert.assertTrue(readOnlyField.isReadOnly());
        Assert.assertFalse(writableField.isReadOnly());
    }
}

