/**
 * Copyright 2016 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rx_cache2.internal;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.ProviderHelper;
import io.rx_cache2.Reply;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ProvidersDynamicsKeysRxCacheTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProvidersRxCache providersRxCache;

    private static final int SIZE = 100;

    private static final String filter1page1 = "filer1_page1";

    private static final String filter1Page2 = "filer1_page2";

    private static final String filter1Page3 = "filer1_page3";

    private static final String filter2Page1 = "filer2_page1";

    private static final String filter2Page2 = "filer2_page2";

    private static final String filter2Page3 = "filer2_page3";

    @Test
    public void Pagination() {
        TestObserver<List<Mock>> testObserver;
        List<Mock> mocksPage1 = createMocks(ProvidersDynamicsKeysRxCacheTest.SIZE);
        String mockPage1Value = mocksPage1.get(0).getMessage();
        testObserver = providersRxCache.getMocksPaginate(Observable.just(mocksPage1), new DynamicKey(1)).test();
        testObserver.awaitTerminalEvent();
        List<Mock> mocksPage2 = createMocks(ProvidersDynamicsKeysRxCacheTest.SIZE);
        String mockPage2Value = mocksPage2.get(0).getMessage();
        testObserver = providersRxCache.getMocksPaginate(Observable.just(mocksPage2), new DynamicKey(2)).test();
        testObserver.awaitTerminalEvent();
        List<Mock> mocksPage3 = createMocks(ProvidersDynamicsKeysRxCacheTest.SIZE);
        String mockPage3Value = mocksPage3.get(0).getMessage();
        testObserver = providersRxCache.getMocksPaginate(Observable.just(mocksPage3), new DynamicKey(3)).test();
        testObserver.awaitTerminalEvent();
        testObserver = providersRxCache.getMocksPaginate(ProviderHelper.<List<Mock>>withoutLoader(), new DynamicKey(1)).test();
        testObserver.awaitTerminalEvent();
        Assert.assertThat(testObserver.values().get(0).get(0).getMessage(), CoreMatchers.is(mockPage1Value));
        testObserver = providersRxCache.getMocksPaginate(ProviderHelper.<List<Mock>>withoutLoader(), new DynamicKey(2)).test();
        testObserver.awaitTerminalEvent();
        Assert.assertThat(testObserver.values().get(0).get(0).getMessage(), CoreMatchers.is(mockPage2Value));
        testObserver = providersRxCache.getMocksPaginate(ProviderHelper.<List<Mock>>withoutLoader(), new DynamicKey(3)).test();
        testObserver.awaitTerminalEvent();
        Assert.assertThat(testObserver.values().get(0).get(0).getMessage(), CoreMatchers.is(mockPage3Value));
    }

    @Test
    public void Pagination_Evict_All() {
        paginationEvictAll();
    }

    @Test
    public void Pagination_With_Evict_Cache_By_Page() {
        TestObserver<Reply<List<Mock>>> observer;
        observer = providersRxCache.getMocksDynamicKeyEvictPage(Observable.just(createMocks(ProvidersDynamicsKeysRxCacheTest.SIZE)), new DynamicKey(1), new EvictDynamicKey(true)).test();
        observer.awaitTerminalEvent();
        observer = providersRxCache.getMocksDynamicKeyEvictPage(Observable.just(createMocks(ProvidersDynamicsKeysRxCacheTest.SIZE)), new DynamicKey(2), new EvictDynamicKey(true)).test();
        observer.awaitTerminalEvent();
        observer = providersRxCache.getMocksDynamicKeyEvictPage(ProviderHelper.<List<Mock>>withoutLoader(), new DynamicKey(1), new EvictDynamicKey(true)).test();
        observer.awaitTerminalEvent();
        Assert.assertThat(observer.errors().size(), CoreMatchers.is(1));
        Assert.assertThat(observer.values().size(), CoreMatchers.is(0));
        observer = providersRxCache.getMocksDynamicKeyEvictPage(ProviderHelper.<List<Mock>>withoutLoader(), new DynamicKey(2), new EvictDynamicKey(false)).test();
        observer.awaitTerminalEvent();
        Assert.assertThat(observer.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(observer.values().size(), CoreMatchers.is(1));
    }

    @Test
    public void Pagination_Filtering_Evicting_DynamicKeyGroup() {
        populateAndCheckRetrieved();
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter1page1);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page2, false);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page3, false);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page1, false);
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter1Page2);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page3, false);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page2, false);
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter1Page3);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page3, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, false);
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter2Page1);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page2, false);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, false);
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter2Page2);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, false);
        evictDynamicKeyGroup(ProvidersDynamicsKeysRxCacheTest.filter2Page3);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, true);
        populateAndCheckRetrieved();
    }

    @Test
    public void Pagination_Filtering_Evicting_DynamicKey() {
        populateAndCheckRetrieved();
        evictDynamicKey(ProvidersDynamicsKeysRxCacheTest.filter1Page2);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page3, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page1, false);
        evictDynamicKey(ProvidersDynamicsKeysRxCacheTest.filter2Page1);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, true);
        populateAndCheckRetrieved();
    }

    @Test
    public void Pagination_Filtering_Evicting_ProviderKey() {
        populateAndCheckRetrieved();
        evictProviderKey(ProvidersDynamicsKeysRxCacheTest.filter1Page2);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter1Page3, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page1, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page2, true);
        retrieveAndCheckFilterPageValue(ProvidersDynamicsKeysRxCacheTest.filter2Page3, true);
        populateAndCheckRetrieved();
    }
}

