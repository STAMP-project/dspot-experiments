/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrunner;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// @RunWith(ConsoleSpammingMockitoJUnitRunner.class)
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ModellingVerboseMockitoTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldLogUnusedStubbingWarningWhenTestFails() throws Exception {
        Mockito.when(mock.simpleMethod(1)).thenReturn("foo");
        Mockito.when(mock.otherMethod()).thenReturn("foo");
        Mockito.when(mock.booleanObjectReturningMethod()).thenReturn(false);
        // TODO: stubbed with those args here -> stubbed with certain args here
        String ret = mock.simpleMethod(2);
        Assert.assertEquals("foo", ret);
        // TODO: should show message from actual failure not at the bottom but at least below 'the actual failure is ...'
    }

    @Test
    public void shouldNotLogAnythingWhenNoWarnings() throws Exception {
        // stub
        Mockito.when(mock.simpleMethod()).thenReturn("foo");
        // use stub:
        mock.simpleMethod();
        // verify:
        Mockito.verify(mock).simpleMethod();
        // should be no warnings:
        Assert.fail();
    }
}

