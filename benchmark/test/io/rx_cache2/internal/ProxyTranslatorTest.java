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


import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.rx_cache2.ConfigProvider;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.EvictProvider;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by victor on 28/12/15.
 */
public class ProxyTranslatorTest {
    private ProxyTranslator proxyTranslatorUT;

    private final Object[] dataMethod = new Object[]{ Observable.just(new Object[]{  }) };

    @Test
    public void Check_Basic_Method() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocks", Observable.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.getProviderKey(), CoreMatchers.is("getMocks"));
        Assert.assertNotNull(configProvider.getLoaderObservable());
        Assert.assertNull(configProvider.getLifeTimeMillis());
        MatcherAssert.assertThat(configProvider.evictProvider().evict(), CoreMatchers.is(false));
        MatcherAssert.assertThat(configProvider.requiredDetailedResponse(), CoreMatchers.is(false));
    }

    @Test
    public void Check_Annotated_Method() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksDifferent", Observable.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.getProviderKey(), CoreMatchers.is("get-mocks-different"));
        Assert.assertNotNull(configProvider.getLoaderObservable());
        Assert.assertNull(configProvider.getLifeTimeMillis());
        MatcherAssert.assertThat(configProvider.evictProvider().evict(), CoreMatchers.is(false));
        MatcherAssert.assertThat(configProvider.requiredDetailedResponse(), CoreMatchers.is(false));
    }

    @Test
    public void Check_Single_Reactive_Type() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksSingle", Single.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        Assert.assertNotNull(configProvider.getLoaderObservable());
    }

    @Test
    public void Check_Maybe_Reactive_Type() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksMaybe", Maybe.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        Assert.assertNotNull(configProvider.getLoaderObservable());
    }

    @Test
    public void Check_Flowable_Reactive_Type() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksFlowable", Flowable.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        Assert.assertNotNull(configProvider.getLoaderObservable());
    }

    @Test
    public void Check_Method_With_Life_Time_Defined() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksLifeTimeMinutes", Observable.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.getLifeTimeMillis(), CoreMatchers.is(60000L));
        mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksLifeTimeSeconds", Observable.class);
        configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.getLifeTimeMillis(), CoreMatchers.is(1000L));
        mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksLifeTimeMillis", Observable.class);
        configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.getLifeTimeMillis(), CoreMatchers.is(65000L));
    }

    @Test
    public void When_Return_Response_Then_Required_Detail_Response_Is_True() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksWithDetailResponse", Observable.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethod);
        MatcherAssert.assertThat(configProvider.requiredDetailedResponse(), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Method_Not_Return_Supported_Reactive_Type_Then_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksBadReturnType", Observable.class);
        proxyTranslatorUT.processMethod(mockMethod, dataMethod);
    }

    @Test
    public void When_Evict_Cache_Evict() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksEvictProvider", Observable.class, EvictProvider.class);
        Object[] dataMethodEvict = new Object[]{ Observable.just(new Object[]{  }), new EvictProvider(true) };
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethodEvict);
        MatcherAssert.assertThat(configProvider.evictProvider().evict(), CoreMatchers.is(true));
        Object[] dataMethodNoEvict = new Object[]{ Observable.just(new Object[]{  }), new EvictProvider(false) };
        configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethodNoEvict);
        MatcherAssert.assertThat(configProvider.evictProvider().evict(), CoreMatchers.is(false));
    }

    @Test
    public void When_Evict_Cache_Dynamic_Key_Evict() throws NoSuchMethodException {
        final String dynamicKey = "aDynamicKey";
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksDynamicKeyEvictPage", Observable.class, DynamicKey.class, EvictDynamicKey.class);
        Object[] dataMethodEvict = new Object[]{ Observable.just(new Object[]{  }), new DynamicKey(dynamicKey), new EvictDynamicKey(true) };
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethodEvict);
        EvictDynamicKey evictDynamicKey = ((EvictDynamicKey) (configProvider.evictProvider()));
        MatcherAssert.assertThat(configProvider.getDynamicKey(), CoreMatchers.is(dynamicKey));
        MatcherAssert.assertThat(evictDynamicKey.evict(), CoreMatchers.is(true));
        Object[] dataMethodNoEvict = new Object[]{ Observable.just(new Object[]{  }), new DynamicKey(dynamicKey), new EvictDynamicKey(false) };
        configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethodNoEvict);
        evictDynamicKey = ((EvictDynamicKey) (configProvider.evictProvider()));
        MatcherAssert.assertThat(configProvider.getDynamicKey(), CoreMatchers.is(dynamicKey));
        MatcherAssert.assertThat(evictDynamicKey.evict(), CoreMatchers.is(false));
    }

    @Test
    public void When_Get_Page_Get_Pages() throws NoSuchMethodException {
        Object[] dataMethodPaginate = new Object[]{ Observable.just(new Object[]{  }), new DynamicKey(1) };
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMocksPaginate", Observable.class, DynamicKey.class);
        ConfigProvider configProvider = proxyTranslatorUT.processMethod(mockMethod, dataMethodPaginate);
        MatcherAssert.assertThat(configProvider.getDynamicKey(), CoreMatchers.is("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Not_Loader_Provided_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockWithoutLoaderObservable");
        Object[] emptyDataMethod = new Object[]{  };
        proxyTranslatorUT.processMethod(mockMethod, emptyDataMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Not_Return_Observable_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockWithoutReturnObservable");
        proxyTranslatorUT.processMethod(mockMethod, dataMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Multiple_Observable_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockMultipleObservables", Observable.class, Observable.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), Observable.just("") };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Multiple_Evict_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockMultipleEvicts", Observable.class, EvictProvider.class, EvictProvider.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new EvictProvider(true), new EvictProvider(true) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Multiple_Dynamic_Keys_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockMultipleDynamicKeys", Observable.class, DynamicKey.class, DynamicKey.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new DynamicKey(1), new DynamicKey(1) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test
    public void When_Use_Evict_Dynamic_Key_Providing_Dynamic_Key_Not_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockEvictDynamicKeyProvidingDynamicKey", Observable.class, DynamicKey.class, EvictDynamicKey.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new DynamicKey("1"), new EvictDynamicKey(true) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Use_Evict_Dynamic_Key_Without_Providing_Dynamic_Key_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockEvictDynamicKeyWithoutProvidingDynamicKey", Observable.class, EvictDynamicKey.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new EvictDynamicKey(true) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test
    public void When_Use_Evict_Dynamic_Key_Group_Providing_Dynamic_Key_Group_Not_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockEvictDynamicKeyGroupProvidingDynamicKeyGroup", Observable.class, DynamicKeyGroup.class, EvictDynamicKeyGroup.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new DynamicKeyGroup("1", "1"), new EvictDynamicKeyGroup(true) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_Use_Evict_Dynamic_Key_Group_Without_Providing_Dynamic_Key_Group_Throw_Exception() throws NoSuchMethodException {
        Method mockMethod = ProvidersRxCache.class.getDeclaredMethod("getMockEvictDynamicKeyGroupWithoutProvidingDynamicKeyGroup", Observable.class, EvictDynamicKeyGroup.class);
        Object[] data = new Object[]{ Observable.just(new Object[]{  }), new EvictDynamicKeyGroup(true) };
        proxyTranslatorUT.processMethod(mockMethod, data);
    }
}

