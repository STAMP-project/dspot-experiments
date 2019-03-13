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
package org.apache.geode.internal.cache;


import java.util.Properties;
import java.util.function.Supplier;
import org.apache.geode.cache.CacheExistsException;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.InternalCacheBuilder.InternalCacheConstructor;
import org.apache.geode.internal.cache.InternalCacheBuilder.InternalDistributedSystemConstructor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link InternalCacheBuilder}.
 */
public class InternalCacheBuilderTest {
    private static final int ANY_SYSTEM_ID = 12;

    private static final String ANY_MEMBER_NAME = "a-member-name";

    private static final Supplier<InternalDistributedSystem> THROWING_SYSTEM_SUPPLIER = () -> {
        throw new AssertionError("throwing system supplier");
    };

    private static final Supplier<InternalCache> THROWING_CACHE_SUPPLIER = () -> {
        throw new AssertionError("throwing cache supplier");
    };

    private static final InternalDistributedSystemConstructor THROWING_SYSTEM_CONSTRUCTOR = ( a, b) -> {
        throw new AssertionError("throwing system constructor");
    };

    private static final InternalCacheConstructor THROWING_CACHE_CONSTRUCTOR = ( a, b, c, d, e, f) -> {
        throw new AssertionError("throwing cache constructor");
    };

    @Mock
    private Supplier<InternalDistributedSystem> nullSingletonSystemSupplier;

    @Mock
    private Supplier<InternalCache> nullSingletonCacheSupplier;

    @Test
    public void create_throwsNullPointerException_ifConfigPropertiesIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(null, new CacheConfig(), nullSingletonSystemSupplier, InternalCacheBuilderTest.constructorOf(constructedSystem()), nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(InternalCacheBuilderTest.constructedCache()));
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create());
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void create_throwsNullPointerException_andCacheConfigIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), null, nullSingletonSystemSupplier, InternalCacheBuilderTest.constructorOf(constructedSystem()), nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(InternalCacheBuilderTest.constructedCache()));
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create());
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void create_constructsSystem_withGivenProperties_ifNoSystemExists_andNoCacheExists() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalDistributedSystemConstructor systemConstructor = InternalCacheBuilderTest.constructorOf(constructedSystem());
        Properties configProperties = new Properties();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(configProperties, new CacheConfig(), nullSingletonSystemSupplier, systemConstructor, nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        internalCacheBuilder.create();
        Mockito.verify(systemConstructor).construct(ArgumentMatchers.same(configProperties), ArgumentMatchers.any());
    }

    @Test
    public void create_returnsConstructedCache_ifNoSystemExists() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), nullSingletonSystemSupplier, InternalCacheBuilderTest.constructorOf(constructedSystem()), nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create();
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void create_setsConstructedCache_onConstructedSystem_ifNoSystemExists() {
        InternalDistributedSystem constructedSystem = constructedSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), nullSingletonSystemSupplier, InternalCacheBuilderTest.constructorOf(constructedSystem), nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        internalCacheBuilder.create();
        Mockito.verify(constructedSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void create_setsConstructedSystem_onConstructedCache_ifNoSystemExists_() {
        InternalDistributedSystem constructedSystem = constructedSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(constructedCache);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), nullSingletonSystemSupplier, InternalCacheBuilderTest.constructorOf(constructedSystem), nullSingletonCacheSupplier, cacheConstructor);
        internalCacheBuilder.create();
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(constructedSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void create_returnsConstructedCache_ifSingletonSystemExists_andNoCacheExists() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem()), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create();
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void create_setsSingletonSystem_onConstructedCache_ifSingletonSystemExists_andNoCacheExists() {
        InternalDistributedSystem singletonSystem = singletonSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(constructedCache);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, cacheConstructor);
        internalCacheBuilder.create();
        Mockito.verify(singletonSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void create_setsConstructedCache_onSingletonSystem_ifSingletonSystemExists_andNoCacheExists() {
        InternalDistributedSystem singletonSystem = singletonSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(constructedCache);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, cacheConstructor);
        internalCacheBuilder.create();
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(singletonSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void create_returnsConstructedCache_ifSingletonSystemExists_andSingletonCacheIsClosed() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem()), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), InternalCacheBuilderTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create();
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void create_setsConstructedCache_onSingletonSystem_ifSingletonSystemExists_andSingletonCacheIsClosed() {
        InternalDistributedSystem singletonSystem = singletonSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), InternalCacheBuilderTest.constructorOf(constructedCache));
        internalCacheBuilder.create();
        Mockito.verify(singletonSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void create_setsSingletonSystem_onConstructedCache_ifSingletonSystemExists_andSingletonCacheIsClosed() {
        InternalDistributedSystem singletonSystem = singletonSystem();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(InternalCacheBuilderTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), cacheConstructor);
        internalCacheBuilder.create();
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(singletonSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void create_throwsCacheExistsException_ifSingletonSystemExists_andSingletonCacheIsOpen_butExistingIsNotOk() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem()), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(false).create());
        assertThat(thrown).isInstanceOf(CacheExistsException.class);
    }

    @Test
    public void create_propagatesCacheConfigException_ifSingletonSystemExists_andSingletonCacheIsOpen_andExistingIsOk_butCacheIsIncompatible() {
        Throwable thrownByCacheConfig = new IllegalStateException("incompatible");
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), InternalCacheBuilderTest.throwingCacheConfig(thrownByCacheConfig), InternalCacheBuilderTest.supplierOf(singletonSystem()), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(true).create());
        assertThat(thrown).isSameAs(thrownByCacheConfig);
    }

    @Test
    public void create_returnsSingletonCache_ifSingletonCacheIsOpen() {
        InternalCache singletonCache = InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.supplierOf(singletonSystem()), InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(singletonCache), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCache result = internalCacheBuilder.create();
        assertThat(result).isSameAs(singletonCache);
    }

    @Test
    public void createWithSystem_throwsNullPointerException_ifSystemIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create(null));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void createWithSystem_returnsConstructedCache_ifNoCacheExists() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create(givenSystem());
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void createWithSystem_setsConstructedCache_onGivenSystem_ifNoCacheExists() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, InternalCacheBuilderTest.constructorOf(constructedCache));
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void createWithSystem_setsGivenSystem_onConstructedCache_ifNoCacheExists() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(InternalCacheBuilderTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, nullSingletonCacheSupplier, cacheConstructor);
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(givenSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_returnsConstructedCache_ifSingletonCacheIsClosed() {
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), InternalCacheBuilderTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create(givenSystem());
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void createWithSystem_setsConstructedCache_onGivenSystem_ifSingletonCacheIsClosed() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache constructedCache = InternalCacheBuilderTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), InternalCacheBuilderTest.constructorOf(constructedCache));
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void createWithSystem_setsGivenSystem_onConstructedCache_ifSingletonCacheIsClosed() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderTest.constructorOf(InternalCacheBuilderTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.CLOSED)), cacheConstructor);
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(givenSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_throwsCacheExistsException_ifSingletonCacheIsOpen_butExistingIsNotOk() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(false).create(givenSystem()));
        assertThat(thrown).isInstanceOf(CacheExistsException.class);
    }

    @Test
    public void createWithSystem_doesNotSetSingletonCache_onGivenSystem_ifSingletonCacheIsOpen_butExistingIsNotOk() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCacheBuilderTest.ignoreThrowable(() -> internalCacheBuilder.setIsExistingOk(false).create(givenSystem));
        Mockito.verify(givenSystem, Mockito.never()).setCache(ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_propagatesCacheConfigException_ifSingletonCacheIsOpen_andExistingIsOk_butCacheIsIncompatible() {
        Throwable thrownByCacheConfig = new IllegalStateException("incompatible");
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), InternalCacheBuilderTest.throwingCacheConfig(thrownByCacheConfig), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(true).create(givenSystem()));
        assertThat(thrown).isSameAs(thrownByCacheConfig);
    }

    @Test
    public void createWithSystem_doesNotSetSingletonCache_onGivenSystem_ifSingletonCacheIsOpen_andExistingIsOk_butCacheIsNotCompatible() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), InternalCacheBuilderTest.throwingCacheConfig(new IllegalStateException("incompatible")), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN)), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCacheBuilderTest.ignoreThrowable(() -> internalCacheBuilder.setIsExistingOk(true).create(givenSystem));
        Mockito.verifyZeroInteractions(givenSystem);
    }

    @Test
    public void createWithSystem_returnsSingletonCache_ifSingletonCacheIsOpen_andExistingIsOk_andCacheIsCompatible() {
        InternalCache singletonCache = InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(singletonCache), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCache result = internalCacheBuilder.setIsExistingOk(true).create(givenSystem());
        assertThat(result).isSameAs(singletonCache);
    }

    @Test
    public void createWithSystem_setsSingletonCache_onGivenSystem_ifSingletonCacheIsOpen_andExistingIsOk_andCacheIsCompatible() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache singletonCache = InternalCacheBuilderTest.singletonCache(InternalCacheBuilderTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderTest.supplierOf(singletonCache), InternalCacheBuilderTest.THROWING_CACHE_CONSTRUCTOR);
        internalCacheBuilder.setIsExistingOk(true).create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(singletonCache));
    }

    enum CacheState {

        OPEN(false),
        CLOSED(true);
        private final boolean isClosed;

        CacheState(boolean isClosed) {
            this.isClosed = isClosed;
        }

        boolean isClosed() {
            return isClosed;
        }
    }
}

