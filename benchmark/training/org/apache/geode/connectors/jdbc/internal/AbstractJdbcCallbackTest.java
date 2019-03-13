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
package org.apache.geode.connectors.jdbc.internal;


import Operation.CREATE;
import Operation.LOCAL_LOAD_CREATE;
import org.apache.geode.cache.Region;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AbstractJdbcCallbackTest {
    private AbstractJdbcCallback jdbcCallback;

    private SqlHandler sqlHandler;

    private InternalCache cache;

    @Test
    public void returnsCorrectSqlHander() {
        assertThat(jdbcCallback.getSqlHandler()).isSameAs(sqlHandler);
    }

    @Test
    public void checkInitializedDoesNothingIfInitialized() {
        jdbcCallback.checkInitialized(Mockito.mock(Region.class));
        assertThat(jdbcCallback.getSqlHandler()).isSameAs(sqlHandler);
    }

    @Test
    public void initializedSqlHandlerIfNoneExists() {
        jdbcCallback = Mockito.spy(AbstractJdbcCallback.class);
        // jdbcCallback = spy(new AbstractJdbcCallback() {});
        InternalCache cache = Mockito.mock(InternalCache.class);
        Region region = Mockito.mock(Region.class);
        Mockito.when(region.getRegionService()).thenReturn(cache);
        Mockito.when(region.getName()).thenReturn("regionName");
        JdbcConnectorService service = Mockito.mock(JdbcConnectorService.class);
        Mockito.when(cache.getService(ArgumentMatchers.any())).thenReturn(service);
        assertThat(jdbcCallback.getSqlHandler()).isNull();
        RegionMapping regionMapping = Mockito.mock(RegionMapping.class);
        Mockito.when(service.getMappingForRegion("regionName")).thenReturn(regionMapping);
        SqlHandler sqlHandler = Mockito.mock(SqlHandler.class);
        Mockito.doReturn(sqlHandler).when(jdbcCallback).createSqlHandler(ArgumentMatchers.same(cache), ArgumentMatchers.eq("regionName"), ArgumentMatchers.any(), ArgumentMatchers.same(service));
        jdbcCallback.checkInitialized(region);
        assertThat(jdbcCallback.getSqlHandler()).isNotNull();
    }

    @Test
    public void verifyLoadsAreIgnored() {
        boolean ignoreEvent = jdbcCallback.eventCanBeIgnored(LOCAL_LOAD_CREATE);
        assertThat(ignoreEvent).isTrue();
    }

    @Test
    public void verifyCreateAreNotIgnored() {
        boolean ignoreEvent = jdbcCallback.eventCanBeIgnored(CREATE);
        assertThat(ignoreEvent).isFalse();
    }
}

