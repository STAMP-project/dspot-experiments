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
package com.hazelcast.internal.nearcache;


import DataStructureMethods.CLEAR;
import DataStructureMethods.DELETE;
import DataStructureMethods.DELETE_ASYNC;
import DataStructureMethods.DESTROY;
import DataStructureMethods.EVICT;
import DataStructureMethods.EVICT_ALL;
import DataStructureMethods.EXECUTE_ON_ENTRIES;
import DataStructureMethods.EXECUTE_ON_ENTRIES_WITH_PREDICATE;
import DataStructureMethods.EXECUTE_ON_KEY;
import DataStructureMethods.EXECUTE_ON_KEYS;
import DataStructureMethods.GET;
import DataStructureMethods.GET_ALL;
import DataStructureMethods.GET_ASYNC;
import DataStructureMethods.INVOKE;
import DataStructureMethods.INVOKE_ALL;
import DataStructureMethods.LOAD_ALL;
import DataStructureMethods.LOAD_ALL_WITH_KEYS;
import DataStructureMethods.LOAD_ALL_WITH_LISTENER;
import DataStructureMethods.PUT;
import DataStructureMethods.PUT_ALL;
import DataStructureMethods.PUT_ASYNC;
import DataStructureMethods.PUT_ASYNC_WITH_EXPIRY_POLICY;
import DataStructureMethods.PUT_ASYNC_WITH_TTL;
import DataStructureMethods.PUT_IF_ABSENT;
import DataStructureMethods.PUT_IF_ABSENT_ASYNC;
import DataStructureMethods.PUT_TRANSIENT;
import DataStructureMethods.REMOVE;
import DataStructureMethods.REMOVE_ALL;
import DataStructureMethods.REMOVE_ALL_WITH_KEYS;
import DataStructureMethods.REMOVE_ASYNC;
import DataStructureMethods.REMOVE_WITH_OLD_VALUE;
import DataStructureMethods.REPLACE;
import DataStructureMethods.REPLACE_WITH_OLD_VALUE;
import DataStructureMethods.SET;
import DataStructureMethods.SET_ASYNC;
import DataStructureMethods.SET_ASYNC_WITH_EXPIRY_POLICY;
import DataStructureMethods.SET_ASYNC_WITH_TTL;
import DataStructureMethods.SET_EXPIRY_POLICY;
import DataStructureMethods.SET_EXPIRY_POLICY_MULTI_KEY;
import DataStructureMethods.SET_TTL;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter.DataStructureMethods;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ConfigureParallelRunnerWith;
import com.hazelcast.test.annotation.HeavilyMultiThreadedTestLimiter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Contains the logic code for unified Near Cache tests.
 *
 * @param <NK>
 * 		key type of the tested Near Cache
 * @param <NV>
 * 		value type of the tested Near Cache
 */
@ConfigureParallelRunnerWith(HeavilyMultiThreadedTestLimiter.class)
@SuppressWarnings("WeakerAccess")
public abstract class AbstractNearCacheBasicTest<NK, NV> extends HazelcastTestSupport {
    /**
     * The default count to be inserted into the Near Caches.
     */
    protected static final int DEFAULT_RECORD_COUNT = 1000;

    /**
     * Number of seconds to expire Near Cache entries via TTL.
     */
    protected static final int MAX_TTL_SECONDS = 2;

    /**
     * Number of seconds to expire idle Near Cache entries.
     */
    protected static final int MAX_IDLE_SECONDS = 1;

    /**
     * The default name used for the data structures which have a Near Cache.
     */
    protected static final String DEFAULT_NEAR_CACHE_NAME = "defaultNearCache";

    /**
     * Defines all {@link DataStructureMethods} which are using EntryProcessors.
     */
    private static final List<DataStructureMethods> ENTRY_PROCESSOR_METHODS = Arrays.asList(INVOKE, EXECUTE_ON_KEY, EXECUTE_ON_KEYS, EXECUTE_ON_ENTRIES, EXECUTE_ON_ENTRIES_WITH_PREDICATE, INVOKE_ALL);

    /**
     * The {@link NearCacheConfig} used by the Near Cache tests.
     * <p>
     * Needs to be set by the implementations of this class in their {@link org.junit.Before} methods.
     */
    protected NearCacheConfig nearCacheConfig;

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#GET} is used
     * and that the {@link com.hazelcast.monitor.NearCacheStats} are calculated correctly.
     */
    @Test
    public void whenGetIsUsed_thenNearCacheShouldBePopulated() {
        whenGetIsUsed_thenNearCacheShouldBePopulated(GET);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#GET_ASYNC} is used
     * and that the {@link com.hazelcast.monitor.NearCacheStats} are calculated correctly.
     */
    @Test
    public void whenGetAsyncIsUsed_thenNearCacheShouldBePopulated() {
        whenGetIsUsed_thenNearCacheShouldBePopulated(GET_ASYNC);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#GET_ALL} is used
     * and that the {@link com.hazelcast.monitor.NearCacheStats} are calculated correctly.
     */
    @Test
    public void whenGetAllIsUsed_thenNearCacheShouldBePopulated() {
        whenGetIsUsed_thenNearCacheShouldBePopulated(GET_ALL);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#GET_ALL} is used on a half
     * filled Near Cache and that the {@link com.hazelcast.monitor.NearCacheStats} are calculated correctly.
     */
    @Test
    public void whenGetAllWithHalfFilledNearCacheIsUsed_thenNearCacheShouldBePopulated() {
        assumeThatMethodIsAvailable(GET_ALL);
        NearCacheTestContext<Integer, String, NK, NV> context = createContext();
        // assert that the Near Cache is empty
        populateDataAdapter(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheSize(context, 0);
        NearCacheTestUtils.assertNearCacheStats(context, 0, 0, 0);
        // populate the Near Cache with half of the entries
        int size = (AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT) / 2;
        populateNearCache(context, GET_ALL, size);
        NearCacheTestUtils.assertNearCacheSizeEventually(context, size);
        NearCacheTestUtils.assertNearCacheStats(context, size, 0, size);
        // generate Near Cache hits and populate the missing entries
        int expectedHits = size;
        populateNearCache(context, GET_ALL);
        NearCacheTestUtils.assertNearCacheSize(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheStats(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT, expectedHits, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        // generate Near Cache hits
        expectedHits += AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT;
        populateNearCache(context, GET_ALL);
        NearCacheTestUtils.assertNearCacheSize(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheStats(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT, expectedHits, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheContent(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
    }

    /**
     * Checks that the Near Cache is not populated when {@link DataStructureMethods#GET_ALL} is used with an empty key set.
     */
    @Test
    public void whenGetAllWithEmptySetIsUsed_thenNearCacheShouldNotBePopulated() {
        assumeThatMethodIsAvailable(GET_ALL);
        NearCacheTestContext<Integer, String, NK, NV> context = createContext();
        // assert that the Near Cache is empty
        populateDataAdapter(context, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheSize(context, 0);
        NearCacheTestUtils.assertNearCacheStats(context, 0, 0, 0);
        // use getAll() with an empty set, which should not populate the Near Cache
        context.nearCacheAdapter.getAll(Collections.<Integer>emptySet());
        NearCacheTestUtils.assertNearCacheSize(context, 0);
        NearCacheTestUtils.assertNearCacheStats(context, 0, 0, 0);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#SET} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenSetIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(SET);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#SET_ASYNC} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenSetAsyncIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(SET_ASYNC);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#SET_ASYNC_WITH_TTL} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenSetAsyncWithTtlIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(SET_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#SET_ASYNC_WITH_EXPIRY_POLICY} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenSetAsyncWithExpiryPolicyIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(SET_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_ASYNC} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutAsyncIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_ASYNC);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_ASYNC_WITH_TTL} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutAsyncWithTtlIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_ASYNC_WITH_EXPIRY_POLICY} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutAsyncWithExpiryPolicyIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_TRANSIENT} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutTransientIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_TRANSIENT);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_IF_ABSENT} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutIfAbsentIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_IF_ABSENT);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_IF_ABSENT_ASYNC} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutIfAbsentAsyncIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_IF_ABSENT_ASYNC);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#REPLACE} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenReplaceIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(REPLACE);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#REPLACE_WITH_OLD_VALUE} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenReplaceWithOldValueIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(REPLACE_WITH_OLD_VALUE);
    }

    /**
     * Checks that the Near Cache is populated when {@link DataStructureMethods#PUT_ALL} with
     * {@link com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy#CACHE_ON_UPDATE} is used.
     */
    @Test
    public void whenPutAllIsUsedWithCacheOnUpdate_thenNearCacheShouldBePopulated() {
        whenEntryIsAddedWithCacheOnUpdate_thenNearCacheShouldBePopulated(PUT_ALL);
    }

    @Test
    public void whenSetExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_EXPIRY_POLICY_MULTI_KEY);
    }

    @Test
    public void whenSetExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_EXPIRY_POLICY_MULTI_KEY);
    }

    @Test
    public void whenSetExpiryPolicyOnSingleKeyIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_EXPIRY_POLICY);
    }

    @Test
    public void whenSetExpiryPolicyOnSingleKeyIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET} is used.
     */
    @Test
    public void whenSetIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET} is used.
     */
    @Test
    public void whenSetIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC} is used.
     */
    @Test
    public void whenSetAsyncIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC} is used.
     */
    @Test
    public void whenSetAsyncIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC_WITH_TTL} is used.
     */
    @Test
    public void whenSetAsyncWithTtlIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC_WITH_TTL} is used.
     */
    @Test
    public void whenSetAsyncWithTtlIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC_WITH_EXPIRY_POLICY}
     * is used.
     */
    @Test
    public void whenSetAsyncWithExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#SET_ASYNC_WITH_EXPIRY_POLICY}
     * is used.
     */
    @Test
    public void whenSetAsyncWithExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT} is used.
     */
    @Test
    public void whenPutIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT} is used.
     */
    @Test
    public void whenPutIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC} is used.
     */
    @Test
    public void whenPutAsyncIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT_ASYNC);
    }

    @Test
    public void whenSetTTLIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, SET_TTL);
    }

    @Test
    public void whenSetTTLIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, SET_TTL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC} is used.
     */
    @Test
    public void whenPutAsyncIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC_WITH_TTL} is used.
     */
    @Test
    public void whenPutAsyncWithTtlIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC_WITH_TTL} is used.
     */
    @Test
    public void whenPutAsyncWithTtlIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT_ASYNC_WITH_TTL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC_WITH_EXPIRY_POLICY}
     * is used.
     */
    @Test
    public void whenPutAsyncWithExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ASYNC_WITH_EXPIRY_POLICY}
     * is used.
     */
    @Test
    public void whenPutAsyncWithExpiryPolicyIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT_ASYNC_WITH_EXPIRY_POLICY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_TRANSIENT}
     * is used.
     */
    @Test
    public void whenPutTransientIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT_TRANSIENT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_TRANSIENT}
     * is used.
     */
    @Test
    public void whenPutTransientIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT_TRANSIENT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REPLACE} is used.
     */
    @Test
    public void whenReplaceIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, REPLACE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REPLACE} is used.
     */
    @Test
    public void whenReplaceIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, REPLACE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REPLACE_WITH_OLD_VALUE} is used.
     */
    @Test
    public void whenReplaceWithOldValueIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, REPLACE_WITH_OLD_VALUE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REPLACE_WITH_OLD_VALUE} is used.
     */
    @Test
    public void whenReplaceWithOldValueIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, REPLACE_WITH_OLD_VALUE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#INVOKE} is used.
     */
    @Test
    public void whenInvokeIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, INVOKE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#INVOKE} is used.
     */
    @Test
    public void whenInvokeIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, INVOKE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_KEY} is used.
     */
    @Test
    public void whenExecuteOnKeyIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, EXECUTE_ON_KEY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_KEY} is used.
     */
    @Test
    public void whenExecuteOnKeyIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, EXECUTE_ON_KEY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_KEYS} is used.
     */
    @Test
    public void whenExecuteOnKeysIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, EXECUTE_ON_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_KEYS} is used.
     */
    @Test
    public void whenExecuteOnKeysIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, EXECUTE_ON_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_ENTRIES} is used.
     */
    @Test
    public void whenExecuteOnEntriesIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, EXECUTE_ON_ENTRIES);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_ENTRIES} is used.
     */
    @Test
    public void whenExecuteOnEntriesIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, EXECUTE_ON_ENTRIES);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_ENTRIES_WITH_PREDICATE}
     * is used.
     */
    @Test
    public void whenExecuteOnEntriesWithPredicateIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, EXECUTE_ON_ENTRIES_WITH_PREDICATE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EXECUTE_ON_ENTRIES_WITH_PREDICATE}
     * is used.
     */
    @Test
    public void whenExecuteOnEntriesWithPredicateIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, EXECUTE_ON_ENTRIES_WITH_PREDICATE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ALL} is used.
     */
    @Test
    public void whenPutAllIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, PUT_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#PUT_ALL} is used.
     */
    @Test
    public void whenPutAllIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, PUT_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#INVOKE_ALL} is used.
     */
    @Test
    public void whenInvokeAllIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(false, INVOKE_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#INVOKE_ALL} is used.
     */
    @Test
    public void whenInvokeAllIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsChanged_thenNearCacheShouldBeInvalidated(true, INVOKE_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL} is used.
     */
    @Test
    public void whenLoadAllIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(false, LOAD_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL} is used.
     */
    @Test
    public void whenLoadAllIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(true, LOAD_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL_WITH_KEYS} is used.
     */
    @Test
    public void whenLoadAllWithKeysIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(false, LOAD_ALL_WITH_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL_WITH_KEYS} is used.
     */
    @Test
    public void whenLoadAllWithKeysIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(true, LOAD_ALL_WITH_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL_WITH_LISTENER} is used.
     */
    @Test
    public void whenLoadAllWithListenerIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(false, LOAD_ALL_WITH_LISTENER);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#LOAD_ALL_WITH_LISTENER} is used.
     */
    @Test
    public void whenLoadAllWithListenerIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(true, LOAD_ALL_WITH_LISTENER);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EVICT} is used.
     */
    @Test
    public void whenEvictIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(false, EVICT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EVICT} is used.
     */
    @Test
    public void whenEvictIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(true, EVICT);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EVICT_ALL} is used.
     */
    @Test
    public void whenEvictAllIsUsed_thenNearCacheIsInvalidated_onNearCacheAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(false, EVICT_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#EVICT_ALL} is used.
     */
    @Test
    public void whenEvictAllIsUsed_thenNearCacheIsInvalidated_onDataAdapter() {
        whenEntryIsLoaded_thenNearCacheShouldBeInvalidated(true, EVICT_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE} is used.
     */
    @Test
    public void whenRemoveIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, REMOVE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE} is used.
     */
    @Test
    public void whenRemoveIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, REMOVE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ASYNC} is used.
     */
    @Test
    public void whenRemoveAsyncIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, REMOVE_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ASYNC} is used.
     */
    @Test
    public void whenRemoveAsyncIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, REMOVE_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_WITH_OLD_VALUE)} is used.
     */
    @Test
    public void whenRemoveWithOldValueIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, REMOVE_WITH_OLD_VALUE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_WITH_OLD_VALUE)} is used.
     */
    @Test
    public void whenRemoveWithOldValueIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, REMOVE_WITH_OLD_VALUE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DELETE} is used.
     */
    @Test
    public void whenDeleteIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, DELETE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DELETE} is used.
     */
    @Test
    public void whenDeleteIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, DELETE);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DELETE_ASYNC} is used.
     */
    @Test
    public void whenDeleteAsyncIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, DELETE_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DELETE_ASYNC} is used.
     */
    @Test
    public void whenDeleteAsyncIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, DELETE_ASYNC);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ALL)} is used.
     */
    @Test
    public void whenRemoveAllIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, REMOVE_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ALL)} is used.
     */
    @Test
    public void whenRemoveAllIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, REMOVE_ALL);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ALL_WITH_KEYS)} is used.
     */
    @Test
    public void whenRemoveAllWithKeysIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, REMOVE_ALL_WITH_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#REMOVE_ALL_WITH_KEYS)} is used.
     */
    @Test
    public void whenRemoveAllWithKeysIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, REMOVE_ALL_WITH_KEYS);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#CLEAR)} is used.
     */
    @Test
    public void whenClearIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, CLEAR);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#CLEAR)} is used.
     */
    @Test
    public void whenClearIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, CLEAR);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DESTROY)} is used.
     */
    @Test
    public void whenDestroyIsUsed_thenNearCacheShouldBeInvalidated_onNearCacheAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(false, DESTROY);
    }

    /**
     * Checks that the Near Cache is eventually invalidated when {@link DataStructureMethods#DESTROY)} is used.
     */
    @Test
    public void whenDestroyIsUsed_thenNearCacheShouldBeInvalidated_onDataAdapter() {
        whenEntryIsRemoved_thenNearCacheShouldBeInvalidated(true, DESTROY);
    }

    /**
     * Checks that the Near Cache works correctly when {@link DataStructureMethods#CONTAINS_KEY} is used.
     */
    @Test
    public void testContainsKey_onNearCacheAdapter() {
        testContainsKey(false);
    }

    /**
     * Checks that the Near Cache works correctly when {@link DataStructureMethods#CONTAINS_KEY} is used.
     */
    @Test
    public void testContainsKey_onDataAdapter() {
        testContainsKey(true);
    }

    @Test
    public void whenNullKeyIsCached_thenContainsKeyShouldReturnFalse() {
        NearCacheTestContext<Integer, Integer, NK, NV> context = createContext();
        int absentKey = 1;
        Assert.assertNull("Returned value should be null", context.nearCacheAdapter.get(absentKey));
        Assert.assertFalse("containsKey() should return false", context.nearCacheAdapter.containsKey(absentKey));
    }

    /**
     * Checks that the Near Cache eviction works as expected if the Near Cache is full.
     */
    @Test
    public void testNearCacheEviction() {
        NearCacheTestUtils.setEvictionConfig(nearCacheConfig, EvictionPolicy.LRU, MaxSizePolicy.ENTRY_COUNT, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestContext<Integer, String, NK, NV> context = createContext();
        // populate the backing data structure with an extra entry
        populateDataAdapter(context, ((AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT) + 1));
        populateNearCache(context);
        // all Near Cache implementations use the same eviction algorithm, which evicts a single entry
        int expectedEvictions = 1;
        // we expect (size + the extra entry - the expectedEvictions) entries in the Near Cache
        long expectedOwnedEntryCount = ((AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT) + 1) - expectedEvictions;
        long expectedHits = context.stats.getHits();
        long expectedMisses = (context.stats.getMisses()) + 1;
        // trigger eviction via fetching the extra entry
        context.nearCacheAdapter.get(AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheEvictionsEventually(context, expectedEvictions);
        NearCacheTestUtils.assertNearCacheStats(context, expectedOwnedEntryCount, expectedHits, expectedMisses, expectedEvictions, 0);
    }

    @Test
    public void testNearCacheExpiration_withTTL() {
        nearCacheConfig.setTimeToLiveSeconds(AbstractNearCacheBasicTest.MAX_TTL_SECONDS);
        testNearCacheExpiration(AbstractNearCacheBasicTest.MAX_TTL_SECONDS);
    }

    @Test
    public void testNearCacheExpiration_withMaxIdle() {
        nearCacheConfig.setMaxIdleSeconds(AbstractNearCacheBasicTest.MAX_IDLE_SECONDS);
        testNearCacheExpiration(AbstractNearCacheBasicTest.MAX_IDLE_SECONDS);
    }

    /**
     * Checks that the memory costs are calculated correctly.
     * <p>
     * This variant uses a single-threaded approach to fill the Near Cache with data.
     */
    @Test
    public void testNearCacheMemoryCostCalculation() {
        testNearCacheMemoryCostCalculation(1);
    }

    /**
     * Checks that the memory costs are calculated correctly.
     * <p>
     * This variant uses a multi-threaded approach to fill the Near Cache with data.
     */
    @Test
    public void testNearCacheMemoryCostCalculation_withConcurrentCacheMisses() {
        testNearCacheMemoryCostCalculation(10);
    }

    /**
     * Checks that the Near Cache never returns its internal {@link NearCache#CACHED_AS_NULL} to the user
     * when {@link DataStructureMethods#GET} is used.
     */
    @Test
    public void whenGetIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache() {
        whenGetIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache(GET);
    }

    /**
     * Checks that the Near Cache never returns its internal {@link NearCache#CACHED_AS_NULL} to the user
     * when {@link DataStructureMethods#GET_ASYNC} is used.
     */
    @Test
    public void whenGetAsyncIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache() {
        whenGetIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache(GET_ASYNC);
    }

    /**
     * Checks that the Near Cache never returns its internal {@link NearCache#CACHED_AS_NULL} to the user
     * when {@link DataStructureMethods#GET_ALL} is used.
     */
    @Test
    public void whenGetAllIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache() {
        whenGetIsUsedOnEmptyDataStructure_thenAlwaysReturnNullFromNearCache(GET_ALL);
    }

    /**
     * Checks that the Near Cache updates value for keys which are already in the Near Cache,
     * even if the Near Cache is full an the eviction is disabled (via {@link com.hazelcast.config.EvictionPolicy#NONE}.
     */
    @Test
    public void whenNearCacheIsFull_thenPutOnSameKeyShouldUpdateValue_onNearCacheAdapter() {
        whenNearCacheIsFull_thenPutOnSameKeyShouldUpdateValue(false);
    }

    /**
     * Checks that the Near Cache updates value for keys which are already in the Near Cache,
     * even if the Near Cache is full an the eviction is disabled (via {@link com.hazelcast.config.EvictionPolicy#NONE}.
     */
    @Test
    public void whenNearCacheIsFull_thenPutOnSameKeyShouldUpdateValue_onDataAdapter() {
        whenNearCacheIsFull_thenPutOnSameKeyShouldUpdateValue(true);
    }

    @Test
    public void whenSetTTLIsCalled_thenAnotherNearCacheContextShouldBeInvalidated() {
        assumeThatMethodIsAvailable(SET_TTL);
        nearCacheConfig.setInvalidateOnChange(true);
        NearCacheTestContext<Integer, String, NK, NV> firstContext = createContext();
        NearCacheTestContext<Integer, String, NK, NV> secondContext = createNearCacheContext();
        populateDataAdapter(firstContext, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        populateNearCache(secondContext);
        for (int i = 0; i < (AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT); i++) {
            firstContext.nearCacheAdapter.setTtl(i, 0, TimeUnit.DAYS);
        }
        NearCacheTestUtils.assertNearCacheSizeEventually(secondContext, 0);
    }

    @Test
    public void whenValueIsUpdated_thenAnotherNearCacheContextShouldBeInvalidated() {
        nearCacheConfig.setInvalidateOnChange(true);
        NearCacheTestContext<Integer, String, NK, NV> firstContext = createContext();
        NearCacheTestContext<Integer, String, NK, NV> secondContext = createNearCacheContext();
        // populate the data adapter in the first context
        populateDataAdapter(firstContext, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        // populate Near Cache in the second context
        populateNearCache(secondContext);
        // update values in the first context
        for (int i = 0; i < (AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT); i++) {
            firstContext.nearCacheAdapter.put(i, ("newValue-" + i));
        }
        // wait until the second context is invalidated
        NearCacheTestUtils.assertNearCacheSizeEventually(secondContext, 0);
        // populate the second context again with the updated values
        populateNearCache(secondContext, GET, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT, "newValue-");
    }

    @Test
    public void whenValueIsRemoved_thenAnotherNearCacheContextCannotGetValueAgain() {
        nearCacheConfig.setInvalidateOnChange(true);
        NearCacheTestContext<Integer, String, NK, NV> firstContext = createContext();
        NearCacheTestContext<Integer, String, NK, NV> secondContext = createNearCacheContext();
        // populate the data adapter in the first context
        populateDataAdapter(firstContext, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        // populate Near Cache in the second context
        populateNearCache(secondContext);
        // remove values from the first context
        for (int i = 0; i < (AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT); i++) {
            firstContext.nearCacheAdapter.remove(i);
        }
        // wait until the second context is invalidated
        NearCacheTestUtils.assertNearCacheSizeEventually(secondContext, 0);
        // populate the second context again with the updated values
        populateNearCache(secondContext, GET, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT, null);
    }

    @Test
    public void whenDataStructureIsCleared_thenAnotherNearCacheContextCannotGetValuesAgain() {
        assumeThatMethodIsAvailable(CLEAR);
        nearCacheConfig.setInvalidateOnChange(true);
        NearCacheTestContext<Integer, String, NK, NV> firstContext = createContext();
        NearCacheTestContext<Integer, String, NK, NV> secondContext = createNearCacheContext();
        // populate the data adapter in the first context
        populateDataAdapter(firstContext, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT);
        // populate Near Cache in the second context
        populateNearCache(secondContext);
        // call clear() from the first context
        firstContext.nearCacheAdapter.clear();
        // wait until the second context is invalidated
        NearCacheTestUtils.assertNearCacheSizeEventually(secondContext, 0);
        // populate the second context again with the updated values
        populateNearCache(secondContext, GET, AbstractNearCacheBasicTest.DEFAULT_RECORD_COUNT, null);
    }
}

