package com.vaadin.data;


import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.TextField;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class BinderValueChangeTest extends BinderTestBase<Binder<Person>, Person> {
    private AtomicReference<ValueChangeEvent<?>> event;

    private static class TestTextField extends TextField {
        @Override
        protected boolean setValue(String value, boolean userOriginated) {
            return super.setValue(value, userOriginated);
        }
    }

    @Test
    public void unboundField_noEvents() {
        binder.addValueChangeListener(this::statusChanged);
        BindingBuilder<Person, String> binding = binder.forField(nameField);
        nameField.setValue("");
        Assert.assertNull(event.get());
        binding.bind(Person::getFirstName, Person::setFirstName);
        Assert.assertNull(event.get());
    }

    @Test
    public void setBean_unbound_noEvents() {
        binder.addValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        binder.setBean(item);
        Assert.assertNull(event.get());
    }

    @Test
    public void readBean_unbound_noEvents() {
        binder.addValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        binder.readBean(item);
        Assert.assertNull(event.get());
    }

    @Test
    public void setValue_unbound_singleEventOnSetValue() {
        binder.forField(nameField).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(new StringToIntegerConverter("")).bind(Person::getAge, Person::setAge);
        binder.addValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent(nameField);
    }

    @Test
    public void setValue_bound_singleEventOnSetValue() {
        binder.forField(nameField).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(new StringToIntegerConverter("")).bind(Person::getAge, Person::setAge);
        binder.setBean(item);
        binder.addValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent(nameField);
    }

    @Test
    public void userOriginatedUpdate_unbound_singleEventOnSetValue() {
        BinderValueChangeTest.TestTextField field = new BinderValueChangeTest.TestTextField();
        binder.forField(field).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(new StringToIntegerConverter("")).bind(Person::getAge, Person::setAge);
        binder.addValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        field.setValue("foo", true);
        verifyEvent(field, true);
    }

    @Test
    public void addListenerFirst_bound_singleEventOnSetValue() {
        binder.addValueChangeListener(this::statusChanged);
        binder.forField(nameField).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(new StringToIntegerConverter("")).bind(Person::getAge, Person::setAge);
        binder.setBean(item);
        Assert.assertNull(event.get());
        ageField.setValue(String.valueOf(1));
        verifyEvent(ageField);
    }
}

