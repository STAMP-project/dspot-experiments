/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.eviction;


import CompositeEvictionChecker.CompositionOperator.AND;
import CompositeEvictionChecker.CompositionOperator.OR;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompositeEvictionCheckerTest {
    @Test(expected = IllegalArgumentException.class)
    public void compositionOperatorCannotBeNull() {
        CompositeEvictionChecker.newCompositeEvictionChecker(null, Mockito.mock(EvictionChecker.class), Mockito.mock(EvictionChecker.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void evictionCheckersCannotBeNull() {
        CompositeEvictionChecker.newCompositeEvictionChecker(AND, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evictionCheckersCannotBeEmpty() {
        CompositeEvictionChecker.newCompositeEvictionChecker(AND);
    }

    @Test
    public void resultShouldReturnTrue_whenAllIsTrue_withAndCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsTrue = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsTrue = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsTrue.isEvictionRequired()).thenReturn(true);
        Mockito.when(evictionChecker2ReturnsTrue.isEvictionRequired()).thenReturn(true);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(AND, evictionChecker1ReturnsTrue, evictionChecker2ReturnsTrue);
        Assert.assertTrue(compositeEvictionChecker.isEvictionRequired());
    }

    @Test
    public void resultShouldReturnFalse_whenAllIsFalse_withAndCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsFalse = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsFalse = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsFalse.isEvictionRequired()).thenReturn(false);
        Mockito.when(evictionChecker2ReturnsFalse.isEvictionRequired()).thenReturn(false);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(AND, evictionChecker1ReturnsFalse, evictionChecker2ReturnsFalse);
        Assert.assertFalse(compositeEvictionChecker.isEvictionRequired());
    }

    @Test
    public void resultShouldReturnFalse_whenOneIsFalse_withAndCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsTrue = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsFalse = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsTrue.isEvictionRequired()).thenReturn(true);
        Mockito.when(evictionChecker2ReturnsFalse.isEvictionRequired()).thenReturn(false);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(AND, evictionChecker1ReturnsTrue, evictionChecker2ReturnsFalse);
        Assert.assertFalse(compositeEvictionChecker.isEvictionRequired());
    }

    @Test
    public void resultShouldReturnTrue_whenAllIsTrue_withOrCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsTrue = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsTrue = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsTrue.isEvictionRequired()).thenReturn(true);
        Mockito.when(evictionChecker2ReturnsTrue.isEvictionRequired()).thenReturn(true);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(OR, evictionChecker1ReturnsTrue, evictionChecker2ReturnsTrue);
        Assert.assertTrue(compositeEvictionChecker.isEvictionRequired());
    }

    @Test
    public void resultShouldReturnFalse_whenAllIsFalse_withOrCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsFalse = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsFalse = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsFalse.isEvictionRequired()).thenReturn(false);
        Mockito.when(evictionChecker2ReturnsFalse.isEvictionRequired()).thenReturn(false);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(OR, evictionChecker1ReturnsFalse, evictionChecker2ReturnsFalse);
        Assert.assertFalse(compositeEvictionChecker.isEvictionRequired());
    }

    @Test
    public void resultShouldReturnTrue_whenOneIsTrue_withOrCompositionOperator() {
        EvictionChecker evictionChecker1ReturnsTrue = Mockito.mock(EvictionChecker.class);
        EvictionChecker evictionChecker2ReturnsFalse = Mockito.mock(EvictionChecker.class);
        Mockito.when(evictionChecker1ReturnsTrue.isEvictionRequired()).thenReturn(true);
        Mockito.when(evictionChecker2ReturnsFalse.isEvictionRequired()).thenReturn(false);
        CompositeEvictionChecker compositeEvictionChecker = CompositeEvictionChecker.newCompositeEvictionChecker(OR, evictionChecker1ReturnsTrue, evictionChecker2ReturnsFalse);
        Assert.assertTrue(compositeEvictionChecker.isEvictionRequired());
    }
}

