/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 184
public class ShouldMocksCompareToBeConsistentWithEqualsTest extends TestBase {
    @Test
    public void should_compare_to_be_consistent_with_equals() {
        // given
        Date today = Mockito.mock(Date.class);
        Date tomorrow = Mockito.mock(Date.class);
        // when
        Set<Date> set = new TreeSet<Date>();
        set.add(today);
        set.add(tomorrow);
        // then
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void should_compare_to_be_consistent_with_equals_when_comparing_the_same_reference() {
        // given
        Date today = Mockito.mock(Date.class);
        // when
        Set<Date> set = new TreeSet<Date>();
        set.add(today);
        set.add(today);
        // then
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void should_allow_stubbing_and_verifying_compare_to() {
        // given
        Date mock = Mockito.mock(Date.class);
        Mockito.when(mock.compareTo(ArgumentMatchers.any(Date.class))).thenReturn(10);
        // when
        mock.compareTo(new Date());
        // then
        Assert.assertEquals(10, mock.compareTo(new Date()));
        Mockito.verify(mock, Mockito.atLeastOnce()).compareTo(ArgumentMatchers.any(Date.class));
    }

    @Test
    public void should_reset_not_remove_default_stubbing() {
        // given
        Date mock = Mockito.mock(Date.class);
        Mockito.reset(mock);
        // then
        Assert.assertEquals(1, mock.compareTo(new Date()));
    }
}

