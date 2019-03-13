/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.execute.FunctionInvocationTargetException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.internal.cache.execute.AbstractExecution;
import org.apache.geode.internal.util.CollectionUtils;
import org.apache.geode.management.internal.cli.domain.IndexDetails;
import org.apache.geode.management.internal.cli.functions.ListIndexFunction;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * The ListIndexCommandJUnitTest class is a test suite of test cases testing the contract and
 * functionality of the ListIndexCommand class.
 * </p>
 *
 * @see org.apache.geode.management.internal.cli.commands.ClearDefinedIndexesCommand
 * @see org.apache.geode.management.internal.cli.commands.CreateDefinedIndexesCommand
 * @see org.apache.geode.management.internal.cli.commands.CreateIndexCommand
 * @see org.apache.geode.management.internal.cli.commands.DefineIndexCommand
 * @see org.apache.geode.management.internal.cli.commands.DestroyIndexCommand
 * @see org.apache.geode.management.internal.cli.commands.ListIndexCommand
 * @see org.apache.geode.management.internal.cli.domain.IndexDetails
 * @see org.apache.geode.management.internal.cli.functions.ListIndexFunction
 * @see org.junit.Test
 * @since GemFire 7.0
 */
public class ListIndexCommandJUnitTest {
    private ListIndexCommand listIndexCommand;

    private ResultCollector mockResultCollector;

    private AbstractExecution mockFunctionExecutor;

    @Test
    public void getIndexListingShouldPropagateExceptionsThrownByTheInternalFunctionExecution() {
        Mockito.doThrow(new RuntimeException("Mock RuntimeException")).when(mockFunctionExecutor).execute(ArgumentMatchers.any(ListIndexFunction.class));
        assertThatThrownBy(() -> listIndexCommand.getIndexListing()).isInstanceOf(RuntimeException.class).hasMessageContaining("Mock RuntimeException");
    }

    @Test
    public void getIndexListingShouldReturnTheIndexesOrdered() {
        final IndexDetails indexDetails1 = createIndexDetails("memberOne", "empIdIdx");
        final IndexDetails indexDetails2 = createIndexDetails("memberOne", "empLastNameIdx");
        final IndexDetails indexDetails3 = createIndexDetails("memberTwo", "empDobIdx");
        final List<Set<IndexDetails>> results = new ArrayList<>();
        results.add(CollectionUtils.asSet(indexDetails2, indexDetails1, indexDetails3));
        Mockito.when(mockResultCollector.getResult()).thenReturn(results);
        final List<IndexDetails> expectedIndexDetails = Arrays.asList(indexDetails1, indexDetails2, indexDetails3);
        final List<IndexDetails> actualIndexDetails = listIndexCommand.getIndexListing();
        assertThat(actualIndexDetails).isNotNull();
        assertThat(actualIndexDetails).isEqualTo(expectedIndexDetails);
        Mockito.verify(mockFunctionExecutor, Mockito.times(1)).setIgnoreDepartedMembers(true);
        Mockito.verify(mockFunctionExecutor, Mockito.times(1)).execute(ArgumentMatchers.any(ListIndexFunction.class));
    }

    @Test
    public void getIndexListingShouldIgnoreExceptionsReturnedAsResultsFromTheInternalFunctionExecution() {
        final IndexDetails indexDetails = createIndexDetails("memberOne", "empIdIdx");
        final List<Object> results = new ArrayList<>(2);
        results.add(CollectionUtils.asSet(indexDetails));
        results.add(new FunctionInvocationTargetException("Mock FunctionInvocationTargetException"));
        Mockito.when(mockResultCollector.getResult()).thenReturn(results);
        final List<IndexDetails> expectedIndexDetails = Collections.singletonList(indexDetails);
        final List<IndexDetails> actualIndexDetails = listIndexCommand.getIndexListing();
        assertThat(actualIndexDetails).isNotNull();
        assertThat(actualIndexDetails).isEqualTo(expectedIndexDetails);
        Mockito.verify(mockFunctionExecutor, Mockito.times(1)).setIgnoreDepartedMembers(true);
        Mockito.verify(mockFunctionExecutor, Mockito.times(1)).execute(ArgumentMatchers.any(ListIndexFunction.class));
    }
}

