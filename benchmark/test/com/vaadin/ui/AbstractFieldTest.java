package com.vaadin.ui;


import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class AbstractFieldTest extends EasyMockSupport {
    private final class IdentityTextField extends AbstractFieldTest.TextField {
        @Override
        protected boolean isDifferentValue(String newValue) {
            // Checks for identity instead of equality
            return newValue != (getValue());
        }
    }

    class TextField extends AbstractField<String> {
        String value = "";

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    AbstractFieldTest.TextField field;

    ValueChangeListener<String> l;

    Capture<ValueChangeEvent<String>> capture;

    @Test
    public void readOnlyFieldAcceptsValueChangeFromServer() {
        setReadOnly(true);
        setValue("foo");
        Assert.assertEquals("foo", field.getValue());
    }

    @Test
    public void readOnlyFieldIgnoresValueChangeFromClient() {
        setReadOnly(true);
        field.setValue("bar", true);
        Assert.assertEquals("", field.getValue());
    }

    @Test
    public void valueChangeListenerInvoked() {
        l.valueChange(EasyMock.capture(capture));
        replayAll();
        setValue("foo");
        field.addValueChangeListener(l);
        setValue("bar");
        assertEventEquals(capture.getValue(), "bar", field, false);
        verifyAll();
    }

    @Test
    public void valueChangeListenerInvokedFromClient() {
        l.valueChange(EasyMock.capture(capture));
        replayAll();
        setValue("foo");
        field.addValueChangeListener(l);
        field.setValue("bar", true);
        assertEventEquals(capture.getValue(), "bar", field, true);
        verifyAll();
    }

    @Test
    public void valueChangeListenerNotInvokedIfValueUnchanged() {
        // expect zero invocations of l
        replayAll();
        setValue("foo");
        field.addValueChangeListener(l);
        setValue("foo");
        verifyAll();
    }

    @Test
    public void valueChangeListenerNotInvokedAfterRemove() {
        // expect zero invocations of l
        replayAll();
        field.addValueChangeListener(l).remove();
        setValue("foo");
        verifyAll();
    }

    @Test
    public void identityField_realChange() {
        AbstractFieldTest.TextField identityField = new AbstractFieldTest.IdentityTextField();
        identityField.addValueChangeListener(l);
        // Expect event to both listeners for actual change
        l.valueChange(EasyMock.capture(capture));
        replayAll();
        setValue("value");
        verifyAll();
    }

    @Test
    public void identityField_onlyIdentityChange() {
        AbstractFieldTest.TextField identityField = new AbstractFieldTest.IdentityTextField();
        setValue("value");
        identityField.addValueChangeListener(l);
        // Expect event to both listeners for actual change
        l.valueChange(EasyMock.capture(capture));
        replayAll();
        String sameValueDifferentIdentity = new String("value");
        setValue(sameValueDifferentIdentity);
        verifyAll();
    }

    @Test
    public void identityField_noChange() {
        AbstractFieldTest.TextField identityField = new AbstractFieldTest.IdentityTextField();
        setValue("value");
        identityField.addValueChangeListener(l);
        // Expect no event for identical value
        replayAll();
        setValue(identityField.getValue());
        verifyAll();
    }
}

