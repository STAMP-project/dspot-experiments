/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class VerificationAndStubbingUsingMatchersTest extends TestBase {
    private IMethods one;

    private IMethods two;

    private IMethods three;

    @Test
    public void shouldStubUsingMatchers() {
        Mockito.when(one.simpleMethod(2)).thenReturn("2");
        Mockito.when(two.simpleMethod(ArgumentMatchers.anyString())).thenReturn("any");
        Mockito.when(three.simpleMethod(ArgumentMatchers.startsWith("test"))).thenThrow(new RuntimeException());
        Assert.assertEquals(null, one.simpleMethod(1));
        Assert.assertEquals("2", one.simpleMethod(2));
        Assert.assertEquals("any", two.simpleMethod("two"));
        Assert.assertEquals("any", two.simpleMethod("two again"));
        Assert.assertEquals(null, three.simpleMethod("three"));
        Assert.assertEquals(null, three.simpleMethod("three again"));
        try {
            three.simpleMethod("test three again");
            Assert.fail();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void shouldVerifyUsingMatchers() {
        Mockito.doThrow(new RuntimeException()).when(one).oneArg(true);
        Mockito.when(three.varargsObject(5, "first arg", "second arg")).thenReturn("stubbed");
        try {
            one.oneArg(true);
            Assert.fail();
        } catch (RuntimeException e) {
        }
        one.simpleMethod(100);
        two.simpleMethod("test Mockito");
        three.varargsObject(10, "first arg", "second arg");
        Assert.assertEquals("stubbed", three.varargsObject(5, "first arg", "second arg"));
        Mockito.verify(one).oneArg(ArgumentMatchers.eq(true));
        Mockito.verify(one).simpleMethod(ArgumentMatchers.anyInt());
        Mockito.verify(two).simpleMethod(ArgumentMatchers.startsWith("test"));
        Mockito.verify(three).varargsObject(5, "first arg", "second arg");
        Mockito.verify(three).varargsObject(ArgumentMatchers.eq(10), ArgumentMatchers.eq("first arg"), ArgumentMatchers.startsWith("second"));
        Mockito.verifyNoMoreInteractions(one, two, three);
        try {
            Mockito.verify(three).varargsObject(ArgumentMatchers.eq(10), ArgumentMatchers.eq("first arg"), ArgumentMatchers.startsWith("third"));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }
}

