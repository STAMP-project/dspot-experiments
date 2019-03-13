/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 188
public class CaptorAnnotationAutoboxingTest extends TestBase {
    interface Fun {
        void doFun(double prmitive);

        void moreFun(int howMuch);
    }

    @Mock
    CaptorAnnotationAutoboxingTest.Fun fun;

    @Captor
    ArgumentCaptor<Double> captor;

    @Test
    public void shouldAutoboxSafely() {
        // given
        fun.doFun(1.0);
        // then
        Mockito.verify(fun).doFun(captor.capture());
        Assert.assertEquals(Double.valueOf(1.0), captor.getValue());
    }

    @Captor
    ArgumentCaptor<Integer> intCaptor;

    @Test
    public void shouldAutoboxAllPrimitives() {
        Mockito.verify(fun, Mockito.never()).moreFun(intCaptor.capture());
    }
}

