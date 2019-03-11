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
package org.apache.geode.internal.cache.eviction;


import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.internal.lang.SystemPropertyHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;
import org.mockito.Mockito;


public class EvictionListBuilderTest {
    private static final String EVICTION_PROPERTY_NAME = "geode." + (SystemPropertyHelper.EVICTION_SCAN_ASYNC);

    @Rule
    public ClearSystemProperties clearProperties = new ClearSystemProperties(EvictionListBuilderTest.EVICTION_PROPERTY_NAME);

    private EvictionListBuilder builder;

    private EvictionController controller;

    private EvictionAlgorithm algorithm;

    @Test
    public void createsLIFOListWhenAlgorithmIsLifo() {
        EvictionAlgorithm lifoAlgorithm = EvictionAlgorithm.LIFO_ENTRY;
        Mockito.when(controller.getEvictionAlgorithm()).thenReturn(lifoAlgorithm);
        assertThat(builder.create()).isInstanceOf(LIFOList.class);
    }

    @Test
    public void createsAsyncLruByDefault() {
        assertThat(builder.create()).isInstanceOf(LRUListWithAsyncSorting.class);
    }

    @Test
    public void createsAsyncLruWhenSystemConfiguredToUseIt() {
        System.setProperty(EvictionListBuilderTest.EVICTION_PROPERTY_NAME, "true");
        builder = new EvictionListBuilder(controller);
        assertThat(builder.create()).isInstanceOf(LRUListWithAsyncSorting.class);
    }

    @Test
    public void createdSyncLruWhenSystemConfiguredToUseIt() {
        System.setProperty(EvictionListBuilderTest.EVICTION_PROPERTY_NAME, "false");
        builder = new EvictionListBuilder(controller);
        assertThat(builder.create()).isInstanceOf(LRUListWithSyncSorting.class);
    }
}

