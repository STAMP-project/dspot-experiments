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


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.DiskStoreImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.domain.DiskStoreDetails;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * The ListDiskStoreFunctionJUnitTest test suite class tests the contract and functionality of the
 * ListDiskStoresFunction.
 * </p>
 *
 * @see org.apache.geode.internal.cache.DiskStoreImpl
 * @see org.apache.geode.management.internal.cli.domain.DiskStoreDetails
 * @see org.apache.geode.management.internal.cli.functions.ListDiskStoresFunction
 * @see org.junit.Test
 * @since GemFire 7.0
 */
public class ListDiskStoresFunctionJUnitTest {
    private InternalCache mockCache;

    private FunctionContext mockFunctionContext;

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() throws Throwable {
        final String memberId = "mockMemberId";
        final String memberName = "mockMemberName";
        final UUID mockDiskStoreOneId = UUID.randomUUID();
        final UUID mockDiskStoreTwoId = UUID.randomUUID();
        final UUID mockDiskStoreThreeId = UUID.randomUUID();
        final DiskStoreImpl mockDiskStoreOne = Mockito.mock(DiskStoreImpl.class, "DiskStoreOne");
        final DiskStoreImpl mockDiskStoreTwo = Mockito.mock(DiskStoreImpl.class, "DiskStoreTwo");
        final DiskStoreImpl mockDiskStoreThree = Mockito.mock(DiskStoreImpl.class, "DiskStoreThree");
        final InternalDistributedMember mockMember = Mockito.mock(InternalDistributedMember.class, "DistributedMember");
        final Collection<DiskStore> mockDiskStores = Arrays.asList(mockDiskStoreOne, mockDiskStoreTwo, mockDiskStoreThree);
        final ListDiskStoresFunctionJUnitTest.TestResultSender testResultSender = new ListDiskStoresFunctionJUnitTest.TestResultSender();
        Mockito.when(mockCache.getMyId()).thenReturn(mockMember);
        Mockito.when(mockCache.listDiskStoresIncludingRegionOwned()).thenReturn(mockDiskStores);
        Mockito.when(mockMember.getId()).thenReturn(memberId);
        Mockito.when(mockMember.getName()).thenReturn(memberName);
        Mockito.when(mockDiskStoreOne.getDiskStoreUUID()).thenReturn(mockDiskStoreOneId);
        Mockito.when(mockDiskStoreOne.getName()).thenReturn("ds-backup");
        Mockito.when(mockDiskStoreTwo.getDiskStoreUUID()).thenReturn(mockDiskStoreTwoId);
        Mockito.when(mockDiskStoreTwo.getName()).thenReturn("ds-overflow");
        Mockito.when(mockDiskStoreThree.getDiskStoreUUID()).thenReturn(mockDiskStoreThreeId);
        Mockito.when(mockDiskStoreThree.getName()).thenReturn("ds-persistence");
        Mockito.when(mockFunctionContext.getCache()).thenReturn(mockCache);
        Mockito.when(mockFunctionContext.getResultSender()).thenReturn(testResultSender);
        final ListDiskStoresFunction function = new ListDiskStoresFunction();
        function.execute(mockFunctionContext);
        final List<?> results = testResultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        final Set<DiskStoreDetails> diskStoreDetails = ((Set<DiskStoreDetails>) (results.get(0)));
        assertThat(diskStoreDetails).isNotNull();
        assertThat(diskStoreDetails.size()).isEqualTo(3);
        Mockito.verify(mockMember, Mockito.times(3)).getId();
        Mockito.verify(mockMember, Mockito.times(3)).getName();
        diskStoreDetails.containsAll(Arrays.asList(new DiskStoreDetails(mockDiskStoreOneId, "ds-backup", memberId, memberName), new DiskStoreDetails(mockDiskStoreTwoId, "ds-overflow", memberId, memberName), new DiskStoreDetails(mockDiskStoreThreeId, "ds-persistence", memberId, memberName)));
    }

    @Test
    public void testExecuteOnMemberWithNoCache() {
        final ListDiskStoresFunction testListDiskStoresFunction = new ListDiskStoresFunction();
        final ListDiskStoresFunctionJUnitTest.TestResultSender testResultSender = new ListDiskStoresFunctionJUnitTest.TestResultSender();
        Mockito.when(mockFunctionContext.getCache()).thenThrow(new CacheClosedException("Mocked CacheClosedException"));
        Mockito.when(mockFunctionContext.getResultSender()).thenReturn(testResultSender);
        testListDiskStoresFunction.execute(mockFunctionContext);
        assertThatThrownBy(testResultSender::getResults).isInstanceOf(CacheClosedException.class).hasMessage("Mocked CacheClosedException");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOnMemberHavingNoDiskStores() throws Throwable {
        final InternalDistributedMember mockMember = Mockito.mock(InternalDistributedMember.class, "DistributedMember");
        final ListDiskStoresFunctionJUnitTest.TestResultSender testResultSender = new ListDiskStoresFunctionJUnitTest.TestResultSender();
        Mockito.when(mockCache.getMyId()).thenReturn(mockMember);
        Mockito.when(mockCache.listDiskStoresIncludingRegionOwned()).thenReturn(Collections.emptyList());
        Mockito.when(mockFunctionContext.getCache()).thenReturn(mockCache);
        Mockito.when(mockFunctionContext.getResultSender()).thenReturn(testResultSender);
        final ListDiskStoresFunction function = new ListDiskStoresFunction();
        function.execute(mockFunctionContext);
        final List<?> results = testResultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        final Set<DiskStoreDetails> diskStoreDetails = ((Set<DiskStoreDetails>) (results.get(0)));
        assertThat(diskStoreDetails).isNotNull();
        assertThat(diskStoreDetails.isEmpty()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOnMemberWithANonGemFireCache() throws Throwable {
        final ListDiskStoresFunctionJUnitTest.TestResultSender testResultSender = new ListDiskStoresFunctionJUnitTest.TestResultSender();
        final Cache mockNonGemCache = Mockito.mock(Cache.class, "NonGemCache");
        Mockito.when(mockFunctionContext.getCache()).thenReturn(mockNonGemCache);
        Mockito.when(mockFunctionContext.getResultSender()).thenReturn(testResultSender);
        final ListDiskStoresFunction function = new ListDiskStoresFunction();
        function.execute(mockFunctionContext);
        final List<?> results = testResultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        final Set<DiskStoreDetails> diskStoreDetails = ((Set<DiskStoreDetails>) (results.get(0)));
        assertThat(diskStoreDetails).isNotNull();
        assertThat(diskStoreDetails.isEmpty()).isTrue();
    }

    @Test
    public void testExecuteThrowsRuntimeException() {
        final InternalDistributedMember mockMember = Mockito.mock(InternalDistributedMember.class, "DistributedMember");
        final ListDiskStoresFunctionJUnitTest.TestResultSender testResultSender = new ListDiskStoresFunctionJUnitTest.TestResultSender();
        Mockito.when(mockCache.getMyId()).thenReturn(mockMember);
        Mockito.when(mockCache.listDiskStoresIncludingRegionOwned()).thenThrow(new RuntimeException("Mock RuntimeException"));
        Mockito.when(mockFunctionContext.getCache()).thenReturn(mockCache);
        Mockito.when(mockFunctionContext.getResultSender()).thenReturn(testResultSender);
        final ListDiskStoresFunction function = new ListDiskStoresFunction();
        function.execute(mockFunctionContext);
        assertThatThrownBy(testResultSender::getResults).isInstanceOf(RuntimeException.class).hasMessage("Mock RuntimeException");
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

