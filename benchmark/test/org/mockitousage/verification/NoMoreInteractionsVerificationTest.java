/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class NoMoreInteractionsVerificationTest extends TestBase {
    private LinkedList mock;

    @Test
    public void shouldStubbingNotRegisterRedundantInteractions() throws Exception {
        Mockito.when(mock.add("one")).thenReturn(true);
        Mockito.when(mock.add("two")).thenReturn(true);
        mock.add("one");
        Mockito.verify(mock).add("one");
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void shouldVerifyWhenWantedNumberOfInvocationsUsed() throws Exception {
        mock.add("one");
        mock.add("one");
        mock.add("one");
        Mockito.verify(mock, Mockito.times(3)).add("one");
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void shouldVerifyNoInteractionsAsManyTimesAsYouWant() throws Exception {
        Mockito.verifyNoMoreInteractions(mock);
        Mockito.verifyNoMoreInteractions(mock);
        Mockito.verifyZeroInteractions(mock);
        Mockito.verifyZeroInteractions(mock);
    }

    @Test
    public void shouldFailZeroInteractionsVerification() throws Exception {
        mock.clear();
        try {
            Mockito.verifyZeroInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldFailNoMoreInteractionsVerification() throws Exception {
        mock.clear();
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldPrintAllInvocationsWhenVerifyingNoMoreInvocations() throws Exception {
        mock.add(1);
        mock.add(2);
        mock.clear();
        Mockito.verify(mock).add(2);
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            assertThat(e).hasMessageContaining("list of all invocations");
        }
    }

    @Test
    public void shouldNotContainAllInvocationsWhenSingleUnwantedFound() throws Exception {
        mock.add(1);
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            assertThat(e.getMessage()).doesNotContain("list of all invocations");
        }
    }

    @Test
    public void shouldVerifyOneMockButFailOnOther() throws Exception {
        List<String> list = Mockito.mock(List.class);
        Map<String, Integer> map = Mockito.mock(Map.class);
        list.add("one");
        list.add("one");
        map.put("one", 1);
        Mockito.verify(list, Mockito.times(2)).add("one");
        Mockito.verifyNoMoreInteractions(list);
        try {
            Mockito.verifyZeroInteractions(map);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @SuppressWarnings("all")
    @Test(expected = MockitoException.class)
    public void verifyNoMoreInteractionsShouldScreamWhenNullPassed() throws Exception {
        Mockito.verifyNoMoreInteractions(((Object[]) (null)));
    }
}

