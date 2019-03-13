package com.vaadin.v7.tests.server.component.fieldgroup;


import com.vaadin.ui.FormLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class CaseInsensitiveBindingTest {
    @Test
    public void caseInsensitivityAndUnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("LastName", new com.vaadin.v7.data.util.ObjectProperty<String>("Sparrow"));
        class MyForm extends FormLayout {
            TextField lastName = new TextField("Last name");

            public MyForm() {
                // Should bind to the LastName property
                addComponent(lastName);
            }
        }
        MyForm form = new MyForm();
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);
        Assert.assertTrue("Sparrow".equals(form.lastName.getValue()));
    }

    @Test
    public void UnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name", new com.vaadin.v7.data.util.ObjectProperty<String>("Jack"));
        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // Should bind to the first_name property
                addComponent(firstName);
            }
        }
        MyForm form = new MyForm();
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);
        Assert.assertTrue("Jack".equals(form.firstName.getValue()));
    }

    @Test
    public void perfectMatchPriority() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name", new com.vaadin.v7.data.util.ObjectProperty<String>("Not this"));
        item.addItemProperty("firstName", new com.vaadin.v7.data.util.ObjectProperty<String>("This"));
        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // should bind to the firstName property, not first_name
                // property
                addComponent(firstName);
            }
        }
        MyForm form = new MyForm();
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);
        Assert.assertTrue("This".equals(form.firstName.getValue()));
    }
}

