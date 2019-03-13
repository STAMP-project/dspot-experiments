/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.paging.AsyncPage;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AsyncPageImplTest {
    private static final ImmutableList<String> VALUES1 = ImmutableList.of("1", "2");

    private static final ImmutableList<String> VALUES2 = ImmutableList.of("3", "4");

    private static final ImmutableList<String> VALUES3 = ImmutableList.of("5", "6");

    private static final ImmutableList<String> ALL_VALUES = ImmutableList.<String>builder().addAll(AsyncPageImplTest.VALUES1).addAll(AsyncPageImplTest.VALUES2).addAll(AsyncPageImplTest.VALUES3).build();

    private static final ImmutableList<String> SOME_VALUES = ImmutableList.<String>builder().addAll(AsyncPageImplTest.VALUES2).addAll(AsyncPageImplTest.VALUES3).build();

    private static class TestPageFetcher implements AsyncPageImpl.NextPageFetcher<String> {
        private static final long serialVersionUID = 4703765400378593176L;

        private final AsyncPageImpl<String> nextResult;

        TestPageFetcher(AsyncPageImpl<String> nextResult) {
            this.nextResult = nextResult;
        }

        @Override
        public ApiFuture<AsyncPage<String>> getNextPage() {
            return ApiFutures.<AsyncPage<String>>immediateFuture(nextResult);
        }
    }

    @Test
    public void testPage() {
        final AsyncPageImpl<String> nextResult = new AsyncPageImpl(null, "c", AsyncPageImplTest.VALUES2);
        AsyncPageImpl.NextPageFetcher<String> fetcher = new AsyncPageImplTest.TestPageFetcher(nextResult);
        AsyncPageImpl<String> result = new AsyncPageImpl(fetcher, "c", AsyncPageImplTest.VALUES1);
        Assert.assertEquals(nextResult, result.getNextPage());
        Assert.assertEquals("c", result.getNextPageToken());
        Assert.assertEquals(AsyncPageImplTest.VALUES1, result.getValues());
    }

    @Test
    public void testPageAsync() throws InterruptedException, ExecutionException {
        final AsyncPageImpl<String> nextResult = new AsyncPageImpl(null, "c", AsyncPageImplTest.VALUES2);
        AsyncPageImpl.NextPageFetcher<String> fetcher = new AsyncPageImplTest.TestPageFetcher(nextResult);
        AsyncPageImpl<String> result = new AsyncPageImpl(fetcher, "c", AsyncPageImplTest.VALUES1);
        Assert.assertEquals(nextResult, result.getNextPageAsync().get());
        Assert.assertEquals("c", result.getNextPageToken());
        Assert.assertEquals(AsyncPageImplTest.VALUES1, result.getValues());
    }

    @Test
    public void testIterateAll() {
        final AsyncPageImpl<String> nextResult2 = new AsyncPageImpl(null, "c3", AsyncPageImplTest.VALUES3);
        AsyncPageImpl.NextPageFetcher<String> fetcher2 = new AsyncPageImplTest.TestPageFetcher(nextResult2);
        final AsyncPageImpl<String> nextResult1 = new AsyncPageImpl(fetcher2, "c2", AsyncPageImplTest.VALUES2);
        AsyncPageImpl.NextPageFetcher<String> fetcher1 = new AsyncPageImplTest.TestPageFetcher(nextResult1);
        AsyncPageImpl<String> result = new AsyncPageImpl(fetcher1, "c1", AsyncPageImplTest.VALUES1);
        Assert.assertEquals(AsyncPageImplTest.ALL_VALUES, ImmutableList.copyOf(result.iterateAll()));
    }

    @Test
    public void testAsyncPageAndIterateAll() throws InterruptedException, ExecutionException {
        final AsyncPageImpl<String> nextResult2 = new AsyncPageImpl(null, "c3", AsyncPageImplTest.VALUES3);
        AsyncPageImpl.NextPageFetcher<String> fetcher2 = new AsyncPageImplTest.TestPageFetcher(nextResult2);
        final AsyncPageImpl<String> nextResult1 = new AsyncPageImpl(fetcher2, "c2", AsyncPageImplTest.VALUES2);
        AsyncPageImpl.NextPageFetcher<String> fetcher1 = new AsyncPageImplTest.TestPageFetcher(nextResult1);
        AsyncPageImpl<String> result = new AsyncPageImpl(fetcher1, "c1", AsyncPageImplTest.VALUES1);
        Assert.assertEquals(nextResult1, result.getNextPageAsync().get());
        Assert.assertEquals("c1", result.getNextPageToken());
        Assert.assertEquals(AsyncPageImplTest.VALUES1, result.getValues());
        Assert.assertEquals(AsyncPageImplTest.SOME_VALUES, ImmutableList.copyOf(result.getNextPageAsync().get().iterateAll()));
    }
}

