/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class StubbingConsecutiveAnswersTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void should_return_consecutive_values() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one").thenReturn("two").thenReturn("three");
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertEquals("two", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_values_for_two_nulls() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn(null, ((String[]) (null)));
        Assert.assertNull(mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_values_first_var_arg_null() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one", ((String) (null)));
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_values_var_arg_null() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one", ((String[]) (null)));
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_values_var_args_contain_null() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one", "two", null);
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertEquals("two", mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
        Assert.assertNull(mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_values_set_by_shorten_then_return_method() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one", "two", "three");
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertEquals("two", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
        Assert.assertEquals("three", mock.simpleMethod());
    }

    @Test
    public void should_return_consecutive_value_and_throw_exceptions_set_by_shorten_return_methods() {
        Mockito.when(mock.simpleMethod()).thenReturn("zero").thenReturn("one", "two").thenThrow(new NullPointerException(), new RuntimeException()).thenReturn("three").thenThrow(new IllegalArgumentException());
        Assert.assertEquals("zero", mock.simpleMethod());
        Assert.assertEquals("one", mock.simpleMethod());
        Assert.assertEquals("two", mock.simpleMethod());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (RuntimeException expected) {
        }
        Assert.assertEquals("three", mock.simpleMethod());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void should_throw_consecutively() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new RuntimeException()).thenThrow(new IllegalArgumentException()).thenThrow(new NullPointerException());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (RuntimeException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void should_throw_consecutively_set_by_shorten_then_throw_method() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new RuntimeException(), new IllegalArgumentException(), new NullPointerException());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (RuntimeException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void should_throw_classes() throws Exception {
        // Unavoidable JDK7+ 'unchecked generic array creation' warning
        Mockito.when(mock.simpleMethod()).thenThrow(IllegalArgumentException.class);
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_throw_consecutively_classes_set_by_shorten_then_throw_method() throws Exception {
        // Unavoidable JDK7+ 'unchecked generic array creation' warning
        Mockito.when(mock.simpleMethod()).thenThrow(RuntimeException.class, IllegalArgumentException.class, NullPointerException.class);
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (RuntimeException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void should_mix_consecutive_returns_with_exceptions() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new IllegalArgumentException()).thenReturn("one").thenThrow(new NullPointerException()).thenReturn(null);
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("one", mock.simpleMethod());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        Assert.assertEquals(null, mock.simpleMethod());
        Assert.assertEquals(null, mock.simpleMethod());
    }

    @Test(expected = MockitoException.class)
    public void should_validate_consecutive_exception() throws Exception {
        Mockito.when(mock.simpleMethod()).thenReturn("one").thenThrow(new Exception());
    }

    @Test
    public void should_stub_void_method_and_continue_throwing() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).doNothing().doThrow(new NullPointerException()).when(mock).voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        mock.voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void should_stub_void_method() throws Exception {
        Mockito.doNothing().doThrow(new NullPointerException()).doNothing().when(mock).voidMethod();
        mock.voidMethod();
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        mock.voidMethod();
        mock.voidMethod();
    }

    @Test(expected = MockitoException.class)
    public void should_validate_consecutive_exception_for_void_method() throws Exception {
        Mockito.doNothing().doThrow(new Exception()).when(mock).voidMethod();
    }
}

