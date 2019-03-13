package com.vaadin.data;


import Binder.Binding;
import com.vaadin.tests.data.bean.BeanToValidate;
import org.junit.Assert;
import org.junit.Test;


public class UnbindTest extends BinderTestBase<Binder<BeanToValidate>, BeanToValidate> {
    @Test
    public void binding_unbind_shouldBeRemovedFromBindings() {
        Binding<BeanToValidate, String> firstnameBinding = binder.bind(nameField, "firstname");
        Assert.assertEquals(1, binder.getBindings().size());
        firstnameBinding.unbind();
        Assert.assertTrue(binder.getBindings().isEmpty());
        Assert.assertNull(firstnameBinding.getField());
    }

    @Test
    public void binding_unbindDuringReadBean_shouldBeRemovedFromBindings() {
        Binding<BeanToValidate, String> firstnameBinding = binder.bind(nameField, "firstname");
        Binding<BeanToValidate, String> ageBinding = binder.bind(ageField, "age");
        Assert.assertEquals(2, binder.getBindings().size());
        nameField.addValueChangeListener(( event) -> {
            if ((event.getValue().length()) > 0)
                ageBinding.unbind();

        });
        binder.readBean(item);
        Assert.assertEquals(1, binder.getBindings().size());
        Assert.assertNull(ageBinding.getField());
    }

    @Test
    public void binding_unbindTwice_shouldBeRemovedFromBindings() {
        Binding<BeanToValidate, String> firstnameBinding = binder.bind(nameField, "firstname");
        Assert.assertEquals(1, binder.getBindings().size());
        firstnameBinding.unbind();
        firstnameBinding.unbind();
        Assert.assertTrue(binder.getBindings().isEmpty());
        Assert.assertNull(firstnameBinding.getField());
    }
}

