/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.invocation.finder.AllInvocationsFinder;
import org.mockito.invocation.Invocation;
import org.mockito.stubbing.Stubbing;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class AllInvocationsFinderTest extends TestBase {
    private IMethods mockTwo;

    private IMethods mockOne;

    @Test
    public void no_interactions() throws Exception {
        // expect
        Assert.assertTrue(AllInvocationsFinder.find(Arrays.asList(mockOne, mockTwo)).isEmpty());
        Assert.assertTrue(AllInvocationsFinder.findStubbings(Arrays.asList(mockOne, mockTwo)).isEmpty());
    }

    @Test
    public void provides_invocations_in_order() throws Exception {
        // given
        mockOne.simpleMethod(100);
        mockTwo.simpleMethod(200);
        mockOne.simpleMethod(300);
        // when
        List<Invocation> invocations = AllInvocationsFinder.find(Arrays.asList(mockOne, mockTwo));
        // then
        Assert.assertEquals(3, invocations.size());
        assertArgumentEquals(100, invocations.get(0));
        assertArgumentEquals(200, invocations.get(1));
        assertArgumentEquals(300, invocations.get(2));
    }

    @Test
    public void deduplicates_interactions_from_the_same_mock() throws Exception {
        // given
        mockOne.simpleMethod(100);
        // when
        List<Invocation> invocations = AllInvocationsFinder.find(Arrays.asList(mockOne, mockOne, mockOne));
        // then
        Assert.assertEquals(1, invocations.size());
    }

    @Test
    public void provides_stubbings_in_order() throws Exception {
        // given
        mockOne.simpleMethod(50);// ignored, not a stubbing

        Mockito.when(mockOne.simpleMethod(100)).thenReturn("100");
        Mockito.when(mockOne.simpleMethod(200)).thenReturn("200");
        Mockito.when(mockTwo.simpleMethod(300)).thenReturn("300");
        // when
        List<Stubbing> stubbings = new ArrayList<Stubbing>(AllInvocationsFinder.findStubbings(Arrays.asList(mockOne, mockOne, mockTwo)));
        // then
        Assert.assertEquals(3, stubbings.size());
        assertArgumentEquals(100, stubbings.get(0).getInvocation());
        assertArgumentEquals(200, stubbings.get(1).getInvocation());
        assertArgumentEquals(300, stubbings.get(2).getInvocation());
    }
}

