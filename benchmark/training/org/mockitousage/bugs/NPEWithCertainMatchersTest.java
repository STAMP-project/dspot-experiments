/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class NPEWithCertainMatchersTest extends TestBase {
    @Mock
    IMethods mock;

    @Test
    public void shouldNotThrowNPEWhenIntegerPassed() {
        mock.intArgumentMethod(100);
        Mockito.verify(mock).intArgumentMethod(ArgumentMatchers.isA(Integer.class));
    }

    @Test
    public void shouldNotThrowNPEWhenIntPassed() {
        mock.intArgumentMethod(100);
        Mockito.verify(mock).intArgumentMethod(ArgumentMatchers.isA(Integer.class));
    }

    @Test
    public void shouldNotThrowNPEWhenIntegerPassedToEq() {
        mock.intArgumentMethod(100);
        Mockito.verify(mock).intArgumentMethod(ArgumentMatchers.eq(new Integer(100)));
    }

    @Test
    public void shouldNotThrowNPEWhenIntegerPassedToSame() {
        mock.intArgumentMethod(100);
        Mockito.verify(mock, Mockito.never()).intArgumentMethod(ArgumentMatchers.same(new Integer(100)));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotThrowNPEWhenNullPassedToEq() {
        mock.objectArgMethod("not null");
        Mockito.verify(mock).objectArgMethod(ArgumentMatchers.eq(null));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotThrowNPEWhenNullPassedToSame() {
        mock.objectArgMethod("not null");
        Mockito.verify(mock).objectArgMethod(ArgumentMatchers.same(null));
    }
}

