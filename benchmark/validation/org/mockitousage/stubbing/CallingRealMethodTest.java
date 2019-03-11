/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class CallingRealMethodTest extends TestBase {
    @Mock
    CallingRealMethodTest.TestedObject mock;

    static class TestedObject {
        String value;

        void setValue(String value) {
            this.value = value;
        }

        String getValue() {
            return "HARD_CODED_RETURN_VALUE";
        }

        String callInternalMethod() {
            return getValue();
        }
    }

    @Test
    public void shouldAllowCallingInternalMethod() {
        Mockito.when(mock.getValue()).thenReturn("foo");
        Mockito.when(mock.callInternalMethod()).thenCallRealMethod();
        Assert.assertEquals("foo", mock.callInternalMethod());
    }

    @Test
    public void shouldReturnRealValue() {
        Mockito.when(mock.getValue()).thenCallRealMethod();
        Assert.assertEquals("HARD_CODED_RETURN_VALUE", mock.getValue());
    }

    @Test
    public void shouldExecuteRealMethod() {
        Mockito.doCallRealMethod().when(mock).setValue(ArgumentMatchers.anyString());
        mock.setValue("REAL_VALUE");
        Assert.assertEquals("REAL_VALUE", mock.value);
    }

    @Test
    public void shouldCallRealMethodByDefault() {
        CallingRealMethodTest.TestedObject mock = Mockito.mock(CallingRealMethodTest.TestedObject.class, Mockito.CALLS_REAL_METHODS);
        Assert.assertEquals("HARD_CODED_RETURN_VALUE", mock.getValue());
    }

    @Test
    public void shouldNotCallRealMethodWhenStubbedLater() {
        CallingRealMethodTest.TestedObject mock = Mockito.mock(CallingRealMethodTest.TestedObject.class);
        Mockito.when(mock.getValue()).thenCallRealMethod();
        Mockito.when(mock.getValue()).thenReturn("FAKE_VALUE");
        Assert.assertEquals("FAKE_VALUE", mock.getValue());
    }
}

