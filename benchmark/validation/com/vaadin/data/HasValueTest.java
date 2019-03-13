package com.vaadin.data;


import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class HasValueTest {
    public abstract static class TestHasValue implements HasValue<String> {
        @Override
        public void clear() {
            HasValue.super.clear();
        }
    }

    @Test
    public void clear() {
        HasValueTest.TestHasValue hasValue = Mockito.mock(HasValueTest.TestHasValue.class);
        Mockito.doCallRealMethod().when(hasValue).clear();
        String value = "foo";
        Mockito.when(getEmptyValue()).thenReturn(value);
        hasValue.clear();
        setValue(value);
    }

    @Test
    public void getOptionalValue_nullableHasValue() {
        HasValue<LocalDate> nullable = new DateField();
        // Not using Assert since we're only verifying that DateField is working
        // in a way appropriate for this test
        assert nullable.isEmpty();
        assert (nullable.getValue()) == null;
        Assert.assertFalse(nullable.getOptionalValue().isPresent());
        nullable.setValue(LocalDate.now());
        assert !(nullable.isEmpty());
        Assert.assertSame(nullable.getValue(), nullable.getOptionalValue().get());
    }

    @Test
    public void getOptionalValue_nonNullableHasValue() {
        HasValue<String> nonNullable = new TextField();
        // Not using Assert since we're only verifying that TextField is working
        // in a way appropriate for this test
        assert nonNullable.isEmpty();
        assert (nonNullable.getValue()) != null;
        Assert.assertFalse(nonNullable.getOptionalValue().isPresent());
        nonNullable.setValue("foo");
        assert !(nonNullable.isEmpty());
        Assert.assertSame(nonNullable.getValue(), nonNullable.getOptionalValue().get());
    }
}

