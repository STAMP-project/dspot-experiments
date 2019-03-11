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
package org.apache.geode.management.internal.cli.functions;


import IndexType.FUNCTIONAL;
import IndexType.PRIMARY_KEY;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.internal.lang.ObjectUtils;
import org.apache.geode.internal.util.CollectionUtils;
import org.apache.geode.management.internal.cli.domain.IndexDetails;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * The ListIndexFunctionJUnitTest class is test suite of test cases testing the contract and
 * functionality of the ListIndexFunction GemFire function.
 * </p>
 * </p>
 *
 * @see org.apache.geode.management.internal.cli.functions.ListIndexFunction
 * @see org.junit.Test
 * @since GemFire 7.0
 */
public class ListIndexFunctionJUnitTest {
    private AtomicLong counter;

    private QueryService mockQueryService;

    private ListIndexFunctionJUnitTest.TestResultSender testResultSender;

    private FunctionContext mockFunctionContext;

    private final String mockMemberId = "mockMemberId";

    private final String mockMemberName = "mockMemberName";

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() throws Throwable {
        // Expected Results
        final IndexDetails indexDetailsOne = createIndexDetails("/Employees", "empIdIdx", PRIMARY_KEY, "/Employees", "id", "id, firstName, lastName", "Employees");
        indexDetailsOne.setIndexStatisticsDetails(createIndexStatisticsDetails(10124L, 4096L, 10124L, 1284100L, 280120L));
        final IndexDetails indexDetailsTwo = createIndexDetails("/Employees", "empGivenNameIdx", FUNCTIONAL, "/Employees", "lastName", "id, firstName, lastName", "Employees");
        final IndexDetails indexDetailsThree = createIndexDetails("/Contractors", "empIdIdx", PRIMARY_KEY, "/Contrators", "id", "id, firstName, lastName", "Contractors");
        indexDetailsThree.setIndexStatisticsDetails(createIndexStatisticsDetails(1024L, 256L, 20248L, 768001L, 24480L));
        final IndexDetails indexDetailsFour = createIndexDetails("/Employees", "empIdIdx", FUNCTIONAL, "/Employees", "emp_id", "id, surname, givenname", "Employees");
        final Set<IndexDetails> expectedIndexDetailsSet = new java.util.HashSet(Arrays.asList(indexDetailsOne, indexDetailsTwo, indexDetailsThree));
        // Prepare Mocks
        List<Index> indexes = Arrays.asList(createMockIndex(indexDetailsOne), createMockIndex(indexDetailsTwo), createMockIndex(indexDetailsThree), createMockIndex(indexDetailsFour));
        Mockito.when(mockQueryService.getIndexes()).thenReturn(indexes);
        // Execute Function and Assert Results
        final ListIndexFunction function = new ListIndexFunction();
        function.execute(mockFunctionContext);
        final List<?> results = testResultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        final Set<IndexDetails> actualIndexDetailsSet = ((Set<IndexDetails>) (results.get(0)));
        assertThat(actualIndexDetailsSet).isNotNull();
        assertThat(actualIndexDetailsSet.size()).isEqualTo(expectedIndexDetailsSet.size());
        for (final IndexDetails expectedIndexDetails : expectedIndexDetailsSet) {
            final IndexDetails actualIndexDetails = CollectionUtils.findBy(actualIndexDetailsSet, ( indexDetails) -> ObjectUtils.equals(expectedIndexDetails, indexDetails));
            assertIndexDetailsEquals(actualIndexDetails, expectedIndexDetails);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithNoIndexes() throws Throwable {
        // Prepare Mocks
        Mockito.when(mockQueryService.getIndexes()).thenReturn(Collections.emptyList());
        // Execute Function and assert results
        final ListIndexFunction function = new ListIndexFunction();
        function.execute(mockFunctionContext);
        final List<?> results = testResultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        final Set<IndexDetails> actualIndexDetailsSet = ((Set<IndexDetails>) (results.get(0)));
        assertThat(actualIndexDetailsSet).isNotNull();
        assertThat(actualIndexDetailsSet.isEmpty()).isTrue();
    }

    @Test
    public void testExecuteThrowsException() {
        // Prepare Mocks
        Mockito.when(mockQueryService.getIndexes()).thenThrow(new RuntimeException("Mocked Exception"));
        // Execute Function and assert results
        final ListIndexFunction function = new ListIndexFunction();
        function.execute(mockFunctionContext);
        assertThatThrownBy(() -> testResultSender.getResults()).isInstanceOf(RuntimeException.class).hasMessage("Mocked Exception");
    }

    private static class TestResultSender implements ResultSender {
        private Throwable t;

        private final List<Object> results = new LinkedList<>();

        protected List<Object> getResults() throws Throwable {
            if ((t) != null) {
                throw t;
            }
            return Collections.unmodifiableList(results);
        }

        @Override
        public void lastResult(final Object lastResult) {
            results.add(lastResult);
        }

        @Override
        public void sendResult(final Object oneResult) {
            results.add(oneResult);
        }

        @Override
        public void sendException(final Throwable t) {
            this.t = t;
        }
    }
}

