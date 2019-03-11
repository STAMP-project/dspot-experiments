/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.junit;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ArgMismatchFinderTest extends TestBase {
    ArgMismatchFinder finder = new ArgMismatchFinder();

    @Mock
    IMethods mock1;

    @Mock
    IMethods mock2;

    @Test
    public void no_interactions() throws Exception {
        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(0, mismatches.size());
    }

    @Test
    public void no_mismatch_when_mock_different() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        mock2.simpleMethod(2);// arg mismatch on different mock

        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(0, mismatches.size());
    }

    @Test
    public void no_mismatch_when_method_different() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        mock1.otherMethod();
        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(0, mismatches.size());
    }

    @Test
    public void no_mismatch_when_stubbing_used() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        mock1.simpleMethod(1);// stub used

        mock1.simpleMethod(2);// no stubbing, but we don't want it to be reported, either

        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(0, mismatches.size());
    }

    @Test
    public void stubbing_mismatch() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        mock1.simpleMethod(2);
        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(1, mismatches.size());
    }

    @Test
    public void single_mismatch_with_multiple_invocations() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        mock1.simpleMethod(2);
        mock1.simpleMethod(3);
        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(1, mismatches.size());
        Assert.assertEquals("{mock1.simpleMethod(1);=[mock1.simpleMethod(2);, mock1.simpleMethod(3);]}", mismatches.toString());
    }

    @Test
    public void single_invocation_with_multiple_stubs() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");
        Mockito.when(mock1.simpleMethod(2)).thenReturn("2");
        mock1.simpleMethod(3);
        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals(2, mismatches.size());
        Assert.assertEquals("{mock1.simpleMethod(1);=[mock1.simpleMethod(3);], mock1.simpleMethod(2);=[mock1.simpleMethod(3);]}", mismatches.toString());
    }

    @Test
    public void mismatch_reports_only_unstubbed_invocations() throws Exception {
        // given
        Mockito.when(mock1.simpleMethod(1)).thenReturn("1");// unused

        Mockito.when(mock1.simpleMethod(2)).thenReturn("2");// used

        mock1.simpleMethod(2);// stubbed

        mock1.simpleMethod(3);// unstubbed

        // when
        StubbingArgMismatches mismatches = finder.getStubbingArgMismatches(Arrays.asList(mock1, mock2));
        // then
        Assert.assertEquals("{mock1.simpleMethod(1);=[mock1.simpleMethod(3);]}", mismatches.toString());
    }
}

