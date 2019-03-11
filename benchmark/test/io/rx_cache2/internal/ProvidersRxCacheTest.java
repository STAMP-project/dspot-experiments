/**
 * Copyright 2015 Victor Albertos
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


import Source.CLOUD;
import Source.MEMORY;
import Source.PERSISTENCE;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.ProviderHelper;
import io.rx_cache2.Reply;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


/**
 * Created by victor on 28/12/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProvidersRxCacheTest {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProvidersRxCache providersRxCache;

    private RxCache rxCache;

    private static final int SIZE = 100;

    @Test
    public void _00_Use_Expired_Data() {
        initProviders(true);
        TestObserver<Reply<List<Mock>>> observer;
        observer = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(observer);
        observer.awaitTerminalEvent();
        waitTime(1500);
        observer = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(Observable.just(Arrays.asList(new Mock()))).subscribe(observer);
        observer.awaitTerminalEvent();
        Reply<List<Mock>> reply = observer.values().get(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        Assert.assertThat(observer.errors().size(), CoreMatchers.is(0));
    }

    @Test
    public void _01_Before_Destroy_Memory() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(MEMORY));
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _02_After_Memory_Destroyed() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(PERSISTENCE));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriber = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        waitTime(1100);
        subscriber = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriber = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(MEMORY));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _03_Evicting_Cache() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksEvictProvider(createObservableMocks(ProvidersRxCacheTest.SIZE), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriber = new TestObserver();
        providersRxCache.getMocksEvictProvider(createObservableMocks(ProvidersRxCacheTest.SIZE), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(MEMORY));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriber = new TestObserver();
        providersRxCache.getMocksEvictProvider(createObservableMocks(ProvidersRxCacheTest.SIZE), new EvictProvider(true)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        Assert.assertThat(subscriber.values().get(0).getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _04_Session_Mock() {
        initProviders(false);
        TestObserver<Mock> subscriber = new TestObserver();
        Mock mock = createMocks(ProvidersRxCacheTest.SIZE).get(0);
        // not logged mock
        providersRxCache.getLoggedMock(ProviderHelper.<Mock>withoutLoader(), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(1));
        // login mock
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(Observable.just(mock), new EvictProvider(true)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertNotNull(subscriber.values().get(0));
        // logged mock
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(ProviderHelper.<Mock>withoutLoader(), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertNotNull(subscriber.values().get(0));
        // logout mock
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(ProviderHelper.<Mock>withoutLoader(), new EvictProvider(true)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(1));
        // not logged mock
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(ProviderHelper.<Mock>withoutLoader(), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(1));
    }

    @Test
    public void _06_Not_Use_Expired_Data() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        waitTime(1100);
        subscriber = new TestObserver();
        providersRxCache.getMocksListResponseOneSecond(ProviderHelper.<List<Mock>>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(1));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(0));
    }

    @Test
    public void _07_When_Retrieve_Cached_Data_After_Remove_Item_List_Then_Item_Still_Remains() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        reply.getData().remove(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(((ProvidersRxCacheTest.SIZE) - 1)));
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(ProviderHelper.<List<Mock>>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _08_When_Retrieve_Cached_Data_After_Remove_Item_Array_Then_Item_Still_Remains() {
        initProviders(false);
        TestObserver<Reply<Mock[]>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksArrayResponse(createObservableMocksArray(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<Mock[]> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().length, CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        reply = new Reply(Arrays.copyOf(reply.getData(), ((reply.getData().length) - 1)), reply.getSource(), false);
        Assert.assertThat(reply.getData().length, CoreMatchers.is(((ProvidersRxCacheTest.SIZE) - 1)));
        subscriber = new TestObserver();
        providersRxCache.getMocksArrayResponse(ProviderHelper.<Mock[]>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().length, CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _09_When_Retrieve_Cached_Data_After_Remove_Item_Map_Then_Item_Still_Remains() {
        initProviders(false);
        TestObserver<Reply<Map<Integer, Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksMapResponse(createObservableMocksMap(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<Map<Integer, Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        reply.getData().remove(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(((ProvidersRxCacheTest.SIZE) - 1)));
        subscriber = new TestObserver();
        providersRxCache.getMocksMapResponse(ProviderHelper.<Map<Integer, Mock>>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getData().size(), CoreMatchers.is(ProvidersRxCacheTest.SIZE));
    }

    @Test
    public void _10_When_Retrieve_Cached_Data_After_Modified_Object_On_Item_List_Then_Object_Preserves_Initial_State() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(createObservableMocks(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Type type = new TypeToken<Reply<List<Mock>>>() {}.getType();
        Reply<List<Mock>> replyOriginal = ((Reply<List<Mock>>) (deepCopy(reply, type)));
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(true));
        reply.getData().get(0).setMessage("modified");
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(false));
        subscriber = new TestObserver();
        providersRxCache.getMocksWithDetailResponse(ProviderHelper.<List<Mock>>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        type = new TypeToken<List<Mock>>() {}.getType();
        Assert.assertThat(compare(reply.getData(), replyOriginal.getData(), type), CoreMatchers.is(true));
    }

    @Test
    public void _11_When_Retrieve_Cached_Data_After_Modified_Object_On_Item_Array_Then_Object_Preserves_Initial_State() {
        initProviders(false);
        TestObserver<Reply<Mock[]>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksArrayResponse(createObservableMocksArray(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<Mock[]> reply = subscriber.values().get(0);
        Type type = new TypeToken<Reply<Mock[]>>() {}.getType();
        Reply<Mock[]> replyOriginal = ((Reply<Mock[]>) (deepCopy(reply, type)));
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(true));
        reply.getData()[0].setMessage("modified");
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(false));
        subscriber = new TestObserver();
        providersRxCache.getMocksArrayResponse(ProviderHelper.<Mock[]>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        type = new TypeToken<Mock[]>() {}.getType();
        Assert.assertThat(compare(reply.getData(), replyOriginal.getData(), type), CoreMatchers.is(true));
    }

    @Test
    public void _12_When_Retrieve_Cached_Data_After_Modified_Object_On_Item_Map_Then_Object_Preserves_Initial_State() {
        initProviders(false);
        TestObserver<Reply<Map<Integer, Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksMapResponse(createObservableMocksMap(ProvidersRxCacheTest.SIZE)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<Map<Integer, Mock>> reply = subscriber.values().get(0);
        Type type = new TypeToken<Reply<Map<Integer, Mock>>>() {}.getType();
        Reply<Map<Integer, Mock>> replyOriginal = ((Reply<Map<Integer, Mock>>) (deepCopy(reply, type)));
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(true));
        reply.getData().get(0).setMessage("modified");
        Assert.assertThat(compare(reply, replyOriginal, type), CoreMatchers.is(false));
        subscriber = new TestObserver();
        providersRxCache.getMocksMapResponse(ProviderHelper.<Map<Integer, Mock>>withoutLoader()).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        reply = subscriber.values().get(0);
        type = new TypeToken<Map<Integer, Mock>>() {}.getType();
        Assert.assertThat(compare(reply.getData(), replyOriginal.getData(), type), CoreMatchers.is(true));
    }

    @Test
    public void _13_When_Retrieve_Cached_Data_After_Modified_Object_Then_Object_Preserves_Initial_State() {
        initProviders(false);
        TestObserver<Mock> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(Observable.<Mock>just(createMocks(ProvidersRxCacheTest.SIZE).get(0)), new EvictProvider(true)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Mock mock = subscriber.values().get(0);
        Type type = new TypeToken<Mock>() {}.getType();
        Mock mockOriginal = ((Mock) (deepCopy(mock, type)));
        Assert.assertThat(compare(mock, mockOriginal, type), CoreMatchers.is(true));
        mock.setMessage("modified");
        Assert.assertThat(compare(mock, mockOriginal, type), CoreMatchers.is(false));
        subscriber = new TestObserver();
        providersRxCache.getLoggedMock(ProviderHelper.<Mock>withoutLoader(), new EvictProvider(false)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Assert.assertThat(subscriber.errors().size(), CoreMatchers.is(0));
        Assert.assertThat(subscriber.values().size(), CoreMatchers.is(1));
        mock = subscriber.values().get(0);
        Assert.assertThat(compare(mock, mockOriginal, type), CoreMatchers.is(true));
    }

    @Test
    public void _14_When_0_Is_The_Value_For_Life_Time_Not_Cached_Ad_Infinitum() {
        initProviders(false);
        TestObserver<Reply<List<Mock>>> subscriber;
        subscriber = new TestObserver();
        providersRxCache.getMocksLife0Minutes(createObservableMocks(10)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        Reply<List<Mock>> reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
        subscriber = new TestObserver();
        providersRxCache.getMocksLife0Minutes(createObservableMocks(10)).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        reply = subscriber.values().get(0);
        Assert.assertThat(reply.getSource(), CoreMatchers.is(CLOUD));
    }

    @Test
    public void _15_When_Evict_All_Evict_All() {
        initProviders(false);
        TestObserver<Void> subscriberEvict = new TestObserver();
        rxCache.evictAll().subscribe(subscriberEvict);
        subscriberEvict.awaitTerminalEvent();
        for (int i = 0; i < (ProvidersRxCacheTest.SIZE); i++) {
            TestObserver<List<Mock>> subscriber = new TestObserver();
            providersRxCache.getMocksPaginate(createObservableMocks(1), new DynamicKey(i)).subscribe(subscriber);
            subscriber.awaitTerminalEvent();
        }
        Assert.assertThat(ProvidersRxCacheTest.temporaryFolder.getRoot().listFiles().length, CoreMatchers.is(ProvidersRxCacheTest.SIZE));
        subscriberEvict = new TestObserver();
        rxCache.evictAll().subscribe(subscriberEvict);
        subscriberEvict.awaitTerminalEvent();
        subscriberEvict.assertComplete();
        subscriberEvict.assertNoErrors();
        subscriberEvict.assertNoValues();
        Assert.assertThat(ProvidersRxCacheTest.temporaryFolder.getRoot().listFiles().length, CoreMatchers.is(0));
    }
}

