/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class StubbingWithExtraAnswersTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldWorkAsStandardMockito() throws Exception {
        // when
        List<Integer> list = Arrays.asList(1, 2, 3);
        Mockito.when(mock.objectReturningMethodNoArgs()).thenAnswer(AdditionalAnswers.returnsElementsOf(list));
        // then
        Assert.assertEquals(1, mock.objectReturningMethodNoArgs());
        Assert.assertEquals(2, mock.objectReturningMethodNoArgs());
        Assert.assertEquals(3, mock.objectReturningMethodNoArgs());
        // last element is returned continuously
        Assert.assertEquals(3, mock.objectReturningMethodNoArgs());
        Assert.assertEquals(3, mock.objectReturningMethodNoArgs());
    }

    @Test
    public void shouldReturnNullIfNecessary() throws Exception {
        // when
        List<Integer> list = Arrays.asList(1, null);
        Mockito.when(mock.objectReturningMethodNoArgs()).thenAnswer(AdditionalAnswers.returnsElementsOf(list));
        // then
        Assert.assertEquals(1, mock.objectReturningMethodNoArgs());
        Assert.assertEquals(null, mock.objectReturningMethodNoArgs());
        Assert.assertEquals(null, mock.objectReturningMethodNoArgs());
    }

    @Test
    public void shouldScreamWhenNullPassed() throws Exception {
        try {
            // when
            AdditionalAnswers.returnsElementsOf(null);
            // then
            Assert.fail();
        } catch (MockitoException e) {
        }
    }
}

