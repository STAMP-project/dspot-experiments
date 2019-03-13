package com.vaadin.data;


import com.vaadin.shared.Registration;
import com.vaadin.ui.Label;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public class ReadOnlyHasValueTest {
    private static final String SAY_SOMETHING = "Say something";

    private static final String SAY_SOMETHING_ELSE = "Say something else";

    private static final String NO_VALUE = "-no-value-";

    private Label label;

    private ReadOnlyHasValue<String> hasValue;

    @Test
    public void testBase() {
        hasValue.setReadOnly(true);
        hasValue.setRequiredIndicatorVisible(false);
        Registration registration = hasValue.addValueChangeListener(( e) -> {
        });
        registration.remove();
        hasValue.setValue(ReadOnlyHasValueTest.SAY_SOMETHING);
        Assert.assertEquals(ReadOnlyHasValueTest.SAY_SOMETHING, hasValue.getValue());
        Assert.assertEquals(ReadOnlyHasValueTest.SAY_SOMETHING, label.getCaption());
        hasValue.setValue(ReadOnlyHasValueTest.SAY_SOMETHING_ELSE);
        Assert.assertEquals(ReadOnlyHasValueTest.SAY_SOMETHING_ELSE, hasValue.getValue());
        Assert.assertEquals(ReadOnlyHasValueTest.SAY_SOMETHING_ELSE, label.getCaption());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRO() {
        hasValue.setReadOnly(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndicator() {
        hasValue.setRequiredIndicatorVisible(true);
    }

    @Test
    public void testBind() {
        Binder<ReadOnlyHasValueTest.Bean> beanBinder = new Binder(ReadOnlyHasValueTest.Bean.class);
        Label label = new Label();
        ReadOnlyHasValue<Long> intHasValue = new ReadOnlyHasValue(( i) -> label.setValue(Objects.toString(i, "")));
        beanBinder.forField(intHasValue).bind("v");
        beanBinder.readBean(new ReadOnlyHasValueTest.Bean(42));
        Assert.assertEquals("42", label.getValue());
        Assert.assertEquals(42L, intHasValue.getValue().longValue());
        Registration registration = intHasValue.addValueChangeListener(( e) -> {
            assertEquals(42L, e.getOldValue().longValue());
            assertSame(intHasValue, e.getSource());
            assertSame(null, e.getComponent());
            assertSame(null, e.getComponent());
            assertFalse(e.isUserOriginated());
        });
        beanBinder.readBean(new ReadOnlyHasValueTest.Bean(1984));
        Assert.assertEquals("1984", label.getValue());
        Assert.assertEquals(1984L, intHasValue.getValue().longValue());
        registration.remove();
        beanBinder.readBean(null);
        Assert.assertEquals("", label.getValue());
        Assert.assertEquals(null, intHasValue.getValue());
    }

    @Test
    public void testEmptyValue() {
        Binder<ReadOnlyHasValueTest.Bean> beanBinder = new Binder(ReadOnlyHasValueTest.Bean.class);
        Label label = new Label();
        ReadOnlyHasValue<String> strHasValue = new ReadOnlyHasValue(label::setValue, ReadOnlyHasValueTest.NO_VALUE);
        beanBinder.forField(strHasValue).withConverter(Long::parseLong, (Long i) -> "" + i).bind("v");
        beanBinder.readBean(new ReadOnlyHasValueTest.Bean(42));
        Assert.assertEquals("42", label.getValue());
        beanBinder.readBean(null);
        Assert.assertEquals(ReadOnlyHasValueTest.NO_VALUE, label.getValue());
        Assert.assertTrue(strHasValue.isEmpty());
    }

    public static class Bean {
        public Bean(long v) {
            this.v = v;
        }

        private long v;

        public long getV() {
            return v;
        }

        public void setV(long v) {
            this.v = v;
        }
    }
}

